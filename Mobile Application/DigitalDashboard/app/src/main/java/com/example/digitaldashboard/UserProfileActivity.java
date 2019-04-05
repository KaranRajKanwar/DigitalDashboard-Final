package com.example.digitaldashboard;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth Auth;
    private TextView name, email, uid, provider, address;
    private ImageView photo;
    private Button loadimg, saveimg;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private static int SELECT_PHOTO = 3;
    private SharedPreferences sp;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference myRef;
    private UserDataStructure mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getInstance().getCurrentUser();
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
        }
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        myRef = database.getReference("User Registration Information");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User Registration Information");
        StorageReference imgref = FirebaseStorage.getInstance().getReference("Profile Pictures/");
        userRef.keepSynced(true);

        name = (TextView) findViewById(R.id.tv_name);
        email = (TextView) findViewById(R.id.emailforprofile);
        uid = (TextView) findViewById(R.id.uid);
        provider = (TextView) findViewById(R.id.provider);
        loadimg = (Button) findViewById(R.id.loadimagebtn);
        saveimg = (Button) findViewById(R.id.saveimagebtn);
        photo = (ImageView) findViewById(R.id.photo);
        address = (TextView) findViewById(R.id.tv_address);


        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        uid.setText(user.getUid());
        provider.setText(user.getProviderId());
        sp = getSharedPreferences("profilePicture", MODE_PRIVATE);



        reterieveData();
        if (!sp.getString("sp", "").equals("")) {
            byte[] decodedString = Base64.decode(sp.getString("sp", ""), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            photo.setImageBitmap(decodedByte);
        }

        loadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        saveimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    //TODO image result sent ot database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO used for pulling the uid and email and name from the database
    private void reterieveData() {
        // TODO: Get the data on a single node.
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserDataStructure ds = dataSnapshot.getValue(UserDataStructure.class);
                name.setText(ds.getFname()+" "+ds.getLname());
                address.setText(ds.getCountry());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserDataStructure ds = dataSnapshot.getValue(UserDataStructure.class);
                name.setText(ds.getFname()+" "+ds.getLname());
                address.setText(ds.getCountry());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // TODO: Get the whole data array on a reference.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserDataStructure> arraylist = new ArrayList<>();

                // TODO: Now data is reteieved, needs to process data.
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    // iterate all the items in the dataSnapshot
                    for (DataSnapshot a : dataSnapshot.getChildren()) {
                        UserDataStructure UserDataStructure = new UserDataStructure();
                        UserDataStructure.setEmail(a.getValue(UserDataStructure.class).getEmail());
                        UserDataStructure.setFname(a.getValue(UserDataStructure.class).getFname());
                        UserDataStructure.setLname(a.getValue(UserDataStructure.class).getLname());
                        UserDataStructure.setCountry(a.getValue(UserDataStructure.class).getCountry());

                        arraylist.add(UserDataStructure);  // now all the data is in arraylist.
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("data failed", "Data Loading Cancelled/Failed.", databaseError.toException());
            }
        });
    }

    //TODO Allows the phone to get permission to pull photos from the phones storage
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //TODO Allows for the upload to occur to the storage
    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Profile Pictures/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }

    }

    //TODO Gives the Back button Logic
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), TrackingActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}