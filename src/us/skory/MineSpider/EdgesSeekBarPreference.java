package us.skory.MineSpider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class EdgesSeekBarPreference extends SeekBarPreference {

	public EdgesSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		update();
	}

	public void update(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		int num_nodes = sharedPrefs.getInt("num_nodes", 10);
		setMin(num_nodes);
    	if (num_nodes < 6){
    		setMax((int) Math.pow(2, num_nodes));
    	} else {
    		setMax(num_nodes * 10);
    	}
	}

	public void update(int num_nodes){
		setMin(num_nodes);
    	if (num_nodes < 6){
    		setMax((int) Math.pow(2, num_nodes));
    	} else {
    		setMax(num_nodes * 10);
    	}		
	}
}
