package ar.edu.itba.inge.pab;

import android.app.Application;
import android.widget.Toast;

import androidx.arch.core.util.Function;

import ar.edu.itba.inge.pab.firebase.Api;
import ar.edu.itba.inge.pab.firebase.Error;
import ar.edu.itba.inge.pab.firebase.Repository;
import ar.edu.itba.inge.pab.firebase.Result;

public class MyApplication extends Application {
    private static final float toastHorizontalMargin = 0;
    private static final float toastVerticalMargin = (float) 0.08;

    private static MyApplication instance;
    private Repository apiRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // First call to api, initialize instance
        Api.getInstance();

        apiRepository = Repository.getInstance();
        instance = this;
    }

    public synchronized static MyApplication getInstance() {
        return instance;
    }

    public Repository getApiRepository() {
        return apiRepository;
    }

    public static void makeToast(String message) {
        Toast toast = Toast.makeText(instance, message, Toast.LENGTH_SHORT);
        toast.setMargin(toastHorizontalMargin, toastVerticalMargin);
        toast.show();
    }

    public static void makeToast(Error error) {
        if (error == null || error.getDescription() == null) return;

        Toast toast;
        String errorMessage = error.getDescription();
        // Recognize errors
        toast = Toast.makeText(instance, errorMessage, Toast.LENGTH_SHORT);
        toast.setMargin(toastHorizontalMargin, toastVerticalMargin);
        toast.show();
    }

    public static String getStringResource(int id) {
        return instance.getResources().getString(id);
    }


    public static <T> Function<Result<T>, T> getTransformFunction() {
        return result -> {
            Error error = result.getError();
            if (error != null)
                MyApplication.makeToast(error);
            return result.getResult();
        };
    }
}
