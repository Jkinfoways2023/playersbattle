package com.GlaDius.war.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.model.GamePojo;
import com.GlaDius.war.utils.ExtraOperations;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class SliderHomeAdapter extends PagerAdapter {

    private List<GamePojo> sliderList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SliderHomeAdapter(List<GamePojo> sliderList, Context context) {
        this.sliderList = sliderList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_item_image_slider,null);

        GamePojo slider= sliderList.get(position);

        ImageView gameImage = (ImageView) view.findViewById(R.id.gameImage);
        CardView mainCard = (CardView) view.findViewById(R.id.mainCard);

        if (!slider.getBanner().isEmpty()) {
            gameImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+slider.getBanner().replace(" ", "%20")).placeholder(R.drawable.bg_game_holder).into(gameImage);
        }
        else {
            gameImage.setVisibility(View.GONE);
        }

        mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(context)) {
                    if (slider.getUrl().startsWith("http://") || slider.getUrl().startsWith("https://")  || slider.getUrl().startsWith("www")){
                        context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(slider.getUrl())));
                    }
                }
                else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
