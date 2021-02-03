package com.example.nikecalendar;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MainViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView_shoe;
    TextView textView_date, textView_name, textView_price;
    View v;

    public MainViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView_shoe = itemView.findViewById(R.id.shoe_img);
        textView_date = itemView.findViewById(R.id.txt_shoedate);
        textView_name = itemView.findViewById(R.id.txt_shoename);
        textView_price = itemView.findViewById(R.id.txt_shoeprice);

        v=itemView;

    }


    }

