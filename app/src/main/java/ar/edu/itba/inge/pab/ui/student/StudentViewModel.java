package ar.edu.itba.inge.pab.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class StudentViewModel extends RequestViewModel {
    private LiveData<Result<Project>> project;

    LiveData<List<Project>> getFeed() {
        ApiRequest<List<Project>> feedRequest = MyApplication.getInstance().getApiRepository().getFeed();
        requestListeners.add(feedRequest.getListener());
        return Transformations.map(feedRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    void setViewModelProject(String id) {
        reloadProject(id);
    }

    LiveData<Project> getProject() {
        return Transformations.map(this.project, MyApplication.getTransformFunction());
    }

    private void reloadProject(String id) {
        ApiRequest<Project> projectRequest = MyApplication.getInstance().getApiRepository().getProject(id);
        requestListeners.add(projectRequest.getListener());
        this.project = projectRequest.getLiveData();
    }

    void setStudent(Student student) {
        MyApplication.getInstance().getApiRepository().setStudent(student);
    }

    void setProject(Project project) {
        MyApplication.getInstance().getApiRepository().setProject(project);
    }

    void deleteNotification(String userId, String id) {
        MyApplication.getInstance().getApiRepository().deleteNotification(userId, id);
    }

    LiveData<Project> singleGetProject(String id) {
        ApiRequest<Project> projectRequest = MyApplication.getInstance().getApiRepository().getProject(id);
        requestListeners.add(projectRequest.getListener());
        return Transformations.map(projectRequest.getLiveData(), MyApplication.getTransformFunction());
    }
}
