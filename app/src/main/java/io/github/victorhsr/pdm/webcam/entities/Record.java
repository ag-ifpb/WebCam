package io.github.victorhsr.pdm.webcam.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by victor on 21/03/17.
 */

public class Record implements Serializable {

    private Bitmap preview;
    private String camCode;
    private Date date;
    private String uri;
    private long code;

    public Record() {
    }

    public Record(Bitmap preview, String camCode, Date date, String uri) {
        this.preview = preview;
        this.camCode = camCode;
        this.date = date;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }

    public String getCamCode() {
        return camCode;
    }

    public void setCamCode(String camCode) {
        this.camCode = camCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date) throws ParseException {

        this.date = new Date(Long.parseLong(date));
    }

    public String toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("camCode", camCode);
            jsonObject.put("code", code);
            jsonObject.put("camCode", camCode);

            return jsonObject.toString();
        } catch (Exception e) {
            Log.e("[Record]", e.getMessage() == null ? "":e.getMessage());
        }
        return "";
    }

    public List<Record> listFromJson(String json) throws Exception {

        JSONArray jsonArray = new JSONArray(json);

        List<Record> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(fromJson(jsonArray.getString(i)));
        }

        return result;

    }

    public Record fromJson(String json) throws Exception {

        JSONObject jsonObject = new JSONObject(json);

        Record record = new Record();

        record.setCamCode(jsonObject.getString("camCode"));
        record.setDate(jsonObject.getString("date"));
        record.setUri(jsonObject.getString("uri"));
        record.setCode(jsonObject.getLong("code"));

        String base64Preview = jsonObject.getString("preview");

        byte[] previewBytes = Base64.decode(base64Preview, Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(previewBytes, 0, previewBytes.length);

        record.setPreview(bitmap);

        return record;
    }

    @Override
    public String toString() {
        return "RecordActivity{" +
                "preview=" + preview +
                ", camCode='" + camCode + '\'' +
                ", date=" + date +
                ", uri=" + uri +
                ", code=" + code +
                '}';
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
