package com.example.smartutor.ui.home_student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartutor.R;
import com.example.smartutor.Utilities;
import com.example.smartutor.model.Lesson;
import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Tutor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeStudentFragment extends Fragment {

    private HomeStudentViewModel homeStudentViewModel;
    private TextView helloTv;
    private TextView lessonsThisWeek;
    private TextView lessonsRemain;
    private TextView lessonsTotal;
    private TextView nextLessonSubject;
    private TextView nextLessonTutor;
    private TextView nextLessonDate;
    private ImageView nextLessonSubjectImg;
    private LinearLayout calendarLinearLayout;
    private SwipeRefreshLayout swipeUp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeStudentViewModel = new ViewModelProvider(this).get(HomeStudentViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home_student, container, false);

        helloTv = root.findViewById(R.id.homeStudent_hello_tv);
        lessonsThisWeek = root.findViewById(R.id.homeStudent_thisWeakNumber_tv);
        lessonsRemain = root.findViewById(R.id.homeStudent_remainNumber_tv);
        lessonsTotal = root.findViewById(R.id.homeStudent_totalNumber_tv);
        nextLessonSubject = root.findViewById(R.id.homeStudent_subject_tv);
        nextLessonTutor = root.findViewById(R.id.homeStudent_tutor_tv);
        nextLessonDate = root.findViewById(R.id.homeStudent_date_tv);
        nextLessonSubjectImg = root.findViewById(R.id.homeStudent_subject_img);
        calendarLinearLayout = root.findViewById(R.id.homeStudent_calendar_ll);
        swipeUp = root.findViewById(R.id.homeStudent_swipeUp);

        setCalendar();

        homeStudentViewModel.getStudent().observe(getViewLifecycleOwner(), student -> {
            if(student != null) {
                helloTv.setText("hello, " + student.getFirstName() + " " + student.getLastName());
            }
        });

        homeStudentViewModel.getLessonsByStudent().observe(getViewLifecycleOwner(), new Observer<List<Lesson>>() {
            private LiveData<Tutor> tutor = null;

            @Override
            public void onChanged(List<Lesson> lessons) {
                if(tutor!=null){tutor.removeObservers(getViewLifecycleOwner());}
                if(lessons != null) {
                    lessonsThisWeek.setText(String.valueOf(Utilities.getThisWeekLessons(lessons).size()));
                    lessonsRemain.setText(String.valueOf(Utilities.getRemainLessons(lessons).size()));
                    lessonsTotal.setText(String.valueOf(lessons.size()));

                    Lesson nextLesson = Utilities.getNextLesson(lessons);
                    if (nextLesson == null) {
                        nextLessonSubject.setText("");
                        nextLessonDate.setText("");
                        nextLessonTutor.setText("");
                        nextLessonSubjectImg.setImageResource(R.drawable.ic_baseline_block_24);
                    } else {
                        nextLessonSubject.setText(nextLesson.getSubject().toString().replace("_", " ").toLowerCase());
                        nextLessonDate.setText(nextLesson.getDate().format(DateTimeFormatter.ISO_DATE) + " - " + nextLesson.getDate().getHour() + ":00");
                        tutor = homeStudentViewModel.getTutor(nextLesson.getTutorEmail());
                        tutor.observe(getViewLifecycleOwner(), t -> {
                            if (t != null)
                                nextLessonTutor.setText(t.getFirstName() + " " + t.getLastName());
                        });
                        switch (nextLesson.getSubject()) {
                            case MATH:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_math);
                                break;
                            case HISTORY:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_history);
                                break;
                            case SCIENCE:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_science);
                                break;
                            case LANGUAGE:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_english);
                                break;
                            case LITERATURE:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_literature);
                                break;
                            case COMPUTER_SCIENCE:
                                nextLessonSubjectImg.setImageResource(R.drawable.ic_subject_computer_science);
                                break;
                        }
                    }
                }



                setCalendar();
                for(Lesson lesson : Utilities.getRemainLessons(lessons)){
                    LocalDateTime date = lesson.getDate();
                    LinearLayout hourRow = (LinearLayout)calendarLinearLayout.getChildAt(date.getHour() - 8);
                    ImageView img = (ImageView)hourRow.getChildAt((date.getDayOfWeek().getValue() % 7) + 1);
                    img.setImageResource(R.drawable.ic_baseline_info_24);
                    img.setOnClickListener(v -> {
                        HomeStudentFragmentDirections.ActionNavHomeStudentToLessonDetailsStudentFragment action = HomeStudentFragmentDirections.actionNavHomeStudentToLessonDetailsStudentFragment(date.getHour(), (date.getDayOfWeek().getValue() % 7) + 1);
                        Navigation.findNavController(root).navigate(action);
                    });
                }

            }

        });

        homeStudentViewModel.getStudentLoadingState().observe(getViewLifecycleOwner(), state-> handleLoading());
        homeStudentViewModel.getTutorLoadingState().observe(getViewLifecycleOwner(), state-> handleLoading());
        homeStudentViewModel.getLessonLoadingState().observe(getViewLifecycleOwner(), state-> handleLoading());

        swipeUp.setOnRefreshListener(()-> homeStudentViewModel.refresh());
        return root;
    }


    private void enableCalendar(boolean state){
        for(int i=8;i<=20;i++){
            LinearLayout hourRow = (LinearLayout)calendarLinearLayout.getChildAt(i - 8);
            for(int j = 1;j<=7;j++){
                ImageView img = (ImageView)hourRow.getChildAt(j);
                img.setEnabled(state);
            }
        }
    }

    private void setCalendar(){
        for(int i=8;i<=20;i++){
            for(int j = 1;j<=7;j++){
                LinearLayout hourRow = (LinearLayout)calendarLinearLayout.getChildAt(i - 8);
                ImageView img = (ImageView)hourRow.getChildAt(j);
                img.setImageResource(R.drawable.ic_baseline_block_24);
            }
        }
    }

    private void handleLoading() {
        boolean b = (homeStudentViewModel.getStudentLoadingState().getValue() == LoadingState.loaded &&
                homeStudentViewModel.getTutorLoadingState().getValue() == LoadingState.loaded &&
                homeStudentViewModel.getLessonLoadingState().getValue() == LoadingState.loaded);
        enableCalendar(b);
        swipeUp.setRefreshing(!b);
    }
}