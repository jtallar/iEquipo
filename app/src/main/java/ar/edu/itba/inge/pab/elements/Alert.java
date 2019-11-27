package ar.edu.itba.inge.pab.elements;

public class Alert {
    private String content;
    private String type;
    private Project project;

    public Alert() { }

    public Alert(String type, String content, Project project) {
        this.type = type;
        this.content = content;
        this.project = project;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public Project getProject() { return project; }

    // TODO: OVERRIDE EQUALS AND HASH CODE CUANDO LE PONGAMOS ALGUN ID O ALGO ASI
}

