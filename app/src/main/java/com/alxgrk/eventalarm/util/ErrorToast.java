/*
 * Created on 09.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.alxgrk.eventalarm.R;

public class ErrorToast {

    private ErrorToast() {
    }

    public static void displayError(@NonNull Context context, @NonNull CharSequence cs) {
        NullChecker.checkNull(context);
        NullChecker.checkNull(cs);

        Log.e("ErrorToast", cs.toString());
        Toast.makeText(context, cs, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, R.string.error_sequel, Toast.LENGTH_SHORT).show();
    }

    public static void displayError(@NonNull Context context, @StringRes int resId) {
        String resAsString = context.getResources().getString(resId);
        displayError(context, resAsString);
    }
}
