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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ar.edu.itba.hci.hoh";
    private static MainActivity instance;
    private static Person loggedPerson;
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
            case R.id.action_search:
                MyApplication.makeToast(this.getResources().getString(R.string.search_button_message));
                break;
            case R.id.kebab_profile:
                MyApplication.makeToast(this.getResources().getString(R.string.profile_button_message));
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

    // TODO: VER SI EN TODOS LOS ON DESTROY DE LOS FRAGMENTOS DEBO PONER REMOVE EVENT LISTENER
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            loggedPerson = (Person) intent.getSerializableExtra(LoginActivity.EXTRA_PERSON);
            if (loggedPerson != null) {
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

        MyFirebaseMessagingService.setRequestQueue(this.getApplicationContext());
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        // TODO: ADD NAVIGATE UP LISTENERS TO UPDATE VALUES
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

    // TODO: VER SI ACA DEBO DESLOGUEARME O NO
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        logOut();
    }
}
