package com.example.springjal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarChart_WaterQuality extends AppCompatActivity {
    private List<String> states = Arrays.asList("SGNR", "MDU", "DEL", "BOM", "CHEN");
    private List<Float> rainfallData = Arrays.asList(100f, 150f, 90f, 120f, 80f); // Example rainfall data
    private List<Float> waterDischargeData = Arrays.asList(45f, 85f, 35f, 25f, 55f); // Example water discharge data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_water_quality);
        BarChart barChart = findViewById(R.id.chart1);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList<BarEntry> rainfallEntries = new ArrayList<>();
        ArrayList<BarEntry> waterDischargeEntries = new ArrayList<>();

        for (int i = 0; i < states.size(); i++) {
            rainfallEntries.add(new BarEntry(i, rainfallData.get(i)));
            waterDischargeEntries.add(new BarEntry(i, waterDischargeData.get(i)));
        }

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(200f); // Change the maximum based on your data range
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        BarDataSet rainfallDataSet = new BarDataSet(rainfallEntries, "Rainfall");
        rainfallDataSet.setColor(Color.rgb(104, 241, 175)); // Set a specific color for rainfall bars

        BarDataSet waterDischargeDataSet = new BarDataSet(waterDischargeEntries, "Water Discharge");
        waterDischargeDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(rainfallDataSet);
        dataSets.add(waterDischargeDataSet);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(states));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.groupBars(-0.5f, 0.08f, 0.02f); // Group bars together

        // Show legend
        barChart.getLegend().setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
        barChart.getLegend().setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL);
        barChart.getLegend().setDrawInside(false);
    }
}
