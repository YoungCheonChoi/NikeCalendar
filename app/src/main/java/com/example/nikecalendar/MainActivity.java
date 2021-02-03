package com.example.nikecalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    EditText inputSearch;
    RecyclerView recyclerView;
    FloatingActionButton addfloatingButton;
    FirebaseRecyclerOptions<Newshoes_main> options;
    FirebaseRecyclerAdapter<Newshoes_main, MainViewHolder> adapter;
    DatabaseReference Dataref;

    FirebaseAuth firebaseAuthmain;

//    RecyclerView mrecyclerView;
//    FirebaseDatabase mfirebaseDatabase;
//    DatabaseReference mref;

//    RecyclerView mainrecyclerview;
//    RecyclerView.Adapter mainadapter;
//    RecyclerView.LayoutManager mainlayoutmanager;
//    ArrayList<Newshoes_main> shoesList;
//    FirebaseDatabase maindatabase;
//    DatabaseReference maindatabasereference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuthmain = FirebaseAuth.getInstance();

        Dataref = FirebaseDatabase.getInstance().getReference().child("Newshoes_main");

        inputSearch = findViewById(R.id.search);
        recyclerView = findViewById(R.id.main_recyclerview);
        addfloatingButton = findViewById(R.id.addfloating);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);



        //신발 추가 액티비티로 이동
        addfloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getBaseContext(), AddmainActivity.class));
            }
        });

        LoadData("");

        //리사이클러뷰 검색
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() != null){
                    LoadData(s.toString());
                }else{
                    LoadData("");
                }

            }
        });

/*
        mrecyclerView = findViewById(R.id.main_recyclerview);
        mrecyclerView.setHasFixedSize(true);

        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //파이어베이스 데이터베이스 연동
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mfirebaseDatabase.getReference("Newshoes_main");

        mainrecyclerview = findViewById(R.id.main_recyclerview);
        mainrecyclerview.setHasFixedSize(true);
       mainlayoutmanager = new LinearLayoutManager(this);
        mainrecyclerview.setLayoutManager(mainlayoutmanager);
        shoesList = new ArrayList<>();

        //파이어베이스 데이터베이스 연동
        maindatabase = FirebaseDatabase.getInstance();
        maindatabasereference = maindatabase.getReference("Newshoes_main");
        maindatabasereference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //데이터베이스의 데이터 받아오는 부분
                shoesList.clear(); //기존배열초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){  //반복문으로 데이터 리스트 출력
                    Newshoes_main newshoes_main = snapshot.getValue(Newshoes_main.class); //만들어둔 Newshoes_main에 담는다
                    shoesList.add(newshoes_main); //데이터를 담고 리사이클러뷰로 보낼 준비
                }
                mainadapter.notifyDataSetChanged(); //리스트 저장, 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mainadapter = new MainRecyclerViewAdapter(shoesList,this);
        mainrecyclerview.setAdapter(mainadapter); //리사이클러뷰에 어댑터 연결
        */

        //Bottomnavigation으로 액티비티 이동
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setSelectedItemId(R.id.main);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()){

                    //메인 -> 캘린더
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    //메인 -> 메인
                    case R.id.main:
                        Toast.makeText(getApplicationContext(),"현재 화면입니다.", Toast.LENGTH_SHORT).show();
                        return true;

                    //메인 -> 게시판
                    case R.id.board:
                        startActivity(new Intent(getApplicationContext(), MarketboardActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    //메인 -> 마이페이지
                    case R.id.mypage:
                        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }//onCreate 끝

    private void LoadData(String data) {

      Query query = Dataref.orderByChild("name").startAt(data).endAt(data+"＼uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Newshoes_main>().setQuery(query,Newshoes_main.class).build();
        adapter = new FirebaseRecyclerAdapter<Newshoes_main, MainViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MainViewHolder holder, final int position, @NonNull Newshoes_main model) {

                //아이템에 파이어베이스에 저장된 정보 담기
                holder.textView_date.setText(model.getDate());
                holder.textView_name.setText(model.getName());
                holder.textView_price.setText(model.getPrice());

                //파이어베이스에서 이미지 로드해서 이미지뷰에 담기
                Picasso.get().load(model.getImageuri()).into(holder.imageView_shoe);

                //아이템 클릭
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ShoesinfoActivity.class);
                        intent.putExtra("Shoeskey", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                //데이터 담을 액티비티연결
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newshoes_main_list,parent,false);
                return new MainViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private void checkUSerStatus(){
        FirebaseUser user = firebaseAuthmain.getCurrentUser();
        if(user != null){
            //로그인 되어 있으면 바로 메인액티비티로(자동로그인)

        }else{
            //로그인 상태 아니면 로그인액티비티로
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStart() {
        checkUSerStatus();
        super.onStart();
    }

    /*
    //onstart상태에서 리사이클러뷰 로딩
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Newshoes_main, MainViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Newshoes_main, MainViewHolder>(
                        Newshoes_main.class,
                R.layout.newshoes_main_list,
                MainViewHolder.class,
                mref
        ) {
            @Override
            protected void populateViewHolder(MainViewHolder mainViewHolder, Newshoes_main newshoes_main, int i) {

                mainViewHolder.setDetails(getApplicationContext(), newshoes_main.getDate(), newshoes_main.getName(), newshoes_main.getPrice(), newshoes_main.getShoeimg());
            }
        };

        mrecyclerView.setAdapter(firebaseRecyclerAdapter);

    }
     */
} //마지막
