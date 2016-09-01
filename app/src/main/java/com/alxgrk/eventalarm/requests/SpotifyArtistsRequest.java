/*
 * Created on 23.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.requests.handler.RequestHandler;
import com.alxgrk.eventalarm.util.ErrorToast;
import com.alxgrk.eventalarm.util.NullChecker;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.alxgrk.eventalarm.util.Constants.*;

public class SpotifyArtistsRequest implements Request {

    public final static String ARTIST_URL = "https://api.spotify.com/v1/me/following?type=artist&limit=5";

    private RequestHandler rh;

    private ArtistMap resultMap;

    private Object lock = new Object();

    private Map<String, String> artistMap;

    private String url;

    public SpotifyArtistsRequest(@NonNull RequestHandler rh, @NonNull Map<String, String> artistMap,
            @NonNull String url) {
        NullChecker.checkNull(rh);
        NullChecker.checkNull(artistMap);
        NullChecker.checkNull(url);

        this.rh = rh;
        this.artistMap = artistMap;
        this.url = url;

        resultMap = new ArtistMap();
        rh.setWaitingMessage(R.string.wait_for_spotify);
    }

    @Override
    public void makeRequest() {
        NullChecker.checkNull(artistMap);
        NullChecker.checkNull(url);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(JSONObject response) {
                Log.d(SPOTIFY_ARTIST_REQUEST_TAG, "response received");
                String nextURL = parseJSONResponseToStringList(response, artistMap);
                if (nextURL != "") {
                    Log.d(SPOTIFY_ARTIST_REQUEST_TAG, "Next URL: " + nextURL);
                    setUrl(nextURL);
                    makeRequest();
                } else {
                    Log.d(SPOTIFY_ARTIST_REQUEST_TAG, artistMap.keySet().toString());
                    setResult(artistMap);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(SPOTIFY_ARTIST_REQUEST_TAG, error.toString());
                ErrorToast.displayError(rh.getContext(), R.string.error_artist_request);
            }
        };

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, null, responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", SPOTIFY_REQUEST_TOKEN);
                return headers;
            }
        };

        rh.addToQueue(jsonObjReq);
    }

    @Override
    public ArtistMap getResult() {
        if (!resultMap.ready()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Log.e(SPOTIFY_ARTIST_REQUEST_TAG, "no answer received...");
                    ErrorToast.displayError(rh.getContext(), R.string.error_general);
                }
            }
        }

        return resultMap;
    }

    private void setResult(Map<String, String> artistMap) {
        resultMap.requestFinished(artistMap);
        synchronized (lock) {
            lock.notify();
        }
    }

    private void setUrl(String url) {
        this.url = url;
    }

    /**
     * Fills all the artists names and the links to their picture into a Map.
     * 
     * @param response
     *            the JSON received from the server
     * @param artistMap
     *            the Map to put the artists names and their corresponding
     *            picture links into
     * @return a String with either the link to the next pagination or an empty
     *         value
     */
    private String parseJSONResponseToStringList(JSONObject response,
            Map<String, String> artistMap) {
        try {
            JSONObject generalInfo = response.getJSONObject("artists");
            String next = !generalInfo.isNull("next") ? ARTIST_URL + "&after=" + generalInfo
                    .getJSONObject("cursors").getString("after") : "";

            JSONArray artistsArray = generalInfo.getJSONArray("items");
            for (int i = 0; i < artistsArray.length(); i++) {
                JSONObject artist = artistsArray.getJSONObject(i);

                JSONArray images = artist.getJSONArray("images");
                // will return the smallest image, because Spotify orders the
                // image links by size descending
                String imageURL = images.getJSONObject(0).getString("url");

                String name = artist.getString("name");

                Log.d(SPOTIFY_ARTIST_REQUEST_TAG, "Name: " + name + ", Bild: " + imageURL);

                artistMap.put(name, imageURL);
            }

            return next;
        } catch (JSONException e) {
            Log.e(SPOTIFY_ARTIST_REQUEST_TAG, e.toString());
            return "";
        }
    }

}
