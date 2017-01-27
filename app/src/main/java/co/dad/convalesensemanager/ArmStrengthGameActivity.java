package co.dad.convalesensemanager;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dad.convalesensemanager.model.Result;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArmStrengthGameActivity extends GameActivity {

    int nbClouds;

    @BindView(R.id.cloud_container)
    FrameLayout cloudContainer;

    @BindView(R.id.balloon)
    ImageView balloon;

    @BindView(R.id.instruction)
    TextView instruction;

    @BindView(R.id.success_overlay)
    RelativeLayout successOverlay;

    List<ImageView> clouds = new ArrayList<>();
    Result result = new Result();
    private int execiceId;
    private MediaPlayer m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm_strength_game);

        ButterKnife.bind(this);

        nbClouds = getIntent().getIntExtra("repetition", 0);
        execiceId = getIntent().getIntExtra("exerciceId", 0);

        ViewTreeObserver vto = cloudContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cloudContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                try {
                    generateClouds();

                    result.setStartTime(Calendar.getInstance().getTime());

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
            int maxMargin = 500;
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
            translateAnimation.setFillAfter(true);

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

                        Animation disappearAnimation = new AlphaAnimation(1f, 0f);
                        disappearAnimation.setDuration(500);
                        disappearAnimation.setFillAfter(true);
                        cloud.setAnimation(disappearAnimation);
                        disappearAnimation.start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                playCloudDisappearSound();
                            }
                        }).start();


                        clouds.remove(cloud);

                        if (clouds.size() == 0) {
                            balloon.setVisibility(View.VISIBLE);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    playSuccessSound();
                                }
                            }).start();

                            result.setEndTime(Calendar.getInstance().getTime());
                            result.setCount(nbClouds);
                            result.setExerciceId(execiceId);

                            ConvalesenseAPI.getInstance().getServices().sendScore(result).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    successOverlay.setVisibility(View.VISIBLE);
                                    instruction.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @OnClick(R.id.finish)
    public void onDone() {
        mBTController.disconnect();
        mBTController.release();
        finish();
    }


    public void playCloudDisappearSound() {
        try {
            MediaPlayer m = new MediaPlayer();

            AssetFileDescriptor descriptor = getAssets().openFd("whoosh.wav");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSuccessSound() {
        try {
            MediaPlayer m = new MediaPlayer();

            AssetFileDescriptor descriptor = getAssets().openFd("success.wav");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
