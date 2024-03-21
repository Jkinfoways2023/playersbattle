package com.tournaments.grindbattles.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.ContactUsActivity;
import com.tournaments.grindbattles.activity.MainActivity;
import com.tournaments.grindbattles.activity.MyProfileActivity;
import com.tournaments.grindbattles.activity.MyStatisticsActivity;
import com.tournaments.grindbattles.activity.MyWalletActivity;
import com.tournaments.grindbattles.activity.NotificationActivity;
import com.tournaments.grindbattles.activity.SettingActivity;
import com.tournaments.grindbattles.activity.TopPlayersActivity;
import com.tournaments.grindbattles.activity.game.GameActivity;
import com.tournaments.grindbattles.activity.my_contest.MyContestActivity;
import com.tournaments.grindbattles.adapter.GameAdapter;
import com.tournaments.grindbattles.adapter.SliderHomeAdapter;
import com.tournaments.grindbattles.adapter.game.HtmlGameAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.common.Toolbox;
import com.tournaments.grindbattles.model.GamePojo;
import com.tournaments.grindbattles.model.HtmlGamePojo;
import com.tournaments.grindbattles.utils.AutoScrollHelper;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isNavigationHide = false;

    public SharedPreferences preferences;
    public String prefName = "Sky_Winner";
    public int count = 0;

    private RecyclerView recyclerView, recyclerViewHtmlGame;
    private LinearLayout gameLinearLayout;
    private RecyclerView.Adapter gameAdapter;
    private HtmlGameAdapter htmlGameAdapter;

    private ArrayList<GamePojo> gamePojoList;
    private ArrayList<HtmlGamePojo> htmlGamePojoList;
    private RequestQueue gameRequestQueue;
    private JsonArrayRequest gameJsonArrayRequest;

    private ShimmerFrameLayout shimmer_view_container;
    private NestedScrollView nestedScrollView;
    private TextView announceText;
    private LinearLayout noMatchesLL, noHtmlGameLL;
    private LinearLayout upcomingLL;
    private CardView notificationCard;

    private Timer timer;
    private int page = 0;
    private ViewPager sliderViewPager;
    private SliderHomeAdapter sliderPagerAdapter;
    private RelativeLayout sliderLayout;
    private List<GamePojo> sliderList;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout bannerDots;

    private LinearLayout customerSupport;
    private LinearLayout importantUpdates;
    private LinearLayout myProfile;
    private LinearLayout myStatistics;
    private LinearLayout myWallet;
    private LinearLayout topPlayers;
    private LinearLayout settingsCard;

    private CardView upCommingCard;
    private CardView upOngoingCard;
    private CardView completedCard;
    private RelativeLayout mainLayout;
    private Context context;
    private int page1 = 0;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;

    public GameFragment() {
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
        view = inflater.inflate(R.layout.fragment_game, container, false);
        context = getActivity();

        initView();
        initListener();

        timer = new Timer();
        gamePojoList = new ArrayList<>();
        htmlGamePojoList = new ArrayList<>();
        sliderList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));

        setGameAdapter();

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadAnnouncements();
            loadGames();
            loadHtmlGames();
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

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

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounter();

                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cvWhatsapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                            "https://t.me/GRINDBATTLE"
                    ));
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Telegram have not been installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void setGameAdapter() {
        recyclerViewHtmlGame.setHasFixedSize(true);
        recyclerViewHtmlGame.setNestedScrollingEnabled(false);
        recyclerViewHtmlGame.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initListener() {
        this.myProfile.setOnClickListener(this);
        this.myWallet.setOnClickListener(this);
        this.myStatistics.setOnClickListener(this);
        this.topPlayers.setOnClickListener(this);
        this.importantUpdates.setOnClickListener(this);
        this.customerSupport.setOnClickListener(this);
        this.settingsCard.setOnClickListener(this);

        this.upCommingCard.setOnClickListener(this);
        this.upOngoingCard.setOnClickListener(this);
        this.completedCard.setOnClickListener(this);
    }


    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void loadAnnouncements() {
        Uri.Builder builder = Uri.parse(Constant.ANNOUNCEMENT_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final JSONArray jsonArray = response.getJSONArray("Result");
                    announceText.setText(jsonArray.getJSONObject(0).getString("title"));
                    if (jsonArray.length() == 1) {
                        String announcement = jsonArray.getJSONObject(0).getString("title");
                        announceText.setText(announcement);
                    } else if (jsonArray.length() == 2) {
                        try {
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page) {
                                        page = 0;
                                    } else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else if (jsonArray.length() == 3) {
                        try {
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page) {
                                        page = 0;
                                    } else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }

                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else if (jsonArray.length() == 4) {
                        try {
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page) {
                                        page = 0;
                                    } else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 3) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(3).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else if (jsonArray.length() == 5) {
                        try {
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page) {
                                        page = 0;
                                    } else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 3) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(3).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 4) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(4).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    error.printStackTrace();
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setShouldCache(false);
        MySingleton.getInstance(getActivity()).addToRequestque(jsonObjectRequest);
    }

    private void initView() {
        this.shimmer_view_container = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScroll);
        this.announceText = (TextView) view.findViewById(R.id.announceText);
        this.noMatchesLL = (LinearLayout) view.findViewById(R.id.noMatchesLL);
        this.noHtmlGameLL = (LinearLayout) view.findViewById(R.id.noHtmlGameLL);
        this.upcomingLL = (LinearLayout) view.findViewById(R.id.upcomingLL);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.recyclerViewHtmlGame = (RecyclerView) view.findViewById(R.id.recyclerViewHtmlGame);
        this.notificationCard = (CardView) view.findViewById(R.id.notificationCard);
        this.sliderViewPager = (ViewPager) view.findViewById(R.id.sliderViewPager);
        this.bannerDots = (LinearLayout) view.findViewById(R.id.bannerDots);
        this.sliderLayout = (RelativeLayout) view.findViewById(R.id.sliderLayout);

        this.myProfile = (LinearLayout) view.findViewById(R.id.profileCard);
        this.myWallet = (LinearLayout) view.findViewById(R.id.myWalletCard);
        this.myStatistics = (LinearLayout) view.findViewById(R.id.statsCard);
        this.topPlayers = (LinearLayout) view.findViewById(R.id.topPlayersCard);
        this.importantUpdates = (LinearLayout) view.findViewById(R.id.impUpdates);
        this.customerSupport = (LinearLayout) view.findViewById(R.id.customerSupportCard);
        this.settingsCard = (LinearLayout) view.findViewById(R.id.settingsCard);

        this.upCommingCard = (CardView) view.findViewById(R.id.upcomming);
        this.upOngoingCard = (CardView) view.findViewById(R.id.ongoing);
        this.completedCard = (CardView) view.findViewById(R.id.completed);
        this.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);

    }


    private void loadGames() {
        recyclerView.setVisibility(View.GONE);
        noMatchesLL.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        shimmer_view_container.startShimmer();
        mainLayout.setVisibility(View.GONE);
        notificationCard.setVisibility(View.GONE);
        sliderLayout.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.LIST_GAME_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        Log.e("urlissssss",builder.toString());
        gameJsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        shimmer_view_container.stopShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                        notificationCard.setVisibility(View.VISIBLE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        shimmer_view_container.stopShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                        //notificationCard.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        noMatchesLL.setVisibility(View.VISIBLE);
                    }
                });
        gameRequestQueue = Volley.newRequestQueue(getActivity());
        gameJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        gameJsonArrayRequest.setShouldCache(false);
        gameRequestQueue.getCache().clear();
        gameRequestQueue.add(gameJsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        gamePojoList.clear();
        for (int i = 0; i < response.length(); i++) {
            GamePojo gamePojo = new GamePojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                gamePojo.setId(json.getString("id"));
                gamePojo.setTitle(json.getString("title"));
                gamePojo.setBanner(json.getString("banner"));
                gamePojo.setUrl(json.getString("url"));
                gamePojo.setType(json.getString("type"));

                if (json.getString("type").equals("0")) {
                    gamePojoList.add(gamePojo);
                } else {
                    sliderList.add(gamePojo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!gamePojoList.isEmpty()) {
            gameAdapter = new GameAdapter(gamePojoList, getActivity());
            gameAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(gameAdapter);

            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            noMatchesLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.VISIBLE);
        }
        if (!sliderList.isEmpty()) {
            setSlider();
        }
    }

    private void loadHtmlGames() {
        htmlGamePojoList.clear();
        htmlGamePojoList.add(new HtmlGamePojo("2", "Candy Crush", R.drawable.candy_crush));
        htmlGamePojoList.add(new HtmlGamePojo("1", "Fruit Chop ", R.drawable.fruit_chop));

        if (!htmlGamePojoList.isEmpty()) {
            htmlGameAdapter = new HtmlGameAdapter(htmlGamePojoList, getActivity());
            htmlGameAdapter.notifyDataSetChanged();
            recyclerViewHtmlGame.setAdapter(htmlGameAdapter);

            htmlGameAdapter.setOnItemClickListener(new HtmlGameAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    startActivity(new Intent(context, GameActivity.class)
                            .putExtra(Toolbox.MODEL, htmlGamePojoList.get(position)));
                }
            });
        } else {
        }
    }

    private void setSlider() {
        sliderLayout.setVisibility(View.VISIBLE);
        sliderPagerAdapter = new SliderHomeAdapter(sliderList, getActivity());
        sliderPagerAdapter.notifyDataSetChanged();
        sliderViewPager.setAdapter(sliderPagerAdapter);
        AutoScrollHelper scroolhelper=new AutoScrollHelper(sliderViewPager);
        scroolhelper.startAutoScroll();
        dotsCount = sliderPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            try {
                dots[i] = new ImageView(getActivity());
                dots[i].setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.dot_nonactive));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                bannerDots.addView(dots[i], params);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //dots[0].setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.dot_active));

        sliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    page1 = position;
                    for (int i = 0; i < dotsCount; i++) {
                        try {
                            dots[i].setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.dot_nonactive));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    dots[position].setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.dot_active));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void saveCounter() {
        count = 0;

        preferences = getActivity().getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("counter", count);
        editor.apply();
    }


    public void onResume() {
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            shimmer_view_container.startShimmer();
        }
    }

    public void onPause() {
        shimmer_view_container.stopShimmer();
        super.onPause();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.profileCard) {
            this.startActivity(new Intent(this.getActivity(), MyProfileActivity.class));
        } else if (id == R.id.myWalletCard) {
            this.startActivity(new Intent(this.getActivity(), MyWalletActivity.class));
        } else if (id == R.id.statsCard) {
            this.startActivity(new Intent(this.getActivity(), MyStatisticsActivity.class));
        } else if (id == R.id.topPlayersCard) {
            this.startActivity(new Intent(this.getActivity(), TopPlayersActivity.class));
        } else if (id == R.id.impUpdates) {
            this.startActivity(new Intent(this.getActivity(), NotificationActivity.class));
        } else if (id == R.id.customerSupportCard) {
            this.startActivity(new Intent(this.getActivity(), ContactUsActivity.class));
        } else if (id == R.id.settingsCard) {
            this.startActivity(new Intent(this.getActivity(), SettingActivity.class));
        } else if (id == R.id.upcomming) {
            goToMyMatchActivity(Toolbox.UPCOMING_MATCH);
        } else if (id == R.id.ongoing) {
            goToMyMatchActivity(Toolbox.ON_GOING_MATCH);
        } else if (id == R.id.completed) {
            goToMyMatchActivity(Toolbox.RESULTS_MATCH);
        }
    }

    private void goToMyMatchActivity(String isFrom) {
        Intent intent = new Intent(getActivity(), MyContestActivity.class);
        intent.putExtra(Toolbox.IS_FROM, isFrom);
        //intent.putExtra("TITLE_KEY", strTitle);
        startActivity(intent);

    }
}
