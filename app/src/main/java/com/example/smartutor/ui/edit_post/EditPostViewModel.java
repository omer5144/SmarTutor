package com.example.smartutor.ui.edit_post;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartutor.model.LoadingState;
import com.example.smartutor.model.Model;
import com.example.smartutor.model.Post;

public class EditPostViewModel extends ViewModel {

    private Model model = Model.getInstance();
    private LiveData<Post> post;

    public EditPostViewModel() { }
    public void initial(String id){
        post = model.getPost(id);
        post.observeForever(p->{});
    }

    public LiveData<Post> getPost()                                                     { return post; }
    public void updatePost(Post post, Bitmap bitmap, Model.OnCompleteListener listener) { model.updatePost(post, bitmap, listener); }
    public void updatePost(String id, String description, Model.OnCompleteListener listener)  { model.updatePost(id, description, listener);}
    public void deletePost(Model.OnCompleteListener listener)                           { model.deletePost(post.getValue(), listener); }
    public String getCurrentUserEmail()                                                 { return model.getCurrentUserEmail(); }

    public void refresh() { model.refreshPosts(); }

    public LiveData<LoadingState> getPostLoadingState() {return model.postLoadingState; }
}