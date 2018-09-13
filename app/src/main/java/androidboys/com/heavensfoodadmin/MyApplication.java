package androidboys.com.heavensfoodadmin;

import android.app.Application;


public class MyApplication extends Application {

    public static MyApplication thisApp;

    @Override
    public void onCreate() {
        super.onCreate();
        thisApp = this;
    }


}
