/*
 * Created on 09.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.util.Log;

public class NullChecker {
    private static class CustomNullException extends NullPointerException {
        private static final long serialVersionUID = 8900045294661376647L;

        public <T> CustomNullException(T checkable) {
            Log.e("NULL_CHECKER", "CustomNullException: " + checkable + " should not be null!");
        }
    }

    public static <T extends Object> void checkNull(T checkable) {
        if (checkable == null)
            throw new CustomNullException(checkable);
    }
}
