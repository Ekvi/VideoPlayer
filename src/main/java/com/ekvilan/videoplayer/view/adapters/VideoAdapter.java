package com.ekvilan.videoplayer.view.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekvilan.videoplayer.R;
import com.ekvilan.videoplayer.models.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<Video> videos;

    public VideoAdapter(Context context, List<Video> videos) {
        inflater = LayoutInflater.from(context);
        this.videos = videos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(inflater.inflate(R.layout.playlist_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof VideoViewHolder) {
            VideoViewHolder videoHolder = (VideoViewHolder) viewHolder;
            videoHolder.name.setText(videos.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public VideoViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
