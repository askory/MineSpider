package us.skory.MineSpider;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class DrawableView extends View {
	
	Context mContext;
	ArrayList<Node> nodes;
	Random random;
	Node selectedNode;
	
	public static int NUM_NODES = 10;
	public static double PROB_MINE = 0.33;
	public static double PROB_EDGE = 0.25;
	public static int SIDE_PADDING = 5;
	public static float NODE_RADIUS = 0.02f;
	
	public DrawableView(Context context) {
		super(context);
		
		mContext = context;
		random = new Random();

		nodes = new ArrayList<Node>();
		for (int i = 0; i < NUM_NODES; i++){

			//Create a new node with n.id = i
			Node n = new Node(i);

			//Put a mine in this node according to PROB_MINE
			if (random.nextFloat() < PROB_MINE){
				Log.v("DrawableView","Adding a mine to node "+i);
				n.setMine(true);
			}

			//Position node randomly
			//(need way to figure out screen size)
			n.setX(random.nextFloat());
			n.setY(random.nextFloat());
			
			//All nodes are connected to their two neighbors in a circular chain
			if (i > 0){
					n.addEdge(nodes.get(i-1));
					nodes.get(i-1).addEdge(n);
				if (i == NUM_NODES - 1){
					n.addEdge(nodes.get(0));
					nodes.get(0).addEdge(n);
				}
			}
			
			nodes.add(n);
		}

	
		//Add some edges between non-neighbors according to PROB_EDGE
		for (int i = 0; i < NUM_NODES; i++){
			int stop;
			if (i == 0){
				stop = NUM_NODES - 1;
			}else{
				stop = NUM_NODES;	
			}
			for (int j = i + 2; j < stop; j++){
				if (random.nextFloat() < PROB_EDGE){
					Log.v("DrawableView","Adding an edge between nodes "+i+" and "+j);
					nodes.get(i).addEdge(nodes.get(j));
					nodes.get(j).addEdge(nodes.get(i));
				}
			}
		}
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
		for (Node n : this.nodes){
			//use a rectangle to approximate the node's circle out of laziness
			if (
					   (x > (n.getX() - NODE_RADIUS))
					&& (x < (n.getX() + NODE_RADIUS))
					&& (y > (n.getY() - NODE_RADIUS))
					&& (y < (n.getY() + NODE_RADIUS))
				){
				return n;
			}
		}
		return null;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
		Paint edgePaint = new Paint();
		edgePaint.setStrokeWidth(2);
		edgePaint.setColor(0xFFEEDD86);

		Paint selectedEdgePaint = new Paint();
		selectedEdgePaint.setStrokeWidth(2);
		selectedEdgePaint.setColor(0xFF885586);

		Paint nodePaint = new Paint();
		nodePaint.setStrokeWidth(5);
		nodePaint.setColor(0xFF097286);

		Paint selectedNodePaint = new Paint();
		selectedNodePaint.setStrokeWidth(5);
		selectedNodePaint.setColor(0xFFD17286);

		Paint textPaint = new Paint();
		textPaint.setStrokeWidth(1);
		textPaint.setColor(0xFFFFFFFF);

		//Draw edges
		for (Node n : nodes){
			for (Node e : n.getEdges()){
				//only draw it once
				if (n.getId() > e.getId()){
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
		for (Node n : nodes){
			Paint p;
			if (n == selectedNode){
				p = nodePaint;
			}else{
				p = selectedNodePaint;
			}
			canvas.drawCircle(scaleX(n.getX()), scaleY(n.getY()), scaleX(NODE_RADIUS), p);
			canvas.drawText(Integer.toString(n.getNumNeighborMines()), scaleX(n.getX()) - 3, scaleY(n.getY()) + 3, textPaint);
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent e){
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			this.selectedNode = this.findNodeAtPos(scaleDownX(e.getX()), scaleDownY(e.getY()));
			if (this.selectedNode != null){
				Log.v("DrawableView","ACTION_DOWN on node "+this.selectedNode.getId());
			}
		}else if (e.getAction() == MotionEvent.ACTION_MOVE){
			if (this.selectedNode != null){
				this.selectedNode.setX(scaleDownX(e.getX()));
				this.selectedNode.setY(scaleDownY(e.getY()));
			}
		}
		return true;
	}
}