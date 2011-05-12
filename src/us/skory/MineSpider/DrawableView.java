package us.skory.MineSpider;

import android.util.AttributeSet;
import android.util.Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class DrawableView extends View {
	
	private Context mContext;
	private NodeSet nodeSet;
	private Node selectedNode;
	private RevealButton revealButton;
	private FlagButton flagButton;

	public static int SIDE_PADDING = 5;
	public static float NODE_DRAW_RADIUS = 0.03f;
	public static float NODE_TOUCH_AREA = 0.045f;
	
	public DrawableView(Context context) {
		super(context);
		initView(context);
	}
	
	public DrawableView(Context context, AttributeSet attr) {
		super(context, attr);
		initView(context);
	}
	
	private void initView(Context context){
		mContext = context;
	}

	public void registerNodeSet(NodeSet _nodeSet){
		this.nodeSet = _nodeSet;
	}
	
	public void registerButtons(RevealButton _revealButton, FlagButton _flagButton) {
		this.revealButton = _revealButton;
		this.flagButton = _flagButton;
	}

	
	private int scaleX(float x){
		return (int) (SIDE_PADDING + (this.getWidth() - (2 * SIDE_PADDING)) * x);
	}

	private float scaleDownX(float f){
		return (f - SIDE_PADDING) / (float) (this.getWidth() - (2 * SIDE_PADDING));
	}

	private int scaleY(float y){
		return (int) (SIDE_PADDING + (this.getHeight() - (2 * SIDE_PADDING)) * y);
	}

	private float scaleDownY(float f){
		return (f - SIDE_PADDING) / (float) (this.getHeight() - (2 * SIDE_PADDING));
	}
	
	private Node findNodeAtPos(float x, float y){
		
		if (this.nodeSet == null)
			return null;
		
		for (Node n : nodeSet.getActiveNodes()){
			//use a rectangle to approximate the node's circle out of laziness
			if (
					   (x > (n.getX() - NODE_TOUCH_AREA))
					&& (x < (n.getX() + NODE_TOUCH_AREA))
					&& (y > (n.getY() - NODE_TOUCH_AREA))
					&& (y < (n.getY() + NODE_TOUCH_AREA))
				){
				return n;
			}
		}
		return null;
	}

	
	@Override
	protected void onDraw(Canvas canvas){
		
		if (this.nodeSet == null)
			return;
		
		Paint edgePaint = new Paint();
		edgePaint.setStrokeWidth(2);
		edgePaint.setColor(0xD0EEDD86);

		Paint selectedEdgePaint = new Paint();
		selectedEdgePaint.setStrokeWidth(2);
		selectedEdgePaint.setColor(0xF0885586);

		Paint nodePaint = new Paint();
		nodePaint.setStrokeWidth(5);
		nodePaint.setColor(0xF3097286);

		Paint hiddenNodePaint = new Paint();
		hiddenNodePaint.setStrokeWidth(5);
		hiddenNodePaint.setColor(0xF0DDCB86);

		Paint selectedNodePaint = new Paint();
		selectedNodePaint.setStrokeWidth(5);
		selectedNodePaint.setColor(0xF0D17286);

		Paint numberPaint = new Paint();
		numberPaint.setStrokeWidth(1);
		numberPaint.setColor(0xFFFFFFFF);

		Paint flagPaint = new Paint();
		flagPaint.setStrokeWidth(1);
		flagPaint.setColor(0xFFFF0000);

		Paint minePaint = new Paint();
		minePaint.setStrokeWidth(1);
		minePaint.setColor(0xFF000000);
		
		//Draw edges
		for (Node n : nodeSet.getActiveNodes()){
			for (Node e : n.getEdges()){
				//only draw it once
				if (!e.isDeleted() && (n.getId() > e.getId())){
					Paint p;
					if (e == selectedNode || n == selectedNode){
						p = selectedEdgePaint;
					}else{
						p = edgePaint;
					}
					canvas.drawLine(scaleX(n.getX()), scaleY(n.getY()), scaleX(e.getX()), scaleY(e.getY()), p);
				}
			}
		}
		
		//Draw nodes
		for (Node n : nodeSet.getActiveNodes()){
			Paint p;
			if (n == selectedNode){
				p = nodePaint;
			}else if (n.isHidden()){
				p = hiddenNodePaint;
			}else{
				p = selectedNodePaint;
			}
			canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), p);
			if (!n.isHidden()){
				if (n.isMine()){
					canvas.drawText("B", scaleX(n.getX()) - 3, scaleY(n.getY()) + 3, minePaint);					
				}else{
					canvas.drawText(Integer.toString(n.getNumNeighborMines()), scaleX(n.getX()) - 3, scaleY(n.getY()) + 3, numberPaint);
				}
			}else if (n.isFlagged()){
				canvas.drawText("F", scaleX(n.getX()) - 3, scaleY(n.getY()) + 3, flagPaint);				
			}
		}
		invalidate();
	}

	private void selectNode(Node n) {
		this.selectedNode = n;
		if (revealButton != null)
			revealButton.selectNode(n);
		if (flagButton != null)
		flagButton.selectNode(n);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent e){
		//select a node
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			selectNode(this.findNodeAtPos(scaleDownX(e.getX()), scaleDownY(e.getY())));
			if (this.selectedNode != null){
				Log.v("DrawableView","ACTION_DOWN on node "+this.selectedNode.getId());
			}
		//move a node
		}else if (e.getAction() == MotionEvent.ACTION_MOVE){
			if (this.selectedNode != null){
				if (e.getY() > 0 && e.getY() < this.getHeight() && e.getX() > 0 && e.getX() < this.getWidth()){
					this.selectedNode.setX(scaleDownX(e.getX()));
					this.selectedNode.setY(scaleDownY(e.getY()));
				}
			}
		}else{
			Log.v("DrawableView", "got code: " + e.getAction());
		}
		return true;
	}

}