/*
 * Created on 27.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests.handler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.requests.EventRequest;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.datastores.DataStoreInternal;
import com.alxgrk.eventalarm.util.datastores.DataUpdater;
import com.alxgrk.eventalarm.util.network.NetworkManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFinishedListener;
import com.android.volley.toolbox.Volley;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import static com.alxgrk.eventalarm.util.Constants.*;

public class RequestIntentService extends IntentService {

    // *************************
    // FIXME replace by Google Cloud Messaging
    // (https://developers.google.com/cloud-messaging/)
    // *************************

    private class Handler implements RequestHandler {

        private Context context;

        public Handler(Context context) {
            this.context = context;
        }

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public <T> void addToQueue(@NonNull Request<T> request) {
            NullChecker.checkNull(request);
            NullChecker.checkNull(queue);

            queue.add(request);
            queue.addRequestFinishedListener(new RequestFinishedListener<T>() {
                @Override
                public void onRequestFinished(Request<T> request) {
                    // necessary for not infinitely producing threads
                    queue.stop();
                }
            });
        }

        @Override
        public void setWaitingMessage(int message) {
            // do nothing
        }
    }

    private Context appContext;

    private RequestQueue queue;

    private final Set<String> artists = new TreeSet<>();

    public RequestIntentService() {
        super("RequestIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        queue = Volley.newRequestQueue(appContext);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // FIXME postpone on low memory

        Log.d(BACKGROUND_SERVICE_TAG, "service started");
        DataUpdater dataUpdater = new DataUpdater(getApplication());
        NetworkManager networkManager = new NetworkManager(appContext);

        if (networkManager.hasInternetConnection()) {
            EventMap eventMap;

            if (networkManager.hasWifi()) {
                try {
                    MapHolder mapHolder = requestAll();
                    eventMap = mapHolder.getEventMap();

                    dataUpdater.updateArtists(mapHolder.getArtistMap());
                    dataUpdater.updateImages(mapHolder.getImageMap(), DataStoreInternal.Suffix.SUFFIX_JPG);
                    dataUpdater.updateImages(mapHolder.getBlurredImageMap(),
                            DataStoreInternal.Suffix.SUFFIX_BLURRED_JPG);
                } catch (InterruptedException | ExecutionException e) {
                    eventMap = simpleEventRequest(dataUpdater);
                }
            } else {
                eventMap = simpleEventRequest(dataUpdater);
            }

            dataUpdater.notifyAboutNewEvents(eventMap);
            dataUpdater.updateEvents(eventMap);
        } else {
            // do nothing
        }
    }

    private EventMap simpleEventRequest(DataUpdater dataUpdater) {
        Handler handler = new Handler(appContext);
        ArtistMap artistMap = dataUpdater.getArtistMap();

        Set<String> serializedArtists = artistMap.getMap().keySet();
        artists.addAll(serializedArtists);

        EventRequest request = new EventRequest(handler, artists);
        request.makeRequest();

        EventMap eventMap = request.getResult();
        while (!eventMap.ready()) {
            // wait
        }

        return eventMap;
    }

    private MapHolder requestAll() throws InterruptedException, ExecutionException {
        return new MainRequestHandler(appContext).execute().get();
    }
}
