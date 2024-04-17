package com.example.languagechecker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddSentenceDialog extends AppCompatDialogFragment {

    private EditText editTextSentence;
    private AddSentenceDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_sentence, null);

        builder.setView(view)
                .setTitle("Add Sentence")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sentence = editTextSentence.getText().toString();
                        listener.onAddButtonClicked(sentence);
                    }
                });

        editTextSentence = view.findViewById(R.id.edit_text_sentence);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddSentenceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement AddSentenceDialogListener");
        }
    }

    public interface AddSentenceDialogListener {
        void onAddButtonClicked(String sentence);
    }
}
