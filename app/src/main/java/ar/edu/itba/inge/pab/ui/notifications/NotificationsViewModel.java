package ar.edu.itba.inge.pab.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Alert;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class NotificationsViewModel extends RequestViewModel {

    private LiveData<Result<List<Alert>>> notifications;

    public NotificationsViewModel() {
        super();
        reloadNotifications();
    }

    LiveData<List<Alert>> getNotifications() {
        return Transformations.map(this.notifications, MyApplication.getTransformFunction());
    }

    void reloadNotifications() {
//        ApiRequest<List<Alert>> notifRequest = MyApplication.getInstance().getApiRepository().getNotifications;
    }
}