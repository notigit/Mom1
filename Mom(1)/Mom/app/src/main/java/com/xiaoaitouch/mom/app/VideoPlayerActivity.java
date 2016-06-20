package com.xiaoaitouch.mom.app;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频播放
 *
 * @author huxin
 * @data: 2016/1/10 12:10
 * @version: V1.0
 */
public class VideoPlayerActivity extends BaseActivity implements Callback,
        OnPreparedListener, OnCompletionListener {
    @Bind(R.id.video_player_surfaceView)
    SurfaceView surfaceView;
    @Bind(R.id.video_player_iv)
    ImageView mPlayerIv;

    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private int mWeek = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);
        ButterKnife.bind(this);
        loadData();
    }


    protected void loadData() {
        mWeek = getIntent().getIntExtra("week", 1);
        if (mWeek >= 39) {
            mWeek = 39;
        }
        TextView mTextView = new TextView(mActivity);
        mTextView.setText("第" + mWeek + "周");
        mTextView.setTextColor(getResources().getColor(R.color.white));
        mTextView.setTextSize(18f);

        Toast toast = new Toast(mActivity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(mTextView);
        toast.show();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, dm.widthPixels);
        mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(mLayoutParams);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            String url = Configs.IMAGE_URL + "/video/" + mWeek + ".mp4";
            playUrl(url);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
        Log.e("mediaPlayer", "surface created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    @OnClick(R.id.video_player_iv)
    public void VideoPlayer() {
        surfaceView.setBackgroundColor(getResources().getColor(
                R.color.transparent));
        mPlayerIv.setVisibility(View.INVISIBLE);
        play();
    }

    public void play() {
        mediaPlayer.start();
    }

    public void playUrl(String videoUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();// prepare之后自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            arg0.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        surfaceView.setBackgroundColor(Color.parseColor("#b0000000"));
        mPlayerIv.setVisibility(View.VISIBLE);
    }

}
