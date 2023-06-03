package com.GlaDius.war.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {

    private LinearLayout emailSupport, phoneSupport;
    private TextView titleTv, addressTv, emailTv, phoneTv;
    private ImageView twitterIv, youtubeIv, facebookIv, instagramIv;
    private Button whatsappBt, facebookBt, discordBt, instagramBt, tiwtterBt;

    private String whatsappSt, facebookSt, twitterFollowSt, youtubeFollowSt, facebookFollowSt, instagramFollowSt;

    private SessionManager session;
    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String mnumber;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initToolbar();
        initSession();

        titleTv = findViewById(R.id.titleTv);
        addressTv = findViewById(R.id.addressTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        whatsappBt = findViewById(R.id.whatsappBt);
        facebookBt = findViewById(R.id.facebookBt);
        discordBt = findViewById(R.id.discordBt);
        instagramBt = findViewById(R.id.instagramBt);
        tiwtterBt = findViewById(R.id.tiwtterBt);

        emailSupport = findViewById(R.id.emailSupport);
        phoneSupport = findViewById(R.id.phoneSupport);

        twitterIv = findViewById(R.id.twitterIv);
        youtubeIv = findViewById(R.id.youtubeIv);
        facebookIv = findViewById(R.id.facebookIv);
        instagramIv = findViewById(R.id.instagramIv);

        if (!Config.ENABLE_EMAIL_SUPPORT) {
            emailSupport.setVisibility(View.GONE);
        }
        if (!Config.ENABLE_PHONE_SUPPORT) {
            phoneSupport.setVisibility(View.GONE);
        }

        loadContatUs();

        facebookBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_MESSENGER_SUPPORT) {
                    openMessengerConversationUsingUri(ContactUsActivity.this, facebookSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        whatsappBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_WHATSAPP_SUPPORT) {
                    openWhatsAppConversationUsingUri(ContactUsActivity.this, whatsappSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        discordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_DISCORD_SUPPORT) {
                    openWebPage(Config.DISCORD_CHANNEL_ID);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        instagramBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_INSTAGRAM_FOLLOW) {
                    openWebPage(instagramFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tiwtterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_TWITTER_FOLLOW) {
                    openWebPage(twitterFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });


        twitterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_TWITTER_FOLLOW) {
                    openWebPage(twitterFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        youtubeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_YOUTUBE_FOLLOW) {
                    openWebPage(youtubeFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        facebookIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_FACEBOOK_FOLLOW) {
                    openWebPage(facebookFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        instagramIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.ENABLE_INSTAGRAM_FOLLOW) {
                    openWebPage(instagramFollowSt);
                } else {
                    Toast.makeText(ContactUsActivity.this, "This option disable by admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadContatUs() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.CONTACT_US_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            titleTv.setText(jsonObject1.getString("title"));
                            addressTv.setText(jsonObject1.getString("address"));
                            emailTv.setText(jsonObject1.getString("email"));
                            phoneTv.setText(jsonObject1.getString("phone"));
                            whatsappSt = jsonObject1.getString("whatsapp_no");
                            facebookSt = jsonObject1.getString("messenger_id");
                            facebookFollowSt = jsonObject1.getString("fb_follow");
                            instagramFollowSt = jsonObject1.getString("ig_follow");
                            twitterFollowSt = jsonObject1.getString("twitter_follow");
                            youtubeFollowSt = jsonObject1.getString("youtube_follow");
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

    private void initSession() {
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

    private void openMessengerConversationUsingUri(Context context, String FbUserID) {
        try {
            Uri uri = Uri.parse("fb-messenger://user/" + FbUserID);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(sendIntent);
        } catch (NullPointerException | ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void openWhatsAppConversationUsingUri(Context context, String numberWithCountryCode) {
        try {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + numberWithCountryCode);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(sendIntent);
        } catch (NullPointerException | ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle((CharSequence) "Customer Support");
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

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (NullPointerException | ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
