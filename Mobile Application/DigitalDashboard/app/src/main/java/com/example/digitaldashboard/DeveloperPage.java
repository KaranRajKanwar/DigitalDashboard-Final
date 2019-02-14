package com.example.digitaldashboard;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

public class DeveloperPage extends AppCompatActivity {
    MediaPlayer mPlayer;
    private ImageView mIcon;
    int i = 0;

    private ImageButton img1,img2,img3;
    private ViewPager SlideViewPager;
    private LinearLayout DotLayout;
    private TextView[] Dots;
    private SliderAdapter sliderAdapter;
    private ImageButton SlidePic;
    private int CurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer1);

        SlideViewPager = (ViewPager) findViewById(R.id.slide);
        DotLayout = (LinearLayout) findViewById(R.id.dotslayout);
        sliderAdapter = new SliderAdapter(this);
        SlideViewPager.setAdapter(sliderAdapter);
        SlidePic = (ImageButton) findViewById(R.id.slidepic);
        img1 = (ImageButton) findViewById(R.id.slidepic);
        img2 = (ImageButton) findViewById(R.id.slidepic);
        img3 = (ImageButton) findViewById(R.id.slidepic);
        addDotsIndicator(0);

        SlideViewPager.addOnPageChangeListener(viewListener);

    }

    public void addDotsIndicator(int position) {
        Dots = new TextView[3];
        DotLayout.removeAllViews();

        for (int i = 0; i < Dots.length; i++) {
            Dots[i] = new TextView(this);
            Dots[i].setText(Html.fromHtml("&#8226;"));
            Dots[i].setTextSize(35);
            Dots[i].setTextColor(getResources().getColor(R.color.transparentwhite));

            DotLayout.addView(Dots[i]);
        }
        if (Dots.length > 0) {
            Dots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);

            CurrentPage =i;

            if(i == 0){
                ImageView mIcon = (ImageView) findViewById(R.id.slidepic);
                mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.glowup);
                        mPlayer.start();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DeveloperPage.this);
                        View mView = getLayoutInflater().inflate(R.layout.photoviewlayout, null);
                        PhotoView photoView = mView.findViewById(R.id.imageview);
                        photoView.setImageResource(R.drawable.devpagebackground);
                        mBuilder.setView(mView);
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                    }
                });
            } else if (i == Dots.length-1){
                ImageView mIcon = (ImageView) findViewById(R.id.slidepic);
                mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.allstars2017);
                        mPlayer.start();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DeveloperPage.this);
                        View mView = getLayoutInflater().inflate(R.layout.photoviewlayout, null);
                        PhotoView photoView = mView.findViewById(R.id.imageview);
                        photoView.setImageResource(R.drawable.devpagebackground2);
                        mBuilder.setView(mView);
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                    }
                });
            } else {
                ImageView mIcon = (ImageView) findViewById(R.id.slidepic);
                mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.waffen);
                        mPlayer.start();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DeveloperPage.this);
                        View mView = getLayoutInflater().inflate(R.layout.photoviewlayout, null);
                        PhotoView photoView = mView.findViewById(R.id.imageview);
                        photoView.setImageResource(R.drawable.devpagebackground3);
                        mBuilder.setView(mView);
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                    }
                });
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
    @Override
    public void onPause(){
        super.onPause();
        mPlayer.stop();
        Intent intent = new Intent(DeveloperPage.this,SettingsActivity.class);
        startActivity(intent);
    }
}