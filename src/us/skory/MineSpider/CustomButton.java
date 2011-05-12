package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public abstract class CustomButton extends Button {
	
	protected Node selectedNode;
		
	public CustomButton(Context context){
		super(context);
		this.initButton();
	}

	public CustomButton(Context context, AttributeSet attr){
		super(context, attr);
		this.initButton();
	}

	private void initButton(){
	}
	
	public void selectNode(Node n){
		this.selectedNode = n;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}
	
	public abstract void onPress();

	@Override
	public boolean onTouchEvent(MotionEvent e){
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			onPress();
		}
		return true;
	}
}