package com.AflamTvMaroc.moviesdarija.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.util.IntentKeys;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Log;

public class VideoPlayerActivity extends BaseActivity {

    private static final String TAG = "VideoPlayerActivity";
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_player);

    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    private void init() {

        String videoUrl = getIntent().getStringExtra(IntentKeys.INTENT_VIDEO_URL);
        Log.d(TAG, videoUrl);
//        VideoView video_view = findViewById(R.id.video_view);
//        View progress_bar = findViewById(R.id.progress_bar);
//        video_view.setVideoURI(Uri.parse(videoUrl));
//        video_view.start();
//        video_view.setOnErrorListener((mediaPlayer, i, i1) ->
//
//                {
//                    progress_bar.setVisibility(View.GONE);
//                    Log.d(TAG, "OnError");
//                    Intent intent = new Intent(VideoPlayerActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra(IntentKeys.INTENT_VIDEO_ERROR, true);
//                    startActivity(intent);
//                    return true;
//                }
//        );
//
//        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                progress_bar.setVisibility(View.GONE);
//                mediaPlayer.start();
//            }
//        });


        // Create a data source factory.
//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(this, getString(R.string.app_name)),
//                null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
//                true);


        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUrl));

        player = new ExoPlayer.Builder(this).build();
        player.setPlayWhenReady(true);
        player.setMediaSource(hlsMediaSource);
        player.prepare();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "onPlayerError ", error);
                Intent intent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(IntentKeys.INTENT_VIDEO_ERROR, true);
                startActivity(intent);
            }
        });

        StyledPlayerView playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    private void releasePlayer() {
        if (player != null) {
            // save the player state before releasing its resources
            player.release();
            player = null;
        }
    }
}


