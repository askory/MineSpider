package us.skory.MineSpider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RevealButton extends CustomButton {
	
	public RevealButton(Context context){
		super(context);
		initRevealButton();
	}

	public RevealButton(Context context, AttributeSet attr){
		super(context, attr);
		initRevealButton();
	}

	private void initRevealButton(){
	}
		
	@Override
	public void selectNode(Node n){
		super.selectNode(n);
		if (n != null && !n.isHidden()){
			this.setPressed(true);
		} else {
			this.setPressed(false);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e){
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			if (selectedNode != null){
				selectedNode.reveal(true);
				setPressed(true);
			}
		}
		return true;
	}
	
}
