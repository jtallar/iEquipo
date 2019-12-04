package ar.edu.itba.inge.pab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.Error;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ar.edu.itba.inge.pab.LoginActivity";
    public static final String EXTRA_PERSON = "ar.edu.itba.inge.pab.LoginActivity.extraPerson";

    private static final int RC_SIGN_IN = 123;
    private String data;
    private FirebaseAuth mAuth;
    private ImageButton logInButton;
    private GoogleSignInClient mGoogleSignInClient;

    private LoginData loginData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginData = new LoginData();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .setHostedDomain("itba.edu.ar")
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        logInButton = findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(v -> logIn());

        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(mAuth.getCurrentUser());

        // Intent received if coming from notification
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)  {
            Log.d(LOG_TAG, "We have an intent");
            data = intent.getExtras().getString("data");
            Log.d(LOG_TAG, "Data is: " + data);
        } else data = null;
    }


    private void logIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                MyApplication.makeToast(getResources().getString(R.string.error_login_failed));
                if (e.getMessage() != null) Log.e(LOG_TAG, e.getMessage());
                MyApplication.makeToast(new Error(e.getStatusCode(), e.getMessage()));
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(LOG_TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                MyApplication.makeToast(getResources().getString(R.string.error_firebase_failed));
            }
        });
    }

    private void updateUI(FirebaseUser user){
        if (user != null && user.getEmail() != null){
            searchUser(user);
        }
    }

    private void searchUser(FirebaseUser user) {
        loginData.getStudents().observe(this, students -> {
            if (students != null) {
                for (Student student : students) {
                    if (user.getEmail().equals(student.getEmail())) {
                        goToMain(student);
                        return;
                    }
                }
            }
            loginData.getTeachers().observe(this, teachers -> {
                if (teachers != null) {
                    for (Person teacher : teachers) {
                        if (user.getEmail().equals(teacher.getEmail())) {
                            goToMain(teacher);
                            return;
                        }
                    }
                }
                MyApplication.makeToast(getResources().getString(R.string.error_mail_not_found));
                logOut();
            });
        });
    }

    private void goToMain(Person person) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(EXTRA_PERSON, person);
        if (data != null) myIntent.putExtra("data", data);
        startActivity(myIntent);
        finish();
    }

    private void logOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions tempgso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Google sign out
        GoogleSignIn.getClient(this,tempgso).signOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginData.cancelRequests();
    }
}
