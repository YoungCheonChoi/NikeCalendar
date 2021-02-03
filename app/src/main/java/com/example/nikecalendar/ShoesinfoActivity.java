package com.example.nikecalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ShoesinfoActivity extends AppCompatActivity {

    ImageView shoesinfoimg;
    TextView shoesinfo_date, shoesinfo_name, shoesinfo_price;
    Button btnDelete;

    DatabaseReference ref, DataRef;
    StorageReference StorageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoesinfo);

        shoesinfoimg = findViewById(R.id.shoesinfo_img);
        shoesinfo_date = findViewById(R.id.shoesinfo_date);
        shoesinfo_name = findViewById(R.id.shoesinfo_shoename);
        shoesinfo_price = findViewById(R.id.shoesinfo_shoeprice);
        btnDelete = findViewById(R.id.delete_btn);

        ref = FirebaseDatabase.getInstance().getReference().child("Newshoes_main");

        //삭제
        String Shoeskey = getIntent().getStringExtra("Shoeskey");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Newshoes_main").child(Shoeskey);
        StorageRef = FirebaseStorage.getInstance().getReference().child("Newshoesimage").child(Shoeskey+".jpg");


        ref.child(Shoeskey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String ShoesDate = dataSnapshot.child("date").getValue().toString();
                    String ShoesName = dataSnapshot.child("name").getValue().toString();
                    String ShoesPrice = dataSnapshot.child("price").getValue().toString();
                    String ShoesImage = dataSnapshot.child("imageuri").getValue().toString();

                    Picasso.get().load(ShoesImage).into(shoesinfoimg);
                    shoesinfo_date.setText(ShoesDate);
                    shoesinfo_name.setText(ShoesName);
                    shoesinfo_price.setText(ShoesPrice);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //삭제 버튼
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //메인으로 이동
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        });

                    }
                });
            }
        });


        //공홈이동 버튼
        Button webbutton = findViewById(R.id.websearch_btn);
        webbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.nike.com/kr/launch/?type=upcoming&activeDate=date-filter:AFTER"));
                startActivity(myIntent);
            }
        });

    }//onCreate
}//마지막
