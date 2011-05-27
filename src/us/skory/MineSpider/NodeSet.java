package us.skory.MineSpider;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NodeSet {
	
	public static double PROB_EDGE = 0.13;
	public static float LAYOUT_RADIUS = 0.45f;

	private Context mContext;
	private int num_nodes;
	private int num_mines;
	private int num_edges;
	private ArrayList<Node> nodes;
	private AlertDialog youLoseAlert;
	private AlertDialog youWinAlert;
	private Random random;
	private RevealButton revealButton;
	private FlagButton flagButton;
	private DrawableView drawableView;
	
	private class IntPair{
		int a;
		int b;
		public IntPair (int a, int b){
			this.a = a;
			this.b = b;
		}
	}
	
	public NodeSet(Context _mContext, DrawableView _drawableView, RevealButton _revealButton, FlagButton _flagButton){

		this.mContext = _mContext;
		this.drawableView = _drawableView;
		this.revealButton = _revealButton;
		this.flagButton = _flagButton;
		this.num_nodes = getPref("num_nodes",10,-1);
		this.num_mines = getPref("num_mines", 2, num_nodes);
		this.num_edges = getPref("num_edges", num_nodes * 2, num_nodes * 10);
		this.random = new Random();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(true)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                reInit();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		builder.setMessage("Kaboom. You lose!\nStart a new game?");
		youLoseAlert = builder.create();
		builder.setMessage("Well done, You won!\nStart a new game?");
		youWinAlert = builder.create();
		
		this.reInit();
	}
	
	private int getPref(String name, int d, int max){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		int val = sharedPrefs.getInt(name, d);
		if (max > 0 && val > max){
			return max;
		}
		return val > 0 ? val : d;
	}

	public ArrayList<Node> getActiveNodes(){
		ArrayList<Node> ret = new ArrayList<Node>();
		for (Node n : this.nodes){
			if (!n.isDeleted()){
				ret.add(n);
			}
		}
		return ret;
	}

	public void reInit() {

		this.revealButton.setTextSuffix("("+num_mines+")");
		
		//create num_nodes points on a circle
		float A = (float) (2*Math.PI / (float) num_nodes);
		ArrayList<Number> xps = new ArrayList<Number>();
		ArrayList<Number> yps = new ArrayList<Number>();
		for (int p = 0; p < num_nodes; p++){
			xps.add(0.5 + (LAYOUT_RADIUS * Math.cos(p * A)));
			yps.add(0.5 + (LAYOUT_RADIUS * Math.sin(p * A)));
		}
				
		nodes = new ArrayList<Node>();
		for (int i = 0; i < num_nodes; i++){
	
			//Create a new node with n.id = i
			Node n = new Node(mContext, this, i);
	
			//Position node randomly on circle
			int p = random.nextInt(xps.size());
			n.setX((xps.remove(p).floatValue()));
			n.setY((yps.remove(p).floatValue()));

			nodes.add(n);
		}

		for (int m = 0; m < num_mines; m++){
			int n_id = random.nextInt(num_nodes);
			while (this.nodes.get(n_id).isMine()){
				n_id = random.nextInt(num_nodes);				
			}
			this.nodes.get(n_id).setMine(true);
			
			if (drawableView != null){
				drawableView.setSelectedNode(null);
			}
		}
	
		//All nodes are connected to their two neighbors in a circular chain
		//Then add the rest of the edges randomly between non-neighbors
		for (int i = 0; i < num_nodes; i++){

			if (i > 0){
				nodes.get(i).addEdge(nodes.get(i-1));
				nodes.get(i-1).addEdge(nodes.get(i));
			}
			if (i == num_nodes - 1){
				nodes.get(i).addEdge(nodes.get(0));
				nodes.get(0).addEdge(nodes.get(i));
			}
			
		}

		ArrayList<IntPair> nonEdges = new ArrayList<IntPair>();
		int h = 0;
		for (int j = 2; j < num_nodes - 2; j++){
			nonEdges.add(new IntPair(h,j));
		}
		for (int i = 1; i < num_nodes - 1; i++){
			for (int j = i+2; j < num_nodes - 1; j++){
				nonEdges.add(new IntPair(i,j));
			}			
		}
		
		int numEdgesSoFar = num_nodes;
		while (numEdgesSoFar < num_edges){
			if (nonEdges.size() == 0){
				break;
			}
			IntPair edge = nonEdges.remove(random.nextInt(nonEdges.size()));
			nodes.get(edge.a).addEdge(nodes.get(edge.b));
			nodes.get(edge.b).addEdge(nodes.get(edge.a));
			numEdgesSoFar++;
		}		

		updateCounts();
	}
	
	public void updateCounts(){
		int flagged = 0; 
		for (Node n : this.nodes){
			if (!n.isDeleted() && n.isFlagged()){
				flagged++;
			}
		}
		this.flagButton.setTextSuffix("("+flagged+")");
	}
	
	public void youLose(){
		for (Node n : this.nodes){
			if (!n.isDeleted()){
				n.reveal(false);
			}
		}
		youLoseAlert.show();
	}

	public void checkWin(){
		int hidden = 0;
		int flagged = 0;
		int unflagged = 0;
		for (Node n : this.nodes){
			if (n.isHidden()){
				hidden++;
			}
			if (n.isFlagged()){
				flagged++;
			}
			if (n.isMine() && !n.isFlagged()){
				unflagged++;
			}
		}
		if ((hidden - flagged) == unflagged){
			youWinAlert.show();
		}
		updateCounts();
	}
}