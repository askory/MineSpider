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
	
	public static int NUM_NODES = 10;
	public static double PROB_MINE = 0.33;
	public static double PROB_EDGE = 0.25;
	
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
			n.setX(random.nextInt(300));
			n.setY(random.nextInt(400));
			
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

	@Override
	protected void onDraw(Canvas canvas){
		
		Paint edgePaint = new Paint();
		edgePaint.setStrokeWidth(2);
		edgePaint.setColor(0xFFEEDD86);

		Paint nodePaint = new Paint();
		nodePaint.setStrokeWidth(5);
		nodePaint.setColor(0xFF097286);

		Paint textPaint = new Paint();
		textPaint.setStrokeWidth(1);
		textPaint.setColor(0xCCEE6686);

		//Draw edges
		//(This sub-optimally draws every edge twice)
		for (Node n : nodes){
			for (Node e : n.getEdges()){
				canvas.drawLine(n.getX(), n.getY(), e.getX(), e.getY(), edgePaint);
			}
		}
		for (Node n : nodes){
			canvas.drawCircle(n.getX(), n.getY(), 8, nodePaint);
			canvas.drawText(Integer.toString(n.getNumNeighborMines()), n.getX() - 3, n.getY() + 3, textPaint);
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent e){
		Log.v("DrawableView","touch event of type "+e.getAction()+" at "+e.getX()+","+e.getY());
		return true;
	}
}