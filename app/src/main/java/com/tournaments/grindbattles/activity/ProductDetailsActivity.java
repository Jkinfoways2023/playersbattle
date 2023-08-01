package com.tournaments.grindbattles.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.SliderAdapter;
import com.tournaments.grindbattles.adapter.SlidersAdapter;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.SliderPojo;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.views.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ProductDetailsActivity extends AppCompatActivity {

    private View parent_view;
    private String id,brand,name,image,price,price_discount,description,url;

    private ImageView imageIv;
    private TextView titleTv, brandTv, actualPrice, ourPrice, decTv;
    private AppCompatButton buyBt;
    private CardView noSliderLL;

    private RecyclerView recyclerView;
    private SliderAdapter adapter;

    private ArrayList<SliderPojo> sliderPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private Timer timer;
    private int page = 0;
    private ViewPager viewPager;
    private LinearLayout bannerDots;
    private int dotsCount;
    private ImageView[] dots;

    private SlidersAdapter galleryAdapter;
    private List<SliderPojo> galleryList;
    private RequestQueue galleryRequestQueue;
    private JsonArrayRequest galleryJsonArrayRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initView();
        initToolbar();
        initBundle();
        initComponent();

        sliderPojoList = new ArrayList<>();
        galleryList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        //recyclerView.addItemDecoration(new SpacingItemDecoration(4, Tools.dpToPx(this, 4), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        if (new ExtraOperations().haveNetworkConnection(this)) {
            loadSlider();
        }else {
            Toast.makeText(ProductDetailsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        parent_view = findViewById(R.id.parent_view);
        imageIv =  findViewById(R.id.imageIv);
        titleTv =  findViewById(R.id.titleTv);
        brandTv =  findViewById(R.id.brandTv);
        actualPrice =  findViewById(R.id.actualPrice);
        ourPrice =  findViewById(R.id.ourPrice);
        decTv =  findViewById(R.id.decTv);
        buyBt =  findViewById(R.id.buyBt);
        noSliderLL =  findViewById(R.id.noSliderLL);
    }

    private void initBundle() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("ID");
            brand = extras.getString("BRAND");
            name = extras.getString("NAME");
            image = extras.getString("IMAGE");
            price = extras.getString("PRICE");
            price_discount = extras.getString("OUR_PRICE");
            description = extras.getString("DESC");
            url = extras.getString("URL");

            titleTv.setText(name);
            brandTv.setText(brand);
            actualPrice.setText("₹"+price);
            actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ourPrice.setText("₹"+price_discount);
            if (description != null){
                if (  android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                {
                    decTv.setText(Html.fromHtml(description ,Html.FROM_HTML_MODE_LEGACY));
                }
                else {
                    decTv.setText(Html.fromHtml(description));
                }
            }

            Tools.displayImageOriginal(this, imageIv, Config.FILE_PATH_URL+image);
        }
    }

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initComponent() {

        buyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(url);
            }
        });

    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ProductDetailsActivity.this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void loadSlider() {
        recyclerView.setVisibility(View.GONE);
        noSliderLL.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.GET_SLIDER_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("prod_id", id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        recyclerView.setVisibility(View.GONE);
                        noSliderLL.setVisibility(View.GONE);
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        sliderPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            SliderPojo sliderPojo = new SliderPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                sliderPojo.setId(json.getString("id"));
                sliderPojo.setProd_id(json.getString("prod_id"));
                sliderPojo.setProd_img(json.getString("prod_img"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sliderPojoList.add(sliderPojo);
        }
        if (!sliderPojoList.isEmpty()){
            adapter = new SliderAdapter(sliderPojoList,this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            noSliderLL.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            // on item list clicked
            adapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, SliderPojo obj, int position) {
                    openGallery();
                    //Toast.makeText(getActivity(), "Item " + obj.getName() + " clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            recyclerView.setVisibility(View.GONE);
            noSliderLL.setVisibility(View.GONE);
        }
    }

    private void openGallery() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_silder, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        final ImageView closeIv = (ImageView) inflate.findViewById(R.id.closeIv);
        final ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.viewPager);
        final LinearLayout bannerDots = (LinearLayout) inflate.findViewById(R.id.bannerDots);

        builder.setCancelable(false);
        final AlertDialog create = builder.create();
        //textView3.setOnClickListener(this);
        closeIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                create.cancel();
            }
        });

        create.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == 4) {
                    create.dismiss();
                }
                return true;
            }
        });

        Uri.Builder builders = Uri.parse(Constant.GET_SLIDER_URL).buildUpon();
        builders.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builders.appendQueryParameter("prod_id", id);
        galleryJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, builders.toString(),  null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    galleryList.clear();
                    for (int i = 0; i < response.length(); i++){
                        SliderPojo menuSlider = new SliderPojo();

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            menuSlider.setId(jsonObject.getString("id"));
                            menuSlider.setProd_id(jsonObject.getString("prod_id"));
                            menuSlider.setProd_img(jsonObject.getString("prod_img"));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        galleryList.add(menuSlider);
                    }
                    galleryAdapter = new SlidersAdapter(galleryList,getApplicationContext());
                    galleryAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(galleryAdapter);

                    dotsCount = galleryAdapter.getCount();
                    dots = new ImageView[dotsCount];

                    for (int i=0; i<dotsCount; i++)
                    {
                        try {
                            dots[i] = new ImageView(getApplicationContext());
                            dots[i].setImageDrawable(ActivityCompat.getDrawable(getApplicationContext(),R.drawable.dot_nonactive));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(8,0,8,0);
                            bannerDots.addView(dots[i],params);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    dots[0].setImageDrawable(ActivityCompat.getDrawable(getApplicationContext(),R.drawable.dot_active));

                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            try {
                                page = position;
                                for (int i=0; i<dotsCount; i++)
                                {
                                    try {
                                        dots[i].setImageDrawable(ActivityCompat.getDrawable(getApplicationContext(), R.drawable.dot_nonactive));
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                }
                                dots[position].setImageDrawable(ActivityCompat.getDrawable(getApplicationContext(),R.drawable.dot_active));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        galleryRequestQueue = Volley.newRequestQueue(this);
        galleryJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        galleryJsonArrayRequest.setShouldCache(false);
        galleryRequestQueue.getCache().clear();
        galleryRequestQueue.add(galleryJsonArrayRequest);

        create.show();
    }

    public void onPause() {
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
}
