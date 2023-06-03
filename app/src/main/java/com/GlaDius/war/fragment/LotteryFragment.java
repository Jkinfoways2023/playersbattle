package com.GlaDius.war.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.GlaDius.war.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class LotteryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private String isFrom;
    private LinearLayout mainLayout;

    public LotteryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFrom = getArguments().getString("isFrom");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lottery, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        viewPager.setOffscreenPageLimit(2);

        /*if (isFrom!=null && isFrom.equals("MainActivity")){
            mainLayout.setBackgroundResource(R.drawable.bg_top_right_corner_25);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
            tabLayout.setTabTextColors(ContextCompat.getColor(getContext(),R.color.colorAccent),ContextCompat.getColor(getContext(),R.color.colorAccent));
        }*/

        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragments(new OngoingLotteryFragment());
        pagerAdapter.addFragments(new CompletedLotteryFragment());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            tabLayout.getTabAt(0).setText("ONGOING");
            tabLayout.getTabAt(1).setText("RESULTS");
        }

        return view;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();

        public  void addFragments(Fragment fragments)
        {
            this.fragments.add(fragments);
        }

        public ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }
}