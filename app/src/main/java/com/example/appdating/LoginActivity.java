package com.example.appdating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static String TAG_FACEBOOK_LOGIN = "Facebook Login";
    private static String TAG_GOOGLE_LOGIN = "Google Login";
    private Button mLogin;
    private SignInButton mLoginGG;
    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mForgetPassword, mRegister;
    private EditText mEmail, mPassword;
    LoginButton mLoginFB;
    CallbackManager callbackManager;
    String personName,personEmail;
    Uri personPhoto;
    //Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //nếu tồn tại
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        mLoginGG = findViewById(R.id.btnLoginGG);
        mLogin = findViewById(R.id.btnLogin);
        mEmail = findViewById(R.id.edtEmail);
        mPassword = findViewById(R.id.edtPassword);
        mRegister = (TextView) findViewById(R.id.txtRegister);
        mForgetPassword = (TextView) findViewById(R.id.txtForgetPass);
        mLoginFB = findViewById(R.id.btnLoginFB);
        set_LoginButton();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mLoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //nếu không thành công
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("enter your email to received reset link");
                passwordResetDialog.setView(resetEmail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        String email = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error, Reset link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //đóng cái dialog
                    }

                });
                passwordResetDialog.create().show();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void set_LoginButton() {
        callbackManager = CallbackManager.Factory.create();
        mLoginFB.setReadPermissions(Arrays.asList("public_profile", "email"));
        mLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG_FACEBOOK_LOGIN, "Login success. Result = " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG_FACEBOOK_LOGIN, "Login cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG_FACEBOOK_LOGIN, "Login failed. Result = " + error);

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    Map userInfo = new HashMap<>();
                    userInfo.put("name", user.getDisplayName());
                    userInfo.put("sex", "Male");
                    userInfo.put("profileImageUrl", user.getPhotoUrl().toString());

                    currentUserDb.updateChildren(userInfo);
                    Toast.makeText(LoginActivity.this, "LoginFacebook:onComplete: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "LoginFacebook:onComplete: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(getApplicationContext(),"Signing Success",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch(ApiException e)
        {
            Toast.makeText(getApplicationContext(),"Signing Failed",Toast.LENGTH_SHORT).show();

        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Firebase Success",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(getApplicationContext(),"Firebase Failed",Toast.LENGTH_SHORT).show();
                     updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null)
        {
            personName = account.getDisplayName();
            personEmail = account.getEmail();
            personPhoto = account.getPhotoUrl();

            sendUserToSetupActivity(personName,personPhoto);

        }
    }
    private void sendUserToSetupActivity(String personName, Uri personPhoto) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class)
                .putExtra("Name",personName)
                .putExtra("sex","Female")
                .putExtra("profileImageUrl",personPhoto.toString())
        );
        this.finish();

    }

    //khi bắt đầu sẽ tự động kết nối với firebase
    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        this.finish();
    }

    @Override
    protected void onStop() {
        //đóng kết nối
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}