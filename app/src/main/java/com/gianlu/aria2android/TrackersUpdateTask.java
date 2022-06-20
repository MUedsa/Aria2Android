package com.gianlu.aria2android;

import android.util.Log;

import androidx.annotation.Nullable;

import com.gianlu.commonutils.preferences.Prefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TrackersUpdateTask implements Runnable {
    public static final String TAG = TrackersUpdateTask.class.getSimpleName();
    public static final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36";
    private Callback callback;

    public TrackersUpdateTask(@Nullable Callback callback){
        this.callback = callback;
    }

    @Override
    public void run() {
        String url = Prefs.getString(PK.TRACKERS_UPDATE_URL);
        if(!url.isEmpty()){
            try{
                Log.d(TAG, "TrackersUpdateTask start");
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setRequestProperty("User-Agent", UserAgent);
                String result = convertStreamToString(urlConnection.getInputStream());
                if(callback != null && !result.isEmpty()){
                    Log.d(TAG, "TrackersUpdateTask result:" + result);
                    callback.on(result);
                }
            }
            catch (IOException e){
                Log.e(TAG, "TrackersUpdateTask fail", e);
            }
        }else{
            Log.w(TAG, "Prefs " + PK.TRACKERS_UPDATE_URL.key() + " is empty, TrackersUpdateTask will not start");
        }
    }

    public interface Callback {
        void on(String result);
    }


    private String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        inputStream.close();
        return sb.toString();
    }
}
