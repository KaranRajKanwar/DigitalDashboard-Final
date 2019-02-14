package com.example.digitaldashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button;
    boolean separateOnClickActive;
    private EditText editTextEmail, editTextFirstname, editTextLastname, editTextPassword, country;
    private FirebaseAuth Auth;
    private ProgressBar progressBar;
    private static final String DEFAULT_LOCAL = "Canada";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    UserDataStructure mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User Registration Information");

        editTextFirstname = (EditText) findViewById(R.id.editTextFname);
        editTextLastname = (EditText) findViewById(R.id.editTextLname);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        country = (EditText) findViewById(R.id.Country);
        button = (Button) findViewById(R.id.backbtn);

        findViewById(R.id.createbutton).setOnClickListener(this);
        button.setOnClickListener((View.OnClickListener) this);
    }

    private void registerUser() {
        String firstname = editTextFirstname.getText().toString().trim();
        String lastname = editTextLastname.getText().toString().trim();
        String emailaddress = editTextEmail.getText().toString().trim();
        String thepassword = editTextPassword.getText().toString().trim();
        String thecountry = country.getText().toString().trim();


        if (firstname.isEmpty()) {
            editTextFirstname.setError("First name is required!");
            editTextFirstname.requestFocus();
            return;
        }

        if (lastname.isEmpty()) {
            editTextLastname.setError("Last name is required!");
            editTextLastname.requestFocus();
            return;
        }

        if (emailaddress.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailaddress).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (thepassword.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (thepassword.length() < 6) {
            editTextPassword.setError("Minimum length of password must be over 6 characters");
            editTextPassword.requestFocus();
            return;
        }
        if (thecountry.isEmpty()) {
            country.setError("Location information required!");
            country.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //create user
        Auth.createUserWithEmailAndPassword(emailaddress, thepassword)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            writeData(editTextFirstname.getText(), editTextLastname.getText(), editTextEmail.getText(),country.getText());
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backbtn:
                if (!separateOnClickActive) {
                    finishAndRemoveTask();
                    separateOnClickActive = true;
                }
            case R.id.createbutton:
                if (!separateOnClickActive) {
                    registerUser();
                    break;
                }
        }
    }

    private void writeData(Editable editTextFirstname, Editable editTextLastname, Editable editTextEmail, Editable country) {

        UserDataStructure mData = createData(editTextFirstname, editTextLastname, editTextEmail,country);
        myRef.push().setValue(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Account Not Created", Toast.LENGTH_LONG).show();
            }
        });
    }

    private UserDataStructure createData(Editable editTextFirstname, Editable editTextLastname, Editable editTextEmail, Editable country) {
        return new UserDataStructure(String.valueOf(editTextFirstname),
                String.valueOf(editTextLastname),
                String.valueOf(editTextEmail),
                String.valueOf(country));
    }
}

