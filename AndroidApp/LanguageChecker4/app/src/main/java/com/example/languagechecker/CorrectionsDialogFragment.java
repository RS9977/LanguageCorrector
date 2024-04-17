package com.example.languagechecker;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class CorrectionsDialogFragment extends DialogFragment {

    private String sentence;
    private String corrections; // List of language corrections

    public CorrectionsDialogFragment(String sentence) {
        this.sentence = "test";
        this.corrections = sentence;
    }

    // onCreateDialog() method...

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use AlertDialog.Builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title and message of the dialog
        builder.setTitle("Corrections for: " + sentence)
                .setMessage(corrections);


        // Set positive button (optional)
        builder.setPositiveButton("OK", null);

        // Return the AlertDialog
        return builder.create();
    }
}