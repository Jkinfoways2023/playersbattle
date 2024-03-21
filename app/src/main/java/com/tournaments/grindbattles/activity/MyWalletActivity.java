package com.tournaments.grindbattles.activity;

import static com.tournaments.grindbattles.common.Constant.CREATE_ORDER;
import static com.tournaments.grindbattles.common.Constant.CREATE_ORDER_PAYTM_PAY;

import com.android.volley.toolbox.JsonObjectRequest;
import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.material.textfield.TextInputEditText;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.fragment.AddCoinsFragment;
import com.tournaments.grindbattles.fragment.RedeemCoinsFragment;
import com.tournaments.grindbattles.fragment.TransactionsFragment;
import com.tournaments.grindbattles.paytm.Api;
import com.tournaments.grindbattles.paytm.Checksum;
import com.tournaments.grindbattles.paytm.Paytm;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

public class MyWalletActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback, PaymentResultListener {

    private AppBarLayout app_bar_layout;
    private CollapsingToolbarLayout collapsing_toolbar;
    private ImageView backPress;
    //private CardView coinCv;
    //private TextView coinTv;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CoordinatorLayout main;
    private TextView walletBalanceTv;
    private TextView depositedTv;
    private TextView winningTv;
    private TextView bonusTv;

    private SessionManager session;
    private String id, name, firstname, lastname, email, mnumber, username, password;
    private String walletSt, statusSt, orderIdSt, txnIdSt, mid, amountSt, coinSt, remarkSt, modeSt, currencySt, is_active,paytm_pay_login_id,paytm_pay_api_key;
    private int tot_coins, won_coins, bonus_coins;

    final int UPI_PAYMENT = 0;
    final int PAYPAL_PAYMENT = 1;
    final int GOOGLEPAY_PAYMENT = 2;

    private String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private int GOOGLE_PAY_REQUEST_CODE = 123;

    private String UPI_PACKAGE_NAME = "in.org.npci.upiapp";
    private int UPI_REQUEST_CODE = 456;
    private  String rzp_key="";

    final int REQUEST_CODE = 1;
    private String strToken;
    final String get_token = Config.PAYPAL_URL + "main.php";
    final String send_payment_details = Config.PAYPAL_URL + "checkout.php";
    HashMap<String, String> paramHash;
    private FrameLayout frameLayout;
    private CardView addCoin, redeem, transaction;

    WebView webView;
    RelativeLayout coinsrl;
    String upi_new_order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        initToolbar();
        initView();
        initPreference();

        loadProfile();

        /*viewPager = (ViewPager) findViewById(R.id.viewPager);
        //viewPager.setOffscreenPageLimit(3);
        createViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();*/

        addCoin.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddCoinsFragment()).addToBackStack(null).commit();
        });

        redeem.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new RedeemCoinsFragment()).addToBackStack(null).commit();
        });

        transaction.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new TransactionsFragment()).addToBackStack(null).commit();
        });

        new HttpRequest().execute();
    }

    private void loadProfile() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.GET_PROFILE_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("id", id);
            Log.e("profileurliss",builder.toString());
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            tot_coins = jsonObject1.getInt("cur_balance");
                            won_coins = jsonObject1.getInt("won_balance");
                            bonus_coins = jsonObject1.getInt("bonus_balance");
                            is_active = jsonObject1.getString("status");
                            mid = jsonObject1.getString("mid");
                            rzp_key=jsonObject1.getString("rzp_m_id");
                            paytm_pay_api_key = jsonObject1.getString("paytm_pay_api_key");
                            paytm_pay_login_id = jsonObject1.getString("paytm_pay_login_id");
                            try {
                                MainActivity.toolwallet.setText(String.valueOf(tot_coins));
                            } catch (NullPointerException | NumberFormatException e) {
                                e.printStackTrace();
                            }

                            try {
                                //coinTv.setText(String.valueOf(tot_coins));
                                walletBalanceTv.setText(String.valueOf(tot_coins));
                                winningTv.setText(String.valueOf(won_coins));
                                bonusTv.setText(String.valueOf(bonus_coins));
                            } catch (NullPointerException | NumberFormatException e) {
                                //coinTv.setText("0");
                                walletBalanceTv.setText("0");
                                winningTv.setText("0");
                                bonusTv.setText("0");
                                e.printStackTrace();
                            }

                            try {
                                depositedTv.setText(String.valueOf(tot_coins - won_coins - bonus_coins));
                            } catch (NumberFormatException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void initToolbar() {
        backPress = (ImageView) findViewById(R.id.backPress);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initPreference() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
    }

    private void initView() {
        /*this.coinCv = (CardView) findViewById(R.id.coinCv);
        this.coinTv = (TextView) findViewById(R.id.coinTv);*/
        this.walletBalanceTv = (TextView) findViewById(R.id.walletBalanceTv);
        this.depositedTv = (TextView) findViewById(R.id.depositedTv);
        this.winningTv = (TextView) findViewById(R.id.winningTv);
        this.bonusTv = (TextView) findViewById(R.id.bonusTv);
        //this.main = (CoordinatorLayout) findViewById(R.id.mainLayout);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        addCoin = (CardView) findViewById(R.id.addCoin);
        redeem = (CardView) findViewById(R.id.redeem);
        transaction = (CardView) findViewById(R.id.transaction);
        webView=findViewById(R.id.payment_webview);
        coinsrl=findViewById(R.id.coinsrl);

    }


    private void createTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("ADD MONEY");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("REDEEM MONEY");
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("TRANSACTION");
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void createViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddCoinsFragment(), "Tab 1");
        adapter.addFrag(new RedeemCoinsFragment(), "Tab 2");
        adapter.addFrag(new TransactionsFragment(), "Tab 3");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void Add(String title, String subtitle, String message, String amount, String coins, String id, String status, String image, String mode, String currency) {
        walletSt = title;
        amountSt = amount;
        coinSt = coins;
        currencySt = amount + " " + currency;
        modeSt = mode;
        Log.e("titleiss", walletSt);
        if (walletSt.equalsIgnoreCase("PayTm")) {
            remarkSt = "Added From PayTm";
            generateCheckSum(amount);
            //razorpay_payment(amount);
        } else if (walletSt.equalsIgnoreCase("PayPal")) {
            remarkSt = "Added From PayPal";
            onBraintreeSubmit();
            //razorpay_payment(amount);
        } else if (walletSt.equalsIgnoreCase("GooglePay")) {
            remarkSt = "Added From GooglePay";
            payUsingGooglePay(amount, Config.UPI_ID, name, "Added From GooglePay");
            //razorpay_payment(amount);
        } else if (walletSt.equalsIgnoreCase("UPI")) {
            remarkSt = "Added From BHIM UPI";
            payUsingUpi(amount, Config.UPI_ID, name, "Added From BHIM UPI");
            //razorpay_payment(amount);
        }
        else if (walletSt.equalsIgnoreCase("RazorPay")) {
            remarkSt = "Added From Razorpay";
            razorpay_payment(amount);
        }
        else if(walletSt.equalsIgnoreCase("Upi Pay"))
        {
            generate_txnId(amount,id);
        }
        else if(walletSt.equalsIgnoreCase("Paytm Pay"))
        {
            generate_txnId_paytm_pay(amount,id);
        }
        else {
            Toast.makeText(this, "Unavailable This Option", Toast.LENGTH_SHORT).show();
        }
        Log.e("razorpay_called", "Error in starting Razorpay Checkout");

        //generate_txnId(amount,id);
        /*remarkSt = "Added From PayPal";
        addTransactionDetails(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));*/
    }




    private void generateCheckSum(String amount) {

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating button_paytm object
        //containing all the values required
        Log.e("merchantidis",mid);
        final Paytm paytm = new Paytm(
                mid,
                Config.CHANNEL_ID,
                amount,
                Config.WEBSITE,
                Config.CALLBACK_URL,
                Config.INDUSTRY_TYPE_ID
        );

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, retrofit2.Response<Checksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the button_paytm object as the parameter
                Log.e("checksomeiss",response.message());
                Log.e("checksomeiss", String.valueOf(response.body()));
                Log.e("checksomeiss", String.valueOf(response.body().getOrderId()));
                Log.e("checksomeiss", String.valueOf(response.body().getChecksumHash()));
                Log.e("checksomeiss", String.valueOf(response.body().getPaytStatus()));
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                Log.e("checksomeiss","failed"+t.getMessage());
            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting button_paytm service
        //PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", paytm.getmId());
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());


        //creating a button_paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the button_paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
       Service.startPaymentTransaction(MyWalletActivity.this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        try {
            statusSt = inResponse.getString("STATUS");

            if (statusSt.equalsIgnoreCase("TXN_SUCCESS")) {

                orderIdSt = inResponse.getString("ORDERID");
                txnIdSt = inResponse.getString("TXNID");

                // Loading jsonarray in Background Thread
                addTransactionDetails(orderIdSt, txnIdSt);
            }

        } catch (Exception e) {
            Log.e("paytmexception", String.valueOf(e));
            e.printStackTrace();
        }
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(MyWalletActivity.this, "Network error", Toast.LENGTH_LONG).show();
        Log.e("paytmexception", String.valueOf("Network"));
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Log.e("paytmexception", String.valueOf(inErrorMessage));
        Toast.makeText(MyWalletActivity.this, "" + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Log.e("paytmexception", String.valueOf(inErrorMessage));
        Toast.makeText(MyWalletActivity.this, "" + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Log.e("paytmexception", String.valueOf(inErrorMessage));
        Toast.makeText(MyWalletActivity.this, "" + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(MyWalletActivity.this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Log.e("paytmexception", String.valueOf(inErrorMessage));
        Toast.makeText(MyWalletActivity.this, inResponse.toString(), Toast.LENGTH_LONG).show();
    }


    void payUsingUpi(String amount, String upiId, String name, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        //  only google pay
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(UPI_PACKAGE_NAME);

        // choose any UPI based app
/*
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
*/

        // check if intent resolves
        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(intent, UPI_PAYMENT);
        } else {
            Toast.makeText(MyWalletActivity.this, "BHIM app not found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    void payUsingGooglePay(String amount, String upiId, String name, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        //  only google pay
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);

        // choose any UPI based app
/*
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
*/

        // check if intent resolves
        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(intent, GOOGLEPAY_PAYMENT);
        } else {
            Toast.makeText(MyWalletActivity.this, "GooglePay app not found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(MyWalletActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(MyWalletActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: " + approvalRefNo);
                addTransactionDetails(String.valueOf(System.currentTimeMillis()), approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(MyWalletActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyWalletActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyWalletActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }


    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(strToken);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(MyWalletActivity.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Successful")) {
                            addTransactionDetails(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));
                            Toast.makeText(MyWalletActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyWalletActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
                            Log.d("mylog", "Final Response: " + response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MyWalletActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(PaypalDepositActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                        }
                    });
                    strToken = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(PaypalDepositActivity.this, "Failed to get token: " + ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
            case GOOGLEPAY_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 12)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("GOOGLEPAY", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("GOOGLEPAY", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("GOOGLEPAY", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
            case PAYPAL_PAYMENT:
                if (requestCode == REQUEST_CODE) {
                    if (resultCode == Activity.RESULT_OK)
                    {
                        DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                        PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                        String stringNonce = nonce.getNonce();
                        Log.d("mylog", "Result: " + stringNonce);
                        // Send payment price with the nonce
                        // use the result to update your UI and send the payment method nonce to your server
                        if (!amountSt.isEmpty()) {
                            paramHash = new HashMap<>();
                            paramHash.put("amount", amountSt);
                            paramHash.put("nonce", stringNonce);
                            sendPaymentDetails();
                        } else
                            Toast.makeText(MyWalletActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();

                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        // the user canceled
                        Log.d("mylog", "user canceled");
                    } else {
                        // handle errors here, an exception may be available in
                        Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                        Log.d("mylog", "Error : " + error.toString());
                    }
                }
                break;
        }
    }


    private void addTransactionDetails(String orderIdSt, String txnIdSt) {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.ADD_TRANSACTION_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("order_id", orderIdSt);
            builder.appendQueryParameter("payment_id", txnIdSt);
            builder.appendQueryParameter("req_amount", amountSt);
            builder.appendQueryParameter("coins_used", coinSt);
            builder.appendQueryParameter("getway_name", walletSt);
            builder.appendQueryParameter("remark", remarkSt);
            builder.appendQueryParameter("type", modeSt);
            Log.e("Strrinbuilder",builder.toString());
            StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("Strrinbuilder",response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");
                        String msg = jsonObject1.getString("msg");

                        if (success.equals("0")) {
                            Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            successDialog(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    public void Redeem(String title, String subtitle, String message, String amount, final String coins, String id, String status, String image, String mode, String currency) {
        walletSt = title;
        amountSt = amount;
        coinSt = coins;
        currencySt = amount + " " + currency;
        modeSt = mode;
        remarkSt = "Redeem From " + title;
        try {
            if (is_active.equals("1")) {
                if (won_coins >= Integer.parseInt(coins)) {
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_payout);
                    dialog.setCancelable(false);

                    final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    //final TextView titleTv = dialog.findViewById(R.id.titleTv);
                    final TextView noteTv = dialog.findViewById(R.id.noteTv);
                    final TextInputEditText nameEt = dialog.findViewById(R.id.nameEt);
                    final TextInputEditText idEt = dialog.findViewById(R.id.idEt);
                    final AppCompatButton nextBt = dialog.findViewById(R.id.nextBt);

                    //titleTv.setText(title);
                    noteTv.setText("Note: You can redeem only winning play coin. " + subtitle);
                    nameEt.setHint("Enter Account Holder Name");
                    idEt.setHint(message);

                    ((AppCompatButton) dialog.findViewById(R.id.closeBt)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ((AppCompatButton) dialog.findViewById(R.id.nextBt)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextBt.setEnabled(false);
                            if (is_active.equals("1")) {
                                if (won_coins >= Integer.parseInt(coins)) {
                                    if (!nameEt.getText().toString().trim().isEmpty() && !idEt.getText().toString().trim().isEmpty()) {
                                        dialog.dismiss();
                                        redeemTransactionDetails(nameEt.getText().toString().trim(), idEt.getText().toString().trim(), String.valueOf(System.currentTimeMillis()));
                                    } else {
                                        nextBt.setEnabled(true);
                                        Toast.makeText(MyWalletActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    dialog.dismiss();
                                    nextBt.setEnabled(true);
                                    Toast.makeText(MyWalletActivity.this, "You don't have enough won play coin to redeem", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dialog.dismiss();
                                nextBt.setEnabled(true);
                                Toast.makeText(MyWalletActivity.this, "You are not eligible to redeem play coin as your account is not active.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                } else {
                    Toast.makeText(this, "You don't have enough won play coin to redeem", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You cant't redeem play coin. Your account isn't active.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void redeemTransactionDetails(String account_holder_name, String account_holder_id, String orderIdSt) {
        Uri.Builder builder = Uri.parse(Constant.ADD_TRANSACTION_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("user_id", id);
        builder.appendQueryParameter("order_id", orderIdSt);
        builder.appendQueryParameter("req_amount", currencySt);
        builder.appendQueryParameter("coins_used", coinSt);
        builder.appendQueryParameter("getway_name", walletSt);
        builder.appendQueryParameter("remark", remarkSt);
        builder.appendQueryParameter("type", modeSt);
        builder.appendQueryParameter("request_name", account_holder_name);
        builder.appendQueryParameter("req_from", account_holder_id);
        StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                    String success = jsonObject1.getString("success");
                    String msg = jsonObject1.getString("msg");

                    if (success.equals("0")) {
                        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_LONG).show();
                    } else if (success.equals("1")) {
                        successDialog(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                return parameters;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initPreference();
        loadProfile();
    }

    private void successDialog(String msg) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView titleTv = (TextView) dialog.findViewById(R.id.titleTv);
        TextView subTitleTv = (TextView) dialog.findViewById(R.id.subTitleTv);
        TextView noteTv = (TextView) dialog.findViewById(R.id.noteTv);

        Button cancelBt = (Button) dialog.findViewById(R.id.cancelBt);
        Button okBt = (Button) dialog.findViewById(R.id.okBt);

        noteTv.setText(msg);

        cancelBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Intent intent = new Intent(MyWalletActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        okBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Intent intent = new Intent(MyWalletActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void razorpay_payment(String amount){

        Checkout checkout = new Checkout();
        Log.e("rzpkeyiss",rzp_key);
        checkout.setKeyID(rzp_key);
        try {
            double total = Double.parseDouble(amount);
            total = total * 100;
            JSONObject options = new JSONObject();
            options.put("name", "Your App");
            options.put("description", "Payment");
            //options.put("order_id", "test_order");
            options.put("currency", "INR");
            options.put("amount", total); // Replace with your dynamic amount
            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mnumber);

            options.put("prefill", preFill);
            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("exceptionissss", String.valueOf(e));
            e.printStackTrace();
        }
/*
        Checkout checkout = new Checkout();
*/
       /* try {
            JSONObject options = new JSONObject();

            options.put("name", this.getString(R.string.app_name));
            options.put("description", "Add Fund to app");
            options.put("currency", "INR");
            options.put("order_id", "test_order");

            double total = Double.parseDouble(amount);
            total = total * 100;
            options.put("amount", total);

            //String b_number[] = SharedHelper.getKey(activity,Constant.USER_NUMBER).split(",");

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mnumber);

            options.put("prefill", preFill);
            checkout.open(this, options);

        } catch(Exception e) {
            Log.e("exceptionisss", "Error in starting Razorpay Checkout"+ e);
        }*/
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        //transaction_id = razorpayPaymentID;
        //addBalance();
        addTransactionDetails(String.valueOf(System.currentTimeMillis()), razorpayPaymentID);
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int code, String response) {

        try {
            Log.e("exceptionisss", "Error in starting Razorpay Checkout"+ response);
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("exceptionisss", "Error in starting Razorpay Checkout"+ e);
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }

    public void callupimethod(String url)
    {
        Log.e("responsestatus", "Payment initiated"+url);
        webView.setVisibility(View.VISIBLE);
        coinsrl.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // Do not change Useragent otherwise it will not work. even if not working uncommit below
        // mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new WebviewInterface(), "Interface");
        if (url.startsWith("upi:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }else{
            webView.loadUrl(url);
        }

    }
    public class WebviewInterface {
        @JavascriptInterface
        public void paymentResponse(String client_txn_id, String txn_id) {
            Toast.makeText(MyWalletActivity.this, ""+client_txn_id+" txnidd => "+txn_id, Toast.LENGTH_SHORT).show();
            Log.e("txnstatusisssss",client_txn_id+" txnidd => "+txn_id);
            addTransactionDetails(upi_new_order_id, txn_id);
            webView.setVisibility(View.VISIBLE);
            coinsrl.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }

        @JavascriptInterface
        public void errorResponse() {
            Log.e("canceledd","transaction cancelled");
            Toast.makeText(MyWalletActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            webView.setVisibility(View.GONE);
            coinsrl.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }
    }
    private void generate_txnId(String amount, String id)
    {
        Log.e("responsestatus","response method called");
        upi_new_order_id= String.valueOf(System.currentTimeMillis());
        HashMap<String, String> user = session.getUserDetails();
        try {
            RequestQueue rq = Volley.newRequestQueue(this);
            StringRequest jsonObjectRequest = new StringRequest
                    (Request.Method.POST, CREATE_ORDER , new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("responsestatusisorder","respo " +response);
                            try {
                                JSONObject object=new JSONObject(response);
                                String status=object.getString("status");
                                if(status.equalsIgnoreCase("true"))
                                {
                                    Log.e("responsestatusis","calledd 1252");
                                    JSONObject obj=object.getJSONObject("data");
                                    String url=obj.getString("payment_url");

                                    callupimethod(url);
                                }
                                else
                                {
                                    Toast.makeText(MyWalletActivity.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e)
                            {
                                Log.e("responsestatusis","error "+ String.valueOf(e));
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("responsestatusis", "Volley error "+error.toString());
                            Log.e("responsestatusis", "Volley error "+error.networkResponse);
                        }
                    }){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("key", user.get(SessionManager.upi_gateway_key));
                    params.put("client_txn_id",upi_new_order_id);
                    params.put("amount",amount);
                    params.put("p_info","Product Name");
                    params.put("customer_name",user.get(SessionManager.KEY_FIRST_NAME));
                    params.put("customer_email",user.get(SessionManager.KEY_EMAIL));
                    params.put("redirect_url","http://google.com");
                    params.put("customer_mobile",user.get(SessionManager.KEY_MOBILE));
                    params.put("udf1",user.get(SessionManager.KEY_ID));
                    params.put("udf2","null");//user.get(SessionManager.ACCESS_TOKEN));
                    params.put("udf3","user defined field 3 (max 25 char)");

                 /*   params.put("loginid","9599212427");
                    params.put("apikey","u6nij2l1ta");
                    params.put("orderid",upi_new_order_id);
                    params.put("amt","5");
                    params.put("trxnote",user.get(SessionManager.KEY_EMAIL));
                    params.put("custmobile",user.get(SessionManager.KEY_MOBILE));
                    params.put("redirecturl","redirect_url");
                    params.put("mcallback_url","callback_url");*/

                    Log.e("responsestatusis","url "+CREATE_ORDER);
                    Log.e("responsestatusis","json "+params.toString());
                    return params;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            rq.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generate_txnId_paytm_pay(String amount, String id)
    {
        Log.e("responsestatus","response method called");
        upi_new_order_id= String.valueOf(System.currentTimeMillis());
        HashMap<String, String> user = session.getUserDetails();


        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("loginid", paytm_pay_login_id);
            jsonParams.put("apikey", paytm_pay_api_key);
            jsonParams.put("orderid", upi_new_order_id);
            jsonParams.put("amt", amount);
            jsonParams.put("trxnote", user.get(SessionManager.KEY_ID));
            jsonParams.put("custmobile", user.get(SessionManager.KEY_MOBILE));
            jsonParams.put("redirecturl", "https://www.google.com/");
            // Add more key-value pairs as needed
            Log.e("responsestatusis","url "+CREATE_ORDER);
            Log.e("responsestatusis","json "+jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, // Change to GET if needed
                CREATE_ORDER_PAYTM_PAY,
                jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responsestatusisorder","respo " +response);
                        try {
                            JSONObject object = response;
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                Log.e("responsestatusis", "calledd 1252");
                                //JSONObject obj = object.getJSONObject("data");
                                String url = object.getString("gotourl");

                                callupimethodpaytmpg(url);
                            } else {

                                Toast.makeText(MyWalletActivity.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e)
                        {
                            Log.e("responsestatusis","error "+ String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("responsestatusis", "Volley error "+error.toString());
                        Log.e("responsestatusis", "Volley error "+error.networkResponse);
                    }
                }
        );

// Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MyWalletActivity.this);
        requestQueue.add(jsonObjectRequest);

       /* try {
            RequestQueue rq = Volley.newRequestQueue(this);
            StringRequest jsonObjectRequest = new StringRequest
                    (Request.Method.POST, CREATE_ORDER_PAYTM_PAY , new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            Log.e("responsestatusisorder","respo " +response);
                            try {
                                JSONObject object=new JSONObject(response);
                                String status=object.getString("status");
                                if(status.equalsIgnoreCase("true"))
                                {
                                    Log.e("responsestatusis","calledd 1252");
                                    JSONObject obj=object.getJSONObject("data");
                                    String url=obj.getString("payment_url");

                                    callupimethod(url);
                                }
                                else
                                {
                                    Toast.makeText(MyWalletActivity.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e)
                            {
                                Log.e("responsestatusis","error "+ String.valueOf(e));
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("responsestatusis", "Volley error "+error.toString());
                            Log.e("responsestatusis", "Volley error "+error.networkResponse);
                        }
                    }){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("loginid","9599212427");
                    params.put("apikey","u6nij2l1ta");
                    params.put("orderid",upi_new_order_id);
                    params.put("amt","5");
                    params.put("trxnote",user.get(SessionManager.KEY_EMAIL));
                    params.put("custmobile",user.get(SessionManager.KEY_MOBILE));
                    params.put("redirecturl","redirect_url");
                    params.put("mcallback_url","callback_url");
                    Log.e("responsestatusis","url "+CREATE_ORDER);
                    Log.e("responsestatusis","json "+params.toString());
                    return params;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            rq.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void callupimethodpaytmpg(String url)
    {
        webView.setVisibility(View.VISIBLE);
        coinsrl.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // Do not change Useragent otherwise it will not work. even if not working uncommit below
        // mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new WebviewInterface(), "Interface");
        if (url.startsWith("upi:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }else{
            webView.loadUrl(url);
        }
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                }
                Log.e("urlisssssss",url);
                // Check the URL and decide whether to load it or handle it differently
                if (url.contains("?pgstatus=success")) {
                    Toast.makeText(MyWalletActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    addTransactionDetails(upi_new_order_id, orderIdSt);
                    webView.setVisibility(View.VISIBLE);
                    coinsrl.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }
                if (url.contains("upi:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                if(url.contains("?pgstatus=cancle"))
                {
                    webView.setVisibility(View.GONE);
                    coinsrl.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }
                return true; // Return true to indicate that you've handled the URL loading
            }
        });
    }
}
