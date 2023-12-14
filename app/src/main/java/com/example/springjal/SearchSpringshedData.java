package com.example.springjal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class SearchSpringshedData extends AppCompatActivity {
RelativeLayout fetchbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_springshed_data);
        fetchbtn=findViewById(R.id.fetchbtn);
        fetchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SpringPlotsMapsActivity.class);
                startActivity(intent);
            }
        });
    }
}