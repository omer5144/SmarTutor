package com.example.smartutor.ui.lesson_details_student;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartutor.R;
import com.example.smartutor.model.Lesson;
import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Tutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LessonDetailsStudentFragment extends Fragment {

    private  LessonDetailsStudentViewModel viewModel;
    private TextView date;
    private TextView hour;
    private TextView tutorName;
    private TextView email;
    private ImageView image;
    private Button cancel;
    private SwipeRefreshLayout swipeUp;
    private LocalDateTime dateTime;

    public  LessonDetailsStudentFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocalDateTime now = LocalDateTime.now();
        dateTime = LocalDate.now().atTime(LessonDetailsStudentFragmentArgs.fromBundle(getArguments()).getHour(), 0).plusDays(LessonDetailsStudentFragmentArgs.fromBundle(getArguments()).getDay() - ((now.getDayOfWeek().getValue() % 7) +1));

        viewModel = new ViewModelProvider(this).get(LessonDetailsStudentViewModel.class);
        viewModel.initial(dateTime);

        View root = inflater.inflate(R.layout.fragment_lesson_details_student, container, false);

        date = root.findViewById(R.id.lessonDetailsStudent_date_tv);
        hour = root.findViewById(R.id.lessonDetailsStudent_hour_tv);
        tutorName = root.findViewById(R.id.lessonDetailsStudent_tutor_tv);
        email = root.findViewById(R.id.lessonDetailsStudent_email_tv);
        image = root.findViewById(R.id.lessonDetailsStudent_subject_img);
        cancel = root.findViewById(R.id.lessonDetailsStudent_cancel_btn);
        swipeUp = root.findViewById(R.id.lessonDetailsStudent_swipeUp);

        date.setText(dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        hour.setText(dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        viewModel.getLesson().observe(getViewLifecycleOwner(), new Observer<Lesson>() {
            private LiveData<Tutor> tutor = null;

            @Override
            public void onChanged(Lesson l) {
                if (l != null) {
                    switch (l.getSubject()) {
                        case MATH:
                            image.setImageResource(R.drawable.ic_subject_math);
                            break;
                        case HISTORY:
                            image.setImageResource(R.drawable.ic_subject_history);
                            break;
                        case SCIENCE:
                            image.setImageResource(R.drawable.ic_subject_science);
                            break;
                        case LANGUAGE:
                            image.setImageResource(R.drawable.ic_subject_english);
                            break;
                        case LITERATURE:
                            image.setImageResource(R.drawable.ic_subject_literature);
                            break;
                        case COMPUTER_SCIENCE:
                            image.setImageResource(R.drawable.ic_subject_computer_science);
                            break;
                    }
                    if(tutor != null){tutor.removeObservers(getViewLifecycleOwner());}
                    tutor = viewModel.getTutor(l.getTutorEmail());
                    tutor.observe(getViewLifecycleOwner(), t -> {
                        if (t != null) {
                            tutorName.setText(t.getFirstName() + " " + t.getLastName());
                            email.setText(t.getEmail());
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(v -> {
            v.setEnabled(false);
            viewModel.deleteLesson(()->Navigation.findNavController(root).navigate(R.id.action_global_nav_home_student));

        });

        swipeUp.setOnRefreshListener(()-> viewModel.refresh());

        viewModel.getTutorLoadingState().observe(getViewLifecycleOwner(), state->handleLoading());
        viewModel.getLessonLoadingState().observe(getViewLifecycleOwner(), state->handleLoading());

        return root;
    }

    private void handleLoading(){
        boolean b = viewModel.getTutorLoadingState().getValue()== LoadingState.loaded &&
                    viewModel.getLessonLoadingState().getValue() == LoadingState.loaded;
        cancel.setEnabled(b);
        swipeUp.setRefreshing(!b);
    }
}