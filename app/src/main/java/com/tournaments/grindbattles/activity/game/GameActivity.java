package com.tournaments.grindbattles.activity.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.os.Bundle;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.ViewPagerAdapter;
import com.tournaments.grindbattles.common.Toolbox;
import com.tournaments.grindbattles.databinding.ActivityGameBinding;
import com.tournaments.grindbattles.fragment.game.GameContestsFragment;
import com.tournaments.grindbattles.fragment.game.GameResultFragment;
import com.tournaments.grindbattles.model.HtmlGamePojo;
import com.tournaments.grindbattles.model.TopPlayersPojo;

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