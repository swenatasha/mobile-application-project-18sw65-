package com.example.media_security;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {


    private Context m_context;
    List<Video> array_list;
    VideoView videoView;
    public VideoAdapter(Context context  , List<Video>arraylist   ) {
        m_context = context;
        array_list = arraylist;
    }


    public class VideoViewHolder extends  RecyclerView.ViewHolder{


        public VideoViewHolder(View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.exoplayer);


        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(m_context).inflate(R.layout.video_items  , parent , false);
        return  new VideoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video=array_list.get(position);

        Log.i("uri of video " , video.getVideoUri());
        videoView.setVideoURI(Uri.parse(video.getVideoUri()));
        MediaController mediaController=new MediaController(m_context);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.requestFocus();
        videoView.setZOrderOnTop(true);
        videoView.start();

//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                    @Override
//                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                        if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
//                            videoView.setBackgroundColor(Color.CYAN);
//                        }
//                        return true;
//                    }
//                });
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return array_list.size();
    }
}




