package com.ekvilan.videoplayer.view.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.ekvilan.videoplayer.R;
import com.ekvilan.videoplayer.controllers.VideoController;
import com.ekvilan.videoplayer.view.adapters.VideoAdapter;
import com.ekvilan.videoplayer.view.listeners.RecyclerItemClickListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback {
    private final int OFFSET = 30 * 1000;
    private final int DELAY = 3000;

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
    private PopupWindow playList;
    private RecyclerView recyclerView;

    private Handler handler = new Handler();

    private boolean isShow = false;
    private boolean isMute = false;
    private VideoController videoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoController = new VideoController();

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
                } else {
                    releaseView();
                }
            }
        });

        topPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseView();
            }
        });

        bottomPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseView();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable;
                if(videoController.isPlaying()) {
                    videoController.pause();
                    drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.play, null);
                } else {
                    videoController.play();
                    drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null);
                    startTimer();
                }
                setImage(btnPlay, drawable);
                closePlayList();
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable draw;
                if(!isMute) {
                    draw = ResourcesCompat.getDrawable(getResources(), R.drawable.volume_off, null);
                    setVolume(0f);
                    isMute = true;
                } else {
                    draw = ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null);
                    setVolume(1);
                    isMute = false;
                }
                setImage(btnSound, draw);
                releaseView();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(OFFSET);
                releaseView();
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffset(-OFFSET);
                releaseView();
            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayList();
                handler.removeCallbacks(hide);
                isShow = true;
            }
        });
    }

    private void showPanels() {
        topPanel.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
    }

    private void hidePanels() {
        topPanel.setVisibility(View.GONE);
        bottomPanel.setVisibility(View.GONE);
    }

    private void startTimer() {
        handler.removeCallbacks(hide);
        handler.postDelayed(hide, DELAY);
    }

    private Runnable hide = new Runnable() {
        public void run() {
            if(videoController.isPlaying()) {
                hidePanels();
                isShow = false;
            }
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        videoController.createPlayer(holder);
        updateProgressBar();

        setImage(btnPlay, ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null));
        setImage(btnSound, ResourcesCompat.getDrawable(getResources(), R.drawable.volume_on, null));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }

    private void setImage(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    private void setVolume(float value) {
        videoController.setVolume(value);
    }

    private void updateProgressBar() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        handler.post(progressUpdater);
                    }}, 200, 200, TimeUnit.MILLISECONDS);
    }

    private Runnable progressUpdater = new Runnable() {
          public void run() {
              progressBar.setMax(videoController.getDuration());
              progressBar.setProgress(videoController.getCurrentPosition());
          }
    };

    private void setOffset(int offset) {
        videoController.setOffset(videoController.getCurrentPosition() + offset);
        handler.sendEmptyMessage(videoController.getCurrentPosition() + offset);
    }

    private void showPlayList() {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.playlist, null);

        playList = new PopupWindow(popupView, WRAP_CONTENT, WRAP_CONTENT);
        playList.showAsDropDown(btnPlaylist, 0, -450);
        playList.update(btnPlaylist, 0, 0, 400, 250);

        initPlayListView(popupView);
    }

    private void initPlayListView(View popupView) {
        recyclerView = (RecyclerView) popupView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new VideoAdapter(this, videoController.getVideoList()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                })
        );
    }

    private void closePlayList() {
        if(playList != null) {
            playList.dismiss();
        }
    }

    private void releaseView() {
        closePlayList();
        startTimer();
    }
}

