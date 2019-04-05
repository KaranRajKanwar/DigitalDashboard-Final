package com.example.digitaldashboard;

import android.Manifest;
import android.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener, OnMapReadyCallback {
    private DrawerLayout mdrawerlayout;
    private ActionBarDrawerToggle mtoggle;
    NavigationView nv;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth Auth;
    private String CHANNEL_ID = "personal_notifications";
    private int NOTIFICATION_ID = 001;
    private TextView welcome, name2, email, currentSpeed, latitude, longitude, locaddress, altitude;
    private Button StartTracking, Mark, ChangeType;
    private static final int REQUEST_PERMISSION_FINE_LOCATION_RESULT = 0;
    private android.location.LocationManager LocationManager;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser fUser;
    private UserDataStructure user;
    UserDataStructure mData;
    SensorDataStructure Data;
    private NavigationView navheader, header;
    String address = "Toronto,Ontario";
    private GoogleMap mMap;
    private Spinner spinner;
    private String Value;
    MarkerOptions mp;
    ArrayList<LatLng> pointList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        //TODO firebase initialization and created resources
        Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sensor Information");
        //myRef = database.getReference("User Registration Information");
        DatabaseReference sensorRef = FirebaseDatabase.getInstance().getReference("Sensor Information");
        //DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User Registration Information");
        sensorRef.keepSynced(true);
        //userRef.keepSynced(true);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Mark = (Button) findViewById(R.id.mark);
        navheader = findViewById(R.id.nav_view);
        StartTracking = (Button) findViewById(R.id.startTracking);
        //locationText = (TextView) findViewById(R.id.locationText);
        currentSpeed = (TextView) findViewById(R.id.txtCurrentSpeed);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        locaddress = (TextView) findViewById(R.id.locaddress);
        altitude = (TextView) findViewById(R.id.altitude);
        spinner = (Spinner) findViewById(R.id.spinner);
        mdrawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mtoggle = new ActionBarDrawerToggle(this, mdrawerlayout, R.string.Open, R.string.Close);
        mdrawerlayout.addDrawerListener(mtoggle);
        mtoggle.syncState();


        //TODO Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.spinneroptions,
                        android.R.layout.simple_spinner_item);

        //TODO Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //TODO Apply the adapter to the spinner
        spinner.setAdapter(staticAdapter);


        //TODO get the 3 bar action bar for navdrawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        // Restoring the markers on configuration changes
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("points")) {
                pointList = savedInstanceState.getParcelableArrayList("points");
                if (pointList != null) {
                    for (int i = 0; i < pointList.size(); i++) {
                        drawMarker(pointList.get(i));
                    }
                }
            }
        }
        //TODO as soon as the tracking button is clicked the actions below are performed
        StartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getApplicationContext(), "Application required to access location", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE_LOCATION_RESULT);
                    }
                } else {
                    getLocation();
                }
            }
        });

        //TODO onclick Mark is the write data to the logpage and adding markers to the map
        Mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeData(locaddress.getText(), altitude.getText(), latitude.getText(), longitude.getText(), currentSpeed.getText(), spinner.toString() );
                mMap.addMarker(mp);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mp.getPosition(), 17));
            }
        });
    }

    //TODO Ask user for permission on utilizing the GPS for location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FINE_LOCATION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application will not run without location permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO Use location to find current location and refresh it every minute
    void getLocation() {
        try {
            LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 5, this);
            LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
        }
    }

    //TODO Gives the navigation menu intents and logic
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Tracking:
                Intent tracking = new Intent(TrackingActivity.this, TrackingActivity.class);
                startActivity(tracking);
                break;

            case R.id.Logs:
                Intent logs = new Intent(TrackingActivity.this, LogsActivity.class);
                startActivity(logs);
                break;

            case R.id.Radio:
                Intent radio = new Intent(TrackingActivity.this, RadioActivity.class);
                startActivity(radio);
                break;

            case R.id.Music:
                Intent music = new Intent(TrackingActivity.this, MusicActivity.class);
                startActivity(music);
                break;

            case R.id.UserProfile:
                Intent userprofile = new Intent(TrackingActivity.this, UserProfileActivity.class);
                startActivity(userprofile);
                break;

            case R.id.Aboutus:
                Intent aboutus = new Intent(TrackingActivity.this, AboutusActivity.class);
                startActivity(aboutus);
                break;

            case R.id.Settings:
                Intent Settings = new Intent(TrackingActivity.this, SettingsActivity.class);
                startActivity(Settings);
                break;
            case R.id.Signout:
                Auth.signOut();
                Intent intent = new Intent(TrackingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.quit:
                finishAndRemoveTask();
                break;
        }
        return true;
    }

    //TODO Creates option menu and allows for logic to work with the options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    //TODO get different types of google maps with a button
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.quit:
                finishAndRemoveTask();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO Gives the latitude,longitude,etc strings on the tracking page
    @Override
    public void onLocationChanged(Location location) {
        mp = new MarkerOptions();
        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mp.title("my position");
        if (location == null) {
            // if you can't get speed because reasons :)
            currentSpeed.setText("00km/h");
        } else {
            //int speed=(int) ((location.getSpeed()) is the standard which returns meters per second. In this example i converted it to kilometers per hour
            int speed = (int) ((location.getSpeed() * 3600) / 1000);
            currentSpeed.setText(speed + " km/h");
        }

        altitude.setText("Altitude: " + location.getAltitude() + " meters");
        latitude.setText("Latitude:" + location.getLatitude());
        longitude.setText("Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locaddress.setText("Address: " + addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO required function
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //TODO If GPS or internet is enabled in app while it is disabled then this function will run
    public void onProviderEnabled(String provider) {
        Toast.makeText(TrackingActivity.this, "GPS and Internet have been enabled!", Toast.LENGTH_SHORT).show();
    }

    //TODO If GPS or internet is not enabled then function will run to let the user know
    public void onProviderDisabled(String provider) {
        Toast.makeText(TrackingActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    //TODO the Datastructure for the sensors
    private SensorDataStructure createData(CharSequence address, CharSequence altitude, CharSequence latitude, CharSequence longitude, CharSequence currentspeed, CharSequence Spinner) {
        return new SensorDataStructure(String.valueOf(address), String.valueOf(altitude), String.valueOf(latitude), String.valueOf(longitude), String.valueOf(currentspeed), String.valueOf(Spinner));
    }

    //TODO This function writes data to the realtime database
    private void writeData(CharSequence address, CharSequence altitude, CharSequence latitude, CharSequence longitude, CharSequence currentspeed,CharSequence Spinner) {

        SensorDataStructure Data = createData(address, altitude, latitude, longitude, currentspeed,Spinner);
        // Select one of the following methods to update the data.
        // 1. To set the value of data
        // myRef.setValue(mData);
        // 2. To create a new node on database.
        //  myRef.push().setValue(mData);
        // TODO: Write the data to the database.
        // 3. To create a new node on database and detect if the writing is successful.
        myRef.push().setValue(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Value was set. ", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Writing failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO Setup google maps to perform certain functions and actions
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng position = new LatLng(43.69, -79.81);
        mMap.addMarker(new MarkerOptions().position(position).title("Marker on home"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        googleMap.setTrafficEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Setting click event handler for map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Draw the marker at the taped position
                drawMarker(point);
                pointList.add(point);
            }
        });

        // TODO Setting click event handler for InfoWIndow
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                // Remove the marker
                marker.remove();
            }
        });
    }

    //TODO draw marker on google maps
    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Setting snippet for the InfoWindow
        markerOptions.snippet("Click here to remove marker!");

        // Setting title for the InfoWindow
        markerOptions.title("Custom Placed Marker");

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    //TODO saves the state of the string in landscape or portrait mode
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("my_latitude", latitude.getText().toString());
        outState.putString("my_longitude", longitude.getText().toString());
        outState.putString("my_altitude", altitude.getText().toString());
        outState.putString("my_location", locaddress.getText().toString());
        outState.putString("my_speed", currentSpeed.getText().toString());
        //outState.putString("my_sot", spinner.getText().toString());
        outState.putParcelableArrayList("points", pointList);
        super.onSaveInstanceState(outState);
    }

    //TODO sets text as the same string when changing to landscape or portrait
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        latitude.setText(savedInstanceState.getString("my_latitude"));
        longitude.setText(savedInstanceState.getString("my_longitude"));
        altitude.setText(savedInstanceState.getString("my_altitude"));
        locaddress.setText(savedInstanceState.getString("my_location"));
        currentSpeed.setText(savedInstanceState.getString("my_speed"));
        //spinner.setText(savedInstanceState.getString("my_sot"));
    }

    //TODO Create and display a notification when logged in!
    private void addNotification() {
        createNotificationChannel();
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_tracking)
                .setContentTitle("You are logged in!")
                .setContentText("Click here to hop back in app.");

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(this, TrackingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    //TODO create a Notification channel as API 28 requires it
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notification";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}