/*
 * Created on 06.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util.datastores;

import android.util.Log;

public class RecoveryException extends Exception {
    private static final long serialVersionUID = -184442346357745541L;

    public RecoveryException(String where) {
        Log.e("RecoveryException", "Data not recoverable for " + where);
    }
}