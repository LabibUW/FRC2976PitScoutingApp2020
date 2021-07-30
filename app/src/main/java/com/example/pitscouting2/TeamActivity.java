package com.example.pitscouting2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeamActivity extends AppCompatActivity {

    public static final String TEAM_NUMBER =  "lamo";
    public static final String PATH =  "lamo2";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private DocumentReference documentReference = notebookref.document("Bellingham")
    private TeamNoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamActivity.this, NewTeamNoteActivity.class));

            }
        });
        //Changing Toolbar text
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Teams");

        setupRecyclerView();

    }

    private void setupRecyclerView(){
        Intent intent = getIntent();
        String ID = intent.getStringExtra(EventActivity.EXTRA_ID);
        String PATH1 = intent.getStringExtra(EventActivity.EXTRA_PATH);
        CollectionReference notebookref = db.collection(PATH1 + "/Teams");

        Query query = notebookref;

        FirestoreRecyclerOptions<TeamNote> options = new FirestoreRecyclerOptions.Builder<TeamNote>().setQuery(query, TeamNote.class).build();

        adapter = new TeamNoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TeamNoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //TeamNote note = documentSnapshot.toObject(TeamNote.class);
                //String id = documentSnapshot.getId();
                //String path = documentSnapshot.getReference().getPath();
                //Toast.makeText(TeamActivity.this,
                  //      "Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();
                //Toast.makeText(TeamActivity.this, "GRUh", Toast.LENGTH_SHORT).show();
                String number = (String) documentSnapshot.get("number");
                String path = (String) documentSnapshot.getReference().getPath();
                //String number = "o";
                //Toast.makeText(TeamActivity.this, number, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TeamActivity.this, TeamScoutingActivity.class);
                intent.putExtra(TEAM_NUMBER, number);
                intent.putExtra(PATH, path);

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