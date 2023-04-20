package com.example.findany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class UserAccount extends AppCompatActivity {

    ImageView getprofileimage;
    Button editprofile;
    EditText displayfullname;
    EditText getusername;
    EditText regno;
    EditText email;
    EditText getmobilenumber;
    Button getotp;
    EditText enterotp;
    Button update;
    Button signout;
    Bitmap uritobitmap;
    Uri imageUri;
    FirebaseAuth mAuth;
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = firebaseStorage.getReference();
    private static final int PICK_IMAGE = 100;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentname;
    String fullname;
    String getemail;
    int randomnumber;
    String mobilenumber;
    int OTP;
    int enteredotp;
    Boolean numbervalidation=false;

    Boolean checkotp;
    Boolean validatenumber;

    Boolean finalvalidationflag;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        getusername = findViewById(R.id.username);
        getmobilenumber = findViewById(R.id.phonenumber);
        email = findViewById(R.id.email);
        update = findViewById(R.id.update);
        signout = findViewById(R.id.signout);
        editprofile = findViewById(R.id.editprofile);
        getprofileimage = findViewById(R.id.profileimage);
        displayfullname = findViewById(R.id.fullname);
        regno = findViewById(R.id.regno);
        enterotp = findViewById(R.id.otp);
        getotp = findViewById(R.id.getotp);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Boolean isMobileVerified = prefs.getBoolean("isMobileVerified", false);

        Random random = new Random();
        int digits = 4 + random.nextInt(2);  // 4 or 5
        OTP = (int) (Math.pow(10, digits - 1) + random.nextInt((int) Math.pow(10, digits) - 1));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        
        //if user logged in then document name is assigned as the email he logged in
        if (user != null) {

            getemail = user.getEmail();
            email.setText(getemail);
            int index = getemail.indexOf('@');
            documentname = getemail.substring(0, index);

            String userfullname = user.getDisplayName();
            int nameindex = userfullname.indexOf(String.valueOf(2));
            fullname = userfullname.substring(0, nameindex);

        } else {
            signout();
            Toast.makeText(UserAccount.this, "email not found", Toast.LENGTH_SHORT).show();
        }

        //read the data from the firebase`
        read2firebase(documentname);
        try {
            retrieveImageFromFirebaseStorage(documentname);
        } catch (Exception e) {
            Toast.makeText(UserAccount.this, "image not found", Toast.LENGTH_SHORT).show();

        }
        displayfullname.setText(fullname);
        regno.setText(documentname);

        //to signout the user
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });

        //when user clicks on the editprofile then he will redirect to the gallery to upload the photo as profile
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getotp.setEnabled(false);
                mobilenumber = getmobilenumber.getText().toString();
                if (mobilenumber != null && !mobilenumber.isEmpty()) {
                    Boolean validatenumber=isValidMobileNumber(mobilenumber);
                    if(validatenumber) {
                        sendsms sendsms = new sendsms(mobilenumber, OTP);
                        sendsms.execute();
                    } else {
                        Toast.makeText(UserAccount.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserAccount.this, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
                }

            }
        });


        
        //to upload the data to the firebase database
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobilenumber = getmobilenumber.getText().toString();
                String username = getusername.getText().toString();
                try {
                    enteredotp = Integer.parseInt(enterotp.getText().toString());
                }catch (Exception e){
                }
                checkotp=checkotp(OTP,enteredotp);
                validatenumber=isValidMobileNumber(mobilenumber);

                if(checkotp && validatenumber){
                    write2firebase(username,mobilenumber);
                    uploadImageToFirebase(uritobitmap);
                    Toast.makeText(UserAccount.this,"Updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserAccount.this,"Check wheather the number or otp or username is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void write2firebase(String username,String mobilenumber){
        Map<String, Object> data = new HashMap<>();
        data.put("name", username);
        data.put("Mail", getemail);
        data.put("Full Name",fullname);
        data.put("Reg NO",documentname);
        data.put("Mobile Number",mobilenumber);

        db.collection("UserDetails").document(documentname).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UserAccount.this,"Uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAccount.this,"unable to Uploaded",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void read2firebase(String documentname){
        db.collection("UserDetails").document(documentname).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String ruser = document.getString("name");
                        String rmail = document.getString("Mail");
                        String rmobilenumber = document.getString("Mobile Number");

                        getusername.setText(ruser);
                        email.setText(rmail);
                        getmobilenumber.setText(rmobilenumber);

                    } else {
                        Toast.makeText(UserAccount.this,"Document doesnot exists",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserAccount.this,"Task failed",Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAccount.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void signout(){
        mAuth.signOut();
        GoogleSignInOptions gso
                = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(UserAccount.this, gso);
        SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.PREF_NAME,0);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("HAS_LOGED_IN",false);
        editor.commit();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(UserAccount.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadImageToFirebase(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageReference.child("images/" + documentname);

        UploadTask uploadTask = imageRef.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    writeImageToFirebaseDatabase(downloadUri.toString());
                } else {
                    // Handle failure
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void writeImageToFirebaseDatabase(String downloadUrl) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("images");
        databaseReference.push().setValue(downloadUrl);
    }

    private void retrieveImageFromFirebaseStorage(String imageName) {
        storageReference.child("images/" + imageName).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                getprofileimage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            getBitmapFromUri(imageUri);
            try {
                getprofileimage.setImageURI(imageUri);
            }catch (Exception e){
                Toast.makeText(UserAccount.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        uritobitmap = null;
        try {
            uritobitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
            return uritobitmap;
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        return android.util.Patterns.PHONE.matcher(mobileNumber).matches();
    }

    private boolean checkotp(int random,int enterdnumber){
        if(random==enterdnumber){
            return true;
        }else{
            return false;
        }
    }
}