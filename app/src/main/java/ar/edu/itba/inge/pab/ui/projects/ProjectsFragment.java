package ar.edu.itba.inge.pab.ui.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.ui.explore.ExploreViewModel;

public class ProjectsFragment extends Fragment {
    private ProjectsViewModel projectsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        projectsViewModel = ViewModelProviders.of(this).get(ProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return root;
    }
}
