package com.example.usermoz3com.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.usermoz3com.Data.DataItem;
import com.example.usermoz3com.ListScreen;
import com.example.usermoz3com.R;
import com.example.usermoz3com.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<DataItem>dataItems;
    Context context;
    String counter,tax;
    int i;
    Double price,toloa;
    Double entrycount = 0.0;
    SharedPreference sharedPreference =new SharedPreference();
    HashMap<String,Object> hashMap =new HashMap<>();
    public  Adapter( List<DataItem>dataItems,Context context){
        this.dataItems=dataItems;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carditem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(dataItems.get(position).getName());
        holder.type.setText(dataItems.get(position).getType());
        holder.price.setText(dataItems.get(position).getPrice());
        tax =dataItems.get(position).getTax();
        Picasso.get().load(dataItems.get(position).getUri()).resize(1080,1080).into(holder.profile);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter = holder.count.getText().toString();
                    FirebaseDatabase.getInstance().getReference("item").child(dataItems.get(position).getName()).child("العدد المتاح").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                            final Double inventorycount = Double.parseDouble(snapshot.getValue()+"");
                            try {
                               entrycount = Double.parseDouble(counter + "");
                            }
                            catch (Exception e){ }

                            holder.count.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    holder.checkBox.setChecked(false);
                                    Double inventorycount = Double.parseDouble(snapshot.getValue()+"");
                                    try {
                                        Double entrycount = Double.parseDouble(holder.count.getText()+"");
                                        if (entrycount>inventorycount){
                                            holder.count.setError("العدد المدخل اعلى من العدد الحالي في المتجر يرجى ادخال عدد اقل");
                                            holder.checkBox.setChecked(false);
                                            sharedPreference.getFavoritewithname(context,dataItems.get(position).getName());

                                        }
                                        else {
                                            holder.checkBox.setChecked(false);
                                        }
                                    }
                                    catch (Exception e){
                                        if ((dataItems.get(position).getName()+"" )!= null && (!holder.count.getText().toString().isEmpty())) {
                                            sharedPreference.getFavoritewithname(context, dataItems.get(position).getName()+"");
                                        }
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {
                                    Double inventorycount = Double.parseDouble(snapshot.getValue()+"");
                                    try {
                                        Double entrycount = Double.parseDouble(holder.count.getText()+"");
                                        if (entrycount>inventorycount){
                                            holder.count.setError("العدد المدخل اعلى من العدد الحالي في المتجر يرجى ادخال عدد اقل");
                                            holder.checkBox.setChecked(false);
                                            sharedPreference.getFavoritewithname(context,dataItems.get(position).getName());
                                            System.out.println(dataItems.get(position).getName());
                                        }
                                        else {
                                            holder.checkBox.setChecked(false);
//                                            sharedPreference.getFavoritewithname(context, dataItems.get(position).getName());
//                                            holder.checkBox.setChecked(true);
                                        }
                                    }
                                    catch (Exception e){
                                        if ((dataItems.get(position).getName()+"" )!= null && (!holder.count.getText().toString().isEmpty())) {
                                            sharedPreference.getFavoritewithname(context, dataItems.get(position).getName());
                                        }
                                    }
                                }
                            });

                            if (counter.isEmpty()) {
                                holder.count.setError("ادخل الغدد المطلوب");
                                holder.checkBox.toggle();
                            }
                            else if (entrycount>inventorycount){
                                holder.count.setError("العدد المدخل اعلى من العدد الحالي في المتجر يرجى ادخال عدد اقل");
                                holder.checkBox.toggle();
                            }
                            else {
                                i =Integer.parseInt(counter);
                                ListScreen.order.setVisibility(View.VISIBLE);
                                hashMap.put("name",dataItems.get(position).getName());
                                hashMap.put("price",dataItems.get(position).getPrice());
                                hashMap.put("count",counter);
                                try {
                                    price =Double.parseDouble(dataItems.get(position).getPrice());
                                    toloa =price*i;
                                    System.out.println(toloa);
                                }catch (NullPointerException e){}

                                hashMap.put("total",toloa);
                                hashMap.put("tax",dataItems.get(position).getTax());
                                hashMap.put("type",dataItems.get(position).getType());
                                System.out.println(dataItems.get(position).getTax());
                                sharedPreference.addFavorite(context,hashMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                else {
                    sharedPreference.getFavoritewithname(context, dataItems.get(position).getName());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name ,price,type;
        ImageView profile;
        EditText count;
        CheckBox checkBox;
        public ViewHolder(View view){
            super(view);
            name =view.findViewById(R.id.name);
            type =view.findViewById(R.id.type);
            price =view.findViewById(R.id.price);
            profile =view.findViewById(R.id.img);
            count = view.findViewById(R.id.cont);
            checkBox =view.findViewById(R.id.check);
        }
    }
}
