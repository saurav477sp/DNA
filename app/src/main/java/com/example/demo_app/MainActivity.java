package com.example.demo_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new InfoFragment());

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView imageViewOptions = toolbar.findViewById(R.id.imageViewOptions);

        imageViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if(id == R.id.nav_appointment){
                    replaceFragment(new AppointmentFragment());
                } else if (id == R.id.nav_doctor) {
                    replaceFragment(new DoctorFragment());
                } else if (id == R.id.nav_map) {
                    try {
                        startActivity(new Intent(MainActivity.this,Map_Activity.class));
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Error:Unable to open map", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.nav_info) {
                    replaceFragment(new InfoFragment());
                } else if (id == R.id.nav_logOut) {
                    AlertDialog.Builder builder = createLogoutDialog();
                    builder.show();
                } else if (id == R.id.nav_contactus) {
                    replaceFragment(new ContactUsFragment());
                }

                // Close the navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);


                return true;
            }
        });
    }

    private AlertDialog.Builder createLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, login_page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        return builder;
    }


    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main,fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // If the navigation drawer is open, close it
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Otherwise, let the default back button behavior take place
            super.onBackPressed();
        }
    }

}