package us.skory.MineSpider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MineSpider extends Activity {

	private static DrawableView mDrawableView;
	private static RevealButton mRevealButton;
	private static FlagButton mFlagButton;
	private static TextView mCountsTextView;
	private static Button mNewButton;
	private static Button mPrefsButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDrawableView = (DrawableView) findViewById(R.id.drawableView);
        mRevealButton = (RevealButton) findViewById(R.id.revealButton);
        mFlagButton = (FlagButton) findViewById(R.id.flagButton);
        mCountsTextView = (TextView) findViewById(R.id.countsTextView);
        mDrawableView.registerItems(mRevealButton, mFlagButton, mCountsTextView);
        mDrawableView.initNodeSet();
        
        mNewButton = (Button) findViewById(R.id.newButton);
        mNewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mDrawableView.initNodeSet();
			}
		});

        mPrefsButton = (Button) findViewById(R.id.prefsButton);
        mPrefsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingsActivity = new Intent(getBaseContext(),MineSpiderPreferenceActivity.class);
				startActivity(settingsActivity);
			}
		});

	}
}