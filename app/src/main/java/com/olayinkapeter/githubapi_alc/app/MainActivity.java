package com.olayinkapeter.githubapi_alc.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.olayinkapeter.githubapi_alc.R;
import com.olayinkapeter.githubapi_alc.app.adapter.DeveloperAdapter;
import com.olayinkapeter.githubapi_alc.app.model.DeveloperModel;
import com.olayinkapeter.githubapi_alc.helper.AppController;
import com.olayinkapeter.githubapi_alc.helper.DividerItemDecor;
import com.olayinkapeter.githubapi_alc.helper.EndPoints;
import com.olayinkapeter.githubapi_alc.helper.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String urlJsonObj = EndPoints.BASE_URL + "?q=";
    private String urlQueryParam = "language:java+location:lagos";
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;
    private String developerID, developerUserName, developerImageURL, developerHTMLURL;

    private List<DeveloperModel> developerList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DeveloperAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new DeveloperAdapter(developerList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecor(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);

        makeGitHubJsonObjectRequest(urlJsonObj + urlQueryParam);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DeveloperModel developerModel = developerList.get(position);
                Intent intent = new Intent(MainActivity.this, SingleUserActivity.class);
                intent.putExtra("d_id", developerModel.getDeveloperID());
                intent.putExtra("d_username", developerModel.getDeveloperUserName());
                intent.putExtra("d_imageURL", developerModel.getDeveloperImageURL());
                intent.putExtra("d_HTML_URL", developerModel.getDeveloperHTMLURL());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void makeGitHubJsonObjectRequest(String requestParam) {
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

                        developerID = jsonObject.getString("id");
                        developerUserName = jsonObject.getString("login");
                        developerImageURL = jsonObject.getString("avatar_url");
                        developerHTMLURL = jsonObject.getString("html_url");

                        developerList.add(new DeveloperModel(developerID, developerUserName, developerImageURL, developerHTMLURL));
                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
