package us.skory.MineSpider;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class MineSpiderPresenter {

	private final DrawableView mDrawableView;
	private final AlertDialog mYouLoseDialog;
	private final AlertDialog mYouWinDialog;
	private final RevealButton mRevealButton;
	private final FlagButton mFlagButton;

	private NodeSet mNodeSet;

	public MineSpiderPresenter(NodeSet nodeSet, DrawableView drawableView,
			RevealButton revealButton, FlagButton flagButton, AlertDialog.Builder alertBuilder) {

		mRevealButton = revealButton;
		mFlagButton = flagButton;

		alertBuilder.setCancelable(true)
	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                mNodeSet.reInit();
	           }
	       })
	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });

		alertBuilder.setMessage("Kaboom. You lose!\nStart a new game?");
		mYouLoseDialog = alertBuilder.create();

		alertBuilder.setMessage("Well done, You won!\nStart a new game?");
		mYouWinDialog = alertBuilder.create();

		mDrawableView = drawableView;
		mDrawableView.setPresenter(this);
		setNodeSet(nodeSet);
	}

	public void setRevealButtonCount(int mines) {
		mRevealButton.setTextSuffix("("+mines+")");
	}

	public void setFlagButtonCount(int flagged) {
		mFlagButton.setTextSuffix("("+flagged+")");
	}

	public void showWon() {
		mYouWinDialog.show();		
	}

	public void showLost() {
		mYouLoseDialog.show();
	}

	public void selectNode(Node n) {
		mRevealButton.onNodeSelected(n);
		mFlagButton.onNodeSelected(n);
	}

	public void setNodeSet(NodeSet nodeSet) {
		mNodeSet = nodeSet;
		mNodeSet.setPresenter(this);
		mDrawableView.setNodeSet(mNodeSet);
		setRevealButtonCount(mNodeSet.getNumMines());
		mNodeSet.updateCounts();
	}

	public NodeSet getNodeSet() {
		return mNodeSet;
	}
}
