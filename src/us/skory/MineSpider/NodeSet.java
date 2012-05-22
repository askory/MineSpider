package us.skory.MineSpider;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class NodeSet implements Parcelable {
	
	public static double PROB_EDGE = 0.13;
	public static float LAYOUT_RADIUS = 0.45f;

	private int mNumNodes;
	private int mNumMines;
	private int mNumEdges;
	private final ArrayList<Node> mNodes;

	private Random mRandom;

	private MineSpiderPresenter mPresenter;
	
	private NodeSet() {
		this(0, 0, 0);
	}

	public NodeSet(int numNodes, int numMines, int numEdges){

		mNumNodes = numNodes;
		mNumMines = numMines;
		mNumEdges = numEdges;

		mNodes = new ArrayList<Node>();

		mRandom = new Random();
		
		if (numNodes > 0) {
			reInit();
		}
	}

	public void reInit() {
		
		//create num_nodes points on a circle
		float A = (float) (2*Math.PI / (float) mNumNodes);
		ArrayList<Number> xps = new ArrayList<Number>();
		ArrayList<Number> yps = new ArrayList<Number>();
		for (int p = 0; p < mNumNodes; p++){
			xps.add(0.5 + (LAYOUT_RADIUS * Math.cos(p * A)));
			yps.add(0.5 + (LAYOUT_RADIUS * Math.sin(p * A)));
		}

		mNodes.clear();
		for (int i = 0; i < mNumNodes; i++){
	
			//Create a new node with n.id = i
			Node n = new Node(this, i);
	
			//Position node randomly on circle
			int p = mRandom.nextInt(xps.size());
			n.setX((xps.remove(p).floatValue()));
			n.setY((yps.remove(p).floatValue()));

			mNodes.add(n);
		}

		for (int m = 0; m < mNumMines; m++){
			int n_id = mRandom.nextInt(mNumNodes);
			while (this.mNodes.get(n_id).isMine()){
				n_id = mRandom.nextInt(mNumNodes);				
			}
			this.mNodes.get(n_id).setMine(true);
		}
	
		//All nodes are connected to their two neighbors in a circular chain
		//Then add the rest of the edges randomly between non-neighbors
		for (int i = 0; i < mNumNodes; i++){

			if (i > 0){
				mNodes.get(i).addEdge(mNodes.get(i-1));
				mNodes.get(i-1).addEdge(mNodes.get(i));
			}
			if (i == mNumNodes - 1){
				mNodes.get(i).addEdge(mNodes.get(0));
				mNodes.get(0).addEdge(mNodes.get(i));
			}
			
		}

		ArrayList<Point> nonEdges = new ArrayList<Point>();
		int h = 0;
		for (int j = 2; j < mNumNodes - 2; j++){
			nonEdges.add(new Point(h,j));
		}
		for (int i = 1; i < mNumNodes - 1; i++){
			for (int j = i+2; j < mNumNodes - 1; j++){
				nonEdges.add(new Point(i,j));
			}			
		}
		
		int numEdgesSoFar = mNumNodes;
		while (numEdgesSoFar < mNumEdges){
			if (nonEdges.size() == 0){
				break;
			}
			Point edge = nonEdges.remove(mRandom.nextInt(nonEdges.size()));
			mNodes.get(edge.x).addEdge(mNodes.get(edge.y));
			mNodes.get(edge.y).addEdge(mNodes.get(edge.x));
			numEdgesSoFar++;
		}		

		updateCounts();
	}

	public void updateCounts(){
		int flagged = 0; 
		for (Node n : mNodes){
			if (!n.isDeleted() && n.isFlagged()){
				flagged++;
			}
		}
		if (mPresenter != null) {
			mPresenter.setFlagButtonCount(flagged);
		}
	}

	public void setPresenter(MineSpiderPresenter presenter) {
		mPresenter = presenter;
	}

	public ArrayList<Node> getActiveNodes(){
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (Node n : this.mNodes){
			if (!n.isDeleted()){
				nodes.add(n);
			}
		}
		return nodes;
	}
	public void youLose(){
		for (Node n : this.mNodes){
			if (!n.isDeleted()){
				n.reveal(false);
			}
		}
		if (mPresenter != null) {
			mPresenter.showLost();
		}
	}

	public void checkWin(){
		int hidden = 0;
		int flagged = 0;
		int unflagged = 0;
		for (Node n : this.mNodes){
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
			if (mPresenter != null) {
				mPresenter.showWon();
			}
		}
		updateCounts();
	}

	public int getNumMines() {
		return mNumMines;
	}

	public int getNumFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mNumNodes);
		out.writeInt(mNumMines);
		out.writeInt(mNumEdges);
		Node[] nodes = new Node[mNodes.size()];
		out.writeParcelableArray(mNodes.toArray(nodes), flags);
	}

	public JSONArray toJson() {
		JSONArray json = new JSONArray();
		for (Node n : mNodes) {
			json.put(n.toJson());
		}
		return json;
	}

	public static NodeSet fromJson(JSONArray json) {
		NodeSet nodeSet = new NodeSet();
		int numDirectedEdges = 0;
		try {
			for (int i = 0; i < json.length(); i++) {
				JSONObject nodeJson = json.getJSONObject(i);
				int id = nodeJson.getInt("id");
				nodeSet.mNodes.add(id, Node.fromJson(nodeSet, nodeJson));
			}
			nodeSet.mNumNodes = nodeSet.mNodes.size();
			for (Node n : nodeSet.mNodes) {
				if (n.isMine()) {
					nodeSet.mNumMines++;
				}
				JSONArray edges = json.getJSONObject(n.getId()).getJSONArray("edges");
				numDirectedEdges += edges.length();
				for (int j = 0; j < edges.length(); j++) {
					Node e = nodeSet.mNodes.get(edges.getInt(j));
					n.addEdge(e);
				}
			}
			nodeSet.mNumEdges = numDirectedEdges / 2;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return nodeSet;
	}
}