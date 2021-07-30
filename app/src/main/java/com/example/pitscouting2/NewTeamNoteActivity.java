package com.example.pitscouting2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class NewTeamNoteActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_team_note);

        editTextName = findViewById(R.id.edit_text_name);
        editTextNumber = findViewById(R.id.edit_text_number);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote(){
        Intent intent = getIntent();
        String PATH = intent.getStringExtra(EventActivity.EXTRA_PATH2);
        String name = editTextName.getText().toString();
        String number = editTextNumber.getText().toString();
        //number = Integer.parseInt(editTextNumber.getText().toString());


        //String numberinstring = number + "";
        if(name.trim().isEmpty() || number.trim().isEmpty()){
            Toast.makeText(this, "Please insert team name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("Events/Glacier Peak/Teams");
        notebookRef.add(new TeamNote(name, number));
        Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
