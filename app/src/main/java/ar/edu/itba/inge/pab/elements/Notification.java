package ar.edu.itba.inge.pab.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

import ar.edu.itba.inge.pab.MainActivity;

public class Notification implements Serializable {
    private static final String TAG = "ar.edu.itba.inge.pab.NotificationClass";


    private String id;
    private String title;
    private String message;

    private String sender;
    private String type = "";
    private String project = "";

    public Notification() { }

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.sender = MainActivity.getLoggedPerson().getId();
    }

    public Notification(String title, String message, String project, String type) {
        this(title, message);
        this.type = type;
        this.project = project;
    }


    public JSONObject jsonNotification() {
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("body", message);
            Log.d(TAG, "Notification: " + notificationBody.toString());
        } catch (JSONException e) {
            Log.e(TAG, "On create: " + e.getMessage());
        }
        return notificationBody;
    }

    public JSONObject jsonData() {
        JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("sender", sender);
            notificationData.put("project", project);
            notificationData.put("type", type);
            Log.d(TAG, "Notification: " + notificationData.toString());
        } catch (JSONException e) {
            Log.e(TAG, "On create: " + e.getMessage());
        }
        return notificationData;
    }

    public void setData(JSONObject data) {
        try {
            this.sender = data.getString("sender");
            this.project = data.getString("project");
            this.type = data.getString("type");
        } catch (JSONException e) {
            Log.e(TAG, "On create data: " + e.getMessage());
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", type='" + type + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
