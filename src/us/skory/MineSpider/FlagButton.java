package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

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
		this.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectedNode != null){
					if (!selectedNode.isFlagged()){
						if (selectedNode.flag()){
							setBackgroundColor(0x888800FF);
						}
					} else {
						if (selectedNode.unflag()){
							setBackgroundColor(0xFFFFFFFF);
						}
					}
				}
			}
		});		
	}

	@Override
	public void selectNode(Node n){
		super.selectNode(n);
		if (n != null && n.isFlagged()){
			this.setBackgroundColor(0x888800FF);
		} else {
			this.setBackgroundColor(0xFFFFFFFF);			
		}
	}
	
}
