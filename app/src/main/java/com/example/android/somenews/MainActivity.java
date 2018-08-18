package com.example.android.somenews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private NewsAdapter mAdapter;
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?api-key=89b8d11b-e346-4c29-a9e8-b7a14ab8d9e7&show-tags=contributor";
    private static final int NEWS_LOADER_ID = 1;
    SharedPreferences sharedPrefs;
    Uri baseUri;

    @BindView(R.id.list)
    ListView newsListView;
    @BindView(R.id.empty_list_item)
    TextView emptyView;
    @BindView(R.id.loading_spinner)
    View spinner;

    //OnItemClick intent to move to the news website
    @OnItemClick(R.id.list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        News currentNews = mAdapter.getItem(position);
        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri newsUri = Uri.parse(currentNews.getUrl());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(newsUri);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Setting  LoaderManager
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        newsListView.setAdapter(mAdapter);
        //Set emptyView on newsList
        newsListView.setEmptyView(emptyView);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String choSection = sharedPrefs.getString(
                getString(R.string.settings_section_by_key), ""
        );
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String choType = sharedPrefs.getString(
                getString(R.string.settings_type_by_label),
                "");

        // parse breaks apart the URI string that's passed into its parameter
        baseUri = Uri.parse(NEWS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if (!choSection.isEmpty()) {
            uriBuilder.appendQueryParameter("section", choSection);
        }
        if (!choType.isEmpty()) {
            uriBuilder.appendQueryParameter("type", choType);
        } else {
            return new NewsLoader(this, NEWS_REQUEST_URL);
        }
        return new NewsLoader(this, uriBuilder.toString());
    }

    // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
    // return new NewsLoader(this, uriBuilder.toString());


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        //checking connection status
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        emptyView.setText(getString(R.string.loading));
        mAdapter.clear();
        //depending on network status setting mAdapter with news or emptyView
        if (news != null && !news.isEmpty() && netInfo != null) {
            mAdapter.addAll(news);
        } else {
            emptyView.setText(getString(R.string.empty));
        }
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_reset) {
            sharedPrefs.edit().clear().apply();
            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }
}

