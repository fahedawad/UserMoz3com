package com.example.usermoz3com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.example.usermoz3com.ViewPagerClass.Pager;
import com.example.usermoz3com.ViewPagerClass.ViewPag;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class ListScreen extends AppCompatActivity {
RecyclerView recyclerView;
Adapter adapter;
List<DataItem>dataItems;
List<DataItem>search;
AutoCompleteTextView completeTextView;
List<String>strings;
String [] datanames;
Double finalprice;
public static FloatingActionButton order;
private long backPressed;
SharedPreference sharedPreference = new SharedPreference();
SimpleDateFormat format;
List<OrdarData>ordarData;
Date date;
ArrayList<HashMap<String,Object>>arrayList;
String datetxt;
    String tax;
ProgressDialog progressDialog;
    ViewPager viewPager;
    ViewPag pag;
    List<Pager>pagers;
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
        dataItems = new ArrayList<>();
        getData();
        format =new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        date =new Date();
        datetxt =format.format(date);
        viewPager =findViewById(R.id.viewPager);
        pagers =new ArrayList<>();
        pag =new ViewPag(this,pagers);
        pagers.add(new Pager("id",R.drawable.p1));
        pagers.add(new Pager("id",R.drawable.p2));
        viewPager.setAdapter(pag);
        LinearLayoutManager l =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataItems =new ArrayList<>();
        adapter =new Adapter(search,ListScreen.this);
       order= findViewById(R.id.push);
        ordarData =new ArrayList<>();
        sharedPreference.removeallFavorite(ListScreen.this);
        final DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        order.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ordarData.clear();
               final Dialog dialog = new Dialog(ListScreen.this);
               dialog.setContentView(R.layout.orderdialog);
               final RecyclerView orderrec = dialog.findViewById(R.id.orderrec);
               final Button ordernow = dialog.findViewById(R.id.ordernow);
               Button cancle = dialog.findViewById(R.id.cancle);
               orderrec.setHasFixedSize(true);
               LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListScreen.this, LinearLayoutManager.VERTICAL, false);
               orderrec.setLayoutManager(linearLayoutManager);
               orderrec.setItemAnimator(new DefaultItemAnimator());
               if (sharedPreference.getFavorites(ListScreen.this) != null && sharedPreference.getFavorites(ListScreen.this).size() != 0){
                   arrayList = sharedPreference.getFavorites(ListScreen.this);
               for (int i = 0; i < arrayList.size(); i++) {
                   HashMap<String, Object> map = arrayList.get(i);
                   ordarData.add(new OrdarData(map.get("name") + "", map.get("price") + "", map.get("count") + "", map.get("type") + "", map.get("tax") + "", map.get("total") + ""));
               }
               OrdarAdapter ordarAdapter = new OrdarAdapter(ordarData, ListScreen.this);
               orderrec.setAdapter(ordarAdapter);
               ordernow.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       progressDialog.show();
                       for (int i = 0; i < arrayList.size(); i++) {
                           HashMap<String, Object> map = arrayList.get(i);
                           final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("order").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .child(datetxt).child(map.get("name") + "");
                           final int finalI = i;
                           reference.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                   if (snapshot.exists()) {
                                       final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("item")
                                               .child(ordarData.get(finalI).getName());
                                       ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                               final String purchasingprice = snapshot1.child("سعر الشراء").getValue(String.class);
                                               final Double inventorycount = Double.parseDouble(snapshot1.child("العدد المتاح").getValue(String.class));
                                               Double currentcounter = Double.parseDouble(ordarData.get(finalI).getConter());
                                               Double newinventorycount = inventorycount - currentcounter;
                                               ref.child("العدد المتاح").setValue(newinventorycount + "");
                                               double oldcount = Double.parseDouble(snapshot.child("العدد").getValue(String.class) + "");
                                               final double currentcount = Double.parseDouble(ordarData.get(finalI).getConter() + "");
                                               double newcount = oldcount + currentcount;
                                               double oldtotal = Double.parseDouble(snapshot.child("المجموع").getValue(String.class) + "");
                                               double currenttotal = Double.parseDouble(ordarData.get(finalI).getTotal() + "");
                                               double newtotal = oldtotal + currenttotal;
                                               HashMap<String, Object> hashMap = new HashMap<>();
                                               hashMap.put("السعر", ordarData.get(finalI).getPrice());
                                               hashMap.put("العدد", newcount + "");
                                               if (ordarData.get(finalI).getType().equals("كيلو")) {
                                                   hashMap.put("المجموع", "0.0");
                                               } else {
                                                   hashMap.put("المجموع", newtotal + "");
                                               }
                                               hashMap.put("الضريبه", ordarData.get(finalI).getTax());
                                               hashMap.put("نوع البيع", ordarData.get(finalI).getType());
                                               hashMap.put("سعر الشراء", purchasingprice + "");
                                               reference.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                                   @Override
                                                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                       System.out.println("on complete");
                                                       progressDialog.dismiss();
                                                       adapter.notifyDataSetChanged();
                                                       recyclerView.setAdapter(adapter);
                                                       order.setVisibility(View.GONE);
                                                       sharedPreference.removeallFavorite(ListScreen.this);
                                                       FirebaseDatabase.getInstance().getReference("ordernotifi").child("orderauth")
                                                               .setValue(FirebaseAuth.getInstance().getUid() + new Date() + "");
                                                       System.out.println(datetxt + "datetxt");
                                                       System.out.println(ordarData.get(finalI).getName() + "ordarData.get(finalI).getName()");
                                                       FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                               if (snapshot.exists()) {
                                                                   System.out.println("jard if");
                                                                   Double old_count = Double.parseDouble(snapshot.child("العدد").getValue(String.class));
                                                                   Double new_count = old_count + currentcount;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد").setValue(df.format(new_count) + "");
                                                                   Double old_inventory = Double.parseDouble(snapshot.child("العدد المتاح").getValue(String.class));
                                                                   Double new_inventory = old_inventory - currentcount;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد المتاح").setValue(df.format(new_inventory) + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر الشراء").setValue(purchasingprice);
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر البيع").setValue(ordarData.get(finalI).getPrice());
                                                                   Double total_purchasing_price = Double.parseDouble(purchasingprice) * currentcount;
                                                                   Double purchasing_price_with_tax = (total_purchasing_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_purchasing_price;
                                                                   Double total_selling_price = Double.parseDouble(ordarData.get(finalI).getPrice()) * currentcount;
                                                                   Double selling_price_with_tax = (total_selling_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_selling_price;
                                                                   Double profit = selling_price_with_tax - purchasing_price_with_tax;
                                                                   Double old_profit = Double.parseDouble(snapshot.child("الربح").getValue(String.class));
                                                                   Double new_profit = old_profit + profit;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("الربح").setValue(df.format(new_profit) + "");
                                                                   Double old_total = Double.parseDouble(snapshot.child("المجموع").getValue(String.class));
                                                                   Double new_total = old_total + selling_price_with_tax;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("المجموع").setValue(df.format(new_total) + "");
                                                               } else {
                                                                   System.out.println("jard else");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد").setValue(currentcount + "");
                                                                   Double new_inventory = inventorycount - currentcount;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد المتاح").setValue(new_inventory + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر الشراء").setValue(purchasingprice);
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر البيع").setValue(ordarData.get(finalI).getPrice());
                                                                   Double total_purchasing_price = Double.parseDouble(purchasingprice) * currentcount;
                                                                   Double purchasing_price_with_tax = (total_purchasing_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_purchasing_price;
                                                                   Double total_selling_price = Double.parseDouble(ordarData.get(finalI).getPrice()) * currentcount;
                                                                   Double selling_price_with_tax = (total_selling_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_selling_price;
                                                                   Double profit = selling_price_with_tax - purchasing_price_with_tax;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("الربح").setValue(df.format(profit) + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("المجموع").setValue(df.format(selling_price_with_tax) + "");
                                                               }
                                                           }
                                                           @Override
                                                           public void onCancelled(@NonNull DatabaseError error) {
                                                               System.out.println("mosab");
                                                               System.out.println(error.toString());
                                                           }
                                                       });
                                                       Toast.makeText(ListScreen.this, "تم نحميل الطلبية ", Toast.LENGTH_SHORT).show();
                                                       dialog.dismiss();
                                                   }
                                               });
                                           }
                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {
                                           }
                                       });
                                   } else {
                                       final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("item")
                                               .child(ordarData.get(finalI).getName());
                                       ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                               final String purchasingprice = snapshot1.child("سعر الشراء").getValue(String.class);
                                               final Double inventorycount = Double.parseDouble(snapshot1.child("العدد المتاح").getValue(String.class));
                                               final Double currentcounter = Double.parseDouble(ordarData.get(finalI).getConter());
                                               Double newinventorycount = inventorycount - currentcounter;
                                               ref.child("العدد المتاح").setValue(newinventorycount + "");
                                               HashMap<String, Object> hashMap = new HashMap<>();
                                               hashMap.put("السعر", ordarData.get(finalI).getPrice());
                                               hashMap.put("العدد", ordarData.get(finalI).getConter());
                                               if (ordarData.get(finalI).getType().equals("كيلو")) {
                                                   hashMap.put("المجموع", "0.0");
                                               } else {
                                                   hashMap.put("المجموع", ordarData.get(finalI).getTotal() + "");
                                               }
                                               hashMap.put("الضريبه", ordarData.get(finalI).getTax());
                                               hashMap.put("نوع البيع", ordarData.get(finalI).getType());
                                               hashMap.put("سعر الشراء", purchasingprice + "");
                                               reference.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                                   @Override
                                                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                       System.out.println("on complete");
                                                       progressDialog.dismiss();
                                                       adapter.notifyDataSetChanged();
                                                       recyclerView.setAdapter(adapter);
                                                       order.setVisibility(View.GONE);
                                                       Toast.makeText(ListScreen.this, "تم نحميل الطلبية ", Toast.LENGTH_SHORT).show();
                                                       dialog.dismiss();
                                                       sharedPreference.removeallFavorite(ListScreen.this);
                                                       FirebaseDatabase.getInstance().getReference("ordernotifi").child("orderauth")
                                                               .setValue(FirebaseAuth.getInstance().getUid() + new Date() + "");

                                                       FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                               if (snapshot.exists()) {
                                                                   System.out.println("jard if");
                                                                   Double old_count = Double.parseDouble(snapshot.child("العدد").getValue(String.class));
                                                                   Double new_count = old_count + currentcounter;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد").setValue(df.format(new_count) + "");
                                                                   Double old_inventory = Double.parseDouble(snapshot.child("العدد المتاح").getValue(String.class));
                                                                   Double new_inventory = old_inventory - currentcounter;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد المتاح").setValue(df.format(new_inventory) + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر الشراء").setValue(purchasingprice);
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر البيع").setValue(ordarData.get(finalI).getPrice());
                                                                   Double total_purchasing_price = Double.parseDouble(purchasingprice) * currentcounter;
                                                                   Double purchasing_price_with_tax = (total_purchasing_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_purchasing_price;
                                                                   Double total_selling_price = Double.parseDouble(ordarData.get(finalI).getPrice()) * currentcounter;
                                                                   Double selling_price_with_tax = (total_selling_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_selling_price;
                                                                   Double profit = selling_price_with_tax - purchasing_price_with_tax;
                                                                   Double old_profit = Double.parseDouble(snapshot.child("الربح").getValue(String.class));
                                                                   Double new_profit = old_profit + profit;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("الربح").setValue(df.format(new_profit) + "");
                                                                   Double old_total = Double.parseDouble(snapshot.child("المجموع").getValue(String.class));
                                                                   Double new_total = old_total + selling_price_with_tax;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("المجموع").setValue(df.format(new_total) + "");
                                                               } else {
                                                                   System.out.println("jard else");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد").setValue(currentcounter + "");
                                                                   Double new_inventory = inventorycount - currentcounter;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("العدد المتاح").setValue(new_inventory + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر الشراء").setValue(purchasingprice);
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("سعر البيع").setValue(ordarData.get(finalI).getPrice());
                                                                   Double total_purchasing_price = Double.parseDouble(purchasingprice) * currentcounter;
                                                                   Double purchasing_price_with_tax = (total_purchasing_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_purchasing_price;
                                                                   Double total_selling_price = Double.parseDouble(ordarData.get(finalI).getPrice()) * currentcounter;
                                                                   Double selling_price_with_tax = (total_selling_price * Double.parseDouble(ordarData.get(finalI).getTax())) + total_selling_price;
                                                                   Double profit = selling_price_with_tax - purchasing_price_with_tax;
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("الربح").setValue(df.format(profit) + "");
                                                                   FirebaseDatabase.getInstance().getReference("jard").child(datetxt).child(ordarData.get(finalI).getName()).child("المجموع").setValue(df.format(selling_price_with_tax) + "");
                                                               }
                                                           }
                                                           @Override
                                                           public void onCancelled(@NonNull DatabaseError error) {
                                                               System.out.println("mosab");
                                                               System.out.println(error.toString());
                                                           }
                                                       });
                                                   }
                                               });
                                           }
                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {
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
               Window window = dialog.getWindow();
               window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           }
               else {
                   Toast.makeText(ListScreen.this, "اختر الاصناف اولا", Toast.LENGTH_SHORT).show();
               }
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
                        recyclerView.smoothScrollToPosition(pos);
                    }
                });
            }
        });
            Timer timer =new Timer();
            timer.scheduleAtFixedRate(new ListScreen.TimewTask(),1500,2000);
    }
    public class TimewTask extends java.util.TimerTask {
        @Override
        public void run() {
            ListScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            viewPager.setCurrentItem(1);
                            break;
                        case 1:
                            viewPager.setCurrentItem(0);
                            break;
                    }
                }
            });
        }
    }
    public void getData(){
        dataItems.clear();
        recyclerView.setAdapter(null);
        FirebaseDatabase.getInstance().getReference("item")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                            }catch (NullPointerException ignored){}
                            dataItems.add(new DataItem(s1.getKey(),type,price,img,"",tax));
                            adapter =new Adapter(dataItems,ListScreen.this);
                            recyclerView.setAdapter(adapter);
                        }
                        datanames = new String[strings.size()];
                        datanames = strings.toArray(datanames);
                        ArrayAdapter<String>adapter = new ArrayAdapter<>(ListScreen.this, android.R.layout.simple_dropdown_item_1line, datanames);
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