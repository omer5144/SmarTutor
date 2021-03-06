package com.example.smartutor.ui.tutor_feed_student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartutor.R;
import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Post;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TutorFeedStudentFragment extends Fragment {

    private TutorFeedStudentViewModel tutorFeedStudentViewModel;
    private List<Post> listPosts;
    private String tutorEmail;
    private SwipeRefreshLayout swipeUp;
    private AtomicBoolean enabled;

    private RecyclerView listPostsRecyclerView;
    public TutorFeedStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tutorFeedStudentViewModel = new ViewModelProvider(this).get(TutorFeedStudentViewModel.class);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutor_feed_student, container, false);
        tutorEmail = TutorFeedStudentFragmentArgs.fromBundle(getArguments()).getEmail();
        tutorFeedStudentViewModel.initial(tutorEmail);

        listPostsRecyclerView = view.findViewById(R.id.tutorFeedStudent_listPosts_rv);
        swipeUp = view.findViewById(R.id.tutorFeedStudent_swipeUp);
        listPostsRecyclerView.setHasFixedSize(true);

        listPostsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        listPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        TutorFeedStudentFragment.MyAdapter adapter = new TutorFeedStudentFragment.MyAdapter();

        enabled = new AtomicBoolean();
        enabled.set(true);
        adapter.setOnItemClickListener((v, p) ->{
            if(enabled.get()){
                enabled.set(false);
                Navigation.findNavController(view).navigateUp();
            }
        });
        listPostsRecyclerView.setAdapter(adapter);

        listPosts = new LinkedList<>();

        tutorFeedStudentViewModel.getPostsByTutor(tutorEmail).observe(getViewLifecycleOwner(), posts -> {
            if(posts != null){
                Collections.sort(posts, (p1, p2) -> p1.getDate().isBefore(p2.getDate())?1:-1);
                listPosts = posts;
                listPostsRecyclerView.getAdapter().notifyDataSetChanged();
            }

        });


        swipeUp.setOnRefreshListener(()-> tutorFeedStudentViewModel.refresh());
        tutorFeedStudentViewModel.getTutorLoadingState().observe(getViewLifecycleOwner(), state->handleLoading());
        tutorFeedStudentViewModel.getPostLoadingState().observe(getViewLifecycleOwner(), state->handleLoading());

        return view;
    }
    private void handleLoading(){
        boolean b = tutorFeedStudentViewModel.getTutorLoadingState().getValue() == LoadingState.loaded &&
                    tutorFeedStudentViewModel.getPostLoadingState().getValue() == LoadingState.loaded;
        enabled.set(b);
        swipeUp.setRefreshing(!b);
    }
    private static class TutorFeedStudentViewHolder extends RecyclerView.ViewHolder{
        TutorFeedStudentFragment.OnItemClickListener listener;
        TextView owner;
        ImageView postImg;
        TextView description;
        View item;

        public TutorFeedStudentViewHolder(@NonNull View itemView, TutorFeedStudentFragment.OnItemClickListener listener) {
            super(itemView);
            item = itemView;
            owner = itemView.findViewById(R.id.postRow_owner_tv);
            postImg = itemView.findViewById(R.id.postRow_post_img);
            description = itemView.findViewById(R.id.postRow_description_tv);
            this.listener = listener;
            itemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onClick(v, position);
                    }
                }
            });
        }

        public void bind(String owner, String image, String description){
            this.owner.setText(owner);
            this.description.setText(description);
            this.postImg.setImageResource(R.drawable.ic_baseline_hourglass_empty_24);
            if(image != null && image != ""){
                Picasso.get().load(image).placeholder(R.drawable.ic_baseline_hourglass_empty_24).error(R.drawable.ic_baseline_report_problem_24).into(this.postImg);
            }
        }
    }
    private interface OnItemClickListener {
        void onClick(View view, int position);
    }
    private class MyAdapter extends RecyclerView.Adapter<TutorFeedStudentFragment.TutorFeedStudentViewHolder>{
        TutorFeedStudentFragment.OnItemClickListener listener;

        public void setOnItemClickListener(TutorFeedStudentFragment.OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public TutorFeedStudentFragment.TutorFeedStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.post_row, parent, false);
            TutorFeedStudentFragment.TutorFeedStudentViewHolder holder = new TutorFeedStudentFragment.TutorFeedStudentViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TutorFeedStudentFragment.TutorFeedStudentViewHolder holder, int position) {
            Post post = listPosts.get(position);
            tutorFeedStudentViewModel.getTutor().observe(getViewLifecycleOwner(), tutor -> holder.bind(tutor.getFirstName() + " " + tutor.getLastName(), post.getPicture(), post.getText()));
        }

        @Override
        public int getItemCount() {
            return listPosts.size();
        }
    }

}