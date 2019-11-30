package ar.edu.itba.inge.pab.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private List<Project> data;
    private OnItemClickListener<Project> listener;

    public ProjectAdapter(List<Project> data, OnItemClickListener<Project> itemClickListener) {
        this.data = data;
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProjectViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName;
        TextView tvProjectHours;

        public ProjectViewHolder (View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tv_project_name);
//            tvProjectHours = itemView.findViewById(R.id.tv_project_hours);
        }

        public void bind(final Project project, final OnItemClickListener<Project> listener) {
            tvProjectName.setText(project.getTitulo());
            tvProjectHours.setText(String.format("%d %s", project.getCreditos(), MyApplication.getStringResource(R.string.project_required_hours)));
            itemView.setOnClickListener(view -> listener.onItemClick(project));
        }
    }
}
