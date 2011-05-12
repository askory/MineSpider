package us.skory.MineSpider;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import android.util.Log;

public class NodeSet {
	
	public static double PROB_EDGE = 0.13;
	public static float LAYOUT_RADIUS = 0.45f;

	ArrayList<Node> nodes;
	Random random;
	
	public NodeSet(int num_nodes, int num_mines){

		
		//create num_nodes points on a circle
		float A = (float) (2*Math.PI / (float) num_nodes);
		ArrayList<Number> xps = new ArrayList<Number>();
		ArrayList<Number> yps = new ArrayList<Number>();
		for (int p = 0; p < num_nodes; p++){
			xps.add(0.5 + (LAYOUT_RADIUS * Math.cos(p * A)));
			yps.add(0.5 + (LAYOUT_RADIUS * Math.sin(p * A)));
		}
		
		random = new Random();
		
		nodes = new ArrayList<Node>();
		for (int i = 0; i < num_nodes; i++){
	
			//Create a new node with n.id = i
			Node n = new Node(i);
	
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
		}
	
		//All nodes are connected to their two neighbors in a circular chain
		//And add some edges between non-neighbors according to PROB_EDGE
		for (int i = 0; i < num_nodes; i++){

			if (i > 0){
				nodes.get(i).addEdge(nodes.get(i-1));
				nodes.get(i-1).addEdge(nodes.get(i));
			}
			if (i == num_nodes - 1){
				nodes.get(i).addEdge(nodes.get(0));
				nodes.get(0).addEdge(nodes.get(i));
			}
			
			int stop;
			if (i == 0){
				stop = num_nodes - 1;
			}else{
				stop = num_nodes;	
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

	public ArrayList<Node> getActiveNodes(){
		ArrayList<Node> ret = new ArrayList<Node>();
		for (Node n : this.nodes){
			if (!n.isDeleted()){
				ret.add(n);
			}
		}
		return ret;
	}

}
