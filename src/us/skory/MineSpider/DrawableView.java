package us.skory.MineSpider;

import android.util.AttributeSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	public static final int EDGE_OPACITY = 0xA5;
	public static final int UNSELECTED_EDGE_OPACITY = 0x55;
	public static final int EDGE_COLOR = 0xD0EEDD86;
	public static final int SELECTED_EDGE_COLOR = 0xD0F0F0F0;
	public static final int NODE_OPACITY = 0xD0;
	public static final int UNSELECTED_NODE_OPACITY = 0x75;
	public static final int HIDDEN_NODE_COLOR = 0xD0DDCB86;
	public static final int SELECTED_NODE_COLOR = 0xDDF0F0F0;
	public static final int[] NUMBER_COLORS = new int[]{
		0,
		0xFF1770FF,     //1 = blue
		0xFF00F210,		//2 = green
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
		this.nodeSet = new NodeSet(mContext, this, revealButton, flagButton);
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
	
	private boolean touchingNode(float x, float y, Node n){
		//use a rectangle to approximate the node's circle out of laziness
		return (
				   (x > (n.getX() - NODE_TOUCH_AREA))
					&& (x < (n.getX() + NODE_TOUCH_AREA))
					&& (y > (n.getY() - NODE_TOUCH_AREA))
					&& (y < (n.getY() + NODE_TOUCH_AREA))
				);
	}
	
	private Node findNodeAtPos(float x, float y){
		
		if (this.nodeSet == null)
			return null;
		
		//first test the current selected node and its edges
		if (this.selectedNode != null){
			if (touchingNode(x,y,selectedNode)){
				return selectedNode;
			}else{
				for (Node e : selectedNode.getEdges()){
					if (touchingNode(x,y,e)){
						return e;
					}
				}
			}
		}
		//now test all the nodes
		for (Node n : nodeSet.getActiveNodes()){
			if (touchingNode(x,y,n)){
				return n;
			}
		}
		return null;
	}

	private int setAlpha(int color, int alpha){
		return (color | (0xFF000000)) & ((alpha << 24) | 0x00FFFFFF);
	}
	
	private int getColor(Node n){
		int color;
		int num = n.getNumNeighborMines();
		if (num >= NUMBER_COLORS.length){
			color = NUMBER_COLORS[num - 1];
		} else {
			color = NUMBER_COLORS[num];
		}
		return color;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
		if (this.nodeSet == null)
			return;
		
		//if there is no selected node
		if (this.selectedNode == null || this.selectedNode.isDeleted()){
			//Draw edges
			for (Node n : nodeSet.getActiveNodes()){
				for (Node e : n.getEdges()){
					//don't draw to deleted nodes, and only draw each edge in one direction
					if (!e.isDeleted() && (n.getId() > e.getId())){
						edgePaint.setColor(EDGE_COLOR);
						canvas.drawLine(scaleX(n.getX()), scaleY(n.getY()), scaleX(e.getX()), scaleY(e.getY()), edgePaint);
					}
				}
			}
			//Draw nodes
			for (Node n : nodeSet.getActiveNodes()){
	
				//draw the node's circle
				if (n.isHidden() || n.isMine()){
					nodePaint.setColor(HIDDEN_NODE_COLOR);
				}else{
					nodePaint.setColor(setAlpha(getColor(n),NODE_OPACITY));
				}
				canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
				drawMarker(canvas,n);
			}

		//if there is a selected node
		} else {
			//first draw all nodes and edges that do not touch the selected node
			edgePaint.setColor(setAlpha(EDGE_COLOR,UNSELECTED_EDGE_OPACITY));
			for (Node n : nodeSet.getActiveNodes()){
				if (n != selectedNode){
					for (Node e : n.getEdges()){
						if (!e.isDeleted() && (n.getId() > e.getId()) && (e != selectedNode)){
							canvas.drawLine(scaleX(n.getX()), scaleY(n.getY()), scaleX(e.getX()), scaleY(e.getY()), edgePaint);
						}
					}
					if (n.isHidden() || n.isMine()){
						nodePaint.setColor(setAlpha(HIDDEN_NODE_COLOR,UNSELECTED_NODE_OPACITY));
					}else{
						nodePaint.setColor(setAlpha(getColor(n),UNSELECTED_NODE_OPACITY));
					}
					canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
					drawMarker(canvas,n);
				}
			}
			//next draw the edges to the selected node
			for (Node e : selectedNode.getEdges()){
				if (!e.isDeleted()){
					if (!e.isHidden())
						edgePaint.setColor(setAlpha(getColor(e),EDGE_OPACITY));
					else
						edgePaint.setColor(SELECTED_NODE_COLOR);
					canvas.drawLine(scaleX(selectedNode.getX()), scaleY(selectedNode.getY()), scaleX(e.getX()), scaleY(e.getY()), edgePaint);
				}		
			}
			//finally draw the nodes touching the selected, then the node itself
			for (Node e : selectedNode.getEdges()){
				if (!e.isDeleted()){
					if (e.isHidden() || e.isMine()){
						nodePaint.setColor(HIDDEN_NODE_COLOR);
					}else{
						nodePaint.setColor(setAlpha(getColor(e),NODE_OPACITY));
					}
					canvas.drawCircle(scaleX(e.getX()), scaleY(e.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
					drawMarker(canvas,e);
				}
			}
			nodePaint.setColor(SELECTED_NODE_COLOR);
			canvas.drawCircle(scaleX(selectedNode.getX()), scaleY(selectedNode.getY()), scaleX(NODE_DRAW_RADIUS), nodePaint);
			if (!selectedNode.isHidden() && !selectedNode.isMine()){
				nodePaint.setColor(setAlpha(getColor(selectedNode),NODE_OPACITY));
				canvas.drawCircle(scaleX(selectedNode.getX()), scaleY(selectedNode.getY()), scaleX(SELECTED_NODE_DRAW_RADIUS), nodePaint);
				drawMarker(canvas,selectedNode);
			}
		}

		invalidate();
	}

	private void drawMarker(Canvas canvas, Node n){
		if (!n.isHidden()){
			if (n.isMine()){
				textPaint.setColor(MINE_COLOR);
				canvas.drawBitmap(mine,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, textPaint);
			}else{
				textPaint.setColor(MINE_COLOR);
				canvas.drawText(Integer.toString(n.getNumNeighborMines()), scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
			}
		}else if (n.isFlagged()){
			textPaint.setColor(FLAG_COLOR);
			canvas.drawBitmap(flag,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, textPaint);
		}
		
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

	public void setSelectedNode(Node node) {
		this.selectedNode = node;
	}

}