package com.moonweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
import android.widget.Toast;

import com.moonweather.app.activity.WeatherActivity;

public class HttpUtil {
    public static void sendHttpResquest(final String address,final HttpCallbackListener listener){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url = new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in),10*1024);
					StringBuilder response=new StringBuilder();
					String line;
					while ((line=reader.readLine())!=null) {
						response.append(line);
					}
					if (listener!=null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (listener!=null) {
						listener.onError(e);
					}
				}finally{
					if (connection!=null) {
						connection.disconnect();
					}
				}
			}
		}).start();
    	
    }
}
