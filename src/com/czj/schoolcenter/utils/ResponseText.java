package com.czj.schoolcenter.utils;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.czj.schoolcenter.global.globalconstans;

/**
 * Created by czj on 2016/3/15.
 */
public class ResponseText {
    public static String getresponsetext() {
        String responsetext="";
        try {
            URL url = new URL(globalconstans.responsetext);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                responsetext = StreamTools.streamToStr(inputStream);

            }

        } catch (Exception e) {
            e.printStackTrace();
         System.out.println(e.toString());
        }


        return responsetext;
    }
}
