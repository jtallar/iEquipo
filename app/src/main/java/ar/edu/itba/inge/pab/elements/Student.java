package ar.edu.itba.inge.pab.elements;

public class Student extends Person {
    private String carrera;
    private int porcentaje;
    private int creditos;

    public Student() { }

    public Student(String nombre, String legajo, int porcentaje, int creditos, String email, String carrera) {
        super(nombre, legajo, email);
        this.porcentaje = porcentaje;
        this.creditos = creditos;
        this.carrera = carrera;
    }

    public Student(String nombre, String id, String email) {
        super(nombre, id, email);
    }

    public String getCarrera() {
        return carrera;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public int getCreditos() {
        return creditos;
    }

    public void substractCreditos(int creditos) {
        this.creditos -= creditos;
    }

    public void addCreditos(int creditos) {
        this.creditos += creditos;
    }
}