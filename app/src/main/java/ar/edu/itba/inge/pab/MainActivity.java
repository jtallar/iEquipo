package ar.edu.itba.inge.pab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;
import ar.edu.itba.inge.pab.ui.notifications.NotificationsFragment;
import ar.edu.itba.inge.pab.ui.notifications.NotificationsFragmentDirections;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ar.edu.itba.hci.hoh";
    private static MainActivity instance;
    private static Person loggedPerson;
    private MainData mainData;
//    private static Person loggedPerson = new Student("Julian Tallar", "59356", "jtallar@itba.edu.ar");

    public synchronized static MainActivity getInstance() {
        return instance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kebab_profile:
                NavDestination current = Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination();
                if (current != null && current.getId() == R.id.navigation_profile)
                    MyApplication.makeToast(this.getResources().getString(R.string.profile_button_message));
                else
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_open_profile);
                break;
            case R.id.kebab_about_us:
                aboutUsDialog(item.getActionView());
                break;
            case R.id.kebab_log_out:
                logOut();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            loggedPerson = (Person) intent.getSerializableExtra(LoginActivity.EXTRA_PERSON);
            if (loggedPerson != null) {
                mainData = new MainData(loggedPerson);
                observeLogged();
                Log.e(LOG_TAG, String.format("%s %s %s", loggedPerson.getNombre(), loggedPerson.getId(), loggedPerson.getEmail()));
                for (String a : loggedPerson.getActividades())
                    Log.e(LOG_TAG, String.format("ACT %s", a));
            }
        } else {
            // NO DEBERIA ENTRAR, DEJO EL EXAMPLE
            Log.e(LOG_TAG, "ERROR INTENT");
            loggedPerson.addActivity("A0");
            loggedPerson.addActivity("A1");
        }

        MyFirebaseMessagingService.setParameters(this.getApplicationContext());
        MyFirebaseMessagingService.sendRegistrationToServer();

        instance = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration;
        if (loggedPerson.getClass() == Student.class) {
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

        if (intent != null && intent.getExtras().getString("data") != null) {
            navController.navigate(R.id.navigation_notifications);
        }
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

    public static Person getLoggedPerson() {
        return loggedPerson;
    }

    public static void setLoggedPerson(Person person) {
        loggedPerson = person;
    }

    private void logOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions tempgso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Google sign out
        GoogleSignIn.getClient(this,tempgso).signOut().addOnCompleteListener(this, task -> {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        });
    }

    private void observeLogged() {
        mainData.getLoggedPerson().observe(this, person -> {
            if (person != null)
                loggedPerson = person;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mainData != null)
            mainData.cancelRequests();
    }
}
