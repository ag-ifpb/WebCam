package io.github.victorhsr.pdm.webcam.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.server.WebCamService;

/**
 * Created by victor on 15/05/17.
 */

public class RecordingActivity extends AppCompatActivity {

    private TextView countdown;
    private TextView alertTxt;
    private String camCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        camCode = getIntent().getExtras().getString("camCode");

        alertTxt = (TextView) findViewById(R.id.alert_txt);
        countdown = (TextView) findViewById(R.id.countdown);
        countdown.setText("1:00");

        requestRecord(camCode);
    }

    private void setCountdown(final int newNumber) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countdown.setText(String.valueOf(newNumber));
            }
        });
    }

    private void startCountdown() throws InterruptedException {

        int count = 60;

        while (count > 0) {
            --count;
            setCountdown(count);

            Thread.sleep(1000);
        }

        AlertRunnable alertRunnable = new AlertRunnable();

        synchronized (alertRunnable) {
            runOnUiThread(alertRunnable);
            alertRunnable.wait();
        }

        Thread.sleep(5000);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    private void requestRecord(final String camCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(WebCamService.SERVER_IP, WebCamService.SERVER_PORT);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeInt(WebCamService.REQUEST_RECORD_COMMAND);
                    dos.writeUTF(camCode);
                    dos.flush();

                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    //reading camcode but it's not useful for us
                    dis.readUTF();
                    int response = dis.readInt();

                    if (response == WebCamService.SUCCESS_CODE) {
                        startCountdown();
                    } else if (response == WebCamService.NOT_FOUND_CODE) {
                        showToast("Câmera não encontrada");
                    }

                } catch (Exception e) {
                    Log.e("[RecordingActivity]", e.getMessage() == null ? "" : e.getMessage());
                }

            }
        }).start();
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RecordingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class AlertRunnable implements Runnable {

        @Override
        public void run() {

            alertTxt.setText("Concluído, consulte suas gravações");

            synchronized (this) {
                this.notify();
            }
        }
    }

}
