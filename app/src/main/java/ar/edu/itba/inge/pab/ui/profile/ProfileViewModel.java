package ar.edu.itba.inge.pab.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class ProfileViewModel extends RequestViewModel {
    LiveData<Project> singleGetProject(String id) {
        ApiRequest<Project> projectRequest = MyApplication.getInstance().getApiRepository().getProject(id);
        requestListeners.add(projectRequest.getListener());
        return Transformations.map(projectRequest.getLiveData(), MyApplication.getTransformFunction());
    }
}
