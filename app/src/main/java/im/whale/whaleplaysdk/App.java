package im.whale.whaleplaysdk;

import android.app.Application;

import com.danikula.videocache.HttpProxyCacheServer;

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

        mHttpProxyCacheServer = new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(4L * 1024 * 1024 * 1024)       // 4 Gb for cache
                .build();
    }

    private HttpProxyCacheServer mHttpProxyCacheServer;

    public HttpProxyCacheServer getHttpProxy() {
        return mHttpProxyCacheServer;
    }

}
