package com.teamc2tech.penneru;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.teamc2tech.gstartv.R;

public class FullScreenVideoActivity extends AppCompatActivity {
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;

    private int mResumeWindow;
    private long mResumePosition;
    private ProgressBar progressBar;
  //   private static String selectedUrl = "http://209.58.180.138/gstarhls/live.m3u8";
  // private static String selectedUrl = "http://hbs.livebox.co.in/sandhyatvhls/live.m3u8";

    private static String selectedUrl = "http://tenneru.livebox.co.in/penneruhls/live.m3u8";


    private final String TAG = FullScreenVideoActivity.class.getCanonicalName();
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        frameLayout = (FrameLayout) findViewById(R.id.main_media_frame);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (mExoPlayerView == null) {
            mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
            initFullscreenButton();
            String userAgent = Util.getUserAgent(FullScreenVideoActivity.this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(FullScreenVideoActivity.this, null, httpDataSourceFactory);
            Uri daUri = Uri.parse(selectedUrl);
            mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
        }
        initExoPlayer();
        hideStatusBar();
        frameLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    private void initFullscreenButton() {
        PlaybackControlView controlView = (PlaybackControlView) mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = (ImageView) controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = (FrameLayout) controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setVisibility(View.GONE);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Log.e(TAG, "onClick ");
                int orientation = FullScreenVideoActivity.this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.e(TAG, "onClick Portrait ");
                    changeOrientation();
                } else {
                    Log.e(TAG, "onClick Landscape ");
                    changeOrientation();
                }*/
            }
        });
    }

    /*private void changeOrientation() {
        if (!mExoPlayerFullscreen) {
            Log.e(TAG, "onClick fullscreen false");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            hideStatusBar();
            frameLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mExoPlayerFullscreen = true;
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_expand_less));
        } else {
            Log.e(TAG, "onClick fullscreen true");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            showStatusBar();
            frameLayout.getLayoutParams().height = dpToPx(300);


            mExoPlayerFullscreen = false;
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_expand_more));
        }

        if (Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 2000);
        }
    }
*/
    private void initExoPlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setPlayer(player);


        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
        });

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        mExoPlayerView.getPlayer().prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause ");
        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
            mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());
            mExoPlayerView.getPlayer().release();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
      /*  Log.e(TAG, "onConfigurationChanged ");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mExoPlayerFullscreen) {
                mExoPlayerFullscreen = false;
                //   getSupportActionBar().show();
                showStatusBar();
                frameLayout.getLayoutParams().height = dpToPx(300);
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_expand_more));
            }
        } else {
            if (!mExoPlayerFullscreen) {
                mExoPlayerFullscreen = true;
                //  getSupportActionBar().hide();
                hideStatusBar();
                frameLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_expand_less));
            }
        }
*/

    }

    @Override
    public void onBackPressed() {
        if (mExoPlayerFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            showStatusBar();
            frameLayout.getLayoutParams().height = dpToPx(300);
            mExoPlayerFullscreen = false;
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_expand_more));
            if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }, 2000);
            }
        } else {
            finish();
        }
    }

    void hideStatusBar() {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mExoPlayerView.setSystemUiVisibility(
                View. SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    void showStatusBar() {
     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mExoPlayerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public  int dpToPx(int dp) {
        float density =getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
