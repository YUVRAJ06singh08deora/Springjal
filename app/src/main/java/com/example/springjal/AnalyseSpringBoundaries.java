package com.example.springjal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnalyseSpringBoundaries extends AppCompatActivity {
    ImageView image1,image2,preprocessimg1,preprocessimg2;
    RelativeLayout analysebtn;
    TextView original,preprocessed;
    String imageurl,imageurl2;
    TextView changeAre;
    String link1,link2;
    private loading loadingDialog;
    double area,area1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_spring_boundaries);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        original=findViewById(R.id.AreaOfOriginaltext);
        preprocessed=findViewById(R.id.AreaOfpreprocess);
        preprocessimg1=findViewById(R.id.preprocessedimage1);
        preprocessimg2=findViewById(R.id.image2preprocessed);
        changeAre=findViewById(R.id.chgedArea);
        loadingDialog= new loading(AnalyseSpringBoundaries.this);
        analysebtn=findViewById(R.id.analysebtn);
        Intent in=getIntent();
        imageurl=in.getStringExtra("imageurl");
        imageurl2= "https://firebasestorage.googleapis.com/v0/b/sih-springshed.appspot.com/o/Screenshot%202023-12-20%20121618.jpg?alt=media&token=61ffe801-79c7-4fc4-9bda-7a562620fe65";
        Picasso.get()
                .load(imageurl)
                .resize(600, 800) // Provide valid positive integers for width and height
                .centerInside()
                .into(image1);
        Picasso.get()
                .load(imageurl2)
                .resize(600, 800) // Provide valid positive integers for width and height
                .centerInside()
                .into(image2);




        analysebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                new FlaskRequestTaskImg().execute(imageurl);

                new FlaskRequestTaskImg2().execute(imageurl2);



            }
        });
    }
    private class FlaskRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String imageUrl = params[0];
            String serverUrl = "https://chiragswmcalculatepixel.azurewebsites.net/analyze";
            String Imagefetch = "https://flask-production-5149.up.railway.app/process_image";// Replace with your server's IP address

            try {
                URL url = new URL(serverUrl);
                URL urlimg = new URL(Imagefetch);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = "{\"image_url\": \"" + imageUrl + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get response from the Flask server
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the Flask server response here
            // For simplicity, we will just print the result to the console
            System.out.println("Flask Server Response: " + result);
            try {
                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(result);

                // Fetch the "area" data
                area = 100-jsonObject.getDouble("area");

                // Convert the area to a string
                String areaString = Double.toString(area);
                original.setText(areaString);

                // Print the result
                System.out.println("Area: " + areaString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    private class FlaskRequestTaskImg extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String imageUrl = params[0];
            String serverUrl = "https://flask-production-5149.up.railway.app/process_image";// Replace with your server's IP address

            try {
                URL url = new URL(serverUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = "{\"image_url\": \"" + imageUrl + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get response from the Flask server
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the Flask server response here
            // For simplicity, we will just print the result to the console
            System.out.println("Flask Server Response: " + result);
            try {
                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(result);

                // Fetch the "area" data
                link1 = jsonObject.getString("output_image_url");
                new FlaskRequestTask().execute(link1);

                Picasso.get()
                        .load(link1)
                        .resize(600, 800) // Provide valid positive integers for width and height
                        .centerInside()
                        .into(preprocessimg1);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    private class FlaskRequestTaskImg2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String imageUrl = params[0];
            String serverUrl = "https://flask-production-5149.up.railway.app/process_image";// Replace with your server's IP address

            try {
                URL url = new URL(serverUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = "{\"image_url\": \"" + imageUrl + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get response from the Flask server
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result1) {
            // Handle the Flask server response here
            // For simplicity, we will just print the result to the console
            System.out.println("Flask Server Response: " + result1);
            try {
                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(result1);

                // Fetch the "area" data
                link2 = jsonObject.getString("output_image_url");
                System.out.println(link2);
                new FlaskRequestTask2().execute(link2);


                Picasso.get()
                        .load(link2)
                        .resize(600, 800) // Provide valid positive integers for width and height
                        .centerInside()
                        .into(preprocessimg2);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private class FlaskRequestTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String imageUrl = params[0];
            String serverUrl = "https://chiragswmcalculatepixel.azurewebsites.net/analyze";
            String Imagefetch = "https://flask-production-5149.up.railway.app/process_image";// Replace with your server's IP address

            try {
                URL url = new URL(serverUrl);
                URL urlimg = new URL(Imagefetch);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = "{\"image_url\": \"" + imageUrl + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get response from the Flask server
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the Flask server response here
            // For simplicity, we will just print the result to the console
            System.out.println("Flask Server Response: " + result);
            try {
                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(result);

                // Fetch the "area" data
                double area1 = 100-jsonObject.getDouble("area");

                // Convert the area to a string
                String areaString = Double.toString(area1);
                preprocessed.setText(areaString);
                double areaa=((area1-area)/area)*100;
                changeAre.setText(""+areaa);
                loadingDialog.dismiss();

                // Print the result
                System.out.println("Area: " + areaString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }}
