package com.example.nikecalendar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText email_login, pw_login;
    Button btn_login;
    TextView btn_findpw;
    private FirebaseAuth mFirebaseAuth;
    SignInButton btn_googlelogin;

    ProgressDialog progressDialog;


    private GoogleSignInClient mgoogleSignInClient;
    private int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //로그인화면과 연결
        mFirebaseAuth = FirebaseAuth.getInstance();

        email_login = findViewById(R.id.login_email);
        pw_login = findViewById(R.id.login_pw);
        btn_login = findViewById(R.id.login_btn);
        btn_findpw = findViewById(R.id.findpw_btn);
        btn_googlelogin = findViewById(R.id.googlelogin_btn);

        final CheckBox autologin = (CheckBox) findViewById(R.id.checkBox);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인 중..");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//                if (mFirebaseUser != null) {
//                    Toast.makeText(LoginActivity.this, "로그인 성공!",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//                }
//            }
//        };

        //로그인 버튼 클릭

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = email_login.getText().toString();
                final String password = pw_login.getText().toString();


                if (email.isEmpty()) {
                    email_login.setError("이메일 주소를 입력해주세요.");
                    email_login.requestFocus();
                } else if (password.isEmpty()) {
                    pw_login.setError("비밀번호를 입력해주세요.");
                    pw_login.requestFocus();
                } else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "로그인 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "로그인 오류. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            } else {

                                //로그인 성공
                                login(email, password);


                                if(autologin.isChecked() == true) {

                                    Toast.makeText(LoginActivity.this, "자동로그인이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                                    //로그인 성공
                                    login(email, password);
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "오류 발생!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //구글로그인 버튼 클릭
        btn_googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mgoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        //로그인 -> 회원가입
        Button button = findViewById(R.id.register_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, 998);
            }
        });

        //로그인 -> 비밀번호 찾기
        TextView textView = findViewById(R.id.findpw_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindpasswordActivity.class);
                startActivityForResult(intent, 998);
            }
        });

    }//onCreate

    //로그인
    private void login(String email, String password) {

        progressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            //로그인 성공하면 메인화면으로
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {

                            //로그인 실패
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                //로그인 실패 메세지
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

            }
        });
    }

//    private void googlesignin(){
//        Intent googlesignin = mgoogleSignInClient.getSignInIntent();
//        startActivityForResult(googlesignin,RC_SIGN_IN);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == RC_SIGN_IN){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
//        try{
//            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
//            Toast.makeText(LoginActivity.this, "구글 로그인 성공", Toast.LENGTH_SHORT ).show();
//            FirebaseGoogleAuth(acc);
//
//            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(i);
//
//        }catch (ApiException e){
//            Toast.makeText(LoginActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT ).show();
//            FirebaseGoogleAuth(null);
//        }
//    }

//    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
//        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(LoginActivity.this, "성공", Toast.LENGTH_SHORT ).show();
//                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                    updateUI(user);
//                }else{
//                    Toast.makeText(LoginActivity.this, "실패", Toast.LENGTH_SHORT ).show();
//                    updateUI(null);
//                }
//            }
//        });
//    }

//    private void updateUI(FirebaseUser fuser){
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//        if(account != null){
//            String personname = account.getDisplayName();
//            String persongivenname = account.getGivenName();
//            String personfamilyname = account.getFamilyName();
//            String personemail = account.getEmail();
//            String personid = account.getId();
//            Uri personphoto = account.getPhotoUrl();
//
//            Toast.makeText(LoginActivity.this, personname+"님이 로그인했습니다.", Toast.LENGTH_SHORT ).show();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        //mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){

                                String email = user.getEmail();
                                String uid = user.getUid();

                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("nickname", "");
                                hashMap.put("image", "");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                //데이터베이스 Users 폴더 안에 정보 저장
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);
                            }

                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("nickname", "");
                            hashMap.put("image", "");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //데이터베이스 Users 폴더 안에 정보 저장
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);


                            Toast.makeText(LoginActivity.this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();

                            //구글로그인 성공
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //에러메세지 보여주기
            }
        });
    }

}//마지막
