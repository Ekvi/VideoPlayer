package com.ekvilan.videoplayer.view.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ekvilan.videoplayer.R;
import com.ekvilan.videoplayer.models.Video;
import com.ekvilan.videoplayer.view.adapters.VideoAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class MainActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    private final int OFFSET = 30 * 1000;

    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private RelativeLayout topPanel;
    private RelativeLayout bottomPanel;
    private ImageView btnPlay;
    private ImageView btnSound;
    private ImageView btnForward;
    private ImageView btnRewind;
    private ImageView btnPlaylist;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView tvName;

    private boolean isShow = false;
    private boolean isMute = false;


    //String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    //String vidAddress = "http://testapi.qix.sx/video/sky.mp4";
    String vidAddress = "http://testapi.qix.sx/video/mamahohotala.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPanels();
        initVideoHolder();

        addListeners();
    }

    private void initPanels() {
        topPanel = (RelativeLayout) findViewById(R.id.top_panel);
        bottomPanel = (RelativeLayout) findViewById(R.id.bottom_panel);
        btnPlay = (ImageView) findViewById(R.id.play);
        btnSound = (ImageView) findViewById(R.id.sound);
        btnForward = (ImageView) findViewById(R.id.fast_forward);
        btnRewind = (ImageView) findViewById(R.id.fast_rewind);
        btnPlaylist = (ImageView) findViewById(R.id.playlist);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                if(!isShow) {
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
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.play, null);
                    setImage(btnPlay, d);
                } else {
                    mediaPlayer.start();
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null);
                    setImage(btnPlay, d);
                    startTimer();
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMute) {
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.volume_off, null);
                    setImage(btnSound, d);
                    setVolume(0f);
                    isMute = true;
                } else {
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null);
                    setImage(btnSound, d);
                    setVolume(1);
                    isMute = false;
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(OFFSET);
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(-OFFSET);
            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayList();
            }
        });
    }

    private void showPanels() {
        topPanel.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
    }

    private void dismissPanels() {
        topPanel.setVisibility(View.GONE);
        bottomPanel.setVisibility(View.GONE);
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

        updateProgressBar();

        setImage(btnPlay, ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
        setImage(btnSound, ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null));
    }

    private void setImage(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    private void setVolume(float value) {
        mediaPlayer.setVolume(value, value);
    }

    private void updateProgressBar() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
                    }}, 200, 200, TimeUnit.MILLISECONDS);
    }

    private Handler monitorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressBar.setMax(mediaPlayer.getDuration());
            progressBar.setProgress(mediaPlayer.getCurrentPosition() + msg.what);

        }
    };

    private void setOffset(int offset) {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + offset);
        monitorHandler.sendEmptyMessage(offset);
    }

    private void showPlayList() {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.playlist, null);
        PopupWindow playList = new PopupWindow(popupView, WRAP_CONTENT, WRAP_CONTENT);

        initPlayListView(popupView);

        playList.showAsDropDown(btnPlaylist, 0, -450);
        playList.update(btnPlaylist, 0, 0, 400, 250);

        fillPlaylist();
    }

    private void initPlayListView(View popupView) {
        recyclerView = (RecyclerView) popupView.findViewById(R.id.recyclerView);
        tvName = (TextView) popupView.findViewById(R.id.name);
    }

    private void fillPlaylist() {
        List<Video> videos = new ArrayList<>();
        videos.add(new Video("Небо", "http://testapi.qix.sx/video/sky.mp4"));
        videos.add(new Video("Юмор", "http://testapi.qix.sx/video/mamahohotala.mp4"));
        videos.add(new Video("Пустая ссылка 1", ""));
        videos.add(new Video("Пустая ссылка 2", ""));

        recyclerView.setAdapter(new VideoAdapter(this, videos));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
