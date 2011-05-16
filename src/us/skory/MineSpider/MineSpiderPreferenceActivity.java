package us.skory.MineSpider;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MineSpiderPreferenceActivity extends PreferenceActivity {
 
	NodesSeekBarPreference nodesSeekBar;
	EdgesSeekBarPreference edgesSeekBar;
	MinesSeekBarPreference minesSeekBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        nodesSeekBar = (NodesSeekBarPreference) this.findPreference("num_nodes");
        edgesSeekBar = (EdgesSeekBarPreference) this.findPreference("num_edges");
        minesSeekBar = (MinesSeekBarPreference) this.findPreference("num_mines");
        nodesSeekBar.registerSeekBars(edgesSeekBar, minesSeekBar);
    }
}
