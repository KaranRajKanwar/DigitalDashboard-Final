package com.example.digitaldashboard;

import android.content.Intent;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText input_email, input_password;
    private android.widget.EditText Password;
    private Button Login, Anonlogin;
    private TextView Link, ForgotPassword;
    AppCompatCheckBox Checkbox;
    private ProgressBar progressBar;
    private SignInButton GoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "main_activity";
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("89260683613-bn9dbg1ejkjk2nsrgtldfeljjke7l9kj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleBtn = (SignInButton) findViewById(R.id.googleBtn);
        Login = (Button) findViewById(R.id.loginbutton);
        Anonlogin = (Button) findViewById(R.id.anonloginbutton);
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText) findViewById(R.id.password);
        Password = (EditText) findViewById(R.id.password);
        Link = (TextView) findViewById(R.id.link_signup);
        ForgotPassword = (TextView) findViewById(R.id.forgotpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox);

        findViewById(R.id.googleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        Anonlogin.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                                                 // Sign in success, update UI with the signed-in user's information
                                                                 finishAffinity();
                                                                 startActivity(new Intent(MainActivity.this, TrackingActivity.class));
                                                             }
                                                             // ...
                                                         });



        Checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });

        Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });


                Login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = input_email.getText().toString().trim();
                        final String password = input_password.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            if (password.length() < 6) {
                                input_password.setError(getString(R.string.minpass));
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(VISIBLE);
                        //authenticate user
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    public static final String TAG = "";

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //progressBar.setVisibility(View.GONE);
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            finishAffinity();
                                            startActivity(new Intent(MainActivity.this, TrackingActivity.class));
                                            //FirebaseUser user = Auth.getCurrentUser();
                                        }
                                    }
                                });
                    }
                });
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                //if the requestCode is the Google Sign In code that we defined at starting
                if (requestCode == RC_SIGN_IN) {

                    //Getting the GoogleSignIn Task
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        //Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        //authenticating with firebase
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
                Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
                //getting the auth credential
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

                //Now using firebase we are signing in the user here
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, TrackingActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            private void revokeAccess() {
                mGoogleSignInClient.revokeAccess()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
            }

            //this method is called on click
            private void signIn() {
                //getting the google signin intent
                progressBar.setVisibility(VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                //starting the activity for result
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

            //TODO Create a menu and allow it to open in the HomeActivity
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.main_menu, menu);
                return true;
            }

            //TODO When the Quit option is clicked close the application
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.quit) {
                    finish();
                    System.exit(0);
                } else {
                    return super.onOptionsItemSelected(item);
                }
                return true;
            }

            //TODO Check and see if user is already logged in
            @Override
            protected void onStart() {
                super.onStart();
                if (mAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(this, TrackingActivity.class));
                }
            }
        }
