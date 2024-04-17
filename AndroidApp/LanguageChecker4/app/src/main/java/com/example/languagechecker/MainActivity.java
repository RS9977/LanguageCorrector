package com.example.languagechecker;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        AddSentenceDialog.AddSentenceDialogListener,
        DeleteSentenceDialog.DeleteSentenceDialogListener,
        SentenceAdapter.OnItemClickListener {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private SentenceAdapter adapter;

    private LanguageChecker languageChecker;

    private String sentenceToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the LanguageChecker class
        languageChecker = new LanguageChecker();
        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example: Update hash table with sentences
        //List<String> sentencesToUpdate = getSentencesToUpdate();
        //languageChecker.updateHashTable(sentencesToUpdate);

        // Example: Check sentences for correctness
        //List<String> sentencesToCheck = getSentencesToCheck();
        //languageChecker.checkSentences(sentencesToCheck);

        updateRecyclerView();

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSentenceDialog();
            }
        });
    }

    private void updateRecyclerView() {
        List<String> sentences = dbHelper.getAllSentences();
        adapter = new SentenceAdapter(sentences, this);
        recyclerView.setAdapter(adapter);
    }

    private void openAddSentenceDialog() {
        AddSentenceDialog dialog = new AddSentenceDialog();
        dialog.show(getSupportFragmentManager(), "AddSentenceDialog");
    }

    @Override
    public void onAddButtonClicked(String sentence) {
        long id = dbHelper.addSentence(sentence);
        if (id != -1) {
            updateRecyclerView();
            Toast.makeText(this, "Sentence added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add sentence", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteButtonClicked() {
        // Handle delete confirmation here
        dbHelper.deleteSentence(sentenceToDelete);
        updateRecyclerView();
        Toast.makeText(this, "Sentence deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(String sentence) {
        // Show corrections dialog
        showCorrectionsDialog(sentence);
    }

    @Override
    public void onItemLongClick(final String sentence) {
        // Show delete confirmation dialog
        sentenceToDelete = sentence;
        openDeleteSentenceDialog(sentence);
    }

    private void showCorrectionsDialog(final String sentence) {
        // Implement dialog to show corrections for the selected sentence
        CorrectionsDialogFragment dialog = new CorrectionsDialogFragment(languageChecker.analyzeSentence(sentence));
        dialog.show(getSupportFragmentManager(), "CorrectionsDialogFragment");
    }

    private void openDeleteSentenceDialog(final String sentence) {
        sentenceToDelete = sentence; // Store the sentence to delete
        DeleteSentenceDialog dialog = new DeleteSentenceDialog();
        dialog.show(getSupportFragmentManager(), "DeleteSentenceDialog");
    }
}