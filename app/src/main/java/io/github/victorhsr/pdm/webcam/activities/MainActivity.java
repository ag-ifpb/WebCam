package io.github.victorhsr.pdm.webcam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.victorhsr.pdm.webcam.R;

/**
 * Created by victor on 18/03/17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main);

        final Button camBtn = (Button) findViewById(R.id.cam_btn);
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebCamActivity.class));
            }
        });

        final Button controlBtn = (Button) findViewById(R.id.controller_btn);
        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ControllerActivity.class));
            }
        });
    }

}
