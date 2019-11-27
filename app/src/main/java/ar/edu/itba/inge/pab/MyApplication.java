package ar.edu.itba.inge.pab;

import android.app.Application;
import android.widget.Toast;

public class MyApplication extends Application {
    private static final float toastHorizontalMargin = 0;
    private static final float toastVerticalMargin = (float) 0.08;

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create API instance, no more context needed
//        Api.getInstance(this);

        instance = this;
    }

    public synchronized static MyApplication getInstance() {
        return instance;
    }

    public static void makeToast(String message) {
        Toast toast = Toast.makeText(instance, message, Toast.LENGTH_SHORT);
        toast.setMargin(toastHorizontalMargin, toastVerticalMargin);
        toast.show();
    }

    public static String getStringResource(int id) {
        return instance.getResources().getString(id);
    }
}
