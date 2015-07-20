package com.ekvilan.videoplayer.controllers;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import com.ekvilan.videoplayer.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoController implements MediaPlayer.OnPreparedListener {
    private List<Video> videos = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    public VideoController() {
        setUpVideo();
    }

    private void setUpVideo() {
        videos.add(new Video("Небо", "http://testapi.qix.sx/video/sky.mp4"));
        videos.add(new Video("Юмор", "http://testapi.qix.sx/video/mamahohotala.mp4"));
        videos.add(new Video("Пустая ссылка 1", "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"));
        videos.add(new Video("Пустая ссылка 2", ""));
    }

    public void createPlayer(SurfaceHolder surfaceHolder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(videos.get(1).getUrl());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void setVolume(float value) {
        mediaPlayer.setVolume(value, value);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void setOffset(int value) {
        mediaPlayer.seekTo(value);
    }

    public List<Video> getVideoList() {
        return videos;
    }
}
