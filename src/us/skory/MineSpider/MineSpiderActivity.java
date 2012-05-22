package us.skory.MineSpider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class MineSpiderActivity extends Activity {

	private static DrawableView mDrawableView;
	private static RevealButton mRevealButton;
	private static FlagButton mFlagButton;
	private static CustomButton mNewButton;
	private static CustomButton mPrefsButton;

	private MineSpiderPresenter mPresenter;

	private int mNumNodes;
	private int mNumMines;
	private int mNumEdges;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.main);

        mDrawableView = (DrawableView) findViewById(R.id.drawableView);
        mRevealButton = (RevealButton) findViewById(R.id.revealButton);
        mFlagButton = (FlagButton) findViewById(R.id.flagButton);
        
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

		NodeSet nodeSet = null;
		if (savedInstanceState != null) {
			nodeSet = savedInstanceState.getParcelable("nodeSet");
		}
        if (nodeSet == null) {
        	nodeSet = readNodeSet();
        }
        if (nodeSet == null) {
        	nodeSet = createNewNodeSet();
        }

        mPresenter = new MineSpiderPresenter(nodeSet, mDrawableView, mRevealButton, mFlagButton,
        		alertBuilder);

        mNewButton = (CustomButton) findViewById(R.id.newButton);
        mNewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        mPresenter.setNodeSet(createNewNodeSet());
			}
		});

        mPrefsButton = (CustomButton) findViewById(R.id.prefsButton);
        mPrefsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent settingsActivity = new Intent(getBaseContext(), MineSpiderPreferenceActivity.class);
				startActivity(settingsActivity);
			}
		});

	}

	private NodeSet readNodeSet() {
		NodeSet nodeSet = null;
		try {
			FileInputStream fis = openFileInput("node_set.json");
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer, 0, fis.available());
			String jsonString = new String(buffer);
			nodeSet = NodeSet.fromJson(new JSONArray(jsonString));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return nodeSet;
	}

	private void writeNodeSet() {
		JSONArray nodeSetJson = mPresenter.getNodeSet().toJson();
		try {
			FileOutputStream fos = openFileOutput("node_set.json", Context.MODE_PRIVATE);
			fos.write(nodeSetJson.toString().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("nodeSet", mPresenter.getNodeSet());
	}

	@Override
	public void onPause() {
		writeNodeSet();
		super.onPause();
	}

	private NodeSet createNewNodeSet() {
		mNumNodes = getPref("num_nodes",10,-1);
		mNumMines = getPref("num_mines", 2, mNumNodes);
		mNumEdges = getPref("num_edges", mNumNodes * 2, mNumNodes * 10);
		return new NodeSet(mNumNodes, mNumMines, mNumEdges);
	}

	private int getPref(String name, int d, int max){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int val = sharedPrefs.getInt(name, d);
		if (max > 0 && val > max){
			return max;
		}
		return val > 0 ? val : d;
	}

}