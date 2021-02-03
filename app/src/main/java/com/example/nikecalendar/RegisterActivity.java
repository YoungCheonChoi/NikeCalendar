package com.example.nikecalendar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //회원가입 액티비티
    EditText email_reg, pw_reg, nickname_reg;
    Button btn_reg;

    private FirebaseAuth mFirebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //회원가입 화면과 연결
        mFirebaseAuth = FirebaseAuth.getInstance();

        email_reg = findViewById(R.id.register_email);
        pw_reg = findViewById(R.id.register_pw);
       nickname_reg = findViewById(R.id.register_nickname);
        btn_reg = findViewById(R.id.register_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("회원가입 중..");


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = email_reg.getText().toString();
                String password = pw_reg.getText().toString();
                String nickname = nickname_reg.getText().toString().trim();

                //회원가입 조건 정리(이메일 패턴, 비번 6자리 이상)
                if (password.length() < 6) {
                    //비번 6자리 이상
                    pw_reg.setError("비밀번호는 6자리 이상 입력해주세요");
                    pw_reg.setFocusable(true);
                } else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "회원가입 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "유효한 이메일 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //회원가입 조건 충족
                    register(email, password, nickname);
                }
            }
        }); //회원가입버튼

    }//onCreate

    private void register(String email, String password, final String nickname) {

        progressDialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // 회원가입 성공
                            progressDialog.dismiss();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("nickname", nickname.toLowerCase());
                            hashMap.put("image", "https://firebasestorage.googleapis.com/v0/b/nikecalendar-e7ffb.appspot.com/o/20200809_200946.png?alt=media&token=9d4fee84-d96d-4c04-bfa8-11277e0504a1");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //데이터베이스 Users 폴더 안에 정보 저장
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "회원가입 성공 - " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            //회원가입 후 로그인 창으로
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {

                            //회원 가입 실패
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


}//마지막
