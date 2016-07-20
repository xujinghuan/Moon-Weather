package com.moonweather.app.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment.SavedState;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.moonweather.app.db.MoonWeatherDB;
import com.moonweather.app.model.City;
import com.moonweather.app.model.County;
import com.moonweather.app.model.Province;

//解析地址类
public class Utility {
       public synchronized static boolean HandleProvincesResponse(MoonWeatherDB moonWeatherDB,String response) {
		   if(!TextUtils.isEmpty(response)){
    	   String allProvinces[]=response.split(",");
    	   if(allProvinces!=null&&allProvinces.length>0){
    	   for (String p:allProvinces) {
			Province province=new Province();
			province.setProvinceCode(p.split("\\|")[0]);
			province.setProvinceName(p.split("\\|")[1]);
			moonWeatherDB.saveProvince(province);
		      }
    	   return true;
		   }
		}
    	   return false;
	}
       
       
       public synchronized static boolean HandleCitiesResponse(MoonWeatherDB moonWeatherDB,String response,int provinceId) {
		   if(!TextUtils.isEmpty(response)){
    	   String allcities[]=response.split(",");
    	   if(allcities!=null&&allcities.length>0){
    	   for (String c:allcities) {
			City city=new City();
			city.setCityCode(c.split("\\|")[0]);
			city.setCityName(c.split("\\|")[1]);
			city.setProvinceId(provinceId);
			moonWeatherDB.saveCity(city);
		      }
    	   return true;
		   }
		}
    	   return false;
	}
       
       
       public synchronized static boolean HandleCountiesResponse(MoonWeatherDB moonWeatherDB,String response,int cityId) {
		   if(!TextUtils.isEmpty(response)){
    	   String allcounties[]=response.split(",");
    	   if(allcounties!=null&&allcounties.length>0){
    	   for (String c:allcounties) {
			County county=new County();
			county.setCountyCode(c.split("\\|")[0]);
			county.setCountyName(c.split("\\|")[1]);
			county.setCityId(cityId);
			moonWeatherDB.saveCounty(county);
		      }
    	   return true;
		   }
		}
    	   return false;
	}
       
       //解析服务器返回的JSON数据，并将解析出的数据存储到本地
       public static void handleWeatherResponse(Context context,String response){
    	   try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
       }
       //将服务器返回的所有天气信息存储到SharedPreferences文件中
       public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishtime){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
    	SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
    	editor.putBoolean("city_selected", true);
    	editor.putString("city_name", cityName);
    	editor.putString("weather_code", weatherCode);
    	editor.putString("temp1", temp1);
    	editor.putString("temp2", temp2);
    	editor.putString("weather_desp", weatherDesp);
    	editor.putString("publish_time", publishtime);
    	editor.putString("current_date", sdf.format(new Date()));
    	editor.commit();
     } 
}
