package ar.edu.itba.inge.pab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ar.edu.itba.hci.hoh";
    private static MainActivity instance;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                MyApplication.makeToast(this.getResources().getString(R.string.search_button_message));
                break;
            case R.id.kebab_profile:
                MyApplication.makeToast(this.getResources().getString(R.string.profile_button_message));
                break;
            case R.id.kebab_about_us:
                aboutUsDialog(item.getActionView());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration;
        if (false) {
            // STUDENT
            navView.inflateMenu(R.menu.bottom_nav_menu_student);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_projects, R.id.navigation_explore, R.id.navigation_notifications)
            .build();
        } else {
            // TEACHER
            navView.inflateMenu(R.menu.bottom_nav_menu_teacher);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_projects, R.id.navigation_students, R.id.navigation_notifications)
                    .build();
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp();
    }


    private void aboutUsDialog(View view) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_about_us, (ViewGroup) view);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        ImageButton closeDialogButton = dialogView.findViewById(R.id.close_dialog_about_us);
        if (closeDialogButton != null) closeDialogButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
