package chris.test.nfcproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Splash extends AppCompatActivity {
	
	private Context mContext = this;
	
	private int STARTUP_TIME = 4000;
	
	final Handler handler = new Handler( );
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		
		handler.postDelayed(new Runnable( ) {
			@Override
			public void run () {
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
			}
		}, STARTUP_TIME);
		
	}
	
}
