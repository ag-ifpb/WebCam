package io.github.victorhsr.pdm.webcam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.github.victorhsr.pdm.webcam.R;

public class ControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_configs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText targetCamCode = (EditText) findViewById(R.id.cam_id_txt);
        targetCamCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        final Button showRecordsBtn = (Button) findViewById(R.id.show_records);
        showRecordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControllerActivity.this, RecordsActivity.class));
            }
        });

        final Button requestRecord = (Button) findViewById(R.id.record_btn);
        requestRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControllerActivity.this, RecordingActivity.class);
                intent.putExtra("camCode", targetCamCode.getText().toString());
                startActivity(intent);
            }
        });

        final Button requestLiveStreaming = (Button) findViewById(R.id.watch_live_btn);
        requestLiveStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControllerActivity.this, LiveStreamRenderActivity.class);
                intent.putExtra("camCode", targetCamCode.getText().toString());
                startActivity(intent);
            }
        });
    }


}
