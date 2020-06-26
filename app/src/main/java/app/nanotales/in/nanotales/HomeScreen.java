package app.nanotales.in.nanotales;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    private Button signout;
    BottomNavigationView bottomNav;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        setupUI();
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home()).commit();
       /* signout = (Button)findViewById(R.id.btn_signout);
        firebaseAuth = FirebaseAuth.getInstance();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(HomeScreen.this, "Sign out successfully", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(HomeScreen.this,MainActivity.class));
            }
        });*/

    }

    private void setupUI() {
         bottomNav = findViewById(R.id.bottom_navigation);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.home_page:
                            selectedFragment = new Home();
                            break;
                        case R.id.search_page:
                            selectedFragment = new Search();
                            break;
                        case R.id.create_page:
                            selectedFragment = new Create();
                            break;
                        case R.id.notif_page:
                            selectedFragment = new Notif();
                            break;
                        case R.id.profile_page:
                            selectedFragment = new Profile();
                            break;


                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };

}
