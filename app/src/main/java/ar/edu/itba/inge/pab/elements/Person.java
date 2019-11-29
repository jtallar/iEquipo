package ar.edu.itba.inge.pab.elements;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
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

    public void removeActivity(String activityId) {
        actividades.remove(activityId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getId().equals(person.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
