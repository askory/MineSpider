package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FlagButton extends CustomButton {
	
	public FlagButton(Context context){
		super(context);
		initFlagButton();
	}

	public FlagButton(Context context, AttributeSet attr){
		super(context, attr);
		initFlagButton();
	}

	private void initFlagButton() {
	}

	@Override
	public void selectNode(Node n){
		super.selectNode(n);
		if (n != null && n.isFlagged()){
			this.setPressed(true);
		} else {
			this.setPressed(false);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			if (selectedNode != null){
				if (!selectedNode.isFlagged()){
					if (selectedNode.flag()){
						setPressed(true);
					}
				} else {
					if (selectedNode.unflag()){
						setPressed(false);
					}
				}
			}
		}
		if (this.drawableView != null){
			this.drawableView.invalidate();
		}
		return true;
	}

}