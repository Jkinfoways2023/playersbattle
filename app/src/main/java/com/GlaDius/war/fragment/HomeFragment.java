package com.GlaDius.war.fragment;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.GlaDius.war.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;

    private Bundle bundle;
    private String strId,strTitle;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        loadBundle();

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        PlayFragment playFragment = new PlayFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        playFragment.setArguments(bundle);

        LiveFragment liveFragment = new LiveFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        liveFragment.setArguments(bundle);

        ResultFragment resultFragment = new ResultFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        resultFragment.setArguments(bundle);

        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragments(playFragment);
        pagerAdapter.addFragments(liveFragment);
        pagerAdapter.addFragments(resultFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            tabLayout.getTabAt(0).setText("UPCOMING");
            tabLayout.getTabAt(1).setText("ONGOING");
            tabLayout.getTabAt(2).setText("RESULTS");
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


    private void loadBundle() {
        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null){
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
        }
    }

}
