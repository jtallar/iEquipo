package ar.edu.itba.inge.pab.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    ValueEventListener getFreeStudents(ValueEventListener listener) {
        return database.child("Lista de Becarios Disponibles").addValueEventListener(listener);
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

    void setProject(Project project) {
        database.child("Feed").child(project.getId()).setValue(project);
    }

    void setStudent(Student student) {
        database.child("Usuarios").child("Becarios").child(student.getId()).setValue(student);
    }

    void setTeacher(Person teacher) {
        database.child("Usuarios").child("Profesores").child(teacher.getId()).setValue(teacher);
    }

    // BEFORE CALLING DELETE, YOU SHOULD GET STUDENTS AND REMOVE ACTIVITY FROM STUDENTS IN PROJECT
    void deleteProject(String id) {
        database.child("Feed").child(id).removeValue();
    }

//    ValueEventListener getNotifications(String id, ValueEventListener listener) {
//
//    }

    public void cancelRequest(ValueEventListener listener) {
        database.removeEventListener(listener);
    }

    public Error handleError(@NonNull DatabaseError error) {
        return new Error(error.getCode(), error.getMessage());
    }
}
