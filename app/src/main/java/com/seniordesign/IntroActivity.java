package com.seniordesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position;
    Button btnGetStarted;
    Animation btnAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // check for first time launch boolean
        if(restorePrefData()){
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }


        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();




        // ini views
        btnNext = findViewById(R.id.intro_next_btn);
        btnGetStarted = findViewById(R.id.button_getstarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);



        // setup viewpager
        final List<IntroScreenItem> mList = new ArrayList<>();
        mList.add(new IntroScreenItem("Hello from ISPY!", "This app is awesome", R.drawable.ispy_logo));
        mList.add(new IntroScreenItem("Detection at its best!", "Ispy is probably the best android app out there", R.drawable.intro_img1));
        mList.add(new IntroScreenItem("Get Started!", "Capture an image for detection and find the nearest location to buy the item", R.drawable.intro_img2));

        screenPager = findViewById(R.id.introscreen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // next button click listener
        btnNext.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                position = screenPager.getCurrentItem();
                if(position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                    loadPrevScreen();
                }
                if(position == mList.size() -1){
                    // when we reach last intro screen
                    // TODO: show the GET STARTED Button and hide the indicator and next button
                    loadLastScreen();

                }

            }

        });

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size() -1){
                    loadLastScreen();
                } else{
                    loadPrevScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // getStarted button click listener
        btnGetStarted.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                // open main activity
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage
                // to restrict intro screen for first time app use
                // use shared preferences for that
                savePrefsData();
                finish();

            }
        });
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean firstTime = pref.getBoolean("isIntroOpened", false);
        return firstTime;
    }
    // save preference data
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }
    // show the GET STARTED Button and hide the indicator and next button
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        // TODO: add animation
        btnGetStarted.setAnimation(btnAnim);
    }
    private void loadPrevScreen() {
        btnNext.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);

    }

}
