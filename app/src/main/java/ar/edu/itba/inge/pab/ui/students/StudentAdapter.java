package ar.edu.itba.inge.pab.ui.students;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> data;
    private OnItemClickListener<Student> listener;

    public StudentAdapter(List<Student> data, OnItemClickListener<Student> itemClickListener) {
        this.data = data;
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvStudentHours;
        TextView tvStudentCareer;

        public StudentViewHolder (View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentHours = itemView.findViewById(R.id.tv_student_hours);
            tvStudentCareer = itemView.findViewById(R.id.tv_student_carreer);
        }

        public void bind(final Student student, final OnItemClickListener<Student> listener) {
            tvStudentName.setText(student.getNombre());
            tvStudentHours.setText(String.format("%d %s", student.getCreditos(), MyApplication.getStringResource(R.string.student_free_hours)));
            tvStudentCareer.setText(student.getCarrera());
            itemView.setOnClickListener(view -> listener.onItemClick(student));
        }
    }
}
