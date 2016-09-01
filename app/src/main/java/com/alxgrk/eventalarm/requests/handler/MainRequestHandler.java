/*
 * Created on 24.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.requests.ArtistImageRequest;
import com.alxgrk.eventalarm.requests.EventRequest;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.requests.SpotifyArtistsRequest;
import com.alxgrk.eventalarm.util.NullChecker;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.Set;

public class MainRequestHandler extends AsyncTask<Void, Integer, MapHolder> implements
        RequestHandler {

    private Context context;

    private RequestQueue queue;

    private MapHolder mapHolder;

    public MainRequestHandler(@NonNull Context context) {
        NullChecker.checkNull(context);

        this.context = context;
        mapHolder = new MapHolder();
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    @Override
    public Context getContext() {
        return context.getApplicationContext();
    }

    @Override
    protected MapHolder doInBackground(Void... params) {
        startRequests();

        while (!mapHolder.ready()) {
            // wait
        }

        mapHolder.generateBlurredImages(context.getApplicationContext());
        queue.stop(); // necessary for not infinitely producing threads
        return mapHolder;
    }

    @Override
    protected void onProgressUpdate(Integer... resources) {
        int resource = resources[0].intValue();

        if (context instanceof HomeActivity) {
            HomeActivity home = (HomeActivity) context;
            home.setWaitingMessage(home.getResources().getString(resource));
        }
    }

    @Override
    protected void onPostExecute(MapHolder result) {
        if (context instanceof HomeActivity) {
            HomeActivity home = ((HomeActivity) context);
            home.setMapHolder(mapHolder);
            home.checkAllRequestFinished();
        }
    }

    private void startRequests() {
        ArtistMap artistMap = startArtistRequest();

        EventMap eventMap = startEventRequest(artistMap.keySet());

        ImageMap imageMap = startImageRequest(artistMap.getMap());

        mapHolder = new MapHolder(artistMap, eventMap, imageMap);
    }

    private ArtistMap startArtistRequest() {
        SpotifyArtistsRequest artistsRequest = new SpotifyArtistsRequest(this, mapHolder
                .getArtistMap().getMap(), SpotifyArtistsRequest.ARTIST_URL);
        artistsRequest.makeRequest();

        return artistsRequest.getResult();
    }

    private EventMap startEventRequest(Set<String> artists) {
        EventRequest eventRequest = new EventRequest(this, artists);
        eventRequest.makeRequest();

        return eventRequest.getResult();
    }

    private ImageMap startImageRequest(Map<String, String> links) {
        ArtistImageRequest imageRequest = new ArtistImageRequest(this, context.getResources(),
                links);
        imageRequest.makeRequest();

        return imageRequest.getResult();
    }

    @Override
    public <T> void addToQueue(@NonNull Request<T> request) {
        NullChecker.checkNull(request);

        queue.add(request);
    }

    @Override
    public void setWaitingMessage(int message) {
        publishProgress(Integer.valueOf(message));
    }
}
