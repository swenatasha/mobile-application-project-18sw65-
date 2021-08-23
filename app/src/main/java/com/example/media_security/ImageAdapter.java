package com.example.media_security;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context m_context;
    List<Image> array_list;


    public ImageAdapter(Context context  , List<Image>arraylist )
    {
        m_context=context;
        array_list=arraylist;

    }

    public class ImageViewHolder extends  RecyclerView.ViewHolder{


        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);


        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(m_context).inflate(R.layout.image_items  , parent , false);
        return  new ImageViewHolder(view);

    }



    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image uploadImage = array_list.get(position);
        if (uploadImage.getImageUri() != null) {
            Picasso.with(m_context).load(uploadImage.getImageUri())
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=uploadImage.getUid();
                Intent intent=new Intent(m_context  , ShowImage.class);
               intent.putExtra("Image_Uri" , uploadImage.getImageUri());
               intent.putExtra("Uid" , user);
               intent.putExtra("Image_encrypted"  , uploadImage.getEncryptedUri());
               m_context.startActivity(intent);


            }
        });


    }


    @Override
    public int getItemCount() {
        return array_list.size();
    }
}




