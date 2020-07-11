package com.example.usermoz3com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usermoz3com.AllBillsForUser;
import com.example.usermoz3com.Data.itmeList;
import com.example.usermoz3com.R;
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
public class AdapterSuper extends RecyclerView.Adapter<AdapterSuper.ViewHolder> {
    Context context;
    ArrayList<List<itmeList>> list;
    AdapterSubList addList;
    SimpleDateFormat simpleDateFormat;
    String datet,id,salary;
    Double   total;
    Double sum ,sum2,taxsum04,x,y,taxsum10,taxsum16,z,i,f;
    ArrayList <String> dates;
    public AdapterSuper(Context context,ArrayList<List<itmeList>> list){
        this.context=context;
        this.list=list;
    }


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardinvoice,parent,false);
        context = parent.getContext();
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RecyclerView itemlistrec = holder.itemlistrec;
        simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        datet =simpleDateFormat.format(new Date());
        final TextView tax = holder.tax;
        itemlistrec.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
        itemlistrec.setLayoutManager(linearLayoutManager);
        itemlistrec.setItemAnimator(new DefaultItemAnimator());
        addList = new AdapterSubList(context,list.get(position));
        holder.date.setText(list.get(position).get(0).getDate());
        final DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        itemlistrec.setAdapter(addList);
        dates = new ArrayList<>();
        taxsum04 = 0.0;
        taxsum10 = 0.0;
        taxsum16 = 0.0;
        sum = 0.0;
        sum2 = 0.0;
        x = 0.0;
        y = 0.0;
        z = 0.0;
        total = 0.0;


             final int finalI = 0;
            FirebaseDatabase.getInstance().getReference("order").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(list.get(position).get(0).getDate()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                            taxsum04 = 0.0;
                            taxsum10 = 0.0;
                            taxsum16 = 0.0;
                            sum = 0.0;
                            sum2 = 0.0;
                            x = 0.0;
                            y = 0.0;
                            z = 0.0;
                            total = 0.0;

                            holder.type.setText(dataSnapshot.child("طريقة الدفع").getValue(String.class));

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (snapshot.getKey().equals("طريقة الدفع")) {

                                } else {
                                    total = Double.parseDouble(snapshot.child("المجموع").getValue(String.class));
                                    Double parseDouble = Double.parseDouble(list.get(position).get(finalI).getTotal() + "");
                                    sum = sum + total;
                                    Double tax4 = Double.parseDouble(snapshot.child("الضريبه").getValue(String.class));
                                    if (tax4 == 0.04) {
                                        taxsum04 = tax4 * total;
                                        y += taxsum04;


                                        tax.setText("ضريبة المبيعات 0.04:" + "\t" + "\t" + df.format(y));
                                    } else {
                                        tax.setText("ضريبة المبيعات 0.04:" + "\t" + "\t" + y);
                                    }

                                    if (tax4 == 0.10) {
                                        taxsum10 = tax4 * total;
                                        x += taxsum10;
                                        holder.tax10.setText("ضريبة المبيعات 0.10:" + "\t" + "\t" + df.format(x));
                                    } else {
                                        holder.tax10.setText("ضريبة المبيعات 0.10:" + "\t" + "\t" + x);
                                    }
                                    if (tax4 == 0.16) {
                                        taxsum16 = tax4 * total;
                                        z += taxsum16;
                                        holder.tax16.setText("ضريبة المبيعات %16:" + "\t" + "\t" + df.format(z));
                                    } else {
                                        holder.tax16.setText("ضريبة المبيعات %16:" + "\t" + "\t" + z);
                                    }
                                    sum2 = y + x + z + sum;
                                    holder.totaloftotal.setText("السعر شامل الضريبة:" + "\t" + "\t" + df.format(sum2));
                                }
                            }


                            FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    f = dataSnapshot.child("رصيد السابق").getValue(Double.class);
                                    holder.name.setText(dataSnapshot.child("name").getValue(String.class));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            if (dataSnapshot.getKey().equals(datet)) {
                            } else {
                                holder.editText.setVisibility(View.GONE);
                                holder.credit.setVisibility(View.GONE);
                                holder.df3a.setVisibility(View.GONE);
                                holder.checkBox.setVisibility(View.GONE);
                                holder.radioGroup.setVisibility(View.GONE);
                            }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        holder.credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.editText.getText().toString().isEmpty()){
                    holder.editText.setError("أدخل قيمة الرصيد السابق");
                }
                else {
                String num =holder.editText.getText().toString();
                 i =Double.parseDouble(num);
                holder.editText.setText("");
                Previousmoney(i);}
            }
        });
        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.cash :
                        typeCash("نقدي");
                        break;
                    case R.id.thmam:
                        Previousmoney(sum);
                        typeCash("ذمم");
                        break;
                }
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true){
                    holder.linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.df3a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (holder.txtdf3a.getText().toString().isEmpty()){
                       holder.txtdf3a.setError("أدخل قيمة الدفعة");
                    }
                    else  {
                        Double c =Double.parseDouble(holder.txtdf3a.getText().toString());
                        c =-c;
                        holder.txtdf3a.setText("");
                        Previousmoney(c);

                    }
            }
        });

    }
    public void Previousmoney(Double x){
            f+=x;
        HashMap<String ,Object>hashMap =new HashMap<>();
        hashMap.put("رصيد السابق",f);
        FirebaseDatabase.getInstance().getReference("user").child(id).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(context, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void typeCash( String type){
      DatabaseReference reference=  FirebaseDatabase.getInstance().getReference("order")
                .child(id).child(datet);
        HashMap<String ,Object>map =new HashMap<>();
        map.put("طريقة الدفع",type);
        reference.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView itemlistrec;
        TextView date , totaloftotal,name,tax,tax10,tax16,type;
        RadioGroup radioGroup;
        EditText editText,txtdf3a;
        Button credit,df3a;
        LinearLayout linearLayout;
        CheckBox checkBox;
        public ViewHolder(@NonNull View i) {
            super(i);
            name =i.findViewById(R.id.nametxt);
            date =i.findViewById(R.id.txtdate);
            totaloftotal =i.findViewById(R.id.total);
            tax =i.findViewById(R.id.tax);
            tax10 =i.findViewById(R.id.tax10);
            tax16 =i.findViewById(R.id.tax16);
            itemlistrec =i.findViewById(R.id.recorder);
            editText =i.findViewById(R.id.e);
            credit =i.findViewById(R.id.save);
            radioGroup =i.findViewById(R.id.radiox);
            type =i.findViewById(R.id.type);
            txtdf3a =i.findViewById(R.id.txtdfa);
            df3a =i.findViewById(R.id.dfa);
            linearLayout =i.findViewById(R.id.liner);
            checkBox =i.findViewById(R.id.dd);
        }
    }
}
