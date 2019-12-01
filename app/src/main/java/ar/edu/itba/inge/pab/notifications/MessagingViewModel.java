package ar.edu.itba.inge.pab.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class MessagingViewModel extends RequestViewModel {

    public MessagingViewModel() {
        super();
    }

    LiveData<Person> getTeacher(String id) {
        ApiRequest<Person> personRequest = MyApplication.getInstance().getApiRepository().singleGetTeacher(id);
        requestListeners.add(personRequest.getListener());
        return Transformations.map(personRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    LiveData<Student> getStudent(String id) {
        ApiRequest<Student> studentRequest = MyApplication.getInstance().getApiRepository().singleGetStudent(id);
        requestListeners.add(studentRequest.getListener());
        return Transformations.map(studentRequest.getLiveData(), MyApplication.getTransformFunction());
    }

}
