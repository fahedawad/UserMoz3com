package com.example.usermoz3com.Adapter;

import android.content.Context;
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
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<DataItem>dataItems;
    Context context;
    String counter,tax;
    int i;
    Double price,toloa;
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
                    if (counter.isEmpty()) {
                        holder.count.setError("ادخل الغدد المطلوب");
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
                        hashMap.put("tax",tax);
                        hashMap.put("type",dataItems.get(position).getType());
                        sharedPreference.addFavorite(context,hashMap);
                    }

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
