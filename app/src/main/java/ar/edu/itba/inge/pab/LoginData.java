package ar.edu.itba.inge.pab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;

class LoginData {
    private LiveData<Result<List<Student>>> students;
    private LiveData<Result<List<Person>>> teachers;
    private List<ValueEventListener> requestListeners = new ArrayList<>();


    public LoginData() {
        super();
        reloadData();
    }

    LiveData<List<Student>> getStudents() {
        return Transformations.map(this.students, MyApplication.getTransformFunction());
    }

    LiveData<List<Person>> getTeachers() {
        return Transformations.map(this.teachers, MyApplication.getTransformFunction());
    }

    void reloadData() {
        ApiRequest<List<Student>> studentsRequest = MyApplication.getInstance().getApiRepository().getStudents();
        requestListeners.add(studentsRequest.getListener());
        this.students = studentsRequest.getLiveData();

        ApiRequest<List<Person>> teachersRequest = MyApplication.getInstance().getApiRepository().getTeachers();
        requestListeners.add(teachersRequest.getListener());
        this.teachers = teachersRequest.getLiveData();
    }

    void cancelRequests() {
        // Any repository can be used to cancel requests, method declared in Repository
        for (ValueEventListener listener : requestListeners)
            MyApplication.getInstance().getApiRepository().cancelRequest(listener);
    }
}
