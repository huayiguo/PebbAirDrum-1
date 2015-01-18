package hackathon.smarthit;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleAckReceiver;
import com.getpebble.android.kit.PebbleKit.PebbleNackReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class Main extends Activity {
	boolean PBCONNECTED;
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("0b8f7129-f5f2-4681-b16a-9820f0d5a8f4");
	private final static int WHICH_HAND_KEY = 4;
    private LinkedHashMap<Integer, Float> datas = new LinkedHashMap<Integer, Float>();;
	private Canvas current =null;
	private String variable;
	
	private Bitmap drum;
	private int id =100;
	private int flag = 1;
	private int count =0;
	private  Intent svc;
	private MediaRecorder myAudioRecorder;
	private MediaPlayer player;
	private int current_step=1;
	private BitmapDrawable bmd;
	
	private String outputFile = null;
	private int value;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.activity_main);
	    
	    Bundle extras = getIntent().getExtras();
	    value = extras.getInt("hand");
	    
	    ImageButton start = (ImageButton) findViewById(R.id.Start);
	    ImageButton stop= (ImageButton) findViewById(R.id.Stop);
	    ImageButton play = (ImageButton) findViewById(R.id.Play);
	    ImageButton stop_back=(ImageButton)findViewById(R.id.Stop_back);
	    
	    stop_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService(svc);
				
			}});
	    
	    outputFile = Environment.getExternalStorageDirectory().
	    	      getAbsolutePath() + "/myrecording.3gp";;
	    	      
	  
	  
   
	    start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				      try {
				    	 myAudioRecorder = new MediaRecorder();
				  	     myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				  	     myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				  	     myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB); 
				    	 myAudioRecorder.setOutputFile(outputFile);
				         myAudioRecorder.prepare();
				         myAudioRecorder.start();
				      } catch (IllegalStateException e) {
				         // TODO Auto-generated catch block
				         e.printStackTrace();
				      } catch (IOException e) {
				         // TODO Auto-generated catch block
				         e.printStackTrace();
				      }
				      Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
			}
	    });
	    
	    stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  myAudioRecorder.stop();
				  myAudioRecorder.release();
				  //myAudioRecorder  = null;
				
				  Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				  Toast.LENGTH_SHORT).show();	  
			}
	    });
	    
	    play.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			  
				   MediaPlayer m = new MediaPlayer();
				   try {
					m.setDataSource(outputFile);
					m.prepare();
					m.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				   Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
			}
	    });
	    
	    final ImageButton drum = (ImageButton) findViewById(R.id.drum);
	    drum.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum1);
				//MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.1);
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
					}});
				
				// Shake(1);
        		
				Resources resources = getResources();
				drum.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));

			}});
	    
	    final ImageButton drum1 = (ImageButton) findViewById(R.id.drum1);
	    drum1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum2);
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer mp) {

						mp.release();
					}});
				 //Shake(2);
				Resources resources = getResources();
				drum1.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));	
			}});
	    
	    final ImageButton drum2 = (ImageButton) findViewById(R.id.drum2);
	    drum2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum3);
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
					}});
					
				// Shake(3);
				Resources resources = getResources();
				drum2.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));

			}});
	    
	    final ImageButton drum3 = (ImageButton) findViewById(R.id.drum3);
	    drum3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum4);
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
					}});
				Resources resources = getResources();
				drum3.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));

			}});
	   
	    PBCONNECTED = PebbleKit.isWatchConnected(getApplicationContext());
        PebbleKit.registerPebbleConnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                      //  Log.i(getLocalClassName(), "Pebble connected!");
                        PBCONNECTED = true;
            }
        });
        PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                 
                    PBCONNECTED = false;
                }
        });

        //Processing the Pebble information
        final Handler beathandler = new Handler();
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
                @Override
                public void receiveData(final Context context, final int transactionId,  final PebbleDictionary data) {
                        
                        beathandler.post(new Runnable() {
                            @Override
                            public void run() {
                                long Xvalue = data.getInteger(0);
                                long Yvalue = data.getInteger(1);
                                long Zvalue = data.getInteger(2);
                                      
                                int next_step = translation(current_step,Yvalue*value,Xvalue*value);
                                current_step=next_step;
                                MediaPlayer mPlayer =null;
                                switch(next_step){
                                case 1:
                                	mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum1);
                                	runOnUiThread(new Runnable() {
                						@Override
                						public void run() {
                							ImageButton flash = (ImageButton)findViewById(R.id.drum);
                							Resources resources = getResources();
                							flash.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));
                							
                						}
                					});
                                	
                                	break;
                                case 2:
                                	mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum2);
                                	runOnUiThread(new Runnable() {
                						@Override
                						public void run() {
                							ImageButton flash3 = (ImageButton)findViewById(R.id.drum1);
                							Resources resources = getResources();
                							flash3.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));
                							
                						}
                					});
                                	
                                	break;
                                case 3:
                                	mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum3);
                                	runOnUiThread(new Runnable() {
                						@Override
                						public void run() {
                							ImageButton flash2 = (ImageButton)findViewById(R.id.drum2);
                							Resources resources = getResources();
                							flash2.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));
                							
                						}
                					});
                                	
                                	break;
                                case 4:
                                	mPlayer = MediaPlayer.create(getBaseContext(), R.raw.drum4);
                                	runOnUiThread(new Runnable() {
                						@Override
                						public void run() {
                							ImageButton flash4 = (ImageButton)findViewById(R.id.drum3);
                							Resources resources = getResources();
                							flash4.setImageDrawable(resources.getDrawable(R.drawable.realdrum_hit));
                							
                						}
                					});
                                	
                                	break;
                                default:
                                	break;
                                }
                               
                				mPlayer.start();
                				mPlayer.setOnCompletionListener(new OnCompletionListener(){
                					@Override
                					public void onCompletion(MediaPlayer mp) {
                						// TODO Auto-generated method stub
                						mp.release();
                					}});
                                System.out.println("data: "+Xvalue+" "+Yvalue);
                                //Toast.makeText(getApplicationContext(), "data: "+Xvalue+" "+Yvalue, Toast.LENGTH_SHORT).show();
                            }

                        });
                        PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
                }
        });
        PebbleKit.registerReceivedAckHandler(getApplicationContext(), new PebbleAckReceiver(PEBBLE_APP_UUID){
			@Override
			public void receiveAck(Context context, int transactionId) {
				// TODO Auto-generated method stub
				Log.i(getLocalClassName(),"Received Ack from Pbl.");
			}
        });
        PebbleKit.registerReceivedNackHandler(getApplicationContext(), new PebbleNackReceiver(PEBBLE_APP_UUID){
			@Override
			public void receiveNack(Context context, int transactionId) {
				// TODO Auto-generated method stub
				Log.i(getLocalClassName(),"Received Nack from Pbl.");
				Toast.makeText(getApplicationContext(), "can't send info to pebble",Toast.LENGTH_SHORT).show();
			}
        });
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
        PebbleDictionary data = new PebbleDictionary();
        data.addInt32(WHICH_HAND_KEY, value);
        System.out.println("value is "+value);
        PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);
        TimerThread refresh = new TimerThread();
        refresh.start();
        
        svc=new Intent(this, BackgroundSoundService.class);
        startService(svc);
	}
	

	public int translation(int current_step, long zvalue, long xvalue){
		int next_step =1;
		
		System.out.println("curr: "+current_step);
		switch(current_step){
		case 1:
			if(zvalue>0&&xvalue<=0)next_step=2;
			else if(zvalue<=0&&xvalue>0)next_step=3;
			else if(zvalue>0&&xvalue>0)next_step=4;
			else next_step=1;
			break;
		case 2:
			if(zvalue<0&&xvalue<=0)next_step=1;
			else if(zvalue>=0&&xvalue>0)next_step=4;
			else if(zvalue<0&&xvalue>0)next_step=3;
			else next_step=2;
			break;
		case 3:
			if(zvalue>0&&xvalue>=0)next_step=4;
			else if(zvalue<=0&&xvalue<0)next_step=1;
			else if(zvalue>0&&xvalue<0)next_step=2;
			else next_step=3;
			break;
		case 4:
			if(zvalue<0&&xvalue>=0)next_step=3;
			else if(zvalue>=0&&xvalue<0)next_step=2;
			else if(zvalue<0&&xvalue<0)next_step=1;
			else next_step=4;
			break;
		default:
			break;
		}
		System.out.println("next: "+ next_step);
		return next_step;
	}
	

	private class TimerThread extends Thread{
		public void run() {
			while(true){
				try {
					
					Thread.sleep(500);
					if(count==2){
						count=0;
						MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.gong);
						mPlayer.start();
						mPlayer.setOnCompletionListener(new OnCompletionListener(){
							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub
								mp.release();
							}});
						runOnUiThread(new Runnable() {
							@Override							
							public void run() {
								ImageButton gong = (ImageButton)findViewById(R.id.gong);
								gong.setImageDrawable(getResources().getDrawable(R.drawable.gong));

							}
						});
						
					}
					count++;
					runOnUiThread(new Runnable() {
						@Override
						
						public void run() {
							ImageButton drum = (ImageButton)findViewById(R.id.drum);
							drum.setImageDrawable(getResources().getDrawable(R.drawable.realdrum));
							ImageButton drum1 = (ImageButton)findViewById(R.id.drum1);
							drum1.setImageDrawable(getResources().getDrawable(R.drawable.realdrum));
							ImageButton drum2 = (ImageButton)findViewById(R.id.drum2);
							drum2.setImageDrawable(getResources().getDrawable(R.drawable.realdrum));
							ImageButton drum3 = (ImageButton)findViewById(R.id.drum3);
							drum3.setImageDrawable(getResources().getDrawable(R.drawable.realdrum));
							if(count!=1){
								ImageButton gong = (ImageButton)findViewById(R.id.gong);
								gong.setImageDrawable(getResources().getDrawable(R.drawable.gong_1));
							}
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}

	public void Shake(int number){

		switch(number){
		case 1:
			 ImageButton img = (ImageButton)findViewById(R.id.drum);
			 img.setImageDrawable(bmd);
			break;
		case 2:
			 ImageButton img2 = (ImageButton)findViewById(R.id.drum1);
			 img2.setImageDrawable(bmd);
			break;
		case 3:
			 ImageButton img3 = (ImageButton)findViewById(R.id.drum2);
			 img3.setImageDrawable(bmd);
			break;
		case 4:
			 ImageButton img4 = (ImageButton)findViewById(R.id.drum3);
			 img4.setImageDrawable(bmd);
			break;
		default:
			break;
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
	
	public class BackgroundSoundService extends Service {
	   // private static final String TAG = null;
		
	    public IBinder onBind(Intent arg0) {

	        return null;
	    }
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        player = MediaPlayer.create(this, R.raw.gong);
	        player.setLooping(true); // Set looping
	        player.setVolume(0.1f,0.1f);

	    }
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        player.start();
	        return 1;
	    }

	    public void onStart(Intent intent, int startId) {
	        // TO DO
	    }
	    public boolean onUnBind(Intent arg0) {
	        // TO DO Auto-generated method
	    	player.setLooping(false);
	        player.stop();
		    player.release();
	        return false;
	    }
	   
	    @Override
	    public void onDestroy() {
	        player.stop();
	        player.release();
	    }

	    @Override
	    public void onLowMemory() {
	    	
	    }
	    
	    
	 }
	
}
