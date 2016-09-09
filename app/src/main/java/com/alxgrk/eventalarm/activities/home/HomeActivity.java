package com.alxgrk.eventalarm.activities.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.LauncherActivity;
import com.alxgrk.eventalarm.activities.home.storing.PersistentStorage;
import com.alxgrk.eventalarm.activities.home.ui.BackgroundImageProcessor;
import com.alxgrk.eventalarm.activities.settings.SettingsFragment;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.requests.handler.MainRequestHandler;
import com.alxgrk.eventalarm.requests.handler.RequestScheduler;
import com.alxgrk.eventalarm.util.EventNotificator;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.datastores.StorageHelper;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.spotify.sdk.android.authentication.AuthenticationClient;

import static com.alxgrk.eventalarm.util.Constants.*;

public class HomeActivity extends Activity {

    private static final String SURFACE_VIEW_ARTIST = "surfaceViewArtist";

    // ----------- VIEWS -----------------

    ProgressBar progressBar;

    TextView tvProgress;

    FrameLayout settingsFragment;

    FrameLayout bandFragment;

    TextView tvArtistNumber;

	TextView tvRefresh;

	TextView tvLogout;

    ViewPager mainView;

    BubbleTextView bandBubble;

    HorizontalListView sideListView;

    LinearLayout footerView;

    SurfaceView backgrView;

    // ----------- DATA ------------------

    private MapHolder mapHolder;

    // ----------- HELPERS ---------------

    private StorageHelper storageHelper;

    private PersistentStorage persistentStorage;

    private ListConfigurator listConfig;

    private UIHelper uiHelper;

    private EventNotificator notificator;

    private RequestScheduler scheduler;

    private BackgroundImageProcessor imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TODO put loading into fragment

        progressBar = (ProgressBar) findViewById(R.id.progBar);
        tvProgress = (TextView) findViewById(R.id.tv_progress);

        settingsFragment = (FrameLayout) findViewById(R.id.settings_fragment);
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment,
                new SettingsFragment()).commit();
        bandFragment = (FrameLayout) findViewById(R.id.band_fragment);
        getFragmentManager().beginTransaction().replace(R.id.band_fragment,
                new EventDetailsFragment()).commit();

        tvArtistNumber = (TextView) findViewById(R.id.tv_artist_number);
		tvRefresh = (TextView) findViewById(R.id.tv_refresh);
		tvLogout = (TextView) findViewById(R.id.tv_logout);
		
        mainView = (ViewPager) findViewById(R.id.mainView);
        sideListView = (HorizontalListView) findViewById(R.id.sideLv);
        footerView = (LinearLayout) findViewById(R.id.footer);

        bandBubble = (BubbleTextView) findViewById(R.id.bandBubble);

        backgrView = (SurfaceView) findViewById(R.id.backgrView);
        backgrView.setBackgroundColor(Color.GRAY); // TODO default bg image

        recreateHelpers(savedInstanceState);
    }

    private void recreateHelpers(Bundle b) {
        if (null != b && b.containsKey("mapHolder")) {
            mapHolder = b.getParcelable("mapHolder");
        } else {
            mapHolder = new MapHolder();
        }
        Log.d(HOME_TAG, mapHolder.toString());

        storageHelper = StorageHelper.getInstance(getApplication());
        persistentStorage = (persistentStorage == null) ? new PersistentStorage(this, storageHelper)
                : persistentStorage;

        listConfig = (listConfig == null) ? new ListConfigurator(this, mapHolder) : listConfig;
        uiHelper = (uiHelper == null) ? new UIHelper(this) : uiHelper;
        uiHelper.configureFooter();
		uiHelper.configureHead();

        imageProcessor = (imageProcessor == null) ? new BackgroundImageProcessor(this, backgrView)
                : imageProcessor;
        if (null != b && b.containsKey(SURFACE_VIEW_ARTIST)) {
            imageProcessor.setLastArtist(b.getString(SURFACE_VIEW_ARTIST));
        } else {
            imageProcessor.setLastArtist(storageHelper.getString(SURFACE_VIEW_ARTIST));
        }

        notificator = (notificator == null) ? new EventNotificator(getApplication()) : notificator;
        scheduler = (scheduler == null) ? new RequestScheduler(getApplicationContext()) : scheduler;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(HOME_TAG, "starting..");
        recover();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mapHolder", mapHolder);
        outState.putString(SURFACE_VIEW_ARTIST, imageProcessor.getLastArtist());

        StringBuilder sb = new StringBuilder();
        sb.append("SavedInstanceState (").append(outState.describeContents()).append("): ");
        for (String key : outState.keySet()) {
            sb.append(key).append("|").append(outState.get(key)).append(" ");
        }
        Log.d(HOME_TAG, sb.toString());

        super.onSaveInstanceState(outState);
    }

    private void recover() {
        if (!mapHolder.ready())
            recoverFromStorageHelper();
        else
            displayActivity();
    }

    private void recoverFromStorageHelper() {
        Log.d(HOME_TAG, "using storage");
        if (persistentStorage.recoverPersistentData())
            displayActivity();
        else
            startRequests();
    }

    void startRequests() {
        mapHolder = new MapHolder();

        MainRequestHandler requestManager = new MainRequestHandler(this);
        requestManager.execute();

        // try {
        // mapHolder = requestManager.get();
        // } catch (InterruptedException | ExecutionException e) {
        // Log.e(HOME_TAG, "could not retrieve maps: " + e.toString());
        // ErrorToast.displayError(this, "No data.. retry later.");
        // logout();
        // }
    }

    void logout() {
        AuthenticationClient.clearCookies(this);
        Log.d(HOME_TAG, "loging out..");
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    public final void checkAllRequestFinished() {

        if (mapHolder.ready()) {
            Log.d(HOME_TAG, "all requests finished");
            requestsFinished();
        }
    }

    private void requestsFinished() {
        notificator.notificate(mapHolder); // FIXME remove

        scheduler.scheduleRequestService();

        persistentStorage.savePersistentData();

        displayActivity();
    }

    private void displayActivity() {
        listConfig.updateMapHolder(mapHolder);
        listConfig.setUpListViews(mainView, sideListView);

        int currentPage = listConfig.getCurrentPage();
        tvArtistNumber.setText(currentPage + "/" + listConfig.getNumberOfArtistsWithEvents());
        mainView.setCurrentItem(currentPage - 1);

        imageProcessor.startAnimation();

        uiHelper.loadsData(false);
    }

    public @NonNull MapHolder getMapHolder() {
        return (mapHolder != null) ? mapHolder : new MapHolder();
    }

    public void setMapHolder(@NonNull MapHolder mapHolder) {
        NullChecker.checkNull(mapHolder);

        this.mapHolder = mapHolder;
    }
    
    public UIHelper getUiHelper() {
        return uiHelper;
    }

    public final void setWaitingMessage(@NonNull String message) {
        NullChecker.checkNull(message);

        tvProgress.setText(message);
    }

	public void updateTvArtistNumber(int position, int numberOfArtistsWithEvents)
	{
		tvArtistNumber.setText((position + 1) + "/" + numberOfArtistsWithEvents);
	}

    @Override
    protected void onStop() {
        super.onStop();

        storageHelper.putString(SURFACE_VIEW_ARTIST, imageProcessor.getLastArtist());

        backgrView.clearAnimation();
        imageProcessor.stopAnimation();
        Log.d(HOME_TAG, "stopped..");
    }
}