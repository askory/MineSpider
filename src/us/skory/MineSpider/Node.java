package us.skory.MineSpider;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Node implements Parcelable {

	private static final long serialVersionUID = 1L;

	private static int MINE = 0x1;
	private static int HIDDEN = 0x2;
	private static int FLAGGED = 0x4;
	private static int DELETED = 0x8;

	private final NodeSet mNodeSet;
	private final int mId;
	private float mX;
	private float mY;

	private int mState = HIDDEN;

	private int numNeighborMines;
	private ArrayList<Node> mEdges;
	
	public Node(NodeSet nodeSet, int id){
		super();
		mNodeSet = nodeSet;
		mId = id;
		mEdges = new ArrayList<Node>();
		numNeighborMines = 0;
	}

	public int getId(){
		return mId;
	}

	public float getX(){
		return mX;
	}

	public float getY(){
		return mY;
	}

	public void setX(float x){
		 mX = x;
	}

	public void setY(float y){
		mY = y;
	}

	public Boolean isMine(){
		return (mState & MINE) > 0;
	}

	public Boolean isHidden(){
		return (mState & ~DELETED & HIDDEN) > 0;
	}

	public Boolean isFlagged(){
		return (mState & FLAGGED) > 0;
	}

	public boolean isDeleted() {
		return (mState & DELETED) > 0;
	}

	public void setMine(Boolean mine){
		mState = mine ? mState | MINE : mState & ~MINE;
	}
	
	public void addEdge(Node n){
		mEdges.add(n);
		if (n.isMine()){
			numNeighborMines++;
		}
	}

	public ArrayList<Node> getEdges(){
		return mEdges;
	}
	
	public int getNumEdges(){
		return mEdges.size();
	}

	public int getNumNeighborMines(){
		return numNeighborMines;
	}

	public void reveal(boolean sideEffects) {
		mState = mState & ~FLAGGED & ~HIDDEN;
		if (isMine()){
			if (sideEffects){
				mNodeSet.youLose();
			}
		} else if (numNeighborMines == 0){
			mState |= DELETED;
			if (sideEffects){
				for (Node n : mEdges){
					if (!n.isDeleted()){
						n.reveal(true);
					}
				}
				mNodeSet.checkWin();
			}
		} else {
			if (sideEffects){
				mNodeSet.checkWin();
			}
		}
	}

	public Boolean flag() {
		if (isHidden()){
			mState |= FLAGGED;
			mNodeSet.checkWin();
			return true;
		}
		return false;
	}

	public Boolean unflag() {
		if (isHidden()){
			mState &= ~FLAGGED;
			mNodeSet.updateCounts();
			return true;
		}
		return false;
	}

	public boolean hasEdge(Node n) {
		return mEdges.contains(n);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeInt(mState);
		int[] edgeIds = new int[mEdges.size()];
		for (int i = 0; i < mEdges.size(); i++) {
			edgeIds[i] = mEdges.get(i).getId();
		}
		dest.writeIntArray(edgeIds);
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		JSONArray edges = new JSONArray();
		try {
			json.put("id", mId);
			json.put("state", mState);
			json.put("x", (int) (mX * 1000));
			json.put("y", (int) (mY * 1000));
			for (Node e : mEdges) {
				edges.put(e.getId());
			}
			json.put("edges", edges);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Node fromJson(NodeSet nodeSet, JSONObject json) {
		Node node = null;
		try {
			int id = json.getInt("id");
			node = new Node(nodeSet, id);
			node.mState = json.getInt("state");
			node.mX = json.getInt("x") / 1000f;
			node.mY = json.getInt("y") / 1000f;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return node;
	}
}