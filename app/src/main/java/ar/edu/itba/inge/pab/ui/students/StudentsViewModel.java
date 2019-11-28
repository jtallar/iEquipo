package ar.edu.itba.inge.pab.ui.students;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class StudentsViewModel extends RequestViewModel {
    private LiveData<Result<List<Student>>> students;

    public StudentsViewModel() {
        super();
        reloadStudents();
    }

    LiveData<List<Student>> getStudents() {
        return Transformations.map(this.students, MyApplication.getTransformFunction());
    }

    void reloadStudents() {
        ApiRequest<List<Student>> studentsRequest = MyApplication.getInstance().getApiRepository().getStudents();
        requestListeners.add(studentsRequest.getListener());
        this.students = studentsRequest.getLiveData();
    }
}
