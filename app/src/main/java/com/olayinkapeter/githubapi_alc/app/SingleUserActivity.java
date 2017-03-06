package com.olayinkapeter.githubapi_alc.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.olayinkapeter.githubapi_alc.R;
import com.olayinkapeter.githubapi_alc.helper.AppController;
import com.olayinkapeter.githubapi_alc.helper.CircleTransform;
import com.olayinkapeter.githubapi_alc.helper.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleUserActivity extends AppCompatActivity {
    private String urlJsonObj = EndPoints.BASE_URL + "?q=";
    private String urlQueryParam;
    private static String TAG = MainActivity.class.getSimpleName();

    String developerID, developerUserName, developerImageURL, developerHTMLURL, developerScore = "Score: \n";

    public TextView developerUserNameText, developerHTMLURLText, developerScoreText;
    public ImageView developerImage, developerImageBG;
    public Button shareButton;

    // Progress dialog
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        developerUserNameText = (TextView) findViewById(R.id.user_name);
        developerHTMLURLText = (TextView) findViewById(R.id.html_url);
        developerScoreText = (TextView) findViewById(R.id.score);
        developerImage = (ImageView) findViewById(R.id.image);
        developerImageBG = (ImageView) findViewById(R.id.bg_image);
        shareButton = (Button) findViewById(R.id.share_button);

        Intent intent = getIntent();
        developerID = intent.getStringExtra("d_id");
        developerUserName = intent.getStringExtra("d_username");
        developerImageURL = intent.getStringExtra("d_imageURL");
        developerHTMLURL = intent.getStringExtra("d_HTML_URL");

        urlQueryParam = developerUserName;

        makeGitHubJsonSingleUserRequest(urlJsonObj + urlQueryParam);
        applyDeveloperImage(developerImageURL);
        applyDeveloperImageBG(developerImageURL);

        developerHTMLURLText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfileInBrowser(developerHTMLURL);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUser("Check out this awesome developer @" + developerUserName + ", " + developerHTMLURL + ".");
            }
        });
    }

    private void makeGitHubJsonSingleUserRequest(String requestParam) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                requestParam, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("items");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        developerScore += jsonObject.getString("score");
                    }
                    developerUserNameText.setText(developerUserName);
                    developerHTMLURLText.setText(developerHTMLURL);
                    developerScoreText.setText(developerScore);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SingleUserActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(SingleUserActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                progressBar.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void applyDeveloperImage(String developerImageURL) {
        if (!TextUtils.isEmpty(developerImageURL)) {
            Glide.with(this).load(developerImageURL)
                    .crossFade()
                    .transform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(developerImage);
            developerImage.setColorFilter(null);
        } else {
            developerImage.setImageResource(R.drawable.bg_circle);
        }
    }

    private void applyDeveloperImageBG(String developerImageURL) {
        if (!TextUtils.isEmpty(developerImageURL)) {
            Glide.with(this).load(developerImageURL)
                    .into(developerImageBG);
        } else {
            developerImageBG.setImageResource(R.drawable.github);
        }
    }

    public void openUserProfileInBrowser(String userProfileURL) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(userProfileURL));
        startActivity(browserIntent);
    }

    public void shareUser(String shareMsg) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareMsg);
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }
}
