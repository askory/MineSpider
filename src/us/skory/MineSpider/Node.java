package us.skory.MineSpider;

import java.util.ArrayList;

import android.util.Log;

public class Node {
	
	private int id;
	private int x;
	private int y;
	private Boolean mine;
	private int numNeighborMines;
	private ArrayList<Node> edges;

	public Node(int _id){
		super();
		this.id = _id;
		this.edges = new ArrayList<Node>();
		this.mine = false;
		this.numNeighborMines = 0;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public void setX(int _x){
		 this.x = _x;
	}

	public void setY(int _y){
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