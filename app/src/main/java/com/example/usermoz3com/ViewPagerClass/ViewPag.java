package com.example.usermoz3com.ViewPagerClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.example.usermoz3com.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPag extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<Pager> pagers;
    public ViewPag(Context context, List<Pager> pagers){
        this.context=context;
        this.pagers=pagers;
    }
    @Override
    public int getCount() {
        return pagers.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.pagerlayout,container,false);
        ImageView imageView =(ImageView) view.findViewById(R.id.pagerimg);

        Picasso.get().load(pagers.get(position).getUri()).resize(1080,1080).into(imageView);
        container.addView(view,0);
        return  view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object) ;
    }
}
