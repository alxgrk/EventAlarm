package com.alxgrk.eventalarm.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.util.NullChecker;

class UIHelper {

    private HomeActivity home;

    private final int longAnimTime;

	private Typeface iconFont;

    UIHelper(@NonNull HomeActivity home) {
        NullChecker.checkNull(home);

        this.home = home;
        longAnimTime = home.getResources().getInteger(android.R.integer.config_longAnimTime);
		iconFont = Typeface.createFromAsset(home.getAssets(), "fonts/fontawesome-webfont.ttf");
    }

    /**
     * Changes the visibility of ProgressBar and ExpandableListView depending on
     * the loading state.
     * 
     * @param loading
     *            true if data gets retrieved; false otherwise
     */
    void loadsData(final boolean loading) {
        home.progressBar.animate().setDuration(longAnimTime).alpha(loading ? 1 : 0).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
                    }
                });

        home.tvProgress.animate().setDuration(longAnimTime).alpha(loading ? 1 : 0).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.tvProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
                    }
                });

		home.tvArtistNumber.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
                    boolean shouldBeVisible = loading || (View.GONE == (int) home.tvArtistNumber.getTag());
                    home.tvArtistNumber.setVisibility(shouldBeVisible ? View.GONE : View.VISIBLE);
                }
			});
			
		home.tvRefresh.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					home.tvRefresh.setVisibility(loading ? View.GONE : View.VISIBLE);
				}
			});
			
		home.tvLogout.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					home.tvLogout.setVisibility(loading ? View.GONE : View.VISIBLE);
				}
			});

        home.mainView.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.mainView.setVisibility(loading ? View.GONE : View.VISIBLE);
                    }
                });

        home.sideListView.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.sideListView.setVisibility(loading ? View.GONE : View.VISIBLE);
                    }
                });

        home.footerView.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.footerView.setVisibility(loading ? View.GONE : View.VISIBLE);
                    }
                });

        home.backgrView.animate().setDuration(longAnimTime).alpha(loading ? 0 : 1).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        home.backgrView.setVisibility(loading ? View.GONE : View.VISIBLE);
                    }
                });
    }

	public void configureHead()
	{
	    home.tvRefresh.setTypeface(iconFont);
	    home.tvLogout.setTypeface(iconFont);
	
		home.tvRefresh.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					loadsData(true);
					home.startRequests();
				}
		});
		home.tvLogout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					home.logout();
				}
		});
	}
	
    void configureFooter() {
        final Button footerHome = (Button) home.footerView.findViewById(R.id.footer_home);
        final Button footerSettings = (Button) home.footerView.findViewById(
                R.id.footer_settings);
                
                footerHome.setTypeface(iconFont);
		footerSettings.setTypeface(iconFont);
				
        final int white = home.getResources().getColor(R.color.white);
        final int creme = home.getResources().getColor(R.color.creme);

        footerHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                footerHome.setTextColor(white);
                footerSettings.setTextColor(creme);
                hideFragment(home.settingsFragment);
            }
        });

        footerSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                footerSettings.setTextColor(white);
                footerHome.setTextColor(creme);
                showFragment(home.settingsFragment);
            }
        });
    }

    void hideFragment(FrameLayout fragmentContainer) {
        if (fragmentContainer.getVisibility() != View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(home.getApplicationContext(),
                    R.anim.vanish_right_down);
            fragmentContainer.startAnimation(animation);
            fragmentContainer.setVisibility(View.GONE);
        }
    }

    void showFragment(FrameLayout fragmentContainer) {
        if (fragmentContainer.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(home.getApplicationContext(),
                    R.anim.appear_right_down);
            fragmentContainer.startAnimation(animation);
            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }
}