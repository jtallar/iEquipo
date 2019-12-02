package ar.edu.itba.inge.pab.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class NotificationsViewModel extends RequestViewModel {

    private LiveData<Result<List<Notification>>> notifications;

    public NotificationsViewModel() {
        super();
        reloadNotifications();
    }

    LiveData<List<Notification>> getNotifications() {
        return Transformations.map(this.notifications, MyApplication.getTransformFunction());
    }

    void reloadNotifications() {
        ApiRequest<List<Notification>> notificationsRequest = MyApplication.getInstance().getApiRepository().getNotifications(MainActivity.getLoggedPerson().getId());
        requestListeners.add(notificationsRequest.getListener());
        this.notifications = notificationsRequest.getLiveData();
    }

    LiveData<Student> getStudent(String id) {
        ApiRequest<Student> studentRequest = MyApplication.getInstance().getApiRepository().singleGetStudent(id);
        requestListeners.add(studentRequest.getListener());
        return Transformations.map(studentRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    LiveData<Project> getProject(String id) {
        ApiRequest<Project> projectRequest = MyApplication.getInstance().getApiRepository().singleGetProject(id);
        requestListeners.add(projectRequest.getListener());
        return Transformations.map(projectRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    void deleteNotification(String userId, String id) {
        MyApplication.getInstance().getApiRepository().deleteNotification(userId, id);
    }
}