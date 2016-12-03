package com.sam_chordas.android.stockhawk;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by iyadkuwatly on 12/3/16.
 */

public class StockHawk extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);

    }
}
