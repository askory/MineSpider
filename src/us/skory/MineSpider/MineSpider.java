package us.skory.MineSpider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MineSpider extends Activity {

	private static DrawableView mDrawableView;
	private static RevealButton mRevealButton;
	private static FlagButton mFlagButton;
	private static CustomButton mNewButton;
	private static CustomButton mPrefsButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.main);

        mDrawableView = (DrawableView) findViewById(R.id.drawableView);
        mRevealButton = (RevealButton) findViewById(R.id.revealButton);
        mRevealButton.setDrawableView(mDrawableView);
        mFlagButton = (FlagButton) findViewById(R.id.flagButton);
        mFlagButton.setDrawableView(mDrawableView);
        mDrawableView.registerItems(mRevealButton, mFlagButton);
        mDrawableView.initNodeSet();
        
        mNewButton = (CustomButton) findViewById(R.id.newButton);
        mNewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mDrawableView.initNodeSet();
		        mDrawableView.invalidate();
			}
		});

        mPrefsButton = (CustomButton) findViewById(R.id.prefsButton);
        mPrefsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingsActivity = new Intent(getBaseContext(),MineSpiderPreferenceActivity.class);
				startActivity(settingsActivity);
			}
		});

	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.v("[MineSpider]", "onPause called");
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.v("[MineSpider]", "onResume called");
	}
}