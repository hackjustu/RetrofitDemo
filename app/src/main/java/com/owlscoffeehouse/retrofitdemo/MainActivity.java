package com.owlscoffeehouse.retrofitdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonElement;
import com.owlscoffeehouse.retrofitdemo.FlickrHttpService.FlickrService;
import com.owlscoffeehouse.retrofitdemo.FlickrHttpService.Model.Item;
import com.owlscoffeehouse.retrofitdemo.FlickrHttpService.Model.ItemHolder;
import com.owlscoffeehouse.retrofitdemo.GitHubService.GitHubService;
import com.owlscoffeehouse.retrofitdemo.GitHubService.Model.Contributor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = this.getClass().getSimpleName();

    private Button flickrButton = null;
    private Button githubButton = null;
    public static final String GITHUB_API_URL = "https://api.github.com";
    public static final String FLICKR_API_URL = "https://api.flickr.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bad practice to allow network operation on Main thread.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        flickrButton = (Button) findViewById(R.id.flickrButton);
        githubButton = (Button) findViewById(R.id.gitHubButton);
        flickrButton.setOnClickListener(this);
        githubButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            switch (v.getId()) {
                case R.id.flickrButton:
                    getFlickrService();
                    break;
                case R.id.gitHubButton:
                    getGithubService();
                    break;
                default:
            }
        } else {
            Log.d(TAG, "Network unavailable...");
        }
    }

    private void getFlickrService() {

        OkHttpClient client = new OkHttpClient();
        // Application interceptor
        client.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                com.squareup.okhttp.Response response = chain.proceed(chain.request());

                // Do anything with response here
                Log.d(TAG, "Application interceptor:\n" + response.toString());

                return response;
            }
        });

        // Network interceptor
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                com.squareup.okhttp.Response response = chain.proceed(chain.request());

                // Do anything with response here
                Log.d(TAG, "Network interceptor:\n" + response.toString());

                return response;
            }
        });

        // Create a very simple REST adapter which points to the Flickr API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FLICKR_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FlickrService flickrService = retrofit.create(FlickrService.class);

        Call<ItemHolder> call = flickrService.getSyncImage("Android");

        call.enqueue(new Callback<ItemHolder>() {
            @Override
            public void onResponse(Response<ItemHolder> response, Retrofit retrofit) {

                Log.d(TAG, "The raw reponse is :\n" + response.raw().toString());

                ArrayList<String> imageURLs = new ArrayList<String>();
                for (Item item : response.body().getItems()) {
                    String url = item.getMedia().getM();
                    imageURLs.add(url);
                    Log.d(TAG, url);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Failed!!");
            }
        });

    }

    // Print out the contributors in a raw JSON format via Github API
    private void getGithubService() {

        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our GitHub API interface.
        GitHubService github = retrofit.create(GitHubService.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<Contributor>> call = github.contributors("square", "retrofit");
        Call<JsonElement> callRaw = github.contributorsRaw("square", "retrofit");

        callRaw.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response<JsonElement> response, Retrofit retrofit) {
                JsonElement jsonElement = response.body();
                Log.d(TAG, jsonElement.toString());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Failed: " + t.getMessage());
            }
        });
    }
}
