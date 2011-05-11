package us.skory.MineSpider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MineSpider extends Activity {

	private static DrawableView mDrawableView;
	private static RevealButton mRevealButton;
	private static FlagButton mFlagButton;
	private static Button mNewButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDrawableView = (DrawableView) findViewById(R.id.drawableView);
        mRevealButton = (RevealButton) findViewById(R.id.revealButton);
        mFlagButton = (FlagButton) findViewById(R.id.flagButton);
        mDrawableView.registerButtons(mRevealButton, mFlagButton);
        mDrawableView.registerNodeSet(new NodeSet(20,5));
        
        mNewButton = (Button) findViewById(R.id.newButton);
        mNewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        mDrawableView.registerNodeSet(new NodeSet(20,5));
			}
		});
	}
}