package com.GlaDius.war.adapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();

    public  void addFragments(Fragment fragments, String title)
    {
        this.fragments.add(fragments);
        this.title.add(title);
    }

    public ViewPagerAdapter(FragmentManager fm,int behavior)
    {
        super(fm,behavior);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}