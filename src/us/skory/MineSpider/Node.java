package us.skory.MineSpider;

import java.util.ArrayList;

import android.util.Log;

public class Node {
		

	private int id;
	private float x;
	private float y;

	private Boolean mine;
	private Boolean hidden;
	private Boolean selected;
	
	private int numNeighborMines;
	private ArrayList<Node> edges;

	public Node(int _id){
		super();
		this.id = _id;
		this.edges = new ArrayList<Node>();
		this.mine = false;
		this.hidden = true;
		this.numNeighborMines = 0;
	}

	public int getId(){
		return this.id;
	}

	public float getX(){
		return this.x;
	}

	public float getY(){
		return this.y;
	}

	public void setX(float _x){
		 this.x = _x;
	}

	public void setY(float _y){
		this.y = _y;
	}

	public Boolean getMine(){
		return this.mine;
	}
	
	public void setMine(Boolean _mine){
		this.mine = _mine;
	}
	
	public void addEdge(Node n){
		this.edges.add(n);
		if (n.getMine()){
			this.numNeighborMines++;
		}
	}

	public ArrayList<Node> getEdges(){
		return this.edges;
	}
	
	public int getNumEdges(){
		return this.edges.size();
	}

	public int getNumNeighborMines(){
		return this.numNeighborMines;
	}

}