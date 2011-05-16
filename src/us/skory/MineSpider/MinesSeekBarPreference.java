package us.skory.MineSpider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class MinesSeekBarPreference extends SeekBarPreference {

	public MinesSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		update();
	}

	public void update(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		int num_nodes = sharedPrefs.getInt("num_nodes", 10);
    	setMax(num_nodes);
	}

	public void update(int num_nodes){
		setMax(num_nodes);
	}
}
