package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;



public class MyService extends Service implements SensorEventListener, OnInitListener
{
	static public float[] lightarray = new float[20];
	static public int[] lightarraybuffer = new int[20];
	static public int lightarray_pointer = 0;
	static public int firstrun = 0;
	static public int usbstatus;
	static public float lightlevel = 0;
	static public float oldlightlevel = 0;
	static int timercounter=0;
	private static Timer timer = new Timer();
	private SensorManager mSensorManager;
	float currentLux = 0;
	public static TextToSpeech tts = null;
	static public AudioManager audiomanager;
	//HashMap<String, String> myHashStream = new HashMap<String, String>();
	public static final String PREFS_NAME = "MyPrefsFile";
	static public int maxspeechvolume=0;
	static public int maxmusicvolume=0;
	static public int ttsready=0;
	static public int oldusbstatus=0;
	static public int usbdecounter=10;
	static public int backlightlevel=255;
	static public int audiostartnotify=20;
	static public int audiostartmusic=20;
    private static final int SCREEN_MODE_MANUAL = 0;
    private static final int SCREEN_MODE_AUTO = 1;
    private PowerManager.WakeLock wakelock;
	
	void restore_settings()
	{
		// Restore preferences
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(new File(getCacheDir(),"")+"NotifyVolume.srl"));
			try {
				audiostartnotify = (Integer) in.readObject();
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			}
			in.close();
		} catch (StreamCorruptedException e) {
			//e.printStackTrace();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		try {
			in = new ObjectInputStream(new FileInputStream(new File(getCacheDir(),"")+"MusicVolume.srl"));
			try {
				audiostartmusic = (Integer) in.readObject();
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			}
			in.close();
		} catch (StreamCorruptedException e) {
			//e.printStackTrace();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	void calculate_usb()
	{
    	if(oldusbstatus==0 && usbstatus==1)
    	{
    		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE); 
    		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE); 
    		lock.disableKeyguard();
    		Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT ,-1);
    		//setbacklightlevel();
    		oldusbstatus=usbstatus;
    		wakelock.acquire();
    		if(usbdecounter==0)
    		{
    			if(ttsready==1)
    			{
    				SystemClock.sleep(1000);
		    	    if(ttsready==1)
		    	    {
		    	    	tts.speak("Einsatzbereit", TextToSpeech.QUEUE_FLUSH, null /*myHashStream*/);
		    	    }
    			}
    			else
    			{
    				SystemClock.sleep(3000);
		    	    if(ttsready==1)
		    	    {
		    	    	tts.speak("Einsatzbereit", TextToSpeech.QUEUE_FLUSH, null /*myHashStream*/);
		    	    }
    			}
    		}
    		usbdecounter=10;
    		lock.disableKeyguard();
    	}
    	if(oldusbstatus==1 && usbstatus==0 && usbdecounter < 10)
    	{
	    	oldusbstatus=usbstatus;
	    	Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT ,70000); 
	    	//Backlight down
	    	//int oldlevel=backlightlevel;
	    	//backlightlevel=1;
	    	//setbacklightlevel();
	    	//backlightlevel=oldlevel;
	    	wakelock.release();
    	}
    	if(usbstatus == 0 && usbdecounter > 0)
    	{
    		usbdecounter--;
    		if(usbdecounter==0)
    		{
    			//for sure offline
    			audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audiostartnotify , 0);
    			audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, audiostartmusic , 0);		
    		}
    	}
	}
	
	void setbacklightlevel()
	{
		android.provider.Settings.System.putInt(getContentResolver(),
      		     android.provider.Settings.System.SCREEN_BRIGHTNESS,
      		     backlightlevel);
		Intent dialogIntent = new Intent(getBaseContext(), FakeActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(dialogIntent);
		SystemClock.sleep(1500);
	}
	
	int calculateLight() 
	{
		int count=0;
		int summe=0;
		for(int li=0; li < 20; li++)
		{
			if(lightarray[li] >= 0)
			{
				summe=summe+(int) lightarray[li];
				count++;
				lightarray[li]=-1;
			}
		}
		if(count==0){return -1;}
		if(count==1){return summe;}
		summe=summe/count;
		return summe;
	}
	
	private class mainTask extends TimerTask
    {
		public void run()
        {
			synchronized(this)
			{
	        	int lightres=calculateLight();
	        	if(lightres != -1) // detect a change in the lightsensor
	        	{
	        		lightlevel=lightres;	
	        	}
	        	int vbacklightlevel=1;
	        	if(usbstatus == 0 && usbdecounter < 10){vbacklightlevel=1;}
	        	else if(lightlevel >= 80){vbacklightlevel=255;}
	    		else if(lightlevel > 40){vbacklightlevel=200;}
	    		else{vbacklightlevel=100;}
	        	if(vbacklightlevel != backlightlevel)
	        	{
	        		backlightlevel=vbacklightlevel;
	        		setbacklightlevel();	
	        	}	 
	        	timercounter++;
	        	calculate_usb();
			}
        }
    };
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		restore_settings();
		//Auto sensor backlight off
		ContentResolver cr = getContentResolver();
		boolean autoBrightOn = (Settings.System.getInt(cr,android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,-1)==SCREEN_MODE_AUTO);
        if(autoBrightOn==true)
        { 
              Settings.System.putInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_MANUAL);
              Toast.makeText(this, "Disabling 'Automatic Brightness'", Toast.LENGTH_SHORT).show();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CivicWakeLock");
	}

	@Override
	public void onDestroy() 
	{
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
		timer.cancel();
		timer.purge();
		if(tts != null)
		{
		     tts.stop();
		     tts.shutdown();
		     tts=null;
		}
		super.onDestroy();
		wakelock.release();
	}
	
	@Override
	public void onStart(Intent intent, int startid) 
	{
		if(firstrun==0)
		{
			// register receiver that handles screen on and screen off logic
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            BroadcastReceiver mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);
			KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE); 
    		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE); 
    		lock.disableKeyguard();
			mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			Sensor mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			mSensorManager.registerListener(this,mLight, SensorManager.SENSOR_DELAY_UI);
			audiomanager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			maxspeechvolume=audiomanager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			maxmusicvolume=audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audiostartnotify , 0);
			audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, audiostartmusic , 0);
			tts = new TextToSpeech(getApplicationContext(),this);
			tts.setLanguage(Locale.GERMAN);
			tts.setSpeechRate(0.3f);
            //myHashStream.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            //myHashStream.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");
            if(usbstatus==1)
            {
            	oldusbstatus=1;
            	Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT ,-1);
    	    	setbacklightlevel();
    	    	wakelock.acquire();
        		lock.disableKeyguard();
            }
            else
            {
            	oldusbstatus=0;
            	Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT , 70000);
            	int oldlevel=backlightlevel;
    	    	backlightlevel=1;
    	    	setbacklightlevel();
    	    	backlightlevel=oldlevel;
    	    	lock.disableKeyguard();
            }
			MyService.firstrun=1;
			Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
			timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
		}
	}
	
	  @Override
	  public void onSensorChanged(SensorEvent event) 
	  {
		  synchronized(this)
		  {
		      if( event.sensor.getType() == Sensor.TYPE_LIGHT)
		      {
		    	  lightarray[MyService.lightarray_pointer]=event.values[0];
		    	  lightarray_pointer++;
		    	  if(lightarray_pointer >= 20){lightarray_pointer=0;}
		      }
		  }
	  }
	  
	  @Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) 
	  {
			// TODO
	  }
	  
	  @Override
	  public void onInit(int arg0) 
	  {
		  if (arg0 == TextToSpeech.SUCCESS)
	      {
			  ttsready=1;
	      }
	 }
}

