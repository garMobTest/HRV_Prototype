package org.marco45.polarheartmonitor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //read data from the selected file

        String fileName = getIntent().getStringExtra("fileName");

        // read data from the file

        //heart rate, hrv score, time Stamp

        String data = DataAccess.readFromFile(fileName, this.getApplicationContext());

        Log.e("read data: ", data);

        List<String> items = new ArrayList<String>(Arrays.asList(data.split(",")));


        TextView dateTV = (TextView) findViewById(R.id.dateView);
        dateTV.setText("Time Stamp: " + items.get(2));

        TextView heartRate = (TextView) findViewById(R.id.heartView);
        heartRate.setText("Heart Rate: " + items.get(0));

        TextView hrv = (TextView) findViewById(R.id.hrvView);
        hrv.setText("HRV Score: " + items.get(1));


        Button btnBack = (Button) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }
}
