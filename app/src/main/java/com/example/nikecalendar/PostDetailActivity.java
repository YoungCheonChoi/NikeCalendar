package com.example.nikecalendar;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PostDetailActivity extends AppCompatActivity {

    String myUid, myEmail, myName, muDp, postId, hisDp, hisName, hisEmail;


    ImageView uPictureIv, pImageIv;
    TextView emailTv,pTimeTv, pContentTv, pTitleTv;

    ImageButton moreBtn;
    ImageView profileIv;

    LinearLayout profileLayout;

    //댓글창
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cprofileIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        //정보 가져오기
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        uPictureIv = findViewById(R.id.market_img);
        pImageIv= findViewById(R.id.market_image);
        emailTv= findViewById(R.id.market_name);
        pTimeTv= findViewById(R.id.market_time);
        pContentTv= findViewById(R.id.textView18);
        pTitleTv= findViewById(R.id.market_title);
        moreBtn = findViewById(R.id.more);
        commentEt= findViewById(R.id.comment_comment);
        sendBtn= findViewById(R.id.comment_send);
        profileIv= findViewById(R.id.coment_profile);
        cprofileIv = findViewById(R.id.coment_profile);

        LoadPostinfo();

    }//onCreate

    private void LoadPostinfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref. orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
