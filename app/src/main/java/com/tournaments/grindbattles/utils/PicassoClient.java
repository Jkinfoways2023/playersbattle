package com.tournaments.grindbattles.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import com.tournaments.grindbattles.R;


/**
 * Created by ashish on 20-02-2017.
 */

public class PicassoClient
{
    public static void downloadImage(Context context, String imageUrl, ImageView imageView)
    {
        if (imageUrl.length()>0 && imageUrl!=null){
           Picasso.get().load(imageUrl).placeholder(R.drawable.ic_logo).into(imageView);
        }
        else
        {
            Picasso.get().load(R.drawable.ic_logo).into(imageView);
        }
    }
}
