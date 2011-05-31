package us.skory.MineSpider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class CustomButton extends Button {
	
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final float ICON_TOP = 0.15f;
	private static final float ICON_LEFT = 0.14f;
	private static final float ICON_BOTTOM = 0.8f;
	private static final float ICON_RIGHT = 0.8f;

	protected Node selectedNode;
	protected Context mContext;
	protected DrawableView drawableView;
	private Paint paint;
	private Bitmap buttonIcon;
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
	
	protected void setDrawableView(DrawableView _drawableView){
		this.drawableView = _drawableView;
	}
	
	protected void setButtonIcon(Bitmap bm){
		this.buttonIcon = bm;
	}
	
	protected Rect scaleRect(){
		int width;
		if (!this.getText().equals(this.textPadding)){
			width = (int) (this.getWidth() - this.getTextSize() - this.getPaddingRight());
		}else{
			width = this.getWidth();
		}
		int height = this.getHeight();
		int left = (int) (ICON_LEFT * width);
		int top = (int) (ICON_TOP * height);
		int right = (int) (ICON_RIGHT * width);
		int bottom = (int) (ICON_BOTTOM * height);
		return new Rect(left,top,right,bottom);
	}
	
	public void setTextSuffix(String suffix){
		this.setText(this.textPadding + suffix);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		if (buttonIcon != null)
			canvas.drawBitmap(buttonIcon, null, scaleRect(), paint);
		super.onDraw(canvas);
	}
	
	public void selectNode(Node n){
		this.selectedNode = n;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}

}