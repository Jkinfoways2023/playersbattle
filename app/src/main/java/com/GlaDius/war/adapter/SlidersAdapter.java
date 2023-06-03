package com.GlaDius.war.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.model.SliderPojo;
import com.GlaDius.war.views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SlidersAdapter extends PagerAdapter {

    private List<SliderPojo> galleryList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SlidersAdapter(List<SliderPojo> galleryList, Context context) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return galleryList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_full_slider,null);

        final SliderPojo gallery = galleryList.get(position);
        TouchImageView imageView = view.findViewById(R.id.iv_details);
        ProgressBar progressBar = view.findViewById(R.id.pb_details);

        Picasso.get().load(Config.FILE_PATH_URL+gallery.getProd_img().replace(" ", "%20")).into(imageView);

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
