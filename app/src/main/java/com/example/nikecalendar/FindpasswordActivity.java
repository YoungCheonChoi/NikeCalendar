package com.example.nikecalendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindpasswordActivity extends AppCompatActivity {

    Button btn_resetpw;
    EditText resetpw_emailsend;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        mAuth = FirebaseAuth.getInstance();
        btn_resetpw = findViewById(R.id.resetpw_btn);
        resetpw_emailsend = findViewById(R.id.resetpw_email);

        btn_resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = resetpw_emailsend.getText().toString();
                if(TextUtils.isEmpty(useremail)){
                    Toast.makeText(FindpasswordActivity.this, "이메일 주소를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(FindpasswordActivity.this, "비밀번호를 재설정할 수 있는 링크를 보냈습니다. 메일함을 확인해주세요.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(FindpasswordActivity.this, LoginActivity.class));
                            }else{
                                Toast.makeText(FindpasswordActivity.this, "오류 발생",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }
}
