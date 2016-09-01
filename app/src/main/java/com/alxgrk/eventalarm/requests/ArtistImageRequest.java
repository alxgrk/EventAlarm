/*
 * Created on 25.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView.ScaleType;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.requests.handler.RequestHandler;
import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.NullChecker;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.IMAGE_REQUEST_TAG;

public class ArtistImageRequest implements Request {

    private Map<String, String> links;

    private ImageMap resultMap = new ImageMap();

    private final RequestHandler rh;

    private final BitmapUtil imageScaler;

    private Resources resources;

    public ArtistImageRequest(@NonNull RequestHandler rh, @NonNull Resources resources,
            @NonNull Map<String, String> links) {
        NullChecker.checkNull(rh);
        NullChecker.checkNull(resources);
        NullChecker.checkNull(links);

        this.rh = rh;
        this.resources = resources;
        this.links = links;

        imageScaler = new BitmapUtil();
    }

    @Override
    public void makeRequest() {
        rh.setWaitingMessage(R.string.wait_for_images);
        final Set<String> artists = links.keySet();

        Map<String, Bitmap> imageMap = imageScaler.fillDefault(artists, resources);

        for (String artist : artists) {
            String url = links.get(artist);
            ImageRequest req = singleRequest(artist, imageMap, url);
            rh.addToQueue(req);
        }
    }

    @Override
    public ImageMap getResult() {
        return resultMap;
    }

    private void setResult(Map<String, Bitmap> imageMap) {
        resultMap.requestFinished(imageMap);
    }

    private ImageRequest singleRequest(final String artist, final Map<String, Bitmap> imageMap,
            final String url) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                imageMap.put(artist, imageScaler.scaleBitmap(bitmap));
                Log.d(IMAGE_REQUEST_TAG, "image received for " + artist);
                if (imageMap.size() == links.keySet().size()) {
                    setResult(imageMap);
                    rh.setWaitingMessage(R.string.wait_for_events);
                }
            }
        }, 0, 0, ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(IMAGE_REQUEST_TAG, error.toString());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(IMAGE_REQUEST_TAG, "receiving image for " + artist);
        return request;
    }
}