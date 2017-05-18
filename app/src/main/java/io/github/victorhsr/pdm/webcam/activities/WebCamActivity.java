package io.github.victorhsr.pdm.webcam.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.server.WebCamService;
import io.github.victorhsr.pdm.webcam.views.CamView;

public class WebCamActivity extends AppCompatActivity {

    private CamView camView;
    private FloatingActionButton flashBtn;
    private TextView camCodeView;
    private CamSocketThread camSocketThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();

        setContentView(R.layout.web_cam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.web_cam_container);
        camCodeView = (TextView) findViewById(R.id.cam_id);
        camView = new CamView(this);

        frameLayout.addView(camView, 0);

        flashBtn = (FloatingActionButton) findViewById(R.id.flash_btn);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    camView.useFlash();
                }
            });
        } else {
            flashBtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
            flashBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(WebCamActivity.this, "Flash indisponível", Toast.LENGTH_SHORT).show();
                }
            });
        }

        startService();
    }

    private void startService() {
        camSocketThread = new CamSocketThread();
        new Thread(camSocketThread).start();
    }

    public void setCamCode(final String camCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                camCodeView.setText(camCode);
            }
        });
    }

    private void requestPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        camSocketThread.disconnect();
        camView.releaseCamera();
    }

    private class CamSocketThread implements Runnable {

        private Socket socket;
        private OutputStream out;
        private InputStream in;
        private DataOutputStream dos;
        private DataInputStream dis;

        @Override
        public void run() {
            try {
                connect();
                waitCamcode();

                while (!socket.isClosed()) {
                    int command = dis.readInt();

                    switch (command) {

                        case WebCamService.RECORD_CAM_COMMAND:
                            recordOneMinute();
                            break;
                        case WebCamService.LIVE_STREAMING_COMMAND:
                            startLiveStreaming();
                            break;
                    }

                    Thread.sleep(500);
                }

            } catch (Exception e) {
                Log.e("[WebCamActivity]", e.getMessage() == null ? "" : e.getMessage());
            }
        }

        private void startLiveStreaming() throws IOException, InterruptedException {

            String targetCamCode = dis.readUTF();
            if (targetCamCode == null || targetCamCode.trim().isEmpty()) {
                return;
            }

            dos.writeInt(WebCamService.LIVE_STREAMING_COMMAND);
            dos.writeUTF(targetCamCode);
            dos.flush();

            while (true) {

                ByteArrayOutputStream framebuffer = camView.getFramebuffer();

                if (framebuffer != null) {

                    byte[] bytes = framebuffer.toByteArray();
                    int frameSize = bytes.length;

                    if (frameSize > 0) {
                        dos.writeInt(frameSize);
                        dos.write(bytes);
                        dos.flush();
                    }

                    Thread.sleep(40);
                }

                if (dis.available() > 0) {

                    int status = dis.readInt();

                    if (status == WebCamService.END_OF_STREAM_CODE) {
                        break;
                    }

                }

//                dis.readInt();
            }

        }

        private void recordOneMinute() throws IOException, InterruptedException {
            int framesSent = 0;

            dos.writeInt(WebCamService.RECORD_CAM_COMMAND);
            dos.flush();

            while (socket.isConnected() && framesSent < 1500) {

                ByteArrayOutputStream framebuffer = camView.getFramebuffer();

                if (framebuffer != null) {

                    byte[] bytes = framebuffer.toByteArray();
                    int frameSize = bytes.length;

                    if (frameSize > 0) {
                        dos.writeInt(frameSize);
                        dos.write(bytes);
                        dos.flush();
                    }

                    Thread.sleep(40);
                    ++framesSent;
                }

                in.read();
            }

            disconnect();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        private void waitCamcode() throws IOException {
            String camCode = dis.readUTF();
            setCamCode(camCode);
        }

        public void disconnect() {
            try {
                dos.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                Log.e("[WebCamActivity]", e.getMessage() == null ? "" : e.getMessage());
            }
        }

        public void connect() {
            if (socket != null && socket.isConnected())
                return;

            try {
                socket = new Socket(WebCamService.SERVER_IP, WebCamService.SERVER_PORT);
                socket.setKeepAlive(true);

                out = socket.getOutputStream();
                dos = new DataOutputStream(out);

                in = socket.getInputStream();
                dis = new DataInputStream(in);
            } catch (IOException e) {
                Log.e("[WebCamActivity]", e.getMessage() == null ? "" : e.getMessage());
            }
        }

    }

}
