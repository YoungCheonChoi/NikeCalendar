package com.example.nikecalendar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MypageActivity extends AppCompatActivity {

    RecyclerView mypagerecyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;

    //프로필사진 저장경로
    String storagePath = "User_Profile_Image/";

    ImageView profileimg;
    TextView profileemail, profilenickname;
    Button profileedit;
    Button Logout;

    ProgressDialog pd;

    String nickname = "";


    //카메라 권한
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;
    String profilephoto;

    List<Fleamarket_list> mypagelists = new ArrayList<>();
    FleamarketpostAdapter mypageadapter;
    String uid;

    public MypageActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        //storageReference = getInstance().getReference();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //화면 연결
        profileimg = findViewById(R.id.mypage_profileimg);
        profileemail = findViewById(R.id.mypage_email);
        profilenickname = findViewById(R.id.mypage_nickname);
        profileedit = findViewById(R.id.profileedit_btn);
        Logout = findViewById(R.id.button3);

        mypagerecyclerView = findViewById(R.id.mypage_recyclerview);

        pd = new ProgressDialog(getApplicationContext());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String nickname = "" + ds.child("nickname").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();


                    profileemail.setText(email);
                    profilenickname.setText(nickname);

                    //프로필이미지
                    try {
                        //이미지 파일 받으면 이미지뷰에 대입
                        Picasso.get().load(image).into(profileimg);
                    } catch (Exception e) {
                        //문제가 생기면 이폴트 이미지로
                        Picasso.get().load(R.drawable.ic_person).into(profileimg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkUserStatus();
        LoadMyposts();


        //Bottomnavigation으로 액티비티 이동
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {

                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.main:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.board:
                        startActivity(new Intent(getApplicationContext(), MarketboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mypage:
                        Toast.makeText(getApplicationContext(), "현재 화면입니다.", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        //로그 아웃
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MypageActivity.this, "로그아웃 했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        //프로필편집
        profileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


    } //onCreate


    //내가 쓴 글 보기
    private void LoadMyposts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        mypagerecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mypagelists.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Fleamarket_list myPosts = ds.getValue(Fleamarket_list.class);

                    mypagelists.add(myPosts);

                    mypageadapter = new FleamarketpostAdapter(getApplicationContext(), mypagelists);

                    mypagerecyclerView.setAdapter(mypageadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraePermission() {
        requestPermissions(storagePermission, CAMERA_REQUEST_CODE);
    }

    //프로필 편집 다이얼로그
    private void showEditProfileDialog() {

        String options[] = {"프로필 이미지 편집", "닉네임 편집"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);

        builder.setTitle("프로필 편집");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    //프로필 이미지 변경
                    pd.setMessage("프로필 사진 업로드 중...");
                    profilephoto = "image";
                    showImagePicDialog();

                } else if (which == 1) {

                    // 닉네임 변경
                    pd.setMessage("프로필 닉네임 업로드 중...");
                    showNicknameUpdateDialog("nickname");
                }
            }
        });
        builder.create().show();
    }

    private void showNicknameUpdateDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
        builder.setTitle("닉네임 설정");

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(0, 10, 10, 10);

        //다이얼로그 에디트텍스트 추가
        final EditText editText = new EditText(getApplicationContext());
        editText.setHint("새 닉네임을 입력해주세요");
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //다이얼로그 변경 버튼 추가
        builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)) {

                    // pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(MypageActivity.this, "변경 완료", Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();

                                }
                            });


                } else {
                    Toast.makeText(MypageActivity.this, "please enter", Toast.LENGTH_SHORT).show();

                }

            }
        });

        //다이얼로그 취소 버튼 추가
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    //이미지 선택 다이얼로그
    private void showImagePicDialog() {

        String options[] = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
        builder.setTitle("프로필 이미지 변경");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //카메라

                    if (!checkCameraPermission()) {
                        requestCameraePermission();
                    } else {
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    //앨범
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();

    }

    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            profileemail.setText(user.getEmail());
            uid = user.getUid();

        } else {
            startActivity(new Intent(MypageActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writestorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "카메라 사용권한 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writestorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "갤러리 사용권한 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //갤러리에서 선택
                image_uri = data.getData();

                uploadProfilephoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //카메라로 선택
                uploadProfilephoto(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilephoto(final Uri uri) {

        //pd.show();

        //이미지의 경로와 이름을 파이어베이스 스토리지에 저장
        String filePathAndName = storagePath + "" + profilephoto + "_" + user.getUid();

        //널포인트발생
        StorageReference storageReference2nd = storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {

                            //이미지 업로드
                            HashMap<String, Object> results = new HashMap<>();

                            results.put(profilephoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(MypageActivity.this, "이미지 업로드 완료", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(MypageActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();

                                        }
                                    });


                        } else {
                            pd.dismiss();
                            //이미지 업로드 에러
                            Toast.makeText(MypageActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        //이미지 업로드 에러
                        Toast.makeText(MypageActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //카메라로 사진
    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //카메라 실행
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    //갤러리에서 사진
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
}//마지막
