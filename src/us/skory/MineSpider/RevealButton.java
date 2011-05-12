package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
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

	public void onPress() {
		if (selectedNode != null){
			selectedNode.reveal();
			setPressed(true);
		}
	}

	
	
	
}
