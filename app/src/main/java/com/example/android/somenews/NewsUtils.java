package com.example.android.somenews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.somenews.MainActivity.LOG_TAG;

public class NewsUtils {

    private NewsUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);


        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.loading), e);
        }

        // Extract relevant fields from the JSON response and create an object
        List<News> guardianNews = extractFeatureFromJson(jsonResponse);

        //return list of News
        return guardianNews;
    }

    //create url and catch MalformedURLException
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.error_url), e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, String.valueOf(R.string.error_res) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, String.valueOf(R.string.error_JSON), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //building String from inputStream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    // Return a list of  News objects that has been built up from parsing a JSON response.

    public static List<News> extractFeatureFromJson(String newsJSON) {
        JSONObject contributor = null;
        String name;
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        ArrayList<News> guardianNews = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.

        try {
            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            //getting the root directory
            JSONObject main = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            JSONArray newsArray = main.getJSONArray("results");

            // For each news in the newsArray, create an News object
            for (int i = 0; i < newsArray.length(); i++) {

                JSONObject currentNews = newsArray.getJSONObject(i);
                //get tags array for author details
                JSONArray tags = currentNews.getJSONArray("tags");

                final int numberOfItems = tags.length();
                for (int j = 0; j < numberOfItems; j++) {
                    contributor = tags.getJSONObject(j);
                }
                if (contributor != null){
                //getting Author name
                name = contributor.getString("webTitle");}
                else {name = "Unknow";}

                // Extract the value for the proper keys
                String sectionName = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String title = currentNews.getString("webTitle");
                String sdate = currentNews.getString("webPublicationDate");
                //Changing string into date
                Date date = null;
                try {
                    date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(sdate);
                } catch (Exception e) {
                    Log.e("parsing", String.valueOf(R.string.error_date), e);
                }

                // Create a new News object with obtained details
                News info = new News(title, sectionName, date, url, name);

                // Add the new obejct to the list.
                guardianNews.add(info);
            }
        } catch (JSONException e) {
            // Catch JsonExeption and gives log msg
            Log.e("QueryUtils", String.valueOf(R.string.error_pars), e);
        }

        // Return the list of news
        return guardianNews;
    }

}

