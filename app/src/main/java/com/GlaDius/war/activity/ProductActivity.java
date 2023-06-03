package com.GlaDius.war.activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.GlaDius.war.R;
import com.GlaDius.war.adapter.ProductAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.ProductPojo;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.views.SpacingItemDecoration;
import com.GlaDius.war.views.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductActivity extends AppCompatActivity {

    private View view;
    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout noProductLL;

    private RecyclerView recyclerView;
    private LinearLayout productLL;
    private ProductAdapter adapter;

    private ArrayList<ProductPojo> productPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initToolbar();

        this.mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.productLL = (LinearLayout) findViewById(R.id.productLL);
        this.noProductLL = (LinearLayout) findViewById(R.id.noProductLL);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);


        productPojoList = new ArrayList<>();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);


        if (new ExtraOperations().haveNetworkConnection(this)) {
            loadProduct();
        }else {
            Toast.makeText(ProductActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle((CharSequence) "Shop Now");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadProduct() {
        recyclerView.setVisibility(View.GONE);
        noProductLL.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.GET_PRODUCT_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noProductLL.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        productPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            ProductPojo productPojo = new ProductPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                productPojo.setId(json.getString("id"));
                productPojo.setBrand(json.getString("brand"));
                productPojo.setName(json.getString("name"));
                productPojo.setImage(json.getString("image"));
                productPojo.setPrice(json.getString("price"));
                productPojo.setPrice_discount(json.getString("price_discount"));
                productPojo.setDescription(json.getString("description"));
                productPojo.setUrl(json.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            productPojoList.add(productPojo);
        }
        if (!productPojoList.isEmpty()){
            adapter = new ProductAdapter(productPojoList,this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noProductLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // on item list clicked
            adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, ProductPojo obj, int position) {
                    //Toast.makeText(ProductActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("ID",obj.getId());
                    intent.putExtra("BRAND",obj.getBrand());
                    intent.putExtra("NAME",obj.getName());
                    intent.putExtra("IMAGE",obj.getImage());
                    intent.putExtra("PRICE",obj.getPrice());
                    intent.putExtra("OUR_PRICE",obj.getPrice_discount());
                    intent.putExtra("DESC",obj.getDescription());
                    intent.putExtra("URL",obj.getUrl());
                    startActivity(intent);
                    //Toast.makeText(getActivity(), "Item " + obj.getName() + " clicked", Toast.LENGTH_SHORT).show();
                }
            });


            adapter.setOnMoreButtonClickListener(new ProductAdapter.OnMoreButtonClickListener() {
                @Override
                public void onItemClick(View view, ProductPojo obj, MenuItem item) {
                    //Toast.makeText(getActivity(), obj.getName() + " (" + item.getTitle() + ") clicked", Toast.LENGTH_SHORT).show();
                    if (item.getTitle().equals("Details")){
                        Intent intent = new Intent(ProductActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("ID",obj.getId());
                        intent.putExtra("BRAND",obj.getBrand());
                        intent.putExtra("NAME",obj.getName());
                        intent.putExtra("IMAGE",obj.getImage());
                        intent.putExtra("PRICE",obj.getPrice());
                        intent.putExtra("OUR_PRICE",obj.getPrice_discount());
                        intent.putExtra("DESC",obj.getDescription());
                        intent.putExtra("URL",obj.getUrl());
                        startActivity(intent);
                    }
                    else if (item.getTitle().equals("Buy Now")) {
                        openWebPage(obj.getUrl());
                    }
                }
            });
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noProductLL.setVisibility(View.VISIBLE);
        }
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
            Toast.makeText(ProductActivity.this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
