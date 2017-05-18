package io.github.victorhsr.pdm.webcam.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.server.WebCamService;

public class LiveStreamRenderActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView camCodeView;
    private String camCode;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_stream);

        camCode = getIntent().getExtras().getString("camCode");

        imageView = (ImageView) findViewById(R.id.imageView);
        camCodeView = (TextView) findViewById(R.id.cam_id_txt);
        camCodeView.setText(camCode);

        requestAndShowStream();
    }

    private void requestAndShowStream() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(WebCamService.SERVER_IP, WebCamService.SERVER_PORT);
                    dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeInt(WebCamService.REQUEST_LIVE_STREAMING_COMMAND);
                    dos.writeUTF(camCode);
                    dos.flush();

                    dis = new DataInputStream(socket.getInputStream());
                    //reading camcode but it's not useful for us
                    dis.readUTF();
                    int response = dis.readInt();

                    if (response == WebCamService.SUCCESS_CODE) {
                        while (true) {

                            int frameLength;

                            byte[] frame;

                            frameLength = dis.readInt();

                            frame = new byte[frameLength];

                            int len = 0;

                            while (len < frameLength) {
                                len += dis.read(frame, len, frameLength - len);
                            }

                            final Bitmap bitmap = BitmapFactory.decodeByteArray(frame, 0, frameLength);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });

                            Thread.sleep(20);

                        }
                    } else if (response == WebCamService.NOT_FOUND_CODE) {
                        showToast("Câmera não encontrada");
                    }

                } catch (Exception e) {
                    Log.e("[LiveStreamActivity]", e.getMessage() == null ? "" : e.getMessage());
                }
            }
        }).start();

    }

    @Override
    protected void onStop() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dos.writeInt(WebCamService.END_OF_STREAM_CODE);
                    dos.flush();

                    dos.close();
                    dis.close();
                    socket.close();
                    dos = null;
                    dis = null;
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        super.onStop();
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveStreamRenderActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
