/*
 * Created on 03.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.info.BandInfo;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.MainViewAdapter;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.SideListAdapter;
import com.meetme.android.horizontallistview.HorizontalListView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.LIST_CONFIGURATOR_TAG;

final class ListConfigurator {

	private int numberOfArtistsWithEvents = 0;
	
	private int currentPage = 1;
	
    private HomeActivity home;

    private MapHolder mapHolder;

    private MainViewAdapter mainViewAdapter;

    private SideListAdapter sideListAdapter;

    public ListConfigurator(@NonNull HomeActivity home, @NonNull MapHolder mapHolder) {
        NullChecker.checkNull(home);
        NullChecker.checkNull(mapHolder);

        this.home = home;
        this.mapHolder = mapHolder;
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
            home.tvArtistNumber.setVisibility(View.GONE);
            listDataMain.add(bandInfo);
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
                Toast.makeText(home, bandName, Toast.LENGTH_SHORT).show();
                // TODO show little flags upon the image instead
            }
        });
    }
}