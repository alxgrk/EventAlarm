/*
 * Created on 03.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.info.BandInfo;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.MainViewAdapter;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.SideListAdapter;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.meetme.android.horizontallistview.HorizontalListView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.LIST_CONFIGURATOR_TAG;

final class ListConfigurator {

	private int numberOfArtistsWithEvents = 0;
	
	private int currentPage = 1;

    private int shortAnimTime;

    private int displayWidth;

    private HomeActivity home;

    private MapHolder mapHolder;

    private MainViewAdapter mainViewAdapter;

    private SideListAdapter sideListAdapter;

    public ListConfigurator(@NonNull HomeActivity home, @NonNull MapHolder mapHolder) {
        NullChecker.checkNull(home);
        NullChecker.checkNull(mapHolder);

        this.home = home;
        this.mapHolder = mapHolder;

        shortAnimTime = home.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        displayWidth = getDisplayWidth();
    }

    private int getDisplayWidth() {
        WindowManager wm = (WindowManager) home.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p.x;
    }

    void setCurrentPage(int position)
	{
		currentPage = position + 1;
	}
	
	int getCurrentPage(){
		return currentPage;
	}
	
	int getNumberOfArtistsWithEvents() {
		return numberOfArtistsWithEvents;
	}

    private void prepareListData() {
        List<BandInfo> listDataMain = new LinkedList<>();
        List<BandInfo> listBandsWithoutEvents = new LinkedList<>();
        // TreeMap<String, List<InfoObject>> listDataChild = new TreeMap<>();

        Set<String> artists = mapHolder.getArtistMap().keySet();
        EventMap eventMap = mapHolder.getEventMap();
        Log.d(LIST_CONFIGURATOR_TAG, "Events: " + eventMap);
        if (!eventMap.getMap().isEmpty()) {
            for (String artist : artists) {
                BandInfo bandInfo = new BandInfo(artist, mapHolder.getImageMap().get(artist));
                if (eventMap.get(artist).isEmpty()) {
                    listBandsWithoutEvents.add(bandInfo);
                } else {
                    listDataMain.add(bandInfo);
                }
            }
        }
        handleEmptiness(listDataMain);
		
		numberOfArtistsWithEvents = listDataMain.size();

        Collections.sort(listDataMain);
        Collections.sort(listBandsWithoutEvents);

        mainViewAdapter = new MainViewAdapter(home, listDataMain);
        sideListAdapter = new SideListAdapter(home, listBandsWithoutEvents);
    }

    private void handleEmptiness(List<BandInfo> listDataMain) {
        if(listDataMain.isEmpty()) {
            String noEventsString = home.getResources().getString(R.string.no_events);
            BandInfo bandInfo = new BandInfo(noEventsString,
                    new BitmapUtil().noBandsBitmap(home.getResources()));
            listDataMain.add(bandInfo);
            home.tvArtistNumber.setTag(View.GONE);
        }
    }

    void updateMapHolder(@NonNull MapHolder mapHolder) {
        this.mapHolder = mapHolder;
    }

    void setUpListViews(@NonNull ViewPager mainView, @NonNull HorizontalListView sideListView) {
        NullChecker.checkNull(mainView);
        NullChecker.checkNull(sideListView);
        prepareListData();

        mainView.setAdapter(mainViewAdapter);
        mainView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					setCurrentPage(position);
					home.updateTvArtistNumber(position, getNumberOfArtistsWithEvents());
				}
			});
		mainView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view)
				{
					home.getUiHelper().showFragment(home.bandFragment);
				}
		});

        sideListView.setAdapter(sideListAdapter);
        sideListView.setDivider(null);
        sideListView.setDividerWidth(10);
        sideListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bandName = ((BandInfo) sideListAdapter.getItem(position)).getBandName();
                home.bandBubble.setText(bandName);
                animateBubble(view, home.bandBubble, true);
            }
        });
    }

    private void animateBubble(final View clickedItem, final BubbleTextView bandBubble, final boolean show) {
        bandBubble.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        final View.OnLayoutChangeListener listener = bindBubbleToView(bandBubble, clickedItem);
                        // bandBubble.setX(clickedItem.getLeft());
                        bandBubble.setArrowTo(clickedItem);
                        bandBubble.setVisibility(show ? View.VISIBLE : View.GONE);
                        if (show) {
                            int appearanceTime = 2000;
                            CountDownTimer cdt = new CountDownTimer(appearanceTime, appearanceTime) {

                                @Override
                                public void onTick(long millisUntilFinished) {}

                                @Override
                                public void onFinish() {
                                    animateBubble(clickedItem, bandBubble, false);
                                    unbindBubbleFromView(clickedItem, bandBubble, listener);
                                }
                            };
                            cdt.start();
                        }
                    }
                });
    }

    private View.OnLayoutChangeListener bindBubbleToView(final BubbleTextView bandBubble, final View viewToBind) {
        View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(LIST_CONFIGURATOR_TAG, "oldLeft: " +  oldLeft + ", newLeft: " + left); // TODO let flag disappear if view not visible
                if ((left > 0 && right > 0 && oldLeft > left)
                        || (left < displayWidth && right < displayWidth && oldRight < right)) {
                    bandBubble.setX(viewToBind.getX());
                }
                Log.d(LIST_CONFIGURATOR_TAG, "oldRight: " + oldRight);
                if ((oldRight < 0 && oldLeft > left) || (oldLeft > displayWidth && oldRight < right)) {
                    unbindBubbleFromView(viewToBind, bandBubble, this);
                }
            }
        };
        viewToBind.addOnLayoutChangeListener(listener);
        return listener;
    }

    private void unbindBubbleFromView(View boundedView, BubbleTextView bandBubble, View.OnLayoutChangeListener listener) {
        boundedView.removeOnLayoutChangeListener(listener);
    }
}