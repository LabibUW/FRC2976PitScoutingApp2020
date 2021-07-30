package com.example.pitscouting2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class TeamScoutingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private String number;
    public String path;
    private ImageView iv_robot;
    private Button btn_choosepic;
    private EditText mEditTextFileName;
    private Button btn_upload;
    private ProgressBar mProgressBar;
    private AutoCompleteTextView et_wheeltype;
    private AutoCompleteTextView et_drivetraintype;
    private EditText et_comments;
    private EditText spn_auto;
    private EditText spn_teleop;
    private EditText et_netweight;
    private RadioGroup ports;
    private RadioGroup trench;
    private RadioGroup climb;
    private RadioGroup controlPanel;
    private Button update;
    private String portsresult = "";
    private String trenchresult = "";
    private String climbresult = "";
    private String controlpanelresult = "";


    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    public static final String[] WHEELS = new String[]{"Performance", "Colson", "Treaded", "Pneumatic", "Omni", "Mecanum"};
    public static final String[] DRIVETRAINS = new String[]{"Tank", "West Coast", "Mecanum", "Swerve"};

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_scouting);


        Intent intent = getIntent();
        number = intent.getStringExtra(TeamActivity.TEAM_NUMBER);
        path = intent.getStringExtra(TeamActivity.PATH);

        //Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mActionBarToolbar);
        //getActionBar().setTitle("YOUR TITLE");
        getSupportActionBar().setTitle("Team " + number);

        iv_robot = (ImageView) findViewById(R.id.iv_robotpic);
        //btn_takepic = (Button) findViewById(R.id.btn_takepicture);
        btn_choosepic = (Button) findViewById(R.id.btn_choosepic);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEditTextFileName = findViewById(R.id.et_filename);
        et_netweight = findViewById(R.id.et_netweight);
        et_wheeltype = findViewById(R.id.et_wheeltype);
        et_drivetraintype = findViewById(R.id.et_drivetraintype);
        spn_auto = findViewById(R.id.spn_auto);
        spn_teleop = findViewById(R.id.spn_teleop);
        ports = findViewById(R.id.portschoice);
        trench = findViewById(R.id.trench_choice);
        climb = findViewById(R.id.climb_choice);
        controlPanel = findViewById(R.id.control_panel);
        et_comments = findViewById(R.id.et_comments);
        update = findViewById(R.id.btn_update);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mEditTextFileName.setText(number);
        btn_choosepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(TeamScoutingActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        ArrayAdapter<String> wheelsadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, WHEELS);
        ArrayAdapter<String> dtadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DRIVETRAINS);
        et_drivetraintype.setAdapter(dtadapter);
        et_wheeltype.setAdapter(wheelsadapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spn_teleop.setAdapter(adapter);
        //spn_auto.setAdapter(adapter);

        String teleopAction = spn_teleop.getText().toString();
        String autoAction = spn_auto.getText().toString();

        ports.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio0:
                        portsresult = "Upper";
                        break;
                    case R.id.radio1:
                        portsresult = "Bottom";
                        break;
                    case R.id.radio2:
                        portsresult = "Both";
                        break;
                }
            }
        });
        trench.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio3:
                        trenchresult = "Yes";
                        break;
                    case R.id.radio4:
                        trenchresult = "No";
                        break;

                }
            }
        });
        climb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio5:
                        climbresult = "Yes";
                        break;
                    case R.id.radio6:
                        climbresult = "No";
                        break;
                }
            }
        });
        controlPanel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio7:
                        controlpanelresult = "Yes";
                        break;
                    case R.id.radio8:
                        controlpanelresult = "No";
                        break;
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInfo();
            }
        });


    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(iv_robot);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(TeamScoutingActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TeamScoutingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadInfo(){
        String weight = et_netweight.getText().toString();
        String wheel = et_wheeltype.getText().toString();
        String drivetrain = et_drivetraintype.getText().toString();
        String teleopAction = spn_teleop.getText().toString();
        String autoAction = spn_auto.getText().toString();
        String comment = et_comments.getText().toString();
        //portsresult;
        //trenchresult;
        //climbresult;
        //controlpanelresult;
        if(!weight.trim().isEmpty()) {
            DocumentReference notebookref = db.document(path);
            notebookref.update(
                    "teleopAction", teleopAction,
                    "autoAction", autoAction,
                    "ports", portsresult,
                    "trench", trenchresult,
                    "climb", climbresult,
                    "controlpanel", controlpanelresult,
                    "comment", comment);
            Toast.makeText(TeamScoutingActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(TeamScoutingActivity.this, "There is a missing field", Toast.LENGTH_LONG).show();
        }
    }
}
