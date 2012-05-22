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
	private NodeSet mNodeSet;
	private Node mSelectedNode;
	
	private Paint mEdgePaint;
	private Paint mNodePaint;
	private Paint mTextPaint;
	private Bitmap mFlagBitmap;
	private Bitmap mMineBitmap;

	private MineSpiderPresenter mPresenter;
	
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
		mFlagBitmap = BitmapFactory.decodeResource(res, R.drawable.flag);
		mMineBitmap = BitmapFactory.decodeResource(res, R.drawable.mine);
		
		mEdgePaint = new Paint();
		mEdgePaint.setStrokeWidth(2);
		mNodePaint = new Paint();
		mNodePaint.setStrokeWidth(5);
		mTextPaint = new Paint();
		mTextPaint.setStrokeWidth(2);
		mTextPaint.setTextScaleX(TEXT_SCALE);
	}

	public void setPresenter(MineSpiderPresenter presenter) {
		mPresenter = presenter;
	}

	public void setNodeSet(NodeSet nodeSet) {
		mNodeSet = nodeSet;
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
		
		if (mNodeSet == null)
			return null;
		
		for (Node n : mNodeSet.getActiveNodes()){
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
		
		if (mNodeSet == null)
			return;
		
		//Draw edges
		for (Node n : mNodeSet.getActiveNodes()){
			for (Node e : n.getEdges()){
				//don't draw to deleted nodes, and only draw each edge in one direction
				if (!e.isDeleted() && (n.getId() > e.getId())){
					if (e == mSelectedNode){
						if (!n.isHidden())
							mEdgePaint.setColor(getColor(n,EDGE_OPACITY));
						else
							mEdgePaint.setColor(SELECTED_NODE_COLOR);
					}else if (n == mSelectedNode){
						if (!e.isHidden())
							mEdgePaint.setColor(getColor(e,EDGE_OPACITY));
						else
							mEdgePaint.setColor(SELECTED_NODE_COLOR);
					}else{
						mEdgePaint.setColor(EDGE_COLOR);
					}
					canvas.drawLine(scaleX(n.getX()), scaleY(n.getY()), scaleX(e.getX()), scaleY(e.getY()), mEdgePaint);
				}
			}
		}
		
		//Draw nodes
		for (Node n : mNodeSet.getActiveNodes()){

			//draw the node's circle
			if (n == mSelectedNode){
				mNodePaint.setColor(SELECTED_NODE_COLOR);
				canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), mNodePaint);
				if (!n.isHidden() && !n.isMine()){
					mNodePaint.setColor(getColor(n,NODE_OPACITY));
					canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(SELECTED_NODE_DRAW_RADIUS), mNodePaint);
				}
			}else{
				if (n.isHidden() || n.isMine()){
					mNodePaint.setColor(HIDDEN_NODE_COLOR);
				}else{
					mNodePaint.setColor(getColor(n,NODE_OPACITY));
				}
				canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_DRAW_RADIUS), mNodePaint);
			}

			//draw node's text if not hidden or if flagged
			if (!n.isHidden()){
				if (n.isMine()){
					mTextPaint.setColor(MINE_COLOR);
//					canvas.drawText("B", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);					
//					canvas.drawText("B", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
					canvas.drawBitmap(mMineBitmap,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, mTextPaint);
				}else{
					mTextPaint.setColor(MINE_COLOR);
					canvas.drawText(Integer.toString(n.getNumNeighborMines()), scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, mTextPaint);
				}
			}else if (n.isFlagged()){
				mTextPaint.setColor(FLAG_COLOR);
//				canvas.drawText("F", scaleX(n.getX()) - TEXT_OFFSET, scaleY(n.getY()) + TEXT_OFFSET, textPaint);
				canvas.drawBitmap(mFlagBitmap,scaleX(n.getX()) + BM_OFFSET_X, scaleY(n.getY()) + BM_OFFSET_Y, mTextPaint);
			}
		}
		invalidate();
	}

	private void selectNode(Node n) {
		mSelectedNode = n;
		if (mPresenter != null) {
			mPresenter.selectNode(n);
		}
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent e){
		//select a node
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			selectNode(findNodeAtPos(scaleDownX(e.getX()), scaleDownY(e.getY())));
		//move a node
		}else if (e.getAction() == MotionEvent.ACTION_MOVE){
			if (mSelectedNode != null){
				if (e.getY() > 0 && e.getY() < getHeight() && e.getX() > 0 && e.getX() < getWidth()){
					mSelectedNode.setX(scaleDownX(e.getX()));
					mSelectedNode.setY(scaleDownY(e.getY()));
				}
			}
		}
		return true;
	}

}