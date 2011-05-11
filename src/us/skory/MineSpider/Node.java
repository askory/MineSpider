package us.skory.MineSpider;

import java.util.ArrayList;

import android.util.Log;

public class Node {
		

	private int id;
	private float x;
	private float y;

	private Boolean mine;
	private Boolean hidden;
	private Boolean flagged;
	private Boolean deleted;
	
	private int numNeighborMines;
	private ArrayList<Node> edges;
	
	public Node(int _id){
		super();
		this.id = _id;
		this.edges = new ArrayList<Node>();
		this.mine = false;
		this.hidden = true;
		this.flagged = false;
		this.deleted = false;
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

	public Boolean isMine(){
		return this.mine;
	}

	public Boolean isHidden(){
		return this.hidden;
	}

	public Boolean isFlagged(){
		return this.flagged;
	}

	public void setMine(Boolean _mine){
		this.mine = _mine;
	}
	
	public void addEdge(Node n){
		this.edges.add(n);
		if (n.isMine()){
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

	public void reveal() {
		if (this.isMine()){
			Log.v("Node","BOOOOOOM!");
			this.flagged = false;
			this.hidden = false;
		} else if (this.numNeighborMines == 0){
			this.deleted = true;
			for (Node n : this.edges){
				if (!n.isDeleted()){
					n.reveal();
				}
			}
		} else {
			this.flagged = false;
			this.hidden = false;
		}
	}

	public void flag() {
		if (this.hidden){
			this.flagged = true;
		}
	}

	public boolean isDeleted() {
		return this.deleted;
	}

}