package im.whale.whaleplaysdk.demo;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import im.whale.whaleplaysdk.App;

/**
 * Created on 2019-11-14.
 *
 * @author ice
 */
public class VideoHelper implements LifecycleObserver {

    private SimpleExoPlayer player;
    private Context mContext;
    private ProgressiveMediaSource.Factory mMediaFactory;
    private boolean mPlayWhenReady;

    public VideoHelper(@Nullable Lifecycle lifecycle, boolean playWhenReady) {
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }

        mContext = App.get();
        mPlayWhenReady = playWhenReady;

        initPlayer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (player != null)
            player.setPlayWhenReady(mPlayWhenReady);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (player != null)
            player.setPlayWhenReady(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
            mMediaFactory = null;
        }
    }

    private void initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(mContext);
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);


        // Produces DataSource instances through which media data is loaded.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(mContext).build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Sprite"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        mMediaFactory = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory);

        player.setPlayWhenReady(mPlayWhenReady);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("xyz", "video error " + error.getMessage());
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.i("xyz", "onPlayerStateChanged " + playWhenReady + " " + playbackState);
            }
        });
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void showVideo(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            Log.e("xyz", "video is empty");
            return;
        }

        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {

        } else {
            videoUrl = "http://" + videoUrl;
        }
        String proxyUrl = App.get().getHttpProxy().getProxyUrl(videoUrl);
        Uri uri = Uri.parse(proxyUrl);

        Log.i("xyz", "proxyUrl " + uri.toString() + " - " + videoUrl);

        MediaSource ms = mMediaFactory.createMediaSource(uri);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare(ms);
    }

}
