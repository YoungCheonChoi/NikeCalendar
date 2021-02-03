package com.example.nikecalendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MarketboardActivity extends AppCompatActivity {

 FirebaseAuth firebaseAuth;

 RecyclerView recyclerView;
 List<Fleamarket_list> fleaList;
 FleamarketpostAdapter fleamarketpostAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketboard);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.market_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //나중에 추가되는게 위로 오게
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        fleaList = new ArrayList<>();

        loadLists();


        //Bottomnavigation으로 액티비티 이동
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setSelectedItemId(R.id.board);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()){

                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.main:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.board:
                        Toast.makeText(getApplicationContext(),"현재 화면입니다.", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.mypage:
                        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //중고거래게시판 -> 중고거래게시판 글쓰기
        Button button1 = findViewById(R.id.fleamarketwrite_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddfleamarketActivity.class);
                startActivityForResult(intent, 999);
            }
        });
    }


    private void loadLists() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        //데이터 가져오기기
       ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fleaList.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Fleamarket_list fleamarket_list = ds.getValue(Fleamarket_list.class);
                    fleaList.add(fleamarket_list);

                    fleamarketpostAdapter = new FleamarketpostAdapter(getApplicationContext(), fleaList);

                    recyclerView.setAdapter(fleamarketpostAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MarketboardActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

