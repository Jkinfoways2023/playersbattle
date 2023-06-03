package com.GlaDius.war.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.GlaDius.war.R;
import com.GlaDius.war.fragment.CompleteFragment;
import com.GlaDius.war.fragment.OngoingFragment;
import com.GlaDius.war.fragment.UpcomingFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMatchesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;

    private Bundle bundle;
    private String strId,strTitle,strURL;

    public MyMatchesActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches);

        initBundel();
        initToolbar();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //viewPager.setOffscreenPageLimit(3);

        OngoingFragment ongoingFragment = new OngoingFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        ongoingFragment.setArguments(bundle);

        UpcomingFragment upcomingFragment = new UpcomingFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        upcomingFragment.setArguments(bundle);

        CompleteFragment completeFragment = new CompleteFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        completeFragment.setArguments(bundle);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(ongoingFragment);
        pagerAdapter.addFragments(upcomingFragment);
        pagerAdapter.addFragments(completeFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            tabLayout.getTabAt(0).setText("ONGOING");
            tabLayout.getTabAt(1).setText("UPCOMING");
            tabLayout.getTabAt(2).setText("Completed");
        }

    }

    private void initBundel() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
            strURL = bundle.getString("URL_KEY");
        }
    }

    private void initToolbar() {
        ImageView backPress = (ImageView) findViewById(R.id.backPress);
       backPress.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
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
