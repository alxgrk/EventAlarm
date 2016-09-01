/*
 * Created on 25.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.info.EventDetails;
import com.alxgrk.eventalarm.info.InfoObject;
import com.alxgrk.eventalarm.requests.handler.RequestHandler;
import com.alxgrk.eventalarm.util.ErrorToast;
import com.alxgrk.eventalarm.util.NullChecker;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.*;

public class EventRequest implements Request {

    private final static String PROCESSING_URL = "http://eventsforspotify.lima-city.de/process.php";

    private RequestHandler rh;

    private EventMap resultMap = new EventMap();

    private Set<String> artists;

    public EventRequest(@NonNull RequestHandler rh, @NonNull Set<String> artists) {
        NullChecker.checkNull(rh);
        NullChecker.checkNull(artists);

        this.rh = rh;
        this.artists = artists;
    }

    @Override
    public void makeRequest() {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(EVENT_REQUEST_TAG, "response received");
                setResult(parseJSONResponseToEventList(response, artists));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(EVENT_REQUEST_TAG, error.toString());
                ErrorToast.displayError(rh.getContext(), R.string.error_event_request);
            }
        };

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(PROCESSING_URL, createJSONBody(
                artists), responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("X-App-ID", APP_ID);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rh.addToQueue(jsonObjReq);
    }

    @Override
    public EventMap getResult() {
        return resultMap;
    }

    private void setResult(Map<String, List<InfoObject>> eventMap) {
        resultMap.requestFinished(eventMap);
    }

    private JSONObject createJSONBody(Set<String> artists) {
        JSONObject body = new JSONObject();

        try {
            // TODO get location from settings
            body.put("location", "Leipzig");

            JSONArray artistsArray = new JSONArray();
            for (String artist : artists) {
                artistsArray.put(compatibleArtistString(artist));
            }

            body.put("artists", artistsArray);
        } catch (JSONException e) {
            Log.e(EVENT_REQUEST_TAG, "artists not parseable");
            ErrorToast.displayError(rh.getContext(), R.string.error_general);
        }

        Log.d(EVENT_REQUEST_TAG, body.toString());
        return body;
    }

    private String compatibleArtistString(String artist) {
        return artist.replace(" ", "-");
    }

    /**
     * Fills all the events into the eventMap.
     * 
     * @param response
     *            the JSON received from the server
     * @param artists
     * @return
     */
    private Map<String, List<InfoObject>> parseJSONResponseToEventList(JSONObject response,
            Set<String> artists) {
        Map<String, List<InfoObject>> eventMap = new HashMap<>();

        for (String artist : artists) {
            ArrayList<InfoObject> eventList = new ArrayList<>();
            try {
                if (!response.isNull(compatibleArtistString(artist))) {
                    JSONArray events = response.getJSONArray(compatibleArtistString(artist));
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        EventDetails eventDetails = new EventDetails( //
                                event.getString("Date"), //
                                event.getString("EventGroupName"), //
                                event.getString("VenueName"), //
                                event.getString("Town"), //
                                event.getInt("TicketCount"), //
                                getPrice(event), //
                                event.getString("SwURL"));
                        Log.d(EVENT_REQUEST_TAG, eventDetails.toString());
                        eventList.add(eventDetails);
                    }
                }
            } catch (JSONException e) {
                Log.e(EVENT_REQUEST_TAG, e.toString());
            }
            eventMap.put(artist, eventList);
        }

        return eventMap;
    }

    private String getPrice(JSONObject event) throws JSONException {
        return event.getDouble("MinPrice") + event.getString("Currency");
    }
}