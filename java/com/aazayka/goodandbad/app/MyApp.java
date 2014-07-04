package com.aazayka.goodandbad.app;

import android.content.Context;

/**
 * Created by andrey.zaytsev on 06.06.2014.
 */
public class MyApp extends android.app.Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
}