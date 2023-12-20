package com.example.springjal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataCollector_new_activity_2 extends AppCompatActivity {
    RelativeLayout uploadbtn;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 2;
    ImageView springimage;
    ImageButton gmapCurrent;
    TextView latitude, longitude;
    EditText additionalDetails;
    private Uri imageUri; // Declaration of imageUri variable
    String state,district,village,beneficiary,status,dateOfSurvey,iotDeviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collector_new2);
        // Handle image input
        Intent intent=  getIntent();
        state = intent.getStringExtra("STATE");
        district = intent.getStringExtra("DISTRICT");
        village = intent.getStringExtra("VILLAGE");
        beneficiary = intent.getStringExtra("BENEFICIARY");
        status = intent.getStringExtra("STATUS");
        dateOfSurvey = intent.getStringExtra("DATE_OF_SURVEY");
        iotDeviceId = intent.getStringExtra("IOT_DEVICE_ID");
        springimage = findViewById(R.id.imageinput);
        latitude = findViewById(R.id.latitudeinput);
        gmapCurrent=findViewById(R.id.gmapImgButn);
        longitude = findViewById(R.id.longitudeinput);
        additionalDetails = findViewById(R.id.additionaldetailsinput);
        double latitudedouble=intent.getDoubleExtra("latitude",0.0);
        double longitudedouble=intent.getDoubleExtra("longitude",0.0);
        String latstr=String.valueOf(latitudedouble);
        String longstr=String.valueOf(longitudedouble);
        latitude.setText(latstr);
        longitude.setText(longstr);
        uploadbtn = findViewById(R.id.uploadbtn);
        springimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   dispatchTakePictureIntent();
                selectFromGallery();
            }
        });
        gmapCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = intent.getStringExtra("STATE");
                district = intent.getStringExtra("DISTRICT");
                village = intent.getStringExtra("VILLAGE");
                beneficiary = intent.getStringExtra("BENEFICIARY");
                status = intent.getStringExtra("STATUS");
                dateOfSurvey = intent.getStringExtra("DATE_OF_SURVEY");
                iotDeviceId = intent.getStringExtra("IOT_DEVICE_ID");
                Intent i = new Intent(DataCollector_new_activity_2.this, Fetch_coordinates.class);
                i.putExtra("STATE", state);
                i.putExtra("DISTRICT", district);
                i.putExtra("VILLAGE", village);
                i.putExtra("BENEFICIARY", beneficiary);
                i.putExtra("STATUS", status);
                i.putExtra("DATE_OF_SURVEY", dateOfSurvey);
                i.putExtra("IOT_DEVICE_ID", iotDeviceId);
                startActivity(i);
            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    // Upload the image to Firebase Storage
                    uploadImageToFirebaseStorage(state,district,village,beneficiary,status,dateOfSurvey,iotDeviceId);
                } else {
                    Toast.makeText(getApplicationContext(), "Please capture an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY);
    }

    // Image capture logic
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    // Convert Bitmap to Uri
                    imageUri = getImageUri(getApplicationContext(), imageBitmap);
                    // Display the image
                    springimage.setImageBitmap(imageBitmap);
                }
            }
        }
        else if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    // Display the selected image
                    springimage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to convert Bitmap to Uri
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebaseStorage(String state, String district, String village, String beneficiary, String status, String dateOfSurvey, String iotDeviceId) {
        // Show the loading dialog
        upload loadingDialog = new upload(DataCollector_new_activity_2.this);
        loadingDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String imageName = "images/" + UUID.randomUUID() + ".jpg"; // Generate a unique image name

        StorageReference imageRef = storageRef.child(imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Retrieve data from the previous activity

                        String latitudetxt = latitude.getText().toString();
                        String longtxt = longitude.getText().toString();
                        String additionaldetailstxt = additionalDetails.getText().toString();
                        // Save all data including imageUrl to Firestore
                        saveDataToFirestore(state, district, village, beneficiary, status, dateOfSurvey, iotDeviceId, latitudetxt, longtxt, additionaldetailstxt, imageUrl);
                        // Dismiss the loading dialog after a delay (4 seconds)
                        new android.os.Handler().postDelayed(
                                loadingDialog::dismiss,
                                3000
                        );
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful image upload
                    Toast.makeText(getApplicationContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Dismiss the loading dialog
                    loadingDialog.dismiss();
                });
    }


    private void saveDataToFirestore(String state, String district, String village, String beneficiary,
                                     String status, String dateOfSurvey, String iotDeviceId,
                                     String latitude, String longitude, String additionalDetails,
                                     String imageUrl) {

        // Access the Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new activity ID or use an existing method to generate one
        String activityId = generateActivityId(village); // Replace this with your method to generate activity ID

        // Create a Map to hold the data including the image URL
        Map<String, Object> activityData = new HashMap<>();
        activityData.put("state", state);
        activityData.put("district", district);
        activityData.put("village", village);
        activityData.put("beneficiary", beneficiary);
        activityData.put("status", status);
        activityData.put("dateOfSurvey", dateOfSurvey);
        activityData.put("iotDeviceId", iotDeviceId);
        activityData.put("latitude", latitude);
        activityData.put("longitude", longitude);
        activityData.put("additionalDetails", additionalDetails);
        activityData.put("imageUrl", imageUrl); // Add the image URL
        activityData.put("approvalStatus", "Not Approved");
        // Add the data to Firestore under a document with the generated activity ID
        db.collection("activities")
                .document(activityId)
                .set(activityData)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully added to Firestore
                    // You can add any additional actions here upon successful data
                    // Data successfully added to Firestore
                    // You can add any additional actions here upon successful data addition
                    Toast.makeText(getApplicationContext(), "Data saved to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that may occur while adding data to Firestore
                    Toast.makeText(getApplicationContext(), "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Map<String, Integer> villageActivityCount = new HashMap<>();

    private String padNumber(int num, int padding) {
        return String.format("%0" + padding + "d", num);
    }

    private String generateActivityId(String villagename) {
        int count = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            count = villageActivityCount.getOrDefault(villagename, 0) + 1;
        }
        villageActivityCount.put(villagename, count);
        String paddedCount = padNumber(count, 4);
        return villagename.substring(0, Math.min(villagename.length(), 3)).toLowerCase() + paddedCount;
    }
}
