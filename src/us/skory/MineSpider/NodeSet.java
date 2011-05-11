package us.skory.MineSpider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.NoSuchElementException;

import android.util.Log;

public class NodeSet implements Iterator<Node> {
	
	public static int NUM_NODES = 15;
	public static double PROB_MINE = 0.15;
	public static double PROB_EDGE = 0.20;

	ArrayList<Node> nodes;
	Random random;
	int curNode = 0;
	
	public NodeSet(){

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

	@Override
	public boolean hasNext() {
		while (this.nodes.get(curNode).isDeleted())
			this.curNode++;
		if (curNode < this.nodes.size() -1)
			return true;
		else
			curNode = 0;
			return false;
	}

	@Override
	public Node next() {
		if (this.hasNext()){
			Node ret;
			while (this.nodes.get(curNode).isDeleted())
				this.curNode++;
			ret = this.nodes.get(curNode);
			curNode++;
			return ret;
		}else{
			this.curNode = 0;
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		// ???		
	}
	

}
