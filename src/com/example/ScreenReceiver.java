package com.example;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

public class ScreenReceiver extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) 
        {
        	if(MyService.usbstatus==1)
        	{
	        	KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE); 
	    		KeyguardLock lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE); 
	    		lock.disableKeyguard();
	    		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	            long l = SystemClock.uptimeMillis();
	            powerManager.userActivity(l, false);
	            lock.disableKeyguard();
        	}
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) 
        {
        	if(MyService.usbstatus==1)
        	{
	        	KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE); 
	    		KeyguardLock lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE); 
	    		lock.disableKeyguard();
	    		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	            long l = SystemClock.uptimeMillis();
	            powerManager.userActivity(l, false);
	            lock.disableKeyguard();
        	}
        }
    }

}