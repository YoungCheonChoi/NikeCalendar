package com.example.nikecalendar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

public class AddfleamarketActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    String nickname, email, uid, dp;

    EditText titleEt, contentEt;
    ImageView fleaimg;
    Button upload;

    //카메라 권한
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    String cameraPermission[];
    String storagePermission[];

    Uri image_rui = null;
    ProgressDialog pd;

    String editTitle, editContent, editImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfleamarket);

        titleEt = findViewById(R.id.editText2);
        contentEt = findViewById(R.id.editText3);
        fleaimg = findViewById(R.id.imageView);
        upload = findViewById(R.id.button2);


        //권한
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        //수정하기
        Intent intent = getIntent();
        final String isUpdateKey = ""+intent.getStringExtra("key");
        final String editPostId = ""+intent.getStringExtra("editPostId");

        if(isUpdateKey.equals("editPost")){
            //업데이트
            upload.setText("업데이트");
            loadPostDate(editPostId);

        }else{
            //새 포스트 추가하기
            upload.setText("업로드");

        }

        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    nickname = "" + ds.child("nickname").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        titleEt = findViewById(R.id.editText2);
        contentEt = findViewById(R.id.editText3);
        fleaimg = findViewById(R.id.imageView);
        upload = findViewById(R.id.button2);

        //이미지뷰 클릭
        fleaimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        //업로드 버튼 클릭
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString().trim();
                String content = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(AddfleamarketActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(AddfleamarketActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isUpdateKey.equals("editPost")){
                    beginedit(title, content, editPostId);
                }else{
                    UploadData(title, content);
                }


//                if (image_rui == null) {
//                    //텍스트만
//                    UploadData(title, content, "noImage");
//                } else {
//                    //사진도 같이
//                    UploadData(title, content, String.valueOf(image_rui));
//
//                }
            }
        });

    }//onCreate

    private void beginedit(String title, String content, String editPostId) {
        pd.setMessage("게시글 수정 중...");
        pd.show();

//        if(!editImage.equals("noImage")){
//            //이미지없는 포스트 수정
//            editwithimage(title, content, editPostId);
//
//        }else{
//        //이미지 있는 포스트 수정
//            editwithoutimage(title, content, editPostId);
//
//        }

        editwithoutimage(title, content, editPostId);
    }

    private void editwithimage(String title, String content, String editPostId) {
        //이미지가 있는 포스트, 기존 이미지 삭제 후 진행
    }

    private void editwithoutimage(String title, String content, String editPostId) {
    }

    private void loadPostDate(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        Query fquery = reference.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //데이터 가져오기
                    editTitle = ""+ds.child("pTitle").getValue();
                    editContent = ""+ds.child("pContent").getValue();
                    editImage = ""+ds.child("pImage").getValue();

                    titleEt.setText(editTitle);
                    contentEt.setText(editContent);

                    if(!editImage.equals("noImage")){
                        try {
                            Picasso.get().load(editImage).into(fleaimg);
                        }catch (Exception e){

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void UploadData(final String title, final String content) {
        pd.setMessage("업로드 중...");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathandName = "Posts/" + "post_" + timeStamp;

        if (fleaimg.getDrawable() != null) {

            Bitmap bitmap = ((BitmapDrawable)fleaimg.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();


            //이미지 있는 포스트
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathandName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //파이어베이스에 올라간 정보 가져오기
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uEmail", email);
                                hashMap.put("uNickname", nickname);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pContent", content);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();

                                                Toast.makeText(AddfleamarketActivity.this, "게시글이 올라갔습니다.", Toast.LENGTH_SHORT).show();

                                                //게시글 작성 후 게시판액티비티로
                                                Intent intentgo = new Intent(getApplicationContext(), MarketboardActivity.class);
                                                startActivityForResult(intentgo, 999);

                                                //게시글작성페이지 리셋
                                                titleEt.setText("");
                                                contentEt.setText("");
                                                fleaimg.setImageURI(null);
                                                image_rui = null;


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                        }
                    });

        } else {

            //사진 없는 포스트
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uEmail", email);
            hashMap.put("uNickname", nickname);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pContent", content);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();

                            Toast.makeText(AddfleamarketActivity.this, "게시글이 올라갔습니다.", Toast.LENGTH_SHORT).show();

                            //게시글 작성 후 게시판액티비티로
                            Intent intentgo = new Intent(getApplicationContext(), MarketboardActivity.class);
                            startActivityForResult(intentgo, 999);

                            titleEt.setText("");
                            contentEt.setText("");
                            fleaimg.setImageURI(null);
                            image_rui = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                        }
                    });

        }

    }

    private void showImagePickDialog() {
        String[] options = {"카메라", "앨범"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 추가");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //카메라
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        PickFromCamera();
                    }
                }
                if (which == 1) {
                    //앨범
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        PickFromGallery();
                    }

                }
            }
        });
        builder.create().show();
    }

    //카메라로 사진찍기
    private void PickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    //갤러리에서 가져오기
    private void PickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    //카메라 권한
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            uid = user.getUid();

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        PickFromCamera();

                    } else {
                        Toast.makeText(this, "카메라, 앨범 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        PickFromGallery();

                    } else {
                        Toast.makeText(this, "카메라, 앨범 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();

                    }
                } else {

                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_rui = data.getData();

                fleaimg.setImageURI(image_rui);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                fleaimg.setImageURI(image_rui);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}//마지막
