package com.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) 
        {
        	MyService.usbstatus=1;
        	Toast.makeText(context, "Zündung aktiviert", Toast.LENGTH_LONG).show();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) 
        {
        	MyService.usbstatus=0;
        	Toast.makeText(context, "Zündung aus", Toast.LENGTH_LONG).show();
        }
    }
}