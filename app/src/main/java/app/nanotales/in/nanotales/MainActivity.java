package app.nanotales.in.nanotales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;


//keytool -exportcert -alias androiddebugkey -keystore "C:\Users\Lenovo\.android\debug.keystore" | "C:\Open ssl\bin\openssl" sha1 -binary | "C:\Open ssl\bin\openssl" base64


//Login Activity
public class MainActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button btnLogin;
    private ImageView GoogleLogin, FacebookLogin;
    private TextView createAccount;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager mCallbackManager;
    private int RC_SIGN_IN = 1;
   // private final SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        //LOGIC TO KEEP USER LOGGED IN

      /*  if(sp.getBoolean("logged",false)){
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
        }*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    loadingBar.setTitle("Welcome");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    String email = Email.getText().toString().trim();
                    String password = Password.getText().toString().trim();

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                   // sp.edit().putBoolean("logged",true).apply();
                                    startActivity(new Intent(MainActivity.this, HomeScreen.class));

                                } else {
                                    Toast.makeText(MainActivity.this, "Please verify your email address by clicking on link sent on your email id.", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(MainActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccount.class));
            }
        });

        /*Google sign in */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });
        FacebookLogin.setOnClickListener(new View.OnClickListener() {
            String TAG ="";
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>()

                {
                    @Override
                    public void onSuccess (LoginResult loginResult){
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel () {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError (FacebookException error){
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
        else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                   // sp.edit().putBoolean("logged",true).apply();
                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
                } else {
                    Toast.makeText(MainActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupUIViews() {

        Email = (EditText) findViewById(R.id.inputName);
        Password = (EditText) findViewById(R.id.inputPassword);
        btnLogin = (Button) findViewById(R.id.btn_login);
        GoogleLogin = (ImageView) findViewById(R.id.googleLogin);
        FacebookLogin = (ImageView) findViewById(R.id.facebookLogin);
        createAccount = (TextView) findViewById(R.id.createAccount);
        loadingBar = new ProgressDialog(this);


    }

    private Boolean validate() {
        Boolean result = false;
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be of 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }


    private void handleFacebookAccessToken(AccessToken token) {
       final String TAG = "";
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                           // sp.edit().putBoolean("logged",true).apply();
                            startActivity(new Intent(MainActivity.this,HomeScreen.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}