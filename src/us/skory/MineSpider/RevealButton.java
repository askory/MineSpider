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
		this.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectedNode != null){
					selectedNode.reveal();
				}
			}
		});
	}
	
	@Override
	public void selectNode(Node n){
		super.selectNode(n);
		if (n != null && !n.isHidden()){
			this.setBackgroundColor(0x888800FF);
		} else {
			this.setBackgroundColor(0xFFFFFFFF);			
		}
	}

	
	
	
}
