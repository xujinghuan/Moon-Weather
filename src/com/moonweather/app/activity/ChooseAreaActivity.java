package com.moonweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.moonweather.app.R;
import com.moonweather.app.db.MoonWeatherDB;
import com.moonweather.app.model.City;
import com.moonweather.app.model.County;
import com.moonweather.app.model.Province;
import com.moonweather.app.util.HttpCallbackListener;
import com.moonweather.app.util.HttpUtil;
import com.moonweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
       public static final int LEVEL_PROVINCE=0;
       public static final int LEVEL_CITY=0;
       public static final int LEVEL_COUNTY=0;
       
       private ProgressDialog progressDialog;
       private ListView listView;
       private TextView titleText;
       private ArrayAdapter<String> adapter;
       private MoonWeatherDB moonWeatherDB;
       private List<String> dataList=new ArrayList<String>();
       
       private List<Province> provinceList;
       private List<City>  cityList;
       private List<County> countyList;
       
       private Province selectedProvince;
       private City selectedCity;
	   
       private int currentLevel;
	    @Override
        protected void onCreate(Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	super.onCreate(savedInstanceState);
        	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        	if (prefs.getBoolean("city_selected", false)) {
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
				finish();
				return;
			}
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        	setContentView(R.layout.choose_area);
        	listView=(ListView) findViewById(R.id.list_view);
        	titleText=(TextView) findViewById(R.id.title_text);
        	
        	adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        	listView.setAdapter(adapter);
        	moonWeatherDB=MoonWeatherDB.getInstance(this);
        	
        	listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (currentLevel==LEVEL_PROVINCE) {
						selectedProvince=provinceList.get(arg2);
						queryCities();
					}else if (currentLevel==LEVEL_CITY) {
						selectedCity=cityList.get(arg2);
						queryCounties();
				    }else if(currentLevel==LEVEL_COUNTY){
				    	String countyCode=countyList.get(arg2).getCountyCode();
				    	Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
				    	intent.putExtra("county_code", countyCode);
				    	startActivity(intent);
				    	finish();
				    }
				}
			});	
        	queryProvinces();
	    }
		
		//从数据库中查看省市县数据
		private void queryProvinces() {
			provinceList=moonWeatherDB.loadProvinces();
			if (provinceList.size()>0) {
				dataList.clear();
				for (Province p:provinceList) {
					dataList.add(p.getProvinceName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText("中国");
				currentLevel=LEVEL_PROVINCE;
			}else {
				queryFromServer(null,"province");
			}
		}
        protected void queryCities() {
        	cityList=moonWeatherDB.loadCities(selectedProvince.getId());
			if (cityList.size()>0){
				dataList.clear();
				for (City c:cityList) {
					dataList.add(c.getCityName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText(selectedProvince.getProvinceName());
				currentLevel=LEVEL_CITY;
			}else {
				queryFromServer(selectedProvince.getProvinceCode(),"city");
			}
		}
       protected void queryCounties() {
    	    countyList=moonWeatherDB.loadCounties(selectedCity.getId());
			if (countyList.size()>0){
				dataList.clear();
				for (County c:countyList) {
					dataList.add(c.getCountyName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText(selectedCity.getCityName());
				currentLevel=LEVEL_COUNTY;
			}else {
				queryFromServer(selectedCity.getcityCode(),"county");
			}
			
		}
		
	    //从网络上加载省市县数据
       private void queryFromServer(final String code,final String type){
    	   String address;
    	   if (!TextUtils.isEmpty(code)) {
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
    	   showProgressDialog();
    	   HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if ("province".equals(type)) {
					result=Utility.HandleProvincesResponse(moonWeatherDB, response);
				}else if ("city".equals(type)) {
					result=Utility.HandleCitiesResponse(moonWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result=Utility.HandleCountiesResponse(moonWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//runOnUiThread方法返回主线程
					runOnUiThread( new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
					
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread( new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});   
       }
         
       //显示对话框
       private void showProgressDialog(){
    	   if (progressDialog==null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("加载中");
			progressDialog.setCanceledOnTouchOutside(false);
		}
    	   progressDialog.show();
       }
	    private void closeProgressDialog(){
	    	if (progressDialog!=null) {
				progressDialog.dismiss();
			}
	    }
	    
	   @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel==LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces(); 
		}else {
			finish();
		}	
	}    
}
