package ar.edu.itba.inge.pab.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

class StudentViewModel extends RequestViewModel {
    LiveData<List<Project>> getFeed() {
        ApiRequest<List<Project>> feedRequest = MyApplication.getInstance().getApiRepository().getFeed();
        requestListeners.add(feedRequest.getListener());
        return Transformations.map(feedRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    void setStudent(Student student) {
        MyApplication.getInstance().getApiRepository().setStudent(student);
    }

    void setProject(Project project) {
        MyApplication.getInstance().getApiRepository().setProject(project);
    }
}
