package com.example.usermoz3com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.usermoz3com.Adapter.Adapter;
import com.example.usermoz3com.Adapter.OrdarAdapter;
import com.example.usermoz3com.Data.DataItem;
import com.example.usermoz3com.Data.OrdarData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ListScreen extends AppCompatActivity {
RecyclerView recyclerView;
Adapter adapter;
List<DataItem>dataItems;
List<DataItem>search;
String numitem;
AutoCompleteTextView completeTextView;
List<String>strings;
String [] datanames;
Double finalprice;
public static FloatingActionButton order;
private long backPressed;
SharedPreference sharedPreference;
SimpleDateFormat format;
List<OrdarData>ordarData;
Date date;
ArrayList<HashMap<String,Object>>arrayList;
String datetxt;
    String tax;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);
        strings =new ArrayList<>();
        search =new ArrayList<>();
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("جاري تحميل الطلب ");
        recyclerView =findViewById(R.id.rec);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        findViewById(R.id.profileUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListScreen.this,AllBillsForUser.class));
            }
        });
        getData();
        sharedPreference = new SharedPreference();
        format =new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        date =new Date();
        datetxt =format.format(date);
        findViewById(R.id.dd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(ListScreen.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        System.out.println(format.format(date)+"");
        LinearLayoutManager l =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataItems =new ArrayList<>();
        adapter =new Adapter(search,ListScreen.this);
       order= findViewById(R.id.push);
       ordarData =new ArrayList<>();
        sharedPreference.removeallFavorite(ListScreen.this);
       order.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ordarData.clear();
                    final Dialog dialog =new Dialog(ListScreen.this);
                    dialog.setContentView(R.layout.orderdialog);
                    final RecyclerView orderrec =dialog.findViewById(R.id.orderrec);
               final Button ordernow = dialog.findViewById(R.id.ordernow);
               Button cancle = dialog.findViewById(R.id.cancle);
               orderrec.setHasFixedSize(true);
               LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListScreen.this, LinearLayoutManager.VERTICAL, false);
               orderrec.setLayoutManager(linearLayoutManager);
               orderrec.setItemAnimator(new DefaultItemAnimator());
              arrayList =sharedPreference.getFavorites(ListScreen.this);
               for (int i =0;i<arrayList.size();i++){
                   HashMap<String,Object>map =arrayList.get(i);
                   ordarData.add(new OrdarData(map.get("name")+"",map.get("price")+"",map.get("count")+"",map.get("type")+"",map.get("tax")+"",map.get("total")+""));
               }
               OrdarAdapter ordarAdapter =new OrdarAdapter(ordarData,ListScreen.this);
               orderrec.setAdapter(ordarAdapter);
               ordernow.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       progressDialog.show();
                       for (int i =0;i <arrayList.size();i++){
                           HashMap<String,Object> map =arrayList.get(i);
                           final DatabaseReference reference =FirebaseDatabase.getInstance().getReference("order").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .child(datetxt).child(map.get("name")+"");
                           final int finalI = i;
                           reference.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if(snapshot.exists()) {
                                       double oldcount = Double.parseDouble(snapshot.child("العدد").getValue(String.class) + "");
                                       double currentcount = Double.parseDouble(ordarData.get(finalI).getConter() + "");
                                       double newcount = oldcount + currentcount;
                                       double oldtotal = Double.parseDouble(snapshot.child("المجموع").getValue(String.class) + "");
                                       double currenttotal = Double.parseDouble(ordarData.get(finalI).getTotal() + "");
                                       double newtotal = oldtotal + currenttotal;
                                       HashMap<String, Object> hashMap = new HashMap<>();
                                       hashMap.put("السعر", ordarData.get(finalI).getPrice());
                                       hashMap.put("العدد", newcount + "");
                                       hashMap.put("المجموع", newtotal + "");
                                       hashMap.put("الضريبه", ordarData.get(finalI).getTax());
                                       hashMap.put("نوع البيع", ordarData.get(finalI).getType());
                                       reference.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                           @Override
                                           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                               progressDialog.dismiss();
                                               adapter.notifyDataSetChanged();
                                               recyclerView.setAdapter(adapter);
                                               order.setVisibility(View.GONE);
                                               Toast.makeText(ListScreen.this, "تم نحميل الطلبية ", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                               sharedPreference.removeallFavorite(ListScreen.this);
                                               FirebaseDatabase.getInstance().getReference("ordernotifi").child("orderauth")
                                                       .setValue(FirebaseAuth.getInstance().getUid() + new Date() + "");
                                           }
                                       });
                                   }
                                   else {
                                       HashMap<String, Object> hashMap = new HashMap<>();
                                       hashMap.put("السعر", ordarData.get(finalI).getPrice());
                                       hashMap.put("العدد", ordarData.get(finalI).getConter());
                                       hashMap.put("المجموع", ordarData.get(finalI).getTotal());
                                       hashMap.put("الضريبه", ordarData.get(finalI).getTax());
                                       hashMap.put("نوع البيع", ordarData.get(finalI).getType());
                                       reference.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                           @Override
                                           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                               progressDialog.dismiss();
                                               adapter.notifyDataSetChanged();
                                               recyclerView.setAdapter(adapter);
                                               order.setVisibility(View.GONE);
                                               Toast.makeText(ListScreen.this, "تم نحميل الطلبية ", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                               sharedPreference.removeallFavorite(ListScreen.this);
                                               FirebaseDatabase.getInstance().getReference("ordernotifi").child("orderauth")
                                                       .setValue(FirebaseAuth.getInstance().getUid() + new Date() + "");
                                           }
                                       });
                                   }

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });

                       }

                   }
               });
               cancle.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.dismiss();
                   }
               });
               dialog.show();
               Window window =dialog.getWindow();
               window.setLayout(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
           }
       });



        completeTextView =findViewById(R.id.searchac);
        completeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        String selection = (String) parent.getItemAtPosition(position);
                        int pos = -1;

                        for (int i = 0; i < datanames.length; i++) {
                            if (datanames[i].equals(selection)) {
                                pos = i;
                                break;
                            }
                        }
                        System.out.println("Position " + pos);
                        recyclerView.smoothScrollToPosition(pos);
                    }
                });
            }
        });

    }

//    private void SearchData(String itme) {
//        FirebaseDatabase.getInstance().getReference("item").child(itme)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String img = dataSnapshot.child("صورة المنتج").getValue(String.class);
//                            String type = dataSnapshot.child("طريقة البيع").getValue(String.class);
//                            String price = dataSnapshot.child("سعر البيع").getValue(String.class);
//                            String tax = dataSnapshot.child("الضريبة").getValue(String.class);
//                            System.out.println(tax+"        tax"   );
////                           Double parseInt =Double.parseDouble(price);
////                           Double taxint =Double.parseDouble(tax);
////                           Double finalprice =(parseInt*taxint)+parseInt;
//                            if (type.equals("فرط")){
//                                numitem =dataSnapshot.child("عدد الحبات داخل الكرتونه").getValue(String.class);
//                                search.add(new DataItem("أسم المنتج:"+"\t"+dataSnapshot.getKey(),"طريقة البيع:"+"\t"+type+"\t"+"عدد الحبات في الكرتونة ,"+numitem,"السعر:"+"\t"+price,img));
//                            }
//                            else { search.add(new DataItem("أسم المنتج:"+"\t"+dataSnapshot.getKey(),"طريقة البيع:"+"\t"+type+"\t","السعر:"+"\t"+price,img));}
//                            recyclerView.setAdapter(adapter);
//                        }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }

    public void getData(){
        FirebaseDatabase.getInstance().getReference("item")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot s1:dataSnapshot.getChildren()){
                            strings.add(s1.getKey());
                            String img = s1.child("صورة المنتج").getValue(String.class);
                            String type = s1.child("طريقة البيع").getValue(String.class);
                            String price = s1.child("سعر البيع").getValue(String.class);
                            try {
                                tax = s1.child("الضريبة").getValue(String.class);
                                Double parseInt =Double.parseDouble(price);
                                Double taxint =Double.parseDouble(tax);
                                finalprice =(parseInt*taxint)+parseInt;
                            }catch (NullPointerException e){

                            }
                            if (type.equals("فرط")){
                                numitem =s1.child("عدد الحبات داخل الكرتونه").getValue(String.class);
                                dataItems.add(new DataItem(s1.getKey(),type,finalprice+"",img,"",tax));
                            }
                           else { dataItems.add(new DataItem(s1.getKey(),type,price,img,"",""));}
                            adapter =new Adapter(dataItems,ListScreen.this);
                            recyclerView.setAdapter(adapter);
                        }
                        System.out.println(strings);
                        datanames = new String[strings.size()];
                        datanames = strings.toArray(datanames);
                        ArrayAdapter<String>adapter =new ArrayAdapter<String>(ListScreen.this,android.R.layout.simple_dropdown_item_1line,datanames);
                        completeTextView.setThreshold(1);
                        completeTextView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public void onBackPressed() {
        if (backPressed +2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(this, "أذا كنت تريد الخروج أضغط مره اخرى", Toast.LENGTH_SHORT).show();
        }
        backPressed =System.currentTimeMillis();
    }
}
