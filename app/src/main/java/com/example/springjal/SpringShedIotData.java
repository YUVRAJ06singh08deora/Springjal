package com.example.springjal;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpringShedIotData extends AppCompatActivity {

    EditText temperatureEditText, turbidityEditText, phEditText, conductivityEditText,
            doEditText, waterFlowEditText, gasSensorEditText, hardnessEditText,
            solidsEditText, chloraminesEditText, sulfateEditText, organicCarbonEditText,
            trihalomethanesEditText, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_shed_iot_data);

        // Initialize EditText fields
        temperatureEditText = findViewById(R.id.temperatureedittextinput);
        turbidityEditText = findViewById(R.id.turbidityedittextdisplay);
        phEditText = findViewById(R.id.phlevelrledittextdisplay);
        conductivityEditText = findViewById(R.id.conductivityEdittextDisplay);
        doEditText = findViewById(R.id.disssolvedoxygenedittextdisplay);
        waterFlowEditText = findViewById(R.id.gaugewaterflowdata);
        gasSensorEditText = findViewById(R.id.gasvalueedittxt);
        hardnessEditText = findViewById(R.id.hardnessEdittextDatadisplay);
        solidsEditText = findViewById(R.id.solidsedittextdisplay);
        chloraminesEditText = findViewById(R.id.chloromineedittxt);
        sulfateEditText = findViewById(R.id.sulfateedt);
        organicCarbonEditText = findViewById(R.id.organiccarbnedt);
        trihalomethanesEditText = findViewById(R.id.trihalomethaneet);
        status=findViewById(R.id.statusedittextDisplay);

        // Firebase reference
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("springshediotsecuredata");

        // Change "deviceId" with your actual device ID
        String deviceId = "spr8561714";

        firebaseRef.child(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String base64Temperature = dataSnapshot.child("Temperature").getValue(String.class);
                    String base64Turbidity = dataSnapshot.child("Turbidity").getValue(String.class);
                    String base64PH = dataSnapshot.child("pHLevel").getValue(String.class);
                    String base64Conductivity = dataSnapshot.child("ConductivityTDS").getValue(String.class);
                    String base64DO = dataSnapshot.child("DissolvedOxygen").getValue(String.class);
                    String base64WaterFlow = dataSnapshot.child("WaterFlow").getValue(String.class);
                    String base64GasSensor = dataSnapshot.child("GasSensor").getValue(String.class);
                    String base64Hardness = dataSnapshot.child("Hardness").getValue(String.class);
                    String base64Solids = dataSnapshot.child("Solids").getValue(String.class);
                    String base64Chloramines = dataSnapshot.child("Chloramines").getValue(String.class);
                    String base64Sulfate = dataSnapshot.child("Sulfate").getValue(String.class);
                    String base64OrganicCarbon = dataSnapshot.child("OrganicCarbon").getValue(String.class);
                    String base64Trihalomethanes = dataSnapshot.child("Trihalomethanes").getValue(String.class);

// Decode Base64 encoded strings
                    String decodedTemperature = base64Decode(base64Temperature);
                    String decodedTurbidity = base64Decode(base64Turbidity);
                    String decodedPH = base64Decode(base64PH);
                    String decodedConductivity = base64Decode(base64Conductivity);
                    String decodedDO = base64Decode(base64DO);
                    String decodedWaterFlow = base64Decode(base64WaterFlow);
                    String decodedGasSensor = base64Decode(base64GasSensor);
                    String decodedHardness = base64Decode(base64Hardness);
                    String decodedSolids = base64Decode(base64Solids);
                    String decodedChloramines = base64Decode(base64Chloramines);
                    String decodedSulfate = base64Decode(base64Sulfate);
                    String decodedOrganicCarbon = base64Decode(base64OrganicCarbon);
                    String decodedTrihalomethanes = base64Decode(base64Trihalomethanes);

                    // Display decoded sensor values in EditText fields
                    temperatureEditText.setText(decodedTemperature);
                    turbidityEditText.setText(decodedTurbidity);
                    phEditText.setText(decodedPH);
                    conductivityEditText.setText(decodedConductivity);
                    doEditText.setText(decodedDO);
                    waterFlowEditText.setText(decodedWaterFlow);
                    gasSensorEditText.setText(decodedGasSensor);
                    hardnessEditText.setText(decodedHardness);
                    solidsEditText.setText(decodedSolids);
                    chloraminesEditText.setText(decodedChloramines);
                    sulfateEditText.setText(decodedSulfate);
                    organicCarbonEditText.setText(decodedOrganicCarbon);
                    trihalomethanesEditText.setText(decodedTrihalomethanes);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("ph", decodedPH);
                        jsonObject.put("Hardness", decodedHardness);
                        jsonObject.put("Solids", decodedSolids);
                        jsonObject.put("Chloramines", decodedChloramines);
                        jsonObject.put("Sulfate", decodedSulfate);
                        jsonObject.put("Conductivity", decodedConductivity);
                        jsonObject.put("Organic_carbon", decodedOrganicCarbon);
                        jsonObject.put("Trihalomethanes", decodedTrihalomethanes);
                        jsonObject.put("Turbidity", decodedTurbidity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

                    Request request = new Request.Builder()
                            .url("https://flask-production-5149.up.railway.app/predict_water_quality")
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override


                        public

                        void

                        onFailure(Call call, IOException e)

                        {
                            e.printStackTrace();
                            Toast.makeText(SpringShedIotData.this, "Prediction failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override


                        public void onResponse(Call call, Response response)

                                throws IOException {
                            final String responseData = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override


                                public void run()

                                {
                                    try {
                                        JSONObject json = new JSONObject(responseData);
                                        String waterQualityPrediction = json.getString("prediction_water_quality");

                                        Toast.makeText(SpringShedIotData.this, waterQualityPrediction, Toast.LENGTH_SHORT).show();
                                        System.out.println(waterQualityPrediction);
                                        String zero="0";
                                        if(waterQualityPrediction.equals(zero))
                                        {
                                            status.setText("BAD");
                                            status.setTextColor(Color.RED);
                                            updateStatusonfirebase("BAD");
                                        }
                                        else{
                                            status.setText("GOOD");
                                            status.setTextColor(Color.GREEN);
                                            updateStatusonfirebase("GOOD");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        // Handle the exception if the JSON parsing fails
                                    }


                                }
                            });
                        }
                    });
                } else {
                    // Data not found
                    status.setText("Data not found");
                }
            } else {
                // Task failed with an exception
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                    status.setText("Task failed with exception");
                }
            }
        });
    }

    private void updateStatusonfirebase(String statusValue) {
        // Reference to the Firebase database
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("springshediotsecuredata")
                .child("spr8561714") // Replace "your_device_id" with your actual device ID
                .child("status"); // Assuming the status is stored under the "status" node

        // Set the status value in Firebase
        firebaseRef.setValue(statusValue)
                .addOnSuccessListener(aVoid -> {
                    // Status update successful
                    Toast.makeText(this, "Status updated in Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Status update failed
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void makePrediction() {

    }

    // Function to decode Base64 string
    private String base64Decode(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return new String(decodedBytes);
    }
}
