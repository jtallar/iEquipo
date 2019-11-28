package ar.edu.itba.inge.pab.ui.projects;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class ProjectsViewModel extends RequestViewModel {
    private LiveData<Result<List<Project>>> feed;

    public ProjectsViewModel() {
        super();
        reloadFeed();
    }

    LiveData<List<Project>> getFeed() {
        return Transformations.map(this.feed, MyApplication.getTransformFunction());
    }

    void reloadFeed() {
        ApiRequest<List<Project>> feedRequest = MyApplication.getInstance().getApiRepository().getFeed();
        requestListeners.add(feedRequest.getListener());
        this.feed = feedRequest.getLiveData();
    }
}
