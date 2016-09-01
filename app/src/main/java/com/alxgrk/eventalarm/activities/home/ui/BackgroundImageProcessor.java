package com.alxgrk.eventalarm.activities.home.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.util.NullChecker;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.*;

public class BackgroundImageProcessor implements SurfaceHolder.Callback {

    private static final long LASTING_DURATION = 7000;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(IMAGE_PROCESSOR_TAG, "Surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(IMAGE_PROCESSOR_TAG, "Surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(IMAGE_PROCESSOR_TAG, "Surface destroyed");
        stopAnimation();
    }

    private HomeActivity home;

    private SurfaceView view;

    private SurfaceHolder holder;

    private String lastArtist = "";

    private Set<Entry<String, Bitmap>> entrySet;

    private WeakReference<Iterator<Entry<String, Bitmap>>> iteratorRef;

    public BackgroundImageProcessor(@NonNull HomeActivity home, @NonNull SurfaceView view) {
        NullChecker.checkNull(home);
        NullChecker.checkNull(view);

        this.home = home;
        this.view = view;
        this.holder = view.getHolder();

        holder.addCallback(this);
    }

    public String getLastArtist() {
        return lastArtist;
    }

    public void setLastArtist(@NonNull String a) {
        NullChecker.checkNull(a);

        lastArtist = a;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void changeViewOnUiThread(Bitmap photo) {
        final BitmapDrawable background = new BitmapDrawable(home.getResources(), photo);

        home.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(background);
                } else {
                    view.setBackgroundDrawable(background);
                }
                view.startAnimation(bundleAnimations());
            }
        });
    }

    private Animation bundleAnimations() {
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeInAnimation());
        animation.addAnimation(fadeOutAnimation());
        animation.addAnimation(scaleAnimation());
        animation.setRepeatCount(1);

        return animation;
    }

    // TODO put in xml file

    private Animation scaleAnimation() {
        Animation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_PARENT, .5f, Animation.RELATIVE_TO_PARENT, .5f);
        scaleAnimation.setDuration(LASTING_DURATION);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    private Animation fadeInAnimation() {
        int fadeInDuration = 300;

        Animation fadeIn = new AlphaAnimation(.2f, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInDuration);

        return fadeIn;
    }

    private Animation fadeOutAnimation() {
        int fadeOutDuration = 300;

        Animation fadeOut = new AlphaAnimation(1, .2f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(fadeOutDuration);
        fadeOut.setStartOffset(LASTING_DURATION - fadeOutDuration);

        return fadeOut;
    }

    public void startAnimation() {
        if (null == iteratorRef || null == iteratorRef.get()) {
            ImageMap blurredImageMap = home.getMapHolder().getBlurredImageMap();
            entrySet = blurredImageMap.getMap().entrySet();
            iteratorRef = new WeakReference<>(entrySet.iterator());
        }
        countDownTimer.start();
    }

    public void stopAnimation() {
        countDownTimer.cancel();
    }

    CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, LASTING_DURATION) {

        @Override
        public void onFinish() {
            Log.d(IMAGE_PROCESSOR_TAG, "run quit");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished == Long.MAX_VALUE) {
                for (Iterator<Entry<String, Bitmap>> it = entrySet.iterator(); it.hasNext();) {
                    Entry<String, Bitmap> photo = it.next();

                    if (!lastArtist.equals("") && !photo.getKey().equals(lastArtist))
                        continue;

                    apply(photo);
                    return;
                }
            }

            Iterator<Entry<String, Bitmap>> iterator = iteratorRef.get();
            if (iterator != null && iterator.hasNext()) {
                apply(iterator.next());
            } else {
                Iterator<Entry<String, Bitmap>> newIterator = entrySet.iterator();
                iteratorRef = new WeakReference<>(newIterator);
                apply(newIterator.next());
            }
        }

        private void apply(Entry<String, Bitmap> photo) {
            lastArtist = photo.getKey();
            Log.d(IMAGE_PROCESSOR_TAG, "new background is " + lastArtist);
            changeViewOnUiThread(photo.getValue());
        }
    };
}