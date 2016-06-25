package com.moonweather.app.util;


import android.text.TextUtils;

import com.moonweather.app.db.MoonWeatherDB;
import com.moonweather.app.model.City;
import com.moonweather.app.model.County;
import com.moonweather.app.model.Province;

//½âÎöµØÖ·Àà
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
}
