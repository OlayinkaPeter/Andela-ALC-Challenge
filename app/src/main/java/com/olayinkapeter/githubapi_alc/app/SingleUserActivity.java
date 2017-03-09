package com.olayinkapeter.githubapi_alc.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
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
    private String urlJsonObj = EndPoints.BASE_USER_URL + "?q=";
    private String urlJsonRepoObj = EndPoints.BASE_REPO_URL + "?q=user:";
    private String urlQueryParam;
    private static String TAG = MainActivity.class.getSimpleName();

    String developerID, developerUserName, developerImageURL, developerHTMLURL, developerLanguage, developerLocation, developerScore = "Score: \n";
    String repoName, repoURL, repoLanguage, repoStargazers, repoForks;

    public TextView developerUserNameText, developerHTMLURLText, developerLanguageText, developerLocationText, developerScoreText;
    public TextView repoNameText, repoURLText, repoLanguageText, repoStargazersText, repoForksText;
    public ImageView developerImage, developerImageBG;
    public Button shareButton;

    public String locationValue = "lagos";

    // Progress dialog
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        developerUserNameText = (TextView) findViewById(R.id.user_name);
        developerHTMLURLText = (TextView) findViewById(R.id.html_url);
        developerLanguageText = (TextView) findViewById(R.id.language);
        developerLocationText = (TextView) findViewById(R.id.location);
        developerScoreText = (TextView) findViewById(R.id.score);
        developerImage = (ImageView) findViewById(R.id.image);
        developerImageBG = (ImageView) findViewById(R.id.bg_image);
        shareButton = (Button) findViewById(R.id.share_button);

        repoNameText = (TextView) findViewById(R.id.repo_name);
        repoURLText = (TextView) findViewById(R.id.repo_url);
        repoLanguageText = (TextView) findViewById(R.id.repo_language);
        repoStargazersText = (TextView) findViewById(R.id.repo_stargazers);
        repoForksText = (TextView) findViewById(R.id.repo_forks);

        developerLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation(locationValue);
            }
        });

        Intent intent = getIntent();
        developerID = intent.getStringExtra("d_id");
        developerUserName = intent.getStringExtra("d_username");
        developerImageURL = intent.getStringExtra("d_imageURL");
        developerHTMLURL = intent.getStringExtra("d_HTML_URL");

        urlQueryParam = developerUserName;

        makeGitHubJsonSingleUserRequest(urlJsonObj + urlQueryParam);
        makeGitHubJsonPopularRepoRequest(urlJsonRepoObj + urlQueryParam);
        applyDeveloperImage(developerImageURL);
        applyDeveloperImageBG(developerImageURL);

        developerHTMLURLText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (developerHTMLURLText != null) {
                    openBrowser(developerHTMLURL);
                }
                else {
                    Toast.makeText(SingleUserActivity.this, "Try refreshing.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        repoURLText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repoURLText != null) {
                    openBrowser(repoURL);
                }
                else {
                    Toast.makeText(SingleUserActivity.this, "Try refreshing.", Toast.LENGTH_SHORT).show();
                }
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
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                requestParam, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("items");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        developerLanguage = "Java developer";
                        developerLocation = "Lagos";
                        developerScore += jsonObject.getString("score");
                    }
                    developerUserNameText.setText(developerUserName);
                    setUnderlinedText(developerHTMLURLText, developerHTMLURL);
                    developerLanguageText.setText(developerLanguage);
                    setUnderlinedText(developerLocationText, developerLocation);
                    developerScoreText.setText(developerScore);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SingleUserActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(SingleUserActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeGitHubJsonPopularRepoRequest(String requestParam) {
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                requestParam, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("items");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    repoName = jsonObject.getString("name");
                    repoURL = jsonObject.getString("html_url");
                    repoLanguage = jsonObject.getString("language");
                    repoStargazers = jsonObject.getString("stargazers_count");
                    repoForks = jsonObject.getString("forks");

                    repoNameText.setText(repoName);
                    setUnderlinedText(repoURLText, repoURL);
                    repoLanguageText.setText(repoLanguage);
                    repoStargazersText.setText(repoStargazers);
                    repoForksText.setText(repoForks);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SingleUserActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(SingleUserActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
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

    private void setUnderlinedText(TextView textView, String content) {
        SpannableString underlinedContent = new SpannableString(content);
        underlinedContent.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(underlinedContent);
    }

    public void openBrowser(String userProfileURL) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(userProfileURL));
        startActivity(browserIntent);
    }

    public void shareUser(String shareMsg) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        startActivity(shareIntent);
    }

    public void showLocation(String locationValue) {
        Intent intent = null, chooser = null;
        intent = new Intent(android.content.Intent.ACTION_VIEW);
        String map = "http://maps.google.co.in/maps?q=" + locationValue;
        intent.setData(Uri.parse(map));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
