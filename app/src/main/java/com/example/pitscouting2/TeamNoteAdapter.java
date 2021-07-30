package com.example.pitscouting2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamNoteAdapter extends FirestoreRecyclerAdapter<TeamNote, TeamNoteAdapter.NoteHolder>{
    private OnItemClickListener listener;

    public TeamNoteAdapter(@NonNull FirestoreRecyclerOptions<TeamNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull TeamNote model) {
        holder.textViewname.setText(model.getName());
        holder.textViewnumber.setText(model.getNumber());
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_note_item, parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        TextView textViewname;
        TextView textViewnumber;

        public NoteHolder(View itemView){
            super(itemView);
            textViewname = itemView.findViewById(R.id.text_view_name);
            textViewnumber = itemView.findViewById(R.id.text_view_number);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }


    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
