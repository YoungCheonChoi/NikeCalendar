package com.example.nikecalendar;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FleamarketpostAdapter  extends RecyclerView.Adapter<FleamarketpostAdapter.MyHolder> {

    Context context;
    List<Fleamarket_list> fleaList;

    String myUid;

    public FleamarketpostAdapter(Context context, List<Fleamarket_list> fleaList) {
        this.context = context;
        this.fleaList = fleaList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fleamarket_list, viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        final String uid = fleaList.get(i).getUid();
        String uemail = fleaList.get(i).getuEmail();
        String uNickname = fleaList.get(i).getuNickName();
        String uDp = fleaList.get(i).getuDp();
        final String pId = fleaList.get(i).getpId();
        String pTitle = fleaList.get(i).getpTitle();
        String pContent = fleaList.get(i).getpContent();
        final String pImage = fleaList.get(i).getPImage();
        String pTimeStamp = fleaList.get(i).getpTime();

        //게시글 아래에 작성 날짜
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();

        //일단은 닉네임대신 이메일 넣음
        myHolder.uNameTv.setText(uemail);

        //myHolder.uNameTv.setText(uNickname);

        myHolder.pTimeTv.setText(pTime);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pContentTv.setText(pContent);


        //유저 프로필 사진 부분
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.noneuser).into(myHolder.uPictureIv);
        } catch (Exception e) {

        }

        //포스트에 이미지 넣기
            try {
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            } catch (Exception e) {
            }




        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMoreOptions(myHolder.moreBtn, uid, myUid, pImage, pId);
            }
        });

        myHolder.ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "공유하기", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.CommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

    }


    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        //프사       내용 사진
        TextView pTimeTv, pTitleTv, pContentTv, pLikesTv, uNameTv;
        ImageButton moreBtn;
        Button ShareBtn, CommentBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.market_img);
            pImageIv = itemView.findViewById(R.id. market_image);
            pTimeTv= itemView.findViewById(R.id. market_time);
            pTitleTv= itemView.findViewById(R.id. market_title);
            pContentTv= itemView.findViewById(R.id. textView18);
            uNameTv= itemView.findViewById(R.id. market_name);

            moreBtn= itemView.findViewById(R.id. more);
            ShareBtn= itemView.findViewById(R.id.market_sharebtn);
            CommentBtn= itemView.findViewById(R.id. market_commentbtn);


        }
    }


    //포스트 수정,삭제 메뉴
    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pImage, final String pId) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //자기 게시물만 수정가능하게
        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0,0,"삭제하기");
            popupMenu.getMenu().add(Menu.NONE, 1,0,"수정하기");
        }else{
            Toast.makeText(context, "본인 게시물 외에는 수정/삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuitem) {
                int id = menuitem.getItemId();

                if(id==0){
                    //삭제
                    beginDelete(pId, pImage);

                }else if(id==1){
                    //수정
                    Intent intent = new Intent(context, AddfleamarketActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);


                }
                return false;
            }
        });
        popupMenu.show();
    }


    //포스트 삭제
    private void beginDelete(String pId, String pImage) {

//        if(pImage.equals("noImage")){
//            deleteWithoutImage(pId);
//        }else{
//            deleteWithImage(pId, pImage);
//        }

        deleteWithoutImage(pId);

    }

    //이미지 있는 포스트 삭제
    private void deleteWithImage(final String pId, String pImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("게시물 삭제 중");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();   //id가 같은 게시물 삭제
                                }
                                Toast.makeText(context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    //이미지 없는 포스트 삭제
    private void deleteWithoutImage(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("게시물 삭제 중");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();   //id가 같은 게시물 삭제
                }
                Toast.makeText(context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return fleaList.size();
    }

}
