package im.whale.whaleplaysdk;

import android.app.Application;

/**
 * Created on 2019-08-31.
 *
 * @author ice
 */
public class App extends Application {

    private static App sApp;

    public static App get() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

}
