package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class NodesSeekBarPreference extends SeekBarPreference {

	private EdgesSeekBarPreference edgesSeekBar;
	private MinesSeekBarPreference minesSeekBar;
	
	public NodesSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void registerSeekBars(EdgesSeekBarPreference _edgesSeekBar, MinesSeekBarPreference _minesSeekBar){
		this.edgesSeekBar = _edgesSeekBar;
		this.minesSeekBar = _minesSeekBar;
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch){
		if (this.edgesSeekBar != null && this.minesSeekBar != null){
			int num_nodes = value + mMin;
	    	this.minesSeekBar.update(num_nodes);
	    	this.edgesSeekBar.update(num_nodes);
		}
		super.onProgressChanged(seek, value, fromTouch);
	}

	
}
