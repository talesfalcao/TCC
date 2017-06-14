package com.example.tales.tcc;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tales on 13/06/2017.
 */

public class CustomDialog extends DialogFragment {
    Button mButton;
    EditText mEditText;
    String title = "";
    String text = "";

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
                Toast.makeText(getActivity(), title + " dialog return", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return dialog;
    }
}