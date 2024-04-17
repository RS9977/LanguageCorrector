package com.example.languagechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.SentenceViewHolder> {

    private List<String> sentences;
    private OnItemClickListener listener;

    public SentenceAdapter(List<String> sentences, OnItemClickListener listener) {
        this.sentences = sentences;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
        return new SentenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceViewHolder holder, int position) {
        holder.bind(sentences.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return sentences.size();
    }

    public static class SentenceViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSentence;

        public SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSentence = itemView.findViewById(R.id.text_view_sentence);
        }

        public void bind(final String sentence, final OnItemClickListener listener) {
            textViewSentence.setText(sentence);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(sentence);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(sentence);
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String sentence);
        void onItemLongClick(String sentence);
    }
}
