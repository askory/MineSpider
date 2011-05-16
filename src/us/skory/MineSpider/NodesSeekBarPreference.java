package us.skory.MineSpider;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

public class NodesSeekBarPreference extends SeekBarPreference {

	private SeekBarPreference edgesSeekBar, minesSeekBar;
	
	public NodesSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void registerSeekBars(SeekBarPreference _edgesSeekBar, SeekBarPreference _minesSeekBar){
		this.edgesSeekBar = _edgesSeekBar;
		this.minesSeekBar = _minesSeekBar;
	}

//	@Override
//	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch){
//		if (this.edgesSeekBar != null && this.minesSeekBar != null){
//			int num_nodes = value + this.mMin;
//	    	this.minesSeekBar.setMax(num_nodes);
//			this.edgesSeekBar.setMin((num_nodes * 2) - 1);
//	    	if (num_nodes < 6){
//	    		this.edgesSeekBar.setMax((int) Math.pow(2, num_nodes));
//	    	} else {
//	    		this.edgesSeekBar.setMax(num_nodes * 10);
//	    	}
//		}
//		super.onProgressChanged(seek, value, fromTouch);
//	}

	
}
