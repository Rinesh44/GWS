package com.example.android.gurkha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gurkha.R;

import com.example.android.gurkha.SavedPictures;
import com.example.android.gurkha.modal.Album;

import java.util.List;

public class AlbumsAdapter extends Adapter<AlbumsAdapter.MyViewHolder> {
    private List<Album> albumList;
    private Context mContext;

    public class MyViewHolder extends ViewHolder {
        public TextView count;
        public ImageView thumbnail;
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.count = (TextView) view.findViewById(R.id.count);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent showPictures = new Intent(AlbumsAdapter.this.mContext, SavedPictures.class);
                    showPictures.putExtra("directory", ((Album) AlbumsAdapter.this.albumList.get(MyViewHolder.this.getAdapterPosition())).getDirectory());
                    AlbumsAdapter.this.mContext.startActivity(showPictures);
                }
            });
        }
    }

    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album album = (Album) this.albumList.get(position);
        holder.title.setText(album.getName());
        TextView textView = holder.count;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(album.getNumOfImages());
        stringBuilder.append(" images");
        textView.setText(stringBuilder.toString());
        Glide.with(this.mContext).load(Integer.valueOf(album.getThumbnail())).into(holder.thumbnail);
    }

    public int getItemCount() {
        return this.albumList.size();
    }
}