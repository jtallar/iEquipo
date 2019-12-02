package ar.edu.itba.inge.pab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.firebase.Result;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class MainData extends RequestViewModel {
    private LiveData<Result<Person>> loggedTeacher;
    private LiveData<Result<Student>> loggedStudent;

    public MainData(Person loggedPerson) {
        super();
        if (loggedPerson.getClass() == Student.class)
            reloadStudent(loggedPerson.getId());
        else
            reloadTeacher(loggedPerson.getId());
    }

    LiveData<? extends Person> getLoggedPerson() {
        if (loggedTeacher != null)
            return Transformations.map(this.loggedTeacher, MyApplication.getTransformFunction());
        else
            return Transformations.map(this.loggedStudent, MyApplication.getTransformFunction());
    }

    private void reloadStudent(String id) {
        ApiRequest<Student> studentRequest = MyApplication.getInstance().getApiRepository().getStudent(id);
        requestListeners.add(studentRequest.getListener());
        this.loggedStudent = studentRequest.getLiveData();
    }

    private void reloadTeacher(String id) {
        ApiRequest<Person> studentRequest = MyApplication.getInstance().getApiRepository().getTeacher(id);
        requestListeners.add(studentRequest.getListener());
        this.loggedTeacher = studentRequest.getLiveData();
    }
}
