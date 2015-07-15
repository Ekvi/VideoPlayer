package com.ekvilan.videoplayer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


import static android.view.ViewGroup.LayoutParams.*;


public class MainActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private PopupWindow topPanel;
    private PopupWindow bottomPanel;
    private RelativeLayout layout;
    private ImageView btnPlay;
    private ImageView btnSound;

    private boolean isShow = false;
    private boolean isMute = false;

    //String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    //String vidAddress = "http://testapi.qix.sx/video/sky.mp4";
    String vidAddress = "http://testapi.qix.sx/video/mamahohotala.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout)findViewById(R.id.mainLayout);

        initPanels();
        initVideoHolder();

        addListeners();
    }

    private void initPanels() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View top = layoutInflater.inflate(R.layout.top_panel, null);
        View bottom = layoutInflater.inflate(R.layout.bottom_panel, null);

        topPanel = new PopupWindow(top, MATCH_PARENT, WRAP_CONTENT);
        bottomPanel = new PopupWindow(bottom, MATCH_PARENT, WRAP_CONTENT);

        initViewImages(bottom);
    }

    private void initViewImages(View bottom) {
        btnPlay = (ImageView) bottom.findViewById(R.id.play);
        btnSound = (ImageView) bottom.findViewById(R.id.sound);
    }

    private void initVideoHolder() {
        surfaceView = (SurfaceView) findViewById(R.id.surfView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private void addListeners() {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    showPanels();
                    isShow = true;
                    startTimer();
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    setPlayImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.play, null));
                } else {
                    mediaPlayer.start();
                    setPlayImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
                    startTimer();
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMute) {
                    setSoundImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_off, null));
                    setVolume(0f);
                    isMute = true;
                } else {
                    setSoundImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null));
                    setVolume(1);
                    isMute = false;
                }
            }
        });
    }

    private void showPanels() {
        topPanel.showAtLocation(layout, Gravity.TOP, 0, 20);
        bottomPanel.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
    }

    private void dismissPanels() {
        topPanel.dismiss();
        bottomPanel.dismiss();
    }

    private void startTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mediaPlayer.isPlaying()) {
                            dismissPanels();
                            isShow = false;
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(vidAddress);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

        setPlayImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
        setSoundImageView(ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null));
    }

    private void setPlayImageView(Drawable drawable) {
        btnPlay.setImageDrawable(drawable);
    }

    private void setSoundImageView(Drawable drawable) {
        btnSound.setImageDrawable(drawable);
    }

    private void setVolume(float value) {
        mediaPlayer.setVolume(value, value);
    }
}
