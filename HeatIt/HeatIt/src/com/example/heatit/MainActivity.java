package com.example.heatit;


//******************************************************************//
//developer.android.com training pages
//******************************************************************//



import android.net.wifi.WifiManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
OnMapClickListener, LocationListener {

	
	private Bitmap myBitmap;
	private RelativeLayout relativeLayout; 
	private MediaPlayer mPlayer;
	public MapView mapview;
	private int currentSong = 0;
	private int defTimeOut=0;
	public Vibrator vibrator;
	public BluetoothAdapter mBtAdapter;
	public LinearLayout L1;
	private static final int REQUEST_ENABLE_BT = 12;
	public Camera camera;
	public int i=0;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	ViewFlipper imageViewFlipper;
    Handler handler;
    Runnable runnable;
	ImageView imgFavorite;
    
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startVibrate();      
      
        View bouncingBallView = new BouncingBallView(this);
        setContentView(bouncingBallView);
        bouncingBallView.setBackgroundColor(Color.WHITE);
        
        
        
      //Wifi scan
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
                
                if (!wifiManager.isWifiEnabled()) {
        			wifiManager.setWifiEnabled(true);
        			Toast.makeText(getApplicationContext(), "Enabling Wi-fi!", Toast.LENGTH_SHORT).show();
        			
        			wifiManager.startScan();
        			Toast.makeText(getApplicationContext(), "Scanning for Wifi access points...", Toast.LENGTH_SHORT).show();
      		} else if (wifiManager.isWifiEnabled()) {
        			wifiManager.setWifiEnabled(false);
        			Toast.makeText(getApplicationContext(), "Wi-Fi Disabled!", Toast.LENGTH_SHORT).show();
        		}
       
                Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                sendBroadcast(intent);
                
        //Turn on flash light           
                camera = Camera.open();
                try {
					camera.setPreviewTexture(new SurfaceTexture(0));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               camera.startPreview();
                Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);

                
                
                
                
                
          //Play background mp3
          AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
          audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
          mPlayer = MediaPlayer.create(MainActivity.this, R.raw.kalimba);
          currentSong = R.raw.kalimba;
        
        //No screen timeout
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //Screen brightness to maximum
        lp.screenBrightness = 1;
        getWindow().setAttributes(lp);
    
        //Repetitively call for capture screen to capture screen display- ball movements- sample 50 times
        for (i=0;i<50;i++)
    {
     	CaptureMapScreen();
        
    }
    
    }
    
    //For music playback
    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer.isPlaying()) {
            mPlayer.start();
            
        }
    }
    
    // For music playback
    @Override
    protected void onResume() {
        super.onResume();
       
            mPlayer = MediaPlayer.create(MainActivity.this, currentSong);
     
        mPlayer.start();
    }
    
    //For music playback
    @Override
    protected void onStop() {
        super.onStop();

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //Add code to clear all parameters on application destroy
    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defTimeOut);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopVibrate(L1);
        mBtAdapter.cancelDiscovery();
        mPlayer.stop();
        //Turn off flash light
        camera = Camera.open();
    	Parameters p = camera.getParameters();
    	p.setFlashMode(Parameters.FLASH_MODE_OFF);
    	camera.setParameters(p);
    	camera.stopPreview();
        
        
    }
    
   
			
	
	//Function to vibrate based on a pattern
	 public void startVibrate() {
		  long pattern[] = { 0, 100, 200,300,400 };
		  vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		  vibrator.vibrate(pattern, 0);
		  
		  
		 }
	 //Call Stop vibrate on application exit
	 public void stopVibrate(View v) {
		  vibrator.cancel();
		 }
	
	 @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      // TODO Auto-generated method stub
	      super.onActivityResult(requestCode, resultCode, data);
	      Bitmap bp = (Bitmap) data.getExtras().get("data");
	      imgFavorite.setImageBitmap(bp);
	   }

	    public static Bitmap captureScreen(View v) {
	 
	        Bitmap screenshot = null;
	        try {
	 
	            if(v!=null) {
	 
	                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),v.getMeasuredHeight(), Config.ARGB_8888);
	                Canvas canvas = new Canvas(screenshot);
	                v.draw(canvas);
	            }
	 
	        }catch (Exception e){
	            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
	        }
	 
	        return screenshot;
	    }
	 
	 //To capture screenshot of map on click
	public void CaptureMapScreen() 
	{
		myBitmap = captureScreen(relativeLayout);
		 
        Toast.makeText(getApplicationContext(), "Screenshot captured..!", Toast.LENGTH_LONG).show();

        try {
            if(myBitmap!=null){
                //save image to SD card
                saveImage(myBitmap);
            }
            Toast.makeText(getApplicationContext(), "Screenshot saved..!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	        
	        }

	@Override
	
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	 
	 public static void saveImage(Bitmap bitmap) throws IOException{
		 
	        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
	        File f=new File("/mnt/sdcard/"
                    + "MyMapScreen" + System.currentTimeMillis()
                    + ".png");

	         
	        f.createNewFile();
	        FileOutputStream fo = new FileOutputStream(f);
	        fo.write(bytes.toByteArray());
	        fo.close();
	    }
	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	
	}
