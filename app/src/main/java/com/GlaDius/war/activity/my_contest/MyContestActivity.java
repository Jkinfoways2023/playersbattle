package com.GlaDius.war.activity.my_contest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Toolbox;
import com.GlaDius.war.fragment.CompleteFragment;
import com.GlaDius.war.fragment.OngoingFragment;
import com.GlaDius.war.fragment.UpcomingFragment;

public class MyContestActivity extends AppCompatActivity {

    private Context context;
    private String url, isFrom;
    private TextView title;
    private ImageView backPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contest);
        context = this;

        initView();
        initMetaData();
        initData();
    }

    private void initView() {
        title = findViewById(R.id.title);
        backPress = findViewById(R.id.backPress);
    }

    private void initMetaData() {
        isFrom = getIntent().getStringExtra(Toolbox.IS_FROM);
    }

    private void initData() {
        if (isFrom != null) {
            title.setText(isFrom);
            switch (isFrom) {
                case Toolbox.UPCOMING_MATCH: {
                    title.setText(R.string.upcoming);
                    UpcomingFragment upcomingFragment = new UpcomingFragment();
                    Bundle bundle = new Bundle();
                    //bundle.putString("ID_KEY",strId);
                    bundle.putBoolean(Toolbox.IS_FROM_MY_CONTEST, true);
                    upcomingFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, upcomingFragment).commit();
                }
                break;
                case Toolbox.ON_GOING_MATCH: {
                    title.setText(R.string.ongoing);
                    OngoingFragment ongoingFragment = new OngoingFragment();
                    Bundle bundle = new Bundle();
                    //bundle.putString("ID_KEY",strId);
                    bundle.putBoolean(Toolbox.IS_FROM_MY_CONTEST, true);
                    ongoingFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ongoingFragment).commit();
                }
                break;
                case Toolbox.RESULTS_MATCH: {
                    title.setText(R.string.completed);
                    CompleteFragment completeFragment = new CompleteFragment();
                    Bundle bundle = new Bundle();
                    //bundle.putString("ID_KEY",strId);
                    bundle.putBoolean(Toolbox.IS_FROM_MY_CONTEST, true);
                    completeFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, completeFragment).commit();
                }
                break;
                default:
                    //
                    break;
            }
        }

        backPress.setOnClickListener(v -> onBackPressed());
    }
}