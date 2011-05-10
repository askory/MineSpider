package us.skory.MineSpider;

import android.app.Activity;
import android.os.Bundle;

public class MineSpider extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawableView(this));
    }
}