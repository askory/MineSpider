package us.skory.MineSpider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class CustomButton extends Button {
	
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final int ICON_TOP = 10;
	private static final int ICON_LEFT = 8;
	private static final int ICON_BOTTOM = 35;
	private static final int ICON_RIGHT = 33;

	protected Node selectedNode;
	protected Context mContext;
	private Paint paint;
	private Bitmap buttonIcon;
	private Rect rect;
	private String textPadding = "";
		
	public CustomButton(Context context){
		super(context);
		this.initButton(context, null);
	}

	public CustomButton(Context context, AttributeSet attr){
		super(context, attr);
		this.initButton(context, attr);
	}

	private void initButton(Context context, AttributeSet attr){
		this.mContext = context;
		if (attr != null){
			textPadding = attr.getAttributeValue(androidns, "text");
			paint = new Paint();
			rect = new Rect(ICON_LEFT,ICON_TOP,ICON_RIGHT,ICON_BOTTOM);
			Resources res = mContext.getResources();
			//Huge cludge until I can figure out this custom namespace business!
			int iconId = 0;
			for (int i = 0; i < attr.getAttributeCount(); i++){
		    	if (attr.getAttributeName(i).equals("icon")){
		    		iconId = attr.getAttributeResourceValue(i, 0);
		    	}
		    }			
			setButtonIcon(BitmapFactory.decodeResource(res, iconId));
		}
	}
	
	protected void setButtonIcon(Bitmap bm){
		this.buttonIcon = bm;
	}
	
	public void setTextSuffix(String suffix){
		this.setText(this.textPadding + suffix);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		if (buttonIcon != null)
			canvas.drawBitmap(buttonIcon, null, rect, paint);
		super.onDraw(canvas);
	}
	
	public void selectNode(Node n){
		this.selectedNode = n;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}
	
}