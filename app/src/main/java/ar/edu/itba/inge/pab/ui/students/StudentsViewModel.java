package ar.edu.itba.inge.pab.ui.students;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public StudentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is students fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
