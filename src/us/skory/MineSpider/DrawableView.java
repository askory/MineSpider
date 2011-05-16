package us.skory.MineSpider;

import android.util.AttributeSet;
import android.util.Log;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class DrawableView extends View {

	private Context mContext;
	private NodeSet nodeSet;
	private Node selectedNode;
	private RevealButton revealButton;
	private FlagButton flagButton;
	
	private Paint edgePaint;
	private Paint nodePaint;
	private Paint textPaint;
	private Bitmap flag;
	private Bitmap mine;
	
	public static final int SIDE_PADDING = 5;
	public static final float NODE_DRAW_RADIUS = 0.03f;
	public static final float SELECTED_NODE_DRAW_RADIUS = 0.023f;
	public static final float NODE_TOUCH_AREA = 0.045f;
	public static final int TEXT_OFFSET = 6;
	public static final float TEXT_SCALE = 1.8f;
	public static final int BM_OFFSET_X = -8;
	public static final int BM_OFFSET_Y = -7;
	
	public static final int EDGE_OPACITY = 0x95;
	public static final int EDGE_COLOR = 0xD0EEDD86;
	public static final int SELECTED_EDGE_COLOR = 0xD0F0F0F0;
	public static final int NODE_OPACITY = 0x88;
	public static final int HIDDEN_NODE_COLOR = 0xD0DDCB86;
	public static final int SELECTED_NODE_COLOR = 0xDDF0F0F0;
	public static final int[] NUMBER_COLORS = new int[]{
		0,
		0xFF3849FF,     //1 = blue
		0xFF0D8500,		//2 = green
		0xFFED2121,		//3 = red
		0xFFC121ED,		//4 = purple
		0xFFC86AF7,		//5 = light purple
		0xFFDFB8F2,		//6 = lighter purple
		0xFFFFFFFF,		//7+ = white
	};
	public static int FLAG_COLOR = 0xFFFF0000;
	public static int MINE_COLOR = 0xFF000000;

	
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

		Resources res = mContext.getResources();
		flag = BitmapFactory.decodeResource(res, R.drawable.flag);
		mine = BitmapFactory.decodeResource(res, R.drawable.mine);
		
		edgePaint = new Paint();
		edgePaint.setStrokeWidth(2);
		nodePaint = new Paint();
		nodePaint.setStrokeWidth(5);
		textPaint = new Paint();
		textPaint.setStrokeWidth(2);
		textPaint.setTextScaleX(TEXT_SCALE);
	}

	public void initNodeSet(){
		this.nodeSet = new NodeSet(mContext, revealButton, flagButton);
	}
	
	public void registerItems(RevealButton _revealButton, FlagButton _flagButton) {
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

	private int getColor(Node n, int alpha){
		int color;
		int num = n.getNumNeighborMines();
		if (num >= NUMBER_COLORS.length){
			color = NUMBER_COLORS[num - 1];
		} else {
			color = NUMBER_COLORS[num];
		}
		return (color & (~0 >> 24) | (alpha << 24));
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
		if (this.nodeSet == null)
			return;
		
		//Draw edges
		for (Node n : nodeSet.getActiveNodes()){
			for (Node e : n.getEdges()){
				//don't draw to deleted nodes, and only draw each edge in one direction
				if (!e.isDeleted() && (n.getId() > e.getId())){
					if (e == selectedNode){
						if (!n.isHidden())
							edgePaint.setColor(getColor(n,EDGE_OPACITY));
						else
							edgePaint.setColor(SELECTED_NODE_COLOR);
					}else if (n == selectedNode){
						if (!e.isHidden())
							edgePaint.setColor(getColor(e,EDGE_OPACITY));
						else
							edgePaint.setColor(SELECTED_NODE_COLOR);
					}else{
						edgePaint.setColor(EDGE_COLOR);
					}
					canvas.drawLine(scaleX(n.getX()), scaleY(n.getY()), scaleX(e.getX()), scaleY(e.getY()), edgePaint);
				}
			}
		}
		
		//Draw nodes
		for (Node n : nodeSet.getActiveNodes()){

			//draw the node's circle
			if (n == selectedNode){
				nodePaint.setColor(SELECTED_NODE_COLOR);
				canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
				if (!n.isHidden() && !n.isMine()){
					nodePaint.setColor(getColor(n,NODE_OPACITY));
					canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(SELECTED_NODE_DRAW_RADIUS), nodePaint);
				}
			}else{
				if (n.isHidden() || n.isMine()){
					nodePaint.setColor(HIDDEN_NODE_COLOR);
				}else{
					nodePaint.setColor(getColor(n,NODE_OPACITY));
				}
				canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
			}

			//draw node's text if not hidden or if flagged
			if (!n.isHidden()){
				if (n.isMine()){
					textPaint.setColor(MINE_COLOR);
//					canvas.drawText("B", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);					
//					canvas.drawText("B", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
					canvas.drawBitmap(mine,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, textPaint);
				}else{
					textPaint.setColor(MINE_COLOR);
					canvas.drawText(Integer.toString(n.getNumNeighborMines()), scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
				}
			}else if (n.isFlagged()){
				textPaint.setColor(FLAG_COLOR);
//				canvas.drawText("F", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
				canvas.drawBitmap(flag,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, textPaint);
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
		//move a node
		}else if (e.getAction() == MotionEvent.ACTION_MOVE){
			if (this.selectedNode != null){
				if (e.getY() > 0 && e.getY() < this.getHeight() && e.getX() > 0 && e.getX() < this.getWidth()){
					this.selectedNode.setX(scaleDownX(e.getX()));
					this.selectedNode.setY(scaleDownY(e.getY()));
				}
			}
		}
		return true;
	}

}