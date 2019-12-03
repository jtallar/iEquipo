package ar.edu.itba.inge.pab.ui.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private List<Student> data;
    private OnItemClickListener<Student> listener;

    public StudentListAdapter(List<Student> data, OnItemClickListener<Student> itemClickListener) {
        this.data = data;
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class StudentListViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        ImageView deleteIcon;

        public StudentListViewHolder (View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.project_student_name);
            deleteIcon = itemView.findViewById(R.id.project_student_delete);
        }

        public void bind(final Student student, final OnItemClickListener<Student> listener) {
            tvStudentName.setText(student.getNombre());
            deleteIcon.setOnClickListener(view -> listener.onItemClick(student));
        }
    }
}
