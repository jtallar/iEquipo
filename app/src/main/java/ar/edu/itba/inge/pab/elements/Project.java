package ar.edu.itba.inge.pab.elements;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String id;
    private String idDocente;
    private String titulo;
    private int creditos;
    private String descripcion;
    private String horarios;
    private String requisitos;
    private int cantidad;
    private String contacto;         // aca deberia ser del contacto del usuario profesor
    private List<String> alumnos = new ArrayList<>();

    public Project() { }

    public Project(String id, String idDocente, String titulo, int creditos, String descripcion, String horarios, String requisitos, String contacto, int cantidad) {
        this.id = id;
        this.idDocente = idDocente;
        this.titulo = titulo;
        this.creditos = creditos;
        this.descripcion = descripcion;
        this.horarios = horarios;
        this.requisitos = requisitos;
        this.contacto = contacto;
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return getId().equals(project.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public String getIdDocente() {
        return idDocente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getCreditos() {
        return creditos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getHorarios() {
        return horarios;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public String getContacto() {
        return contacto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public List<String> getAlumnos() {
        return alumnos;
    }

    public void addStudent(String aluId) {
        alumnos.add(aluId);
    }

    public void removeStudent(String aluId) {
        alumnos.remove(aluId);
    }

    public void editProject(String titulo, int creditos, String descripcion, String horarios, String requisitos, int cantidad) {
        this.titulo = titulo;
        this.creditos = creditos;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.horarios = horarios;
        this.requisitos = requisitos;
    }
}