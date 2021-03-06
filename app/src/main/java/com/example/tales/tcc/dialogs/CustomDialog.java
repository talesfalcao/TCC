package com.example.tales.tcc.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tales.tcc.R;

/**
 * Created by tales on 13/06/2017.
 */

public class CustomDialog extends DialogFragment {
    Button mButton;
    EditText mEditText;
    String title = "";
    String text = "";
    private EditNameDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        title = getArguments().getString("title");
        ((TextView)dialog.findViewById(R.id.title)).setText(title);
        mButton = (Button) dialog.findViewById(R.id.submit);
        mEditText = (EditText) dialog.findViewById(R.id.et);
        mEditText.setText(text);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener = (EditNameDialogListener) getActivity();
                listener.onFinishEditDialog(mEditText.getText().toString());
                dismiss();
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }
}