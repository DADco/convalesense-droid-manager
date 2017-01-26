package co.dad.convalesensemanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArmStrengthGameActivity extends GameActivity {

    int nbClouds;

    @BindView(R.id.cloud_container)
    FrameLayout cloudContainer;

    @BindView(R.id.balloon)
    ImageView balloon;

    List<ImageView> clouds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm_strength_game);

        ButterKnife.bind(this);

        nbClouds = getIntent().getIntExtra("repetition", 0);
        ViewTreeObserver vto = cloudContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cloudContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                try {
                    generateClouds();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void generateClouds() {

        for (int i = 0; i < nbClouds; i++) {

            ImageView cloud = new ImageView(this);

            Random r = new Random();
            int min = 1;
            int max = 5;
            int cloudId = r.nextInt(max-min) + min;
            switch (cloudId) {
                case 1:
                    cloud.setImageDrawable(getResources().getDrawable(R.drawable.cloud_1));
                    break;
                case 2:
                    cloud.setImageDrawable(getResources().getDrawable(R.drawable.cloud_2));
                    break;
                case 3:
                    cloud.setImageDrawable(getResources().getDrawable(R.drawable.cloud_3));
                    break;
                case 4:
                    cloud.setImageDrawable(getResources().getDrawable(R.drawable.cloud_4));
                    break;
                case 5:
                    cloud.setImageDrawable(getResources().getDrawable(R.drawable.cloud_5));
                    break;
            }

            Random rMargin = new Random();
            int minMargin = 0;
            int maxMargin = 200;
            int cloudMargin = rMargin.nextInt(maxMargin-minMargin) + minMargin;

            cloud.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));

            cloudContainer.addView(cloud);
            ((FrameLayout.LayoutParams)cloud.getLayoutParams()).topMargin = cloudMargin;

            TranslateAnimation translateAnimation = new TranslateAnimation(0,cloudContainer.getWidth(),cloudMargin,0);
            Random rDuration = new Random();
            int minDuration = 5000;
            int maxDuration = 10000;
            int cloudDuration = rDuration.nextInt(maxDuration-minDuration) + minDuration;

            translateAnimation.setDuration(cloudDuration);
            translateAnimation.setRepeatCount(Animation.INFINITE);
            translateAnimation.setRepeatMode(Animation.REVERSE);

            cloud.startAnimation(translateAnimation);

            clouds.add(cloud);

        }

    }

    @Override
    public void onNewData(int count) {

        if (clouds.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageView cloud = clouds.get(clouds.size() - 1);
                        cloud.getAnimation().cancel();
                        AlphaAnimation disappearAnimation = new AlphaAnimation(1f, 0f);
                        disappearAnimation.setDuration(500);
                        disappearAnimation.setFillAfter(true);
                        cloud.startAnimation(disappearAnimation);
                        clouds.remove(cloud);

                        if (clouds.size() == 0) {
                            balloon.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
