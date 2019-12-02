package ar.edu.itba.inge.pab.ui;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;

public abstract class RequestViewModel extends ViewModel {
    protected List<ValueEventListener> requestListeners = new ArrayList<>();

    public RequestViewModel() {
        super();
    }

    public void cancelRequests() {
        // Any repository can be used to cancel requests, method declared in Repository
        for (ValueEventListener listener : requestListeners)
            if (listener != null)
                MyApplication.getInstance().getApiRepository().cancelRequest(listener);
    }
}

