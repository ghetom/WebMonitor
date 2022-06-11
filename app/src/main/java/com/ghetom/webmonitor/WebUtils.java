package com.ghetom.webmonitor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WebUtils {

    private static WebUtils instance;

    private WebUtils(){
        instance = null;
    }

    public static WebUtils getInstance(){
        if(instance == null){
            instance = new WebUtils();
        }
        return instance;
    }

    public String getContents(String url) {
        String contents = "";
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            InputStream in = conn.getInputStream();
            contents = convertStreamToString(in);

            //Remove (dynamically changing) script content from source
            String start = "<script";
            String end = "</script>";
            contents = contents.replaceAll(start + ".*" + end, "").toLowerCase();
        } catch (MalformedURLException e) {
            Log.d("",e.getMessage());
        } catch (IOException e) {
            Log.d("",e.getMessage());
        }
        return contents;
    }

    private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}