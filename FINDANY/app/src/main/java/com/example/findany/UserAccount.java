package com.example.findany;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Uri selectedImage;
    FirebaseAuth mAuth;
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = firebaseStorage.getReference();
    private static final int RESULT_LOAD_IMAGE = 1;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentname;
    String fullname;
    String getemail;
    String mobilenumber;
    int OTP;
    int enteredotp;
    Boolean checkotp;
    Boolean validatenumber;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        // Initialize views
        initializeViews();

        // Set up SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Boolean isMobileVerified = prefs.getBoolean("isMobileVerified", false);

        // Generate OTP
        OTP = generateOTP();

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Set up user information
        if (user != null) {
            setUpUserInfo(user);
        } else {
            signout();
            Toast.makeText(UserAccount.this, "email not found", Toast.LENGTH_SHORT).show();
        }

        // Read data from Firebase
        read2firebase(documentname);
        try {
            Toast.makeText(UserAccount.this, documentname, Toast.LENGTH_SHORT).show();

            loadImageFromFirebaseStorage(documentname,getprofileimage);
        } catch (Exception e) {
            Toast.makeText(UserAccount.this, "image not found", Toast.LENGTH_SHORT).show();
        }

        // Set up display information
        displayfullname.setText(fullname);
        regno.setText(documentname);

        // Set up onClickListeners
        setUpOnClickListeners();
    }

    private void initializeViews() {
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
    }

    private int generateOTP() {
        Random random = new Random();
        int digits = 4 + random.nextInt(2);  // 4 or 5
        return (int) (Math.pow(10, digits - 1) + random.nextInt((int) Math.pow(10, digits) - 1));
    }

    private void setUpUserInfo(FirebaseUser user) {
        getemail = user.getEmail();
        email.setText(getemail);
        int index = getemail.indexOf('@');
        documentname = getemail.substring(0, index);

        String userfullname = user.getDisplayName();
        int nameindex = userfullname.indexOf(String.valueOf(2));
        fullname = userfullname.substring(0, nameindex);
    }

    private void setUpOnClickListeners() {
        signout.setOnClickListener(v -> signout());

        editprofile.setOnClickListener(v -> openGallery());

        getotp.setOnClickListener(v -> {
            getotp.setEnabled(false);
            mobilenumber = getmobilenumber.getText().toString();
            if (!mobilenumber.isEmpty()) {
                if (isValidMobileNumber(mobilenumber)) {
                    sendsms sendsms = new sendsms(mobilenumber, OTP);
                    sendsms.execute();
                } else {
                    Toast.makeText(UserAccount.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UserAccount.this, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(v -> {
            mobilenumber = getmobilenumber.getText().toString();
            Toast.makeText(UserAccount.this, mobilenumber, Toast.LENGTH_SHORT).show();

            String username = getusername.getText().toString();
            try {
                enteredotp = Integer.parseInt(enterotp.getText().toString());
            } catch (Exception e) {
            }
            checkotp = checkotp(OTP, enteredotp);
            validatenumber = isValidMobileNumber(mobilenumber);

            if (checkotp && validatenumber) {
                write2firebase(username, mobilenumber);
                handleImageUpload(selectedImage);
                Toast.makeText(UserAccount.this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserAccount.this, "Check whether the number, OTP, or username is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void write2firebase(String username, String mobileNumber) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", username);
        data.put("Mail", getemail);
        data.put("Full Name", fullname);
        data.put("Reg NO", documentname);
        data.put("Mobile Number", mobileNumber);

        db.collection("UserDetails").document(documentname).set(data)
                .addOnSuccessListener(unused -> Toast.makeText(UserAccount.this, "Uploaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Unable to upload", Toast.LENGTH_SHORT).show());
    }

    public void read2firebase(String documentname) {
        db.collection("UserDetails").document(documentname).get()
                .addOnCompleteListener(task -> {
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
                            Toast.makeText(UserAccount.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserAccount.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Error", Toast.LENGTH_SHORT).show());
    }

    public void signout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Clear cached user information
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Navigate back to LoginActivity
                Intent intent = new Intent(UserAccount.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleImageUpload(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference imageRef = storageReference.child(documentname);
            UploadTask uploadTask = imageRef.putBytes(data);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(UserAccount.this, "Unable to upload", Toast.LENGTH_SHORT).show();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    FirebaseDatabase.getInstance().getReference().child("images").push().setValue(downloadUrl);
                } else {
                    Toast.makeText(UserAccount.this, "Unable to upload1", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(UserAccount.this, "Unable to upload2", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromFirebaseStorage(String imageName, ImageView imageView) {
        FirebaseStorage.getInstance().getReference().child(imageName).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this).load(uri).into(imageView);
        });
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            // Get the selected image's URI
            selectedImage = data.getData();
            Glide.with(this).load(selectedImage).into(getprofileimage);

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

        }
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        // Define the regular expression pattern for a valid mobile number
        String pattern = "^[6-9]\\d{9}$";

        // Compile the pattern into a regular expression object
        Pattern regex = Pattern.compile(pattern);

        // Match the mobile number against the pattern
        Matcher matcher = regex.matcher(mobileNumber);

        // Return true if the mobile number matches the pattern, false otherwise
        return matcher.matches();
    }

    private boolean checkotp(int random, int enteredNumber) {
        return random == enteredNumber;
    }
}