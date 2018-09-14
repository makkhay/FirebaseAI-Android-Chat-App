package com.makkhay.chat.ui;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.makkhay.chat.R;
import com.makkhay.chat.model.GradientColor;
import com.makkhay.chat.util.DayAxisValueFormatter;
import com.makkhay.chat.util.MyAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class BarChartActivity extends AppCompatActivity
       {

    protected BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bar_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        mChart = (BarChart) findViewById(R.id.chart1);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);


        setData(12, 50);




    }


           private void setData(int count, float range) {

               float start = 1f;

               ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

               for (int i = (int) start; i < start + count + 1; i++) {
                   float mult = (range + 1);
                   float val = (float) (Math.random() * mult);

                   if (Math.random() * 100 < 25) {
                       yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_menu_send)));
                   } else {
                       yVals1.add(new BarEntry(i, val));
                   }
               }

               BarDataSet set1;

               if (mChart.getData() != null &&
                       mChart.getData().getDataSetCount() > 0) {
                   set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                   set1.setValues(yVals1);
                   mChart.getData().notifyDataChanged();
                   mChart.notifyDataSetChanged();
               } else {
                   set1 = new BarDataSet(yVals1, "The year 2018");

                   set1.setDrawIcons(false);



                   int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
                   int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
                   int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
                   int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
                   int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
                   int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
                   int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
                   int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
                   int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
                   int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

                   List<GradientColor> gradientColors = new ArrayList<>();
                   gradientColors.add(new GradientColor(startColor1, endColor1));
                   gradientColors.add(new GradientColor(startColor2, endColor2));
                   gradientColors.add(new GradientColor(startColor3, endColor3));
                   gradientColors.add(new GradientColor(startColor4, endColor4));
                   gradientColors.add(new GradientColor(startColor5, endColor5));

                   set1.setColors(ColorTemplate.MATERIAL_COLORS);


                   ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                   dataSets.add(set1);

                   BarData data = new BarData(dataSets);
                   data.setValueTextSize(10f);
                   data.setBarWidth(0.9f);

                   mChart.setData(data);
               }
           }

           @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // for back button
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}