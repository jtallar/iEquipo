package ar.edu.itba.inge.pab.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ar.edu.itba.inge.pab.firebase_messaging";
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String TYPE = "application/json";
    private static final String KEY = "";
    private static RequestQueue requestQueue;
    private static Context context;
    private static String userToken;
    private static MessagingViewModel messagingViewModel;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        userToken = token;

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer();
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageTitle FCM message title received.
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     */
    public static void sendRegistrationToServer() {
        Person user = MainActivity.getLoggedPerson();
        if (user == null || userToken == null) return;

        Log.d(TAG, "Registration of token: " + userToken);

        user.setToken(userToken);
        if (user.getClass() == Student.class)
            MyApplication.getInstance().getApiRepository().setStudent((Student) user);
        else MyApplication.getInstance().getApiRepository().setTeacher(user);
    }

    /**
     * Sends a message to a particular device, having its ID.
     *
     * @param title FCM title to be send.
     * @param message FCM message to be send.
     * @param id User destination ID.
     */
    public static void sendMessage(String title, String message, String id) {
        Log.d(TAG, "Request for message to: " + id);

        // TODO change != to ==, when final
        if (id.charAt(0) != 'P')
            messagingViewModel.getStudent(id).observe(MainActivity.getInstance(), student -> {
                if (student == null) return;
                publishMessage(title, message, student.getToken());
            });
        else messagingViewModel.getTeacher(id).observe(MainActivity.getInstance(), person -> {
            if (person == null) return;
            publishMessage(title, message, person.getToken());
        });
    }

    /**
     * Sends a message to a particular device, having its token.
     *
     * @param title FCM title to be send.
     * @param message FCM message to be send.
     * @param token User destination token.
     */
    private static void publishMessage(String title, String message, String token) {
        JSONObject notification = new JSONObject();
        JSONObject dataBody = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("body", message);

            // TODO check the need of functionality and add here
            dataBody.put("sender", MainActivity.getLoggedPerson().getId());

            notification.put("to", token);
            notification.put("notification", notificationBody);
            notification.put("data", dataBody);
            Log.d(TAG, "Notification: " + notification.toString());
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    /**
     *  Sends the notification to the server.
     *
     * @param notification JSON Object properly modeled.
     */
    private static void sendNotification(JSONObject notification) {
        Log.d(TAG, "Sending Notification");
        JsonObjectRequest request = new JsonObjectRequest(FCM_API, notification, response -> Log.i(TAG, String.format("onResponse: %s", response.toString())), error -> Log.i(TAG, "onErrorResponse: Didn't work")){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", KEY);
                params.put("Content-Type", TYPE);
                return params;
            }
        };
        requestQueue.add(request);
    }

    /**
     *  Initializes only once the requestQueue
     *
     * @param contextIn Context given to run the queue.
     */
    public static void setParameters(Context contextIn) {
        if (context == null) context = contextIn;
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(contextIn);
        if (messagingViewModel == null) messagingViewModel = new MessagingViewModel();
    }
}