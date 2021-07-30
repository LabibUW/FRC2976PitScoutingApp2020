package com.example.pitscouting2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.pitscouting2.ID";
    public static final String EXTRA_PATH = "com.example.pitscouting2.PATH";
    public static final String EXTRA_ID2 = "com.example.pitscouting2.ID";
    public static final String EXTRA_PATH2 = "com.example.pitscouting2.PATH";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookref = db.collection("Events");
    //private DocumentReference documentReference = notebookref.document("Bellingham")
    private EventNoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Changing Toolbar text
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Events");

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        Query query = notebookref;

        FirestoreRecyclerOptions<EventNote> options = new FirestoreRecyclerOptions.Builder<EventNote>().setQuery(query, EventNote.class).build();

        adapter = new EventNoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new EventNoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                EventNote note = documentSnapshot.toObject(EventNote.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                //Toast.makeText(EventActivity.this,
                      //"Position: " + position + " ID: " + path, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventActivity.this, TeamActivity.class);
                Intent intent2 = new Intent(EventActivity.this, NewTeamNoteActivity.class);
                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_PATH, path);
                intent2.putExtra(EXTRA_ID2, id);
                intent2.putExtra(EXTRA_PATH2, path);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}