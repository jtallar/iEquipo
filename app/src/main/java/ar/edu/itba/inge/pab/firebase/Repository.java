package ar.edu.itba.inge.pab.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;

public class Repository {
    private static Repository instance;
    private static final Api api = Api.getInstance();

    public static synchronized Repository getInstance() {
        if (instance == null)
            instance = new Repository();
        return instance;
    }

    private static <T> ValueEventListener getListener(final MutableLiveData<Result<T>> result, Class<T> tClass) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.setValue(new Result<>(dataSnapshot.getValue(tClass), null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.setValue(new Result<>(null, api.handleError(databaseError)));
            }
        };
    }

    private static <T> ValueEventListener getListListener(final MutableLiveData<Result<List<T>>> result, Class<T> tClass) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<T> list = new ArrayList<>();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(tClass) != null)
                        list.add(snapshot.getValue(tClass));
                }
                result.setValue(new Result<>(list, null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.setValue(new Result<>(null, api.handleError(databaseError)));
            }
        };
    }

    public void cancelRequest(ValueEventListener listener) {
        api.cancelRequest(listener);
    }

    public ApiRequest<List<Person>> getTeachers() {
        final MutableLiveData<Result<List<Person>>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getTeachers(getListListener(result, Person.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<List<Student>> getStudents() {
        final MutableLiveData<Result<List<Student>>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getStudents(getListListener(result, Student.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<List<Project>> getFeed() {
        final MutableLiveData<Result<List<Project>>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getFeed(getListListener(result, Project.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<Project> getProject(String id) {
        final MutableLiveData<Result<Project>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getProject(id, getListener(result, Project.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<Person> getTeacher(String id) {
        final MutableLiveData<Result<Person>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getTeacher(id, getListener(result, Person.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<Student> getStudent(String id) {
        final MutableLiveData<Result<Student>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getStudent(id, getListener(result, Student.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<List<Notification>> getNotifications(String id) {
        final MutableLiveData<Result<List<Notification>>> result = new MutableLiveData<>();
        ValueEventListener listener = api.getNotifications(id, getListListener(result, Notification.class));
        return new ApiRequest<>(listener, result);
    }

    public ApiRequest<Student> singleGetStudent(String id) {
        final MutableLiveData<Result<Student>> result = new MutableLiveData<>();
        api.singleGetStudent(id, getListener(result, Student.class));
        return new ApiRequest<>(null, result);
    }

    public ApiRequest<Person> singleGetTeacher(String id) {
        final MutableLiveData<Result<Person>> result = new MutableLiveData<>();
        api.singleGetTeacher(id, getListener(result, Person.class));
        return new ApiRequest<>(null, result);
    }

    public ApiRequest<Project> singleGetProject(String id) {
        final MutableLiveData<Result<Project>> result = new MutableLiveData<>();
        api.singleGetProject(id, getListener(result, Project.class));
        return new ApiRequest<>(null, result);
    }

    public void setProject(Project project) {
        api.setProject(project);
    }

    public void setStudent(Student student) {
        api.setStudent(student);
    }

    public void setTeacher(Person teacher) {
        api.setTeacher(teacher);
    }

    public void setNotification(String userId, Notification notification) { api.setNotification(userId, notification); }

    public void deleteProject(String id) {
        api.deleteProject(id);
    }

    public void deleteNotification(String userId, String id) { api.deleteNotification(userId, id);}
}
