package us.skory.MineSpider;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MineSpiderPreferenceActivity extends PreferenceActivity {
 
	NodesSeekBarPreference nodesSeekBar;
	SeekBarPreference edgesSeekBar;
	SeekBarPreference minesSeekBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
//        nodesSeekBar = (NodesSeekBarPreference) this.findPreference("num_nodes");
//        edgesSeekBar = (SeekBarPreference) this.findPreference("num_edges");
//        minesSeekBar = (SeekBarPreference) this.findPreference("num_mines");
//        nodesSeekBar.registerSeekBars(edgesSeekBar, minesSeekBar);
    }
}
