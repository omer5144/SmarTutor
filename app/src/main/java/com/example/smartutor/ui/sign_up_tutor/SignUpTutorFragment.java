package com.example.smartutor.ui.sign_up_tutor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.smartutor.MultiSpinner;
import com.example.smartutor.R;
import com.example.smartutor.Utilities;
import com.example.smartutor.model.Gender;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Tutor;
import com.example.smartutor.ui.TutorMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SignUpTutorFragment extends Fragment {
    //view model
    private SignUpTutorViewModel signUpTutorViewModel;

    //views
    private TextView signIn;
    private Spinner gender;
    private EditText password;
    private EditText confirm;
    private MultiSpinner professions;
    private Button chooseDate;
    private EditText date;
    private Button signUp;
    private EditText email;
    private EditText lastName;
    private EditText firstName;
    private  EditText aboutMe;
    private SwipeRefreshLayout swipeUp;
    public SignUpTutorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //view model
        signUpTutorViewModel = new ViewModelProvider(this).get(SignUpTutorViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutor_sign_up, container, false);

        // views setup
        signIn = view.findViewById(R.id.signUpTutor_signIn_tv);
        gender = view.findViewById(R.id.signUpTutor_gender_spn);
        password = view.findViewById(R.id.signUpTutor_password_et);
        confirm = view.findViewById(R.id.signUpTutor_confirmPassword_et);
        professions = view.findViewById(R.id.signUpTutor_professions_spn);
        chooseDate = view.findViewById(R.id.signUpTutor_birthdayDate_btn);
        date = view.findViewById(R.id.signUpTutor_birthdayDate_et);
        signUp = view.findViewById(R.id.signUpTutor_signUp_btn);
        email = view.findViewById(R.id.signUpTutor_email_et);
        lastName = view.findViewById(R.id.signUpTutor_lastName_et);
        firstName = view.findViewById(R.id.signUpTutor_firstName_et);
        aboutMe = view.findViewById(R.id.signUpTutor_aboutMe_etml);
        swipeUp = view.findViewById(R.id.signUpTutor_swipeUp);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gender, R.layout.spinner_item);
        gender.setAdapter(genderAdapter);

        professions.setItems(Arrays.asList(getResources().getStringArray(R.array.subject)), "Choose professions.", (MultiSpinner.MultiSpinnerListener) selected -> { });


        // events setup
        signIn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_global_signIn));
        chooseDate.setOnClickListener(v -> {
            DatePicker picker = new DatePicker(getContext());
            picker.setCalendarViewShown(false);
            picker.setMaxDate(new Date().getTime());
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogStyle)
                    .setView(picker)
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                    .setOnCancelListener(dialog -> date.setText(picker.getDayOfMonth()+"/"+ (picker.getMonth() + 1)+"/"+picker.getYear()));
            builder.show();
        });
        signUp.setOnClickListener(v -> {
            signUp.setEnabled(false);
            swipeUp.setRefreshing(true);
            try{
                Utilities.validateEmail(email.getText().toString());
                Utilities.validateLastName(lastName.getText().toString());
                Utilities.validateFirstName(firstName.getText().toString());
                Utilities.validateDate(date.getText().toString());
                Utilities.validateProfessions(professions.getSelectedItem());
                Utilities.validateAboutMe(aboutMe.getText().toString());
                Utilities.validatePassword(password.getText().toString(), confirm.getText().toString());
                if(signUpTutorViewModel.isExistTutor(email.getText().toString()) || signUpTutorViewModel.isExistStudent(email.getText().toString())){
                    Snackbar.make(signUp, "email in use", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else{
                    Tutor tutor = new Tutor(email.getText().toString(), lastName.getText().toString(), firstName.getText().toString(), Gender.valueOf(gender.getSelectedItem().toString().toUpperCase()), Utilities.convertToDate(date.getText().toString()), Utilities.convertToProfessions(professions.getSelectedItem()), aboutMe.getText().toString());
                    signUpTutorViewModel.addTutor(tutor, password.getText().toString(), ()->{
                        Intent intent = new Intent(getActivity(), TutorMenuActivity.class);
                        intent.putExtra("EMAIL", email.getText().toString());
                        Navigation.findNavController(view).navigate(R.id.action_global_signIn);
                        startActivity(intent);
                    });

                }
            }
            catch (Exception e){
                Snackbar.make(signUp, e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            signUp.setEnabled(true);
            swipeUp.setRefreshing(false);
        });

        Model.getInstance().studentLoadingState.observe(getViewLifecycleOwner(), state -> handleLoading());
        Model.getInstance().tutorLoadingState.observe(getViewLifecycleOwner(), state -> handleLoading());
        swipeUp.setOnRefreshListener(()->{
            Model.getInstance().refreshStudents();
            Model.getInstance().refreshTutors();
        });

        return view;
    }

    public void  handleLoading(){
        if(Model.getInstance().tutorLoadingState.getValue() == Model.LoadingState.loaded && Model.getInstance().studentLoadingState.getValue() == Model.LoadingState.loaded){
            signUp.setEnabled(true);
            swipeUp.setRefreshing(false);
        }
        else{
            signUp.setEnabled(false);
            swipeUp.setRefreshing(true);
        }
    }
}