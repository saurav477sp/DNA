package com.example.demo_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class loading_page extends AppCompatActivity {

    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        animateDots();
    }

    private void animateDots() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(dot1, "translationY", 0f, -50f);
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());

        // Create animation for dot2
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(dot2, "translationY", 0f, -50f);
        animator2.setStartDelay(250);
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setRepeatMode(ObjectAnimator.REVERSE);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(dot3, "translationY", 0f, -50f);
        animator3.setRepeatCount(ObjectAnimator.INFINITE);
        animator3.setRepeatMode(ObjectAnimator.REVERSE);
        animator3.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator1).with(animator2).with(animator3);

        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(loading_page.this, doctor_or_patient.class);
                startActivity(intent);
                finish();
            }
        }, 5000);

    }
}
