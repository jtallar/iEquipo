package ar.edu.itba.inge.pab.firebase;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.ValueEventListener;

public class ApiRequest<T> {
    private ValueEventListener listener;
    private LiveData<Result<T>> liveData;

    public ApiRequest(ValueEventListener listener, LiveData<Result<T>> liveData) {
        this.listener = listener;
        this.liveData = liveData;
    }

    public ValueEventListener getListener() {
        return listener;
    }

    public LiveData<Result<T>> getLiveData() {
        return liveData;
    }
}
