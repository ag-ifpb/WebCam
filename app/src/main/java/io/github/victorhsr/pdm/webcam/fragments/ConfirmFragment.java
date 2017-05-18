package io.github.victorhsr.pdm.webcam.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.victorhsr.pdm.webcam.R;

public class ConfirmFragment extends DialogFragment {

    private ConfirmCallBack confirmCallBack;
    private TextView dialogTitle;
    private TextView dialogMessage;
    private Button confirmBtn;
    private Button cancelBtn;
    private ImageView icon;


    public ConfirmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.confirm_fragment, container, false);

        dialogTitle = (TextView) inflatedView.findViewById(R.id.title);
        dialogMessage = (TextView) inflatedView.findViewById(R.id.message);
        confirmBtn = (Button) inflatedView.findViewById(R.id.confirm_btn);
        cancelBtn = (Button) inflatedView.findViewById(R.id.cancel_btn);
        icon = (ImageView) inflatedView.findViewById(R.id.icon);

        String dTitle = getArguments().getString("dialog_title");
        String dMessage = getArguments().getString("dialog_message");
        int dIcon = getArguments().getInt("icon");

        dialogTitle.setText(dTitle);
        dialogMessage.setText(dMessage);

        icon.setImageResource(dIcon);

        confirmCallBack = (ConfirmCallBack) getArguments().getSerializable("callback");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCallBack.onCalcell();
                dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCallBack.onConfirm();
                dismiss();
            }
        });

        return inflatedView;
    }

}
