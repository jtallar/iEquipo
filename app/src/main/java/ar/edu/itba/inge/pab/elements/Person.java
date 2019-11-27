package ar.edu.itba.inge.pab.elements;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String nombre;
    private String id;
    private List<String> actividades = new ArrayList<>();
    private String email;

    public Person() { }

    public Person(String nombre, String id, String email) {
        this.nombre = nombre;
        this.id = id;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getActividades() {
        return actividades;
    }

    public void addActivity(String activityId) {
        actividades.add(activityId);
    }

}
