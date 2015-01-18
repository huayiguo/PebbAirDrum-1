package hackathon.smarthit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Habbit extends Activity {
	private int flag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_habbit);
		
		ImageButton left = (ImageButton)findViewById(R.id.Left);
		left.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(flag==0){
					ImageView needle = (ImageView) findViewById(R.id.Left_hand);
					RotateAnimation r = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r.setDuration((long) 700);
					r.setRepeatCount(0);
					r.setFillAfter(true);
					needle.startAnimation(r);
				}else if(flag==1){
					ImageView needle = (ImageView) findViewById(R.id.Left_hand);
					RotateAnimation r = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r.setDuration((long) 700);
					r.setRepeatCount(0);
					r.setFillAfter(true);
					needle.startAnimation(r);
					
					ImageView needle2 = (ImageView) findViewById(R.id.Right_hand);
					RotateAnimation r2 = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r2.setDuration((long) 700);
					r2.setRepeatCount(0);
					r2.setFillAfter(true);
					needle2.startAnimation(r2);
					
				}else{
					
				}
				flag = -1;
				
			}});
		
		ImageButton right = (ImageButton)findViewById(R.id.Right);
		right.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(flag==0){
					ImageView needle = (ImageView) findViewById(R.id.Right_hand);
					RotateAnimation r = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r.setDuration((long) 700);
					r.setRepeatCount(0);
					r.setFillAfter(true);
					needle.startAnimation(r);
				}else if(flag==-1){
					ImageView needle = (ImageView) findViewById(R.id.Right_hand);
					RotateAnimation r = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r.setDuration((long) 700);
					r.setRepeatCount(0);
					r.setFillAfter(true);
					needle.startAnimation(r);
					
					ImageView needle2 = (ImageView) findViewById(R.id.Left_hand);
					RotateAnimation r2 = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					r2.setDuration((long) 700);
					r2.setRepeatCount(0);
					r2.setFillAfter(true);
					needle2.startAnimation(r2);
					
				}else{
					
				}
				flag = 1;
			}});

		ImageButton confirm= (ImageButton)findViewById(R.id.Confirm);
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if(flag==0) flag=1;
				intent.putExtra("hand", -flag);
				intent.setClass(Habbit.this, Main.class);
				Habbit.this.startActivity(intent);
				finish();
			}});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.habbit, menu);
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
}
