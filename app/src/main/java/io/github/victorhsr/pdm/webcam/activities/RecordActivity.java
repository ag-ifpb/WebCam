package io.github.victorhsr.pdm.webcam.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.fragments.ConfirmCallBack;
import io.github.victorhsr.pdm.webcam.fragments.ConfirmFragment;
import io.github.victorhsr.pdm.webcam.server.WebCamService;

public class RecordActivity extends AppCompatActivity {

    private RelativeLayout progressBar;
    private FloatingActionButton deleteButton;
    private TextView camId;
    private String resource = WebCamService.RESOURCE_URI;
    private io.github.victorhsr.pdm.webcam.entities.Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        record = (io.github.victorhsr.pdm.webcam.entities.Record) getIntent().getExtras().getSerializable("record");

        progressBar = (RelativeLayout) findViewById(R.id.progressBar);
        deleteButton = (FloatingActionButton) findViewById(R.id.delete_btn);
        camId = (TextView) findViewById(R.id.cam_id);

        String cam_id = getIntent().getExtras().getString("camCode");

        camId.setText(cam_id);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("dialog_title", "Atenção");
                bundle.putString("dialog_message", "Deseja realmente excluir a gravação?");
                bundle.putInt("icon", R.drawable.ic_delete_black_24dp);
                bundle.putSerializable("callback", new ConfirmCallBack() {
                    @Override
                    public void onConfirm() {
                        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

                        RequestParams requestParams = new RequestParams();
                        requestParams.add("camcode", record.getCamCode());
                        requestParams.add("code", String.valueOf(record.getCode()));

                        resource += "/" + record.getCamCode() + "/" + record.getCode();
                        resource = resource.replace(" ", "%20");

                        asyncHttpClient.delete(resource, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(RecordActivity.this, "Gravação excluída", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(RecordActivity.this, "Houve uma falha na exclusão, tente mais tarde", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCalcell() {
                    }
                });

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                ConfirmFragment confirmFragment = new ConfirmFragment();
                confirmFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.record_container, confirmFragment, "confirm_fragment");
                fragmentTransaction.commit();
            }
        });

        VideoView videoView = (VideoView) findViewById(R.id.record_surface);
        videoView.setMediaController(new MediaController(RecordActivity.this));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
            }
        });

        videoView.setVideoURI(Uri.parse(record.getUri()));
        videoView.start();

    }

}
