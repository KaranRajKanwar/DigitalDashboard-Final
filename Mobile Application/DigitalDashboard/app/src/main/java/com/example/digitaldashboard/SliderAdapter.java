package com.example.digitaldashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewGroupCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_image = {
            R.drawable.devpagebackground,
            R.drawable.devpagebackground2,
            R.drawable.devpagebackground3,
    };

    public int[] slide_heading = {
            R.drawable.navimg,
            R.drawable.bonesimg,
            R.drawable.rafimg
    };
    //public int[] slide_headings={
    //  R.drawable.navimg,
    //  R.drawable.bonesimg,
    //  R.drawable.rafimg
    //};
    @Override
    public int getCount() {
        return slide_image.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view ==(RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeslide);
        ImageView slide_headings = (ImageView) view.findViewById(R.id.slidename);
        ImageButton slide_images = (ImageButton) view.findViewById(R.id.slidepic);
        slide_images.setImageResource(slide_image[position]);
        slide_headings.setImageResource(slide_heading[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container,int position, Object object){
        container.removeView((RelativeLayout)object);
    }
}