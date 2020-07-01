package com.example.usermoz3com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


import com.example.usermoz3com.Adapter.AdapterSuper;
import com.example.usermoz3com.Data.itmeList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllBillsForUser extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterSuper adapterSuper;
    String date,id,name;
    static ArrayList<itmeList> ncdlist;
    static ArrayList<String> datelist , uidlist;
    public ArrayList <List<itmeList>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bills_for_user);
        recyclerView =findViewById(R.id.all);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        id =getIntent().getStringExtra("id");
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy");
        date =simpleDateFormat.format(new Date());
            datelist = new ArrayList<>();
            uidlist = new ArrayList<>();
            ncdlist = new ArrayList<>();
            list = new ArrayList<>();

        getitem();
        FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name =dataSnapshot.child("name").getValue(String.class);
                System.out.println(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getitem (){

        FirebaseDatabase.getInstance().getReference("order").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) // dates
                    {
                        ncdlist.clear();
                        for (DataSnapshot ds1 : ds.getChildren())//items
                        {
                            if (ds1.getKey().equals("طريقة الدفع")){ }
                            else {

                                int i = Integer.parseInt(ds1.child("العدد").getValue(String.class));
                                Double total = Double.parseDouble(ds1.child("المجموع").getValue(String.class));

                                ncdlist.add(new itmeList(ds1.getKey(), ds1.child("السعر").getValue(String.class), i,ds.getKey(), name,id, total,  ds1.child("نوع البيع").getValue(String.class), total, total));
                            }
                        }
                        ArrayList<itmeList> ncdlist1 = new ArrayList<>();
                        ncdlist1.addAll(ncdlist);
                        list.add(ncdlist1);
                    }
                    adapterSuper = new AdapterSuper(AllBillsForUser.this,list);
                    recyclerView.setAdapter(adapterSuper);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}