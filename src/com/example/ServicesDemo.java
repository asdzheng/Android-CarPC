package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.UITimer;

public class ServicesDemo extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener
{
  Button buttonStart, buttonStop;
  public Handler uiHandler = new Handler();
  UITimer uitimer=null;
  SeekBar BarControl1;
  SeekBar BarControl2;
  
  public boolean isUsbConnected() 
  {
      Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
      return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
	  if(MyService.firstrun==0)
	  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Set service values
        boolean usbst= isUsbConnected();
        if(usbst==true){MyService.usbstatus = 1;}else{MyService.usbstatus = 0;}
        for(int i=0; i < 20; i++){MyService.lightarray[i]=-1;}     
        MyService.lightarray_pointer=0;
        //Start service
    	startService(new Intent(this, MyService.class));
    	//Quit starter activity
        finish();
	  }
	  else
	  {
		setTheme(android.R.style.Theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Set sliders
		BarControl1 = (SeekBar)findViewById(R.id.SpeechVolumeBar);
		BarControl1.setOnSeekBarChangeListener(this);
		BarControl2 = (SeekBar)findViewById(R.id.MusicVolumeBar);
		BarControl2.setOnSeekBarChangeListener(this);
		BarControl1.setMax(MyService.maxspeechvolume);
		BarControl2.setMax(MyService.maxmusicvolume);
		BarControl1.setProgress(MyService.audiostartnotify);
		BarControl2.setProgress(MyService.audiostartmusic);
    	buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
    	if(uitimer==null){uitimer = new UITimer(uiHandler, timerMethod, 1000);}      
    	uitimer.start();
	  }
  }

  @Override
  public void onStop() 
  {
	  uitimer.stop();
	  super.onStop();
  }

  public void onClick(View src) 
  {
    if (src.getId() == R.id.buttonStart) 
    {
		startService(new Intent(this, MyService.class));
	} 
    else if (src.getId() == R.id.buttonStop) 
	{
		stopService(new Intent(this, MyService.class));
	}
  }
  
  private Runnable timerMethod = new Runnable()
  {
      public void run()
      {
      	    TextView sender = (TextView) findViewById(R.id.powertextid);
            if(sender != null)
            {
           		  sender.setText("Event " + MyService.timercounter + " Light" + MyService.lightlevel + " USB " + MyService.usbstatus + " bl " + MyService.backlightlevel + " tts " + MyService.ttsready);
           	}
      }
  };
  
  public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) 
  {
	  if(arg0.getId() == R.id.SpeechVolumeBar && arg2==true)
	  {
		  MyService.audiostartnotify=arg1;
		  MyService.audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, arg1 , 0);
	      ObjectOutput out;
	      try 
	      {
			out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(),"")+"NotifyVolume.srl"));
			out.writeObject( arg1 );
		    out.close();
	      } catch (FileNotFoundException e) 
	      {
			//e.printStackTrace();
	      } catch (IOException e) 
	      {
			//e.printStackTrace();
	      }
	  }
	  
	  if(arg0.getId() == R.id.MusicVolumeBar && arg2==true)
	  {
		  MyService.audiostartmusic=arg1;
		  MyService.audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1 , 0);
	      ObjectOutput out;
	      try 
	      {
			out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(),"")+"MusicVolume.srl"));
			out.writeObject( arg1 );
		    out.close();
	      } catch (FileNotFoundException e) 
	      {
			//e.printStackTrace();
	      } catch (IOException e) 
	      {
			//e.printStackTrace();
	      }   
	  }
  }
  
  public  void  onStartTrackingTouch(SeekBar seekBar) {} 
  public  void  onStopTrackingTouch(SeekBar seekBar) {} 
}