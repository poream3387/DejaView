package org.poream.dejaview.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.poream.dejaview.Java.Post;
import org.poream.dejaview.R;

/**
 * Created by 이승호 on 2017-10-10.
 */


public class Fragment_myprofile_posts extends Fragment {


    RecyclerView mPostList;
    FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter;

    DatabaseReference databaseReference_Posts;
    Query mQueryCurrentUser;

    //참고) 프래그먼트는 기본 생성자를 무조건 만들어줘야한다!!!! 그래야 앱충돌 안일어난다!!
    public Fragment_myprofile_posts() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        databaseReference_Posts = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference_Posts.keepSynced(true);



    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myprofile_posts, container, false);

        String currentuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .orderByChild("uid")
                .equalTo(currentuserId)
                .limitToLast(50);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();


                firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_row, parent, false);

                return new PostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostViewHolder holder, int position, Post model) {

                final String post_key = getRef(position).getKey();

                holder.setTitle(model.getTitle());
                holder.setContent(model.getContent());
                holder.setImage(getContext(),model.getImage());
                holder.setUsername(model.getUsername());


//                SHOW MORE 구현하기!
//                TextView post_content = holder.mView.findViewById(R.id.postContent);

            }
        };



        mPostList = view.findViewById(R.id.profile_posts_recyclerview);
        mPostList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mPostList.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPostList.setLayoutManager(linearLayoutManager);

        mPostList.setAdapter(firebaseRecyclerAdapter);
        return view;

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View mView) {
            super(mView);
            this.mView = mView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.postTitle);
            post_title.setText(title);
        }

        public void setContent(String content) {
            TextView post_content = mView.findViewById(R.id.postContent);
            post_content.setText(content);
        }

        public void setUsername(String username) {
            TextView post_username = mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setImage(final Context context, final String image) {
            final ImageView post_image = mView.findViewById(R.id.postImage);

            //오프라인일때 로드
            Picasso
                    .with(context)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE).
                    into(post_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso
                                    .with(context)
                                    .load(image)
                                    .into(post_image);
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //이거 있어야한다!
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }

}