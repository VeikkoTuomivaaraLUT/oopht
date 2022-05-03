package OOP.oopht;

import android.app.Application;
import android.content.Context;

// for easy access to context
public class MyApp extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}