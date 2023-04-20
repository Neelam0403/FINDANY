package com.example.findany;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class sendsms extends AsyncTask<Void, Void, Void> {

    private String apiKey="uFOXiJqR13QUnS5ybaToW0g4CPtGAwkB6vspjILDdlZYNerK97uNKQwYAfoH0i1chn8aStWz6LBrU5Me";
    private String senderId="Neelam";
    private String mobileNumber;
    private String message;

    public sendsms(String mobileNumber, int message) {
        this.mobileNumber = mobileNumber;
        this.message = String.valueOf(message);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // create the URL for the Fast2SMS API
            URL url = new URL("https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey + "&sender_id=" + senderId + "&message=" + message + "&language=english&route=p&numbers=" + mobileNumber);

            // open the connection to the URL
            URLConnection connection = url.openConnection();

            // read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
