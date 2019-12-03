package ar.edu.itba.inge.pab.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;

public class Api {
    private final static String LOG_TAG = "ar.edu.itba.inge.pab.firebase.Api";
    private static Api instance;
    private static DatabaseReference database;

    private Api() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    ValueEventListener getTeachers(ValueEventListener listener) {
        return database.child("Usuarios").child("Profesores").addValueEventListener(listener);
    }

    ValueEventListener getStudents(ValueEventListener listener) {
        return database.child("Usuarios").child("Becarios").addValueEventListener(listener);
    }

    ValueEventListener getFeed(ValueEventListener listener) {
        return database.child("Feed").addValueEventListener(listener);
    }

    ValueEventListener getProject(String id, ValueEventListener listener) {
        return database.child("Feed").child(id).addValueEventListener(listener);
    }

    ValueEventListener getTeacher(String id, ValueEventListener listener) {
        return database.child("Usuarios").child("Profesores").child(id).addValueEventListener(listener);
    }

    ValueEventListener getStudent(String id, ValueEventListener listener) {
        return database.child("Usuarios").child("Becarios").child(id).addValueEventListener(listener);
    }

    ValueEventListener getNotifications(String id, ValueEventListener listener) {
        return database.child("Notificaciones").child(id).addValueEventListener(listener);
    }

    void singleGetStudent(String id, ValueEventListener listener) {
        database.child("Usuarios").child("Becarios").child(id).addListenerForSingleValueEvent(listener);
    }

    void singleGetTeacher(String id, ValueEventListener listener) {
        database.child("Usuarios").child("Profesores").child(id).addListenerForSingleValueEvent(listener);
    }

    void singleGetProject(String id, ValueEventListener listener) {
        database.child("Feed").child(id).addListenerForSingleValueEvent(listener);
    }

    void singleGetNotifications(String id, ValueEventListener listener) {
        database.child("Notificaciones").child(id).addListenerForSingleValueEvent(listener);
    }

    String createProject(Project project) {
        String key = database.child("Feed").push().getKey();
        project.setId(key);
        database.child("Feed").child(project.getId()).setValue(project);
        return key;
    }

    void setProject(Project project) {
        database.child("Feed").child(project.getId()).setValue(project);
    }

    void setStudent(Student student) {
        database.child("Usuarios").child("Becarios").child(student.getId()).setValue(student);
    }

    void setTeacher(Person teacher) {
        database.child("Usuarios").child("Profesores").child(teacher.getId()).setValue(teacher);
    }

    void setNotification(String userId, Notification notification) {
        String key = database.child("Notificaciones").child(userId).push().getKey();
        notification.setId(key);
        String date[] = Calendar.getInstance().getTime().toString().split(" ");
        notification.setDate(date[1] + " " + date[2]);
        database.child("Notificaciones").child(userId).child(key).setValue(notification);
    }

    void deleteProject(String id) {
        database.child("Feed").child(id).removeValue();
    }

    void deleteNotification(String userId, String id) {
        database.child("Notificaciones").child(userId).child(id).removeValue();
    }

    void cancelRequest(ValueEventListener listener) {
        database.removeEventListener(listener);
    }

    Error handleError(@NonNull DatabaseError error) {
        return new Error(error.getCode(), error.getMessage());
    }
}
