package com.example.weathernews.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weathernews.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 5000;
    Animation horizontalAnim, topAnim;
    TextView appInfo;
    LottieAnimationView lottieAnimationView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        horizontalAnim = AnimationUtils.loadAnimation(this, R.anim.horizontal_anim);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        appInfo = findViewById(R.id.appInfo);

        lottieAnimationView.setAnimation(topAnim);
        appInfo.setAnimation(horizontalAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);

    }

}