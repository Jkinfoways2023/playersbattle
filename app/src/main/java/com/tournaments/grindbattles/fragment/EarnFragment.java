package com.tournaments.grindbattles.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.LotteryActivity;
import com.tournaments.grindbattles.activity.MainActivity;
import com.tournaments.grindbattles.activity.ProductActivity;
import com.tournaments.grindbattles.activity.RefereEarnActivity;
import com.tournaments.grindbattles.activity.RewardEarnActivity;

public class EarnFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isNavigationHide = false;

    private LinearLayout referandearncard;
    private LinearLayout watchandearncard;
    private LinearLayout playandearncard;
    private LinearLayout shopcard;
    private LinearLayout lotterycard;

    public EarnFragment() {
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
        view = inflater.inflate(R.layout.fragment_earn, container, false);

        referandearncard = view.findViewById(R.id.referandearncard);
        watchandearncard = view.findViewById(R.id.watchandearncard);
        playandearncard = view.findViewById(R.id.playandearncard);
        shopcard = view.findViewById(R.id.shopcard);
        lotterycard = view.findViewById(R.id.lotterycard);

        referandearncard.setOnClickListener(this);
        watchandearncard.setOnClickListener(this);
        playandearncard.setOnClickListener(this);
        shopcard.setOnClickListener(this);
        lotterycard.setOnClickListener(this);

        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                }
            }
        });

        return view;
    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.referandearncard){
            this.startActivity(new Intent(this.getActivity(), RefereEarnActivity.class));
        }
        else if (v.getId()==R.id.watchandearncard){
            this.startActivity(new Intent(this.getActivity(), RewardEarnActivity.class));
        }
        else if (v.getId()==R.id.playandearncard){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new GameFragment()).commit();
        }
        else if (v.getId()==R.id.shopcard){
            this.startActivity(new Intent(this.getActivity(), ProductActivity.class));
        }
        else if (v.getId()==R.id.lotterycard){
            this.startActivity(new Intent(this.getActivity(), LotteryActivity.class));
        }
    }

}
