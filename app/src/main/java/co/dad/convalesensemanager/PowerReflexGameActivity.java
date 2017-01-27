package co.dad.convalesensemanager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

public class PowerReflexGameActivity extends GameActivity {

    int nbClouds;

    @BindView(R.id.balloon_container)
    FrameLayout balloonContainer;

    @BindView(R.id.instruction)
    TextView instruction;

    @BindView(R.id.success_overlay)
    RelativeLayout successOverlay;

    List<ImageView> balloons = new ArrayList<>();
    Result result = new Result();
    private int execiceId;
    private MediaPlayer m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_reflex_game);

        ButterKnife.bind(this);

        nbClouds = getIntent().getIntExtra("repetition", 0);
        execiceId = getIntent().getIntExtra("exerciceId", 0);

        ViewTreeObserver vto = balloonContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                balloonContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                try {
                    generateBalloons();

                    result.setStartTime(Calendar.getInstance().getTime());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void generateBalloons() {

        for (int i = 0; i < nbClouds; i++) {

            ImageView balloon = new ImageView(this);
            balloon.setImageDrawable(getResources().getDrawable(R.drawable.balloon_icon));

            Random rMargin = new Random();
            int minMargin = 0;
            int maxMargin = balloonContainer.getHeight() - balloon.getDrawable().getIntrinsicHeight();
            int cloudMarginTop = rMargin.nextInt(maxMargin-minMargin) + minMargin;

            minMargin = 0;
            maxMargin = balloonContainer.getWidth() - balloon.getDrawable().getIntrinsicWidth();
            int cloudMarginLeft = rMargin.nextInt(maxMargin-minMargin) + minMargin;

            balloon.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));

            balloonContainer.addView(balloon);
            ((FrameLayout.LayoutParams)balloon.getLayoutParams()).topMargin = cloudMarginTop;
            ((FrameLayout.LayoutParams)balloon.getLayoutParams()).leftMargin = cloudMarginLeft;

            TranslateAnimation translateAnimation = new TranslateAnimation(0,0,-50,50);
            Random rDuration = new Random();
            int minDuration = 500;
            int maxDuration = 1000;
            int cloudDuration = rDuration.nextInt(maxDuration-minDuration) + minDuration;

            translateAnimation.setDuration(cloudDuration);
            translateAnimation.setRepeatCount(Animation.INFINITE);
            translateAnimation.setRepeatMode(Animation.REVERSE);
            translateAnimation.setFillAfter(true);

            balloon.startAnimation(translateAnimation);

            balloons.add(balloon);

        }

    }

    @Override
    public void onNewData(int count) {

        if (balloons.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageView balloon = balloons.get(balloons.size() - 1);

                        balloon.setImageDrawable(getResources().getDrawable(R.drawable.balloon_boom_icon));

                        Animation disappearAnimation = new AlphaAnimation(1f, 0f);
                        disappearAnimation.setDuration(500);
                        disappearAnimation.setFillAfter(true);
                        balloon.setAnimation(disappearAnimation);
                        disappearAnimation.start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                playCloudDisappearSound();
                            }
                        }).start();


                        balloons.remove(balloon);

                        if (balloons.size() == 0) {

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

            AssetFileDescriptor descriptor = getAssets().openFd("balloon_pop.wav");
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
