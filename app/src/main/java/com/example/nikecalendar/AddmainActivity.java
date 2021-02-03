package com.example.nikecalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddmainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;

    private ImageView addmainimg;
    private EditText adddate, addname, addprice;
    private TextView textviewprogress;
    private ProgressBar progressBar;
    private Button uploadbtn;

    Uri imageuri;
    boolean isImageadded = false;

    DatabaseReference Dataref;
    StorageReference Stroageref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmain);

        //화면 연결
        addmainimg = findViewById(R.id.img_mainadd);
        adddate = findViewById(R.id.edt_date);
        addname = findViewById(R.id.edt_name);
        addprice = findViewById(R.id.edt_price);
        textviewprogress = findViewById(R.id.textView15);
        progressBar = findViewById(R.id.progressBar);
        uploadbtn = findViewById(R.id.button);

        textviewprogress.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        //Newshoes_main 이랑 연결해놨는데 왜 기존 리사이클러뷰 다 지워지고 새로들어갈까...
        Dataref = FirebaseDatabase.getInstance().getReference().child("Newshoes_main");
        //파이어베이스 스토리지 내 이미지폴더
        Stroageref = FirebaseStorage.getInstance().getReference().child("Newshoesimage");

        //이미지 삽입
        addmainimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        //업로드버튼클릭
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newdate = adddate.getText().toString();
                final String newname = addname.getText().toString();
                final String newprice = addprice.getText().toString();

                if(isImageadded != false && newdate!=null && newname!=null && newprice!=null){
                uploadImage(newdate, newname, newprice);
                }

            }
        });

    }//oncreate

    private void uploadImage(final String newdate, final String newname, final String newprice) {
        textviewprogress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        final String key = Dataref.getKey();
        Stroageref.child(key+".jpg").putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Stroageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("date", newdate);
                        hashMap.put("name", newname);
                        hashMap.put("price", newprice);
                        hashMap.put("pId", newname);

                        hashMap.put("Imageuri", uri.toString());

                        Dataref = FirebaseDatabase.getInstance().getReference().child("Newshoes_main");

                        Dataref.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent (getApplicationContext(), MainActivity.class));
                                Toast.makeText(AddmainActivity.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } //프로그레스바 진행
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            double progress = (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
            progressBar.setProgress((int)progress);
            textviewprogress.setText(progress + " %");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE && data!=null){
        imageuri = data.getData();
        isImageadded = true;
        addmainimg.setImageURI(imageuri);
        }
    }
}//마지막
