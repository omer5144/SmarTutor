package com.example.smartutor.ui.lesson_details_tutor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartutor.model.Lesson;
import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Student;

import java.time.LocalDateTime;

public class LessonDetailsTutorViewModel extends ViewModel {
    private Model model = Model.getInstance();
    private LiveData<Lesson> lesson;

    public LessonDetailsTutorViewModel() {}
    public void initial(LocalDateTime dateTime){
        lesson = model.getLessonByTutor(getCurrentUserEmail(), dateTime);
        lesson.observeForever(l-> { });
    }

    public void deleteLesson(Model.OnCompleteListener listener) { model.deleteLesson(lesson.getValue(), listener); }
    public LiveData<Student> getStudent(String email)           { return model.getStudent(email); }
    public LiveData<Lesson> getLesson()                         { return lesson; }
    public String getCurrentUserEmail()                         { return model.getCurrentUserEmail(); }

    public void refresh() {
        model.refreshStudents();
        model.refreshLessons();
    }

    public LiveData<LoadingState> getStudentLoadingState() {return model.studentLoadingState; }
    public LiveData<LoadingState> getLessonLoadingState() {return model.lessonLoadingState; }

}
