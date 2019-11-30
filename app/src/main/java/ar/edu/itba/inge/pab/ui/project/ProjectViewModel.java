package ar.edu.itba.inge.pab.ui.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.firebase.ApiRequest;
import ar.edu.itba.inge.pab.ui.RequestViewModel;

public class ProjectViewModel extends RequestViewModel {

    LiveData<Student> getStudent(String id) {
        ApiRequest<Student> studentRequest = MyApplication.getInstance().getApiRepository().getStudent(id);
        requestListeners.add(studentRequest.getListener());
        return Transformations.map(studentRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    LiveData<Person> getTeacher(String id) {
        ApiRequest<Person> teacherRequest = MyApplication.getInstance().getApiRepository().getTeacher(id);
        requestListeners.add(teacherRequest.getListener());
        return Transformations.map(teacherRequest.getLiveData(), MyApplication.getTransformFunction());
    }

    void setStudent(Student student) {
        MyApplication.getInstance().getApiRepository().setStudent(student);
    }

    void setTeacher(Person teacher) {
        MyApplication.getInstance().getApiRepository().setTeacher(teacher);
    }

    void setProject(Project project) {
        MyApplication.getInstance().getApiRepository().setProject(project);
    }

    void deleteProject(String id) {
        MyApplication.getInstance().getApiRepository().deleteProject(id);
    }

    void deleteNotification(String userId, String id) {
        MyApplication.getInstance().getApiRepository().deleteNotification(userId, id);
    }
}
