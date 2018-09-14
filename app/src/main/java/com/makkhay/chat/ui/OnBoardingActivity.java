package com.makkhay.chat.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makkhay.chat.R;
import com.makkhay.chat.util.MyPreferences;
import com.makkhay.chat.util.SampleSlide;

/**
 * First onboarding screen.
 */
public class OnBoardingActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.boarding_one));
        addSlide(SampleSlide.newInstance(R.layout.boarding_two));
        addSlide(SampleSlide.newInstance(R.layout.boarding_three));

        boolean isFirstTime = MyPreferences.isFirst(OnBoardingActivity.this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(!isFirstTime && user!= null){
            Intent i = new Intent(this, ChatActivity.class);
            startActivity(i);
        }


        // Override bar/separator color.
        setBarColor(Color.parseColor("#11998e"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);


    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }



}
