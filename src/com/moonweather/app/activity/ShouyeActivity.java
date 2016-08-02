package com.moonweather.app.activity;

import com.moonweather.app.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class ShouyeActivity extends Activity {
	private ImageView ima;;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shouye);
        ima=(ImageView) findViewById(R.id.ima);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(3*1000);
        alphaAnimation.setRepeatCount(0);
        ima.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
           
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            	
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            	Intent intent=new Intent(ShouyeActivity.this,ChooseAreaActivity.class);
            	startActivity(intent);
            	ShouyeActivity.this.finish();
            }
        });
    }
}
