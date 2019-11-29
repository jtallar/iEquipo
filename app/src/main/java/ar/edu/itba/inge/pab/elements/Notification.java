package ar.edu.itba.inge.pab.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import ar.edu.itba.inge.pab.MainActivity;

public class Notification {
    private static final String TAG = "ar.edu.itba.inge.pab.NotificationClass";

    private static Integer idCount = 0;

    private String id;
    private String title;
    private String message;

    private String sender;
    private String type = "";
    private String project = "";

    public Notification(String title, String message) {
        this.id = idCount.toString();
        idCount++;
        this.title = title;
        this.message = message;
        this.sender = MainActivity.getLoggedPerson().getId();
    }

    public Notification(String title, String message, String project, String type) {
        this.id = idCount.toString();
        idCount++;
        this.title = title;
        this.message = message;
        this.sender = MainActivity.getLoggedPerson().getId();
        this.type = type;
        this.project = project;
    }


    public JSONObject getNotification() {
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

    public JSONObject getData() {
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

    public String getId() {
        return id;
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
}
