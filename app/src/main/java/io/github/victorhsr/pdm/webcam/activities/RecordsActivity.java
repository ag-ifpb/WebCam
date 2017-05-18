package io.github.victorhsr.pdm.webcam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.adapters.RecordsAdapter;
import io.github.victorhsr.pdm.webcam.entities.Record;
import io.github.victorhsr.pdm.webcam.server.WebCamService;

public class RecordsActivity extends AppCompatActivity {

    private ListView cardsContainer;
    private ArrayAdapter<Record> adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new RecordsAdapter(RecordsActivity.this, R.layout.record_card);

        cardsContainer = (ListView) findViewById(R.id.cards_container);
        cardsContainer.setAdapter(adapter);

        cardsContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = adapter.getItem(position);
                record.setPreview(null);
                Intent recordIntent = new Intent(RecordsActivity.this, RecordActivity.class)
                        .putExtra("record", record).putExtra("camCode", record.getCamCode());

                startActivity(recordIntent);

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        searchForRecords();
    }

    private void searchForRecords() {
        adapter.clear();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebCamService.RESOURCE_URI, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    List<Record> records = new Record().listFromJson(new String(response));

                    for (Record record : records) {
                        adapter.add(record);
                    }

                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e("[RecordsActivity]", e.getMessage() == null ? "" : e.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("[RecordsActivity]", e.getMessage() == null ? "" : e.getMessage());
            }

        });
    }

}
