package us.skory.MineSpider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class CustomButton extends Button {
	
	@SuppressWarnings("unused")
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
		this.setBackgroundColor(0xFFFFFFFF);
	}
	
	public void selectNode(Node n){
		this.selectedNode = n;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}
}
