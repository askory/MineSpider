package us.skory.MineSpider;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class Node {
		
	private Context mContext;
	private NodeSet nodeSet;
	private int id;
	private float x;
	private float y;

	private Boolean mine;
	private Boolean hidden;
	private Boolean flagged;
	private Boolean deleted;
	
	private int numNeighborMines;
	private ArrayList<Node> edges;
	
	public Node(Context _mContext, NodeSet _nodeSet, int _id){
		super();
		this.mContext = _mContext;
		this.nodeSet = _nodeSet;
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
		return (!this.deleted && this.hidden);
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

	public void reveal(boolean sideEffects) {
		if (this.isMine()){
			this.flagged = false;
			this.hidden = false;
			if (sideEffects){
				nodeSet.youLose();
			}
		} else if (this.numNeighborMines == 0){
			this.deleted = true;
			if (sideEffects){
				for (Node n : this.edges){
					if (!n.isDeleted()){
						n.reveal(true);
					}
				}
				nodeSet.checkWin();
			}
		} else {
			this.flagged = false;
			this.hidden = false;
			if (sideEffects){
				nodeSet.checkWin();
			}
		}
	}

	public Boolean flag() {
		if (this.hidden){
			this.flagged = true;
			nodeSet.checkWin();
			return true;
		}
		return false;
	}

	public Boolean unflag() {
		if (this.hidden){
			this.flagged = false;
			nodeSet.updateCounts();
			return true;
		}
		return false;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public boolean hasEdge(Node n) {
		return this.edges.contains(n);
	}


}