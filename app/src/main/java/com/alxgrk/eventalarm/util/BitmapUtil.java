/*
 * Created on 24.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Matrix3f;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicColorMatrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.*;

public class BitmapUtil {

    public final static Bitmap EMPTY_BITMAP = Bitmap.createBitmap(1, 1, Config.ALPHA_8);

    private final static int DEFAULT_IMAGE_EDGE_LENGTH = 150;

    private int imageEdgeLength;

    public BitmapUtil() {
        imageEdgeLength = DEFAULT_IMAGE_EDGE_LENGTH;
    }

    public BitmapUtil(int imageEdgeLength) {
        this.imageEdgeLength = imageEdgeLength;
    }

    public final Bitmap scaleBitmap(@NonNull Bitmap bitmap) {
        NullChecker.checkNull(bitmap);

        double ratio = bitmap.getHeight() / bitmap.getWidth();
        return (ratio == 0 || ratio == 1) ? bitmap
                : Bitmap.createScaledBitmap(bitmap, imageEdgeLength, (int) (imageEdgeLength
                        * ratio), false);
    }

    public final @NonNull
    ImageMap createScaledBlurredShadedVersionOf(@NonNull ImageMap imageMap,
                                                @NonNull Context c) {
        NullChecker.checkNull(imageMap);
        NullChecker.checkNull(c);

        ImageMap blurredImageMap = new ImageMap();
        Map<String, Bitmap> map = blurredImageMap.getMap();

        for (Map.Entry<String, Bitmap> entry : imageMap.getMap().entrySet()) {
            Bitmap bmpToBlur = entry.getValue();
            Bitmap copiedBmp = cropQuadraticBitmap(bmpToBlur);
            blurBitmap(copiedBmp, c);
            shadeBitmap(copiedBmp, c);
            map.put(entry.getKey(), copiedBmp);
        }

        blurredImageMap.setMap(map);
        blurredImageMap.setReady(true);

        Log.d(IMAGE_PROCESSOR_TAG, "generated blurred imageMap");
        return blurredImageMap;
    }

    public Bitmap cropQuadraticBitmap(@NonNull Bitmap bmpToCrop) {
        int bmpWidth = bmpToCrop.getWidth();
        int bmpHeight = bmpToCrop.getHeight();
        int diff = bmpWidth - bmpHeight;
        int absDiff = Math.abs(diff / 2);

        Bitmap copiedBmp;
        if (diff > 0) {
            // means that width is larger than height
            copiedBmp = Bitmap.createBitmap(bmpToCrop, absDiff, 0, bmpWidth - absDiff, bmpHeight);
        } else if (diff < 0) {
            // means that height is larger than width
            copiedBmp = Bitmap.createBitmap(bmpToCrop, 0, absDiff, bmpWidth, bmpHeight - absDiff);
        } else {
            // simply copy bitmap
            copiedBmp = Bitmap.createScaledBitmap(bmpToCrop, bmpWidth - 5, bmpHeight - 5, false);
        }

        // TODO make it fit to the screen size

        return copiedBmp;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public final void blurBitmap(@NonNull Bitmap bmpToBlur, @NonNull Context c) {
        NullChecker.checkNull(bmpToBlur);
        NullChecker.checkNull(c);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final RenderScript rs = RenderScript.create(c);
            final Allocation input = Allocation.createFromBitmap(rs, bmpToBlur,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            script.setRadius(10);
            script.setInput(input);
            script.forEach(output);

            output.copyTo(bmpToBlur);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public final void shadeBitmap(@NonNull Bitmap bmpToShade, @NonNull Context c) {
        NullChecker.checkNull(bmpToShade);
        NullChecker.checkNull(c);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final RenderScript rs = RenderScript.create(c);
            final Allocation input = Allocation.createFromBitmap(rs, bmpToShade,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());

            ScriptIntrinsicColorMatrix script = ScriptIntrinsicColorMatrix.create(rs);
            script.setColorMatrix(new Matrix3f(new float[] { 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f,
                    0.2f, 0.2f, 0.2f }));
            script.forEach(input, output);

            output.copyTo(bmpToShade);
        }
    }

    public Bitmap loadSampledBitmapFrom(Resources res, int resId, int reqWidth, int reqHeight) {
        Options bounds = loadBoundsOf(res, resId);
        Options sampledOptions = sampledOptions(bounds, reqWidth, reqHeight);
        return BitmapFactory.decodeResource(res, resId, sampledOptions);
    }

    public Bitmap loadSampledBitmapFrom(File file, int reqWidth, int reqHeight) {
        Options bounds = loadBoundsOf(file);
        Options sampledOptions = sampledOptions(bounds, reqWidth, reqHeight);
        return BitmapFactory.decodeFile(file.getAbsolutePath(), sampledOptions);
    }

    private BitmapFactory.Options sampledOptions(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return options;
    }

    private BitmapFactory.Options loadBoundsOf(Resources res, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        return options;
    }

    private BitmapFactory.Options loadBoundsOf(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        return options;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth
                    / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public final @NonNull Map<String, Bitmap> fillDefault(@NonNull Set<String> artists,
            @NonNull Resources resources) {
        NullChecker.checkNull(artists);
        NullChecker.checkNull(resources);

        Map<String, Bitmap> imageMap = new HashMap<>();

        for (String artist : artists) {
            fillDefault(artist, imageMap, resources);
        }

        return imageMap;
    }

    public void fillDefault(@NonNull String artist, @NonNull Map<String, Bitmap> imageMap,
            @NonNull Resources resources) {
        NullChecker.checkNull(artist);
        NullChecker.checkNull(imageMap);
        NullChecker.checkNull(resources);

        // TODO change required size
        Bitmap b = loadSampledBitmapFrom(resources, R.drawable.no_picture, 200, 200);
        imageMap.put(artist, scaleBitmap(b));
    }
}
