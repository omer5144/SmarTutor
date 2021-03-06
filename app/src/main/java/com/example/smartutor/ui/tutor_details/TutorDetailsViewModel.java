package com.example.smartutor.ui.tutor_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartutor.model.Event;
import com.example.smartutor.model.Lesson;
import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Tutor;

import java.util.List;

public class TutorDetailsViewModel extends ViewModel {

    private Model model = Model.getInstance();
    private LiveData<Tutor> tutor;
    private LiveData<List<Lesson>> lessonsByTutor;
    private LiveData<List<Lesson>> lessonsByStudent;
    private LiveData<List<Event>> events;

    public TutorDetailsViewModel() {}
    public void initial(String tutorEmail){
        tutor = model.getTutor(tutorEmail);
        lessonsByStudent = model.getLessonsByStudent(getCurrentUserEmail());
        lessonsByTutor = model.getLessonsByTutor(tutorEmail);
        events = model.getEventsByTutor(tutorEmail);
    }

    public LiveData<Tutor> getTutor()                       { return tutor; }
    public LiveData<List<Lesson>> getLessonsByTutor()       { return lessonsByTutor; }
    public LiveData<List<Lesson>> getLessonsByStudent()     { return lessonsByStudent; }
    public LiveData<List<Event>> getEvents()                { return events; }
    public String getCurrentUserEmail()                     { return model.getCurrentUserEmail(); }

    public void refresh() {
        model.refreshTutors();
        model.refreshEvents();
        model.refreshLessons();
    }

    public LiveData<LoadingState> getTutorLoadingState() {return model.tutorLoadingState; }
    public LiveData<LoadingState> getLessonLoadingState() {return model.lessonLoadingState; }
    public LiveData<LoadingState> getEventLoadingState() {return model.eventLoadingState; }
}