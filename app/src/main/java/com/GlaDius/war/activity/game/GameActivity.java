package com.GlaDius.war.activity.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.os.Bundle;

import com.GlaDius.war.R;
import com.GlaDius.war.adapter.ViewPagerAdapter;
import com.GlaDius.war.common.Toolbox;
import com.GlaDius.war.databinding.ActivityGameBinding;
import com.GlaDius.war.fragment.game.GameContestsFragment;
import com.GlaDius.war.fragment.game.GameResultFragment;
import com.GlaDius.war.model.HtmlGamePojo;
import com.GlaDius.war.model.TopPlayersPojo;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    private Context context;
    private ArrayList<TopPlayersPojo> arrayList=new ArrayList();
    private ViewPagerAdapter viewPagerAdapter;
    private HtmlGamePojo model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;

        model=getIntent().getParcelableExtra(Toolbox.MODEL);

        initData();
    }

    private void initData() {
        binding.backPress.setOnClickListener(v->onBackPressed());

        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.title.setText(model.getTitle());
        binding.gameImage.setImageResource(model.getImage());

        setAdapter();
    }

    private void setAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFragments(GameContestsFragment.newInstance(model),getResources().getString(R.string.contests));
        viewPagerAdapter.addFragments(GameResultFragment.newInstance(model), getResources().getString(R.string.result));
        binding.viewPager.setAdapter(viewPagerAdapter);
    }
}