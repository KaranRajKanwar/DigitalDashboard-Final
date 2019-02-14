package com.example.digitaldashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button delete;
    private FloatingActionButton share;
    private CheckBox selectall;
    private TextView speed, address, altitude, longitude, latitude;
    private FirebaseAuth Auth;
    SensorDataStructure Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);


        //TODO firebase initialization and resource initialization
        Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sensor Information");
        DatabaseReference sensorRef = FirebaseDatabase.getInstance().getReference("Sensor Information");
        sensorRef.keepSynced(true);

        speed = (TextView) findViewById(R.id.speedinfo);
        address = (TextView) findViewById(R.id.locaddressinfo);
        altitude = (TextView) findViewById(R.id.altitudeinfo);
        longitude = (TextView) findViewById(R.id.longitudeinfo);
        latitude = (TextView) findViewById(R.id.latitudeinfo);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        share = (FloatingActionButton) findViewById(R.id.share);
        delete = (Button) findViewById(R.id.delete);
        selectall = (CheckBox) findViewById(R.id.checkBox);
        reterieveData();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = "the location is" + address + "the speed is :" + speed;
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Marker Location Information");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(intent, "Choose sharing method"));
            }
        });
    }

    private void reterieveData() {
        // TODO: Get the data on a single node.
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SensorDataStructure ds = dataSnapshot.getValue(SensorDataStructure.class);
                address.setText(ds.getAddress());
                altitude.setText(ds.getAltitude());
                longitude.setText(ds.getLongitude());
                latitude.setText(ds.getLatitude());
                speed.setText(ds.getSpeed());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SensorDataStructure ds = dataSnapshot.getValue(SensorDataStructure.class);
                address.setText(ds.getAddress());
                altitude.setText(ds.getAltitude());
                longitude.setText(ds.getLongitude());
                latitude.setText(ds.getLatitude());
                speed.setText(ds.getSpeed());
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
                List<SensorDataStructure> arraylist = new ArrayList<>();

                // TODO: Now data is reteieved, needs to process data.
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    // iterate all the items in the dataSnapshot
                    for (DataSnapshot a : dataSnapshot.getChildren()) {
                        SensorDataStructure SensorDataStructure = new SensorDataStructure();
                        SensorDataStructure.setAddress(a.getValue(SensorDataStructure.class).getAddress());
                        SensorDataStructure.setAltitude(a.getValue(SensorDataStructure.class).getAltitude());
                        SensorDataStructure.setLongitude(a.getValue(SensorDataStructure.class).getLongitude());
                        SensorDataStructure.setLatitude(a.getValue(SensorDataStructure.class).getLatitude());
                        SensorDataStructure.setSpeed(a.getValue(SensorDataStructure.class).getSpeed());

                        arraylist.add(SensorDataStructure);  // now all the data is in arraylist.
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("data failed", "Data Loading Cancelled/Failed.", databaseError.toException());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("my_latitude1", latitude.getText().toString());
        outState.putString("my_longitude1", longitude.getText().toString());
        outState.putString("my_altitude1", altitude.getText().toString());
        outState.putString("my_location1", address.getText().toString());
        outState.putString("my_speed1", speed.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        latitude.setText(savedInstanceState.getString("my_latitude1"));
        longitude.setText(savedInstanceState.getString("my_longitude1"));
        altitude.setText(savedInstanceState.getString("my_altitude1"));
        address.setText(savedInstanceState.getString("my_location1"));
        speed.setText(savedInstanceState.getString("my_speed1"));
    }

}