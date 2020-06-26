package app.nanotales.in.nanotales;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText fullName, userName, phoneNo, Email, Password, confirmPassword;
    private Button btnCreateAccount;
    private TextView login;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ProgressDialog loadingBar;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //check if user already loggedin
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(CreateAccount.this, HomeScreen.class));
            finish();
        }
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    loadingBar.setTitle("Welcome");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    //upload to database
                    final String email = Email.getText().toString().trim();
                   final String password = Password.getText().toString().trim();
                    final String fullname = fullName.getText().toString();
                    final String phone = phoneNo.getText().toString();
                    final String username = userName.getText().toString();

                    //check if this email already exists in database
                    checkEmailExistsOrNot(email);

                    //check if username already exists

                   CollectionReference usersRef = db.collection("users");
                    Query query = usersRef.whereEqualTo("userName", username);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                    String user = documentSnapshot.getString("userName");

                                    if (user.equals(username)) {
                                        Log.d(TAG, "User Exists");
                                        Toast.makeText(CreateAccount.this, "Username already exists, Choose another one. ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            if (task.getResult().size() == 0) {
                                Log.d(TAG, "User not Exists");
                                 //You can store new user information here
                                //create user account using firebase

                                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            //send verification e mail

                                            FirebaseUser fuser = firebaseAuth.getCurrentUser();
                                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(CreateAccount.this, "Verification Email has been Sent. Please verify your email id", Toast.LENGTH_SHORT).show();

                                                    Toast.makeText(CreateAccount.this, "Account created successfully,Please login", Toast.LENGTH_SHORT).show();

                                                    userid = firebaseAuth.getCurrentUser().getUid();
                                                    DocumentReference documentReference = db.collection("users").document(userid);
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("fName", fullname);
                                                    user.put("userName", username);
                                                    user.put("email", email);
                                                    user.put("phone", phone);

                                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "On Success: user Profile is created for " + userid);
                                                        }
                                                    });
                                                    firebaseAuth.signOut();
                                                    startActivity(new Intent(CreateAccount.this, MainActivity.class));


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                                }
                                            });

                                        }

                                    }
                                });


                            }

                        }
                    });


                }
                else {
                    Toast.makeText(CreateAccount.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this,MainActivity.class));
            }
        });
    }


    private void setupUIViews() {
        fullName = (EditText) findViewById(R.id.inputName);
        userName = (EditText) findViewById(R.id.input_username);
        phoneNo = (EditText) findViewById(R.id.input_phone_no);
        Email = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);
        confirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        btnCreateAccount = (Button) findViewById(R.id.btn_create_acc);
        login = (TextView) findViewById(R.id.textview_login);
        loadingBar = new ProgressDialog(this);
    }

    private Boolean validate() {
        Boolean result = false;
        String fullname = fullName.getText().toString();
        String username = userName.getText().toString();
        String password = Password.getText().toString();
        String confirmpassword = confirmPassword.getText().toString();
        String email = Email.getText().toString();
        String phone = phoneNo.getText().toString();

        if (fullname.isEmpty() && password.isEmpty() && username.isEmpty() && (email.isEmpty() && phone.isEmpty())) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be of 6 characters", Toast.LENGTH_SHORT).show();
                result = false;
            }
            if (!password.equals(confirmpassword)) {
                Toast.makeText(this, "Password must be equal to confirm password", Toast.LENGTH_SHORT).show();
                result = false;
            }
            if(phone.length()!=10){
                Toast.makeText(this, "Phone number must be of 10 digits", Toast.LENGTH_SHORT).show();
                result = false;
            }
            if(username.length() < 4){
                Toast.makeText(this, "Username must be of 4 characters", Toast.LENGTH_SHORT).show();
                result = false;
            }

            else {
                result = true;
            }

        }
        return result;
    }
    void checkEmailExistsOrNot(String email) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d(TAG, "" + task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() == 0) {
                    // email not existed

                } else {
                    // email existed
                    Toast.makeText(CreateAccount.this, "Email already exists! Please login", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(CreateAccount.this, MainActivity.class));

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


}


