package com.example;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;

public class FakeActivity extends Activity {
       
       
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fakemain);
        toggleBrightness();
    }
   
    private void toggleBrightness() 
    {
        try 
        {
            ContentResolver cr = getContentResolver();
            int brightness = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);            
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness / 255.0f;
            getWindow().setAttributes(lp);
        } 
        catch (Exception e) 
        {
            
        }
        final Activity activity = this;
        Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() 
	    { 
	        public void run() 
	        { 
	        	activity.finish();
	        } 
	    }, 500); 
    }
}