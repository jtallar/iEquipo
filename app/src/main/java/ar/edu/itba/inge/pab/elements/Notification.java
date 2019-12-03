package ar.edu.itba.inge.pab.elements;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;

public class Notification implements Serializable {
    private static final String TAG = "ar.edu.itba.inge.pab.NotificationClass";


    private String id;
    private String title;
    private String message;
    private String body;

    private String sender;
    private String type = "";
    private String project = "";

    public Notification() { }

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.sender = MainActivity.getLoggedPerson().getId();
    }

    public Notification(String title, String message, String project, NotificationType type) {
        this(title, message);
        this.type = type.getType();
        this.project = project;
    }

    public Notification(String title, String message, String body, String project, NotificationType type) {
        this(title, message);
        this.body = body;
        this.type = type.getType();
        this.project = project;
    }

    public JSONObject jsonData() {
        JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("sender", sender);
            notificationData.put("project", project);
            notificationData.put("type", type);
            notificationData.put("body", body);
            notificationData.put("title", title);
            notificationData.put("message", message);
            notificationData.put("id", id);
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

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id.equals(that.id) || (project.equals(that.project)
                                    && sender.equals(that.sender)
                                    && that.type != NotificationType.INFO.getType()
                                    && type.equals(that.type));
    }

    public int hashCode2() {
        return (sender + project + type + message).hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
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

    public enum NotificationType {
        DOWN("Down", MyApplication.getStringResource(R.string.notification_down_title)),
        JOIN("Join project", MyApplication.getStringResource(R.string.notification_join_title)),
        REQUEST("Request student", MyApplication.getStringResource(R.string.notification_request_title)),
        INFO("Info", MyApplication.getStringResource(R.string.notification_info_title));

        private String type;
        private String title;

        NotificationType(String type, String title) {
            this.type = type;
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public static NotificationType getNotificationType(String string) {
            for (NotificationType notificationType : NotificationType.values()) {
                if (notificationType.type.equals(string))
                    return notificationType;
            }
            return null;
        }
    }
}
