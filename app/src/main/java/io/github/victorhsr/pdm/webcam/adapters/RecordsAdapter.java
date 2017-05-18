package io.github.victorhsr.pdm.webcam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.victorhsr.pdm.webcam.R;
import io.github.victorhsr.pdm.webcam.entities.Record;

/**
 * Created by victor on 21/03/17.
 */

public class RecordsAdapter extends ArrayAdapter<Record> {

    public RecordsAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Record record = getItem(position);

        view = LayoutInflater.from(getContext()).inflate(R.layout.record_card, null);

        TextView camId = (TextView) view.findViewById(R.id.cam_code);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView duration = (   TextView) view.findViewById(R.id.horary);
        ImageView preview = (ImageView) view.findViewById(R.id.preview);

        camId.setText(record.getCamCode());
        duration.setText(getHourAndMinutesFromDate(record.getDate()));
        preview.setImageBitmap(record.getPreview());
        date.setText(getFormatedDate(record.getDate()));

        return view;

    }

    private String getFormatedDate(Date date) {

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        return format.format(date);
    }

    private String getHourAndMinutesFromDate(Date date){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

}
