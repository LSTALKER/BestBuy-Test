package com.bestbuy.android.ui.draggable;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.bestbuy.android.R;

public class DraggableIconFrame extends FrameLayout{
	private Point center;
	private Rect[] grid;
	private List<DraggableIcon> icons;
	//TODO private AddIcon addIcon;
	private boolean editMode = false;
	private int hoverIndex = -1;
	private long lastHover;
	private static final int MAX_ICONS = 9;
	private int maxWidth;
	private int centerYOffset = 0;
	private int page = -1;
	private Rect backHoverRect;
	private Rect forwardHoverRect;
	private DraggableGallery draggableGallery;
	
	public DraggableIconFrame(Context context, AttributeSet attrs) {
		super(context, attrs);	
		init();
		//TODO placeAddIcon();
	}
	
	public DraggableIconFrame(Context context, OnClickListener addListener) {
		super(context);	
		init();
		//TODO placeAddIcon(addListener);
	}
	
	private void init(){
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		icons = new ArrayList<DraggableIcon>();
		
		Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		
		center = new Point();
		center.set(display.getWidth()/2, display.getHeight()/2);
		
		maxWidth = display.getWidth() - getPaddingLeft() - getPaddingRight();
		
		int hoverWidth = (int)(display.getWidth() * .1); //10% of screen width
		backHoverRect = new Rect(0,0,hoverWidth,display.getHeight());
		forwardHoverRect = new Rect(display.getWidth() - hoverWidth, 0,display.getWidth(),display.getHeight());
		
		setupDim();	
	}
	
	
	//////////////////////////////////////////////////
	//               Public Methods                 //
	//////////////////////////////////////////////////
	

	public void addIcon(int resourceId, String text,boolean removable, OnTouchListener dragListener, OnClickListener clickListener, OnLongClickListener longListener){
		//TODO removeAddIcon();
		
		DraggableIcon newIcon = new DraggableIcon(this.getContext(),this,resourceId,text,icons.size(),removable,dragListener,clickListener,longListener);
		icons.add(newIcon);
		addView(newIcon);
		
		//TODO placeAddIcon();
		
	}
	
	public void addIcon(DraggableIcon icon){		
		icons.add(icon);
		icon.setGridIndex(icons.size() - 1, true);
		addView(icon);
	}
	
	public DraggableIcon insertIcon(DraggableIcon icon){
		
		//TODO removeAddIcon();
		
		if(icons.size() == MAX_ICONS){
			final DraggableIcon passIcon = icons.get(0);
			passIcon.clearAnimation();
			removeView(passIcon);
			icons.remove(0);
			icons.add(icon.getGridIndex(), icon);
			icon.setParentGrid(this);
			addView(icon);
			for(int i = 0; i < icons.size(); i++){
 				icons.get(i).setGridIndex(i,true);
			}	
			return passIcon;
		}else{
			if(icon.getGridIndex() < icons.size()){
				icons.add(icon.getGridIndex(), icon);
			}else{
				icons.add(icon);
			}
			icon.setParentGrid(this);
			addView(icon);
			for(int i = 0; i < icons.size(); i++){
				icons.get(i).setGridIndex(i,true);
			}	
			return null;
		}
		
		
		
		//TODO placeAddIcon();
		
	}
	
	/**
	 * Delete the draggable icon at the position and reorders the remaining draggable icons
	 * 
	 * @param pos - the position of the draggable icon to be deleted
	 */
	public void deleteIcon(int pos,boolean slide ){
		
		if(icons.get(pos) != null){
			icons.get(pos).clearAnimation();
			removeView(icons.get(pos));
			icons.remove(pos);
			
			if(slide){
				reorderIcons(pos,icons.size());
			}else{
				for(int i = 0; i < icons.size();i++){
					icons.get(i).setGridIndex(i,true);
				}
			}
		}	
	}
	
	/**
	 * Sets the maximum width of the entire draggable icon grid
	 * Max Width is defaulted to the width of the screen - left and right padding
	 * 
	 * @param maxWidth - the new maximum width of the draggable icon grid
	 */
	public void setMaxWidth(int maxWidth){
		this.maxWidth = maxWidth;
		setupDim();
	}
	
	
	/**
	 * Set the distance of the grid from the center of the screen vertically
	 * @param offset in pixel from the center
	 */
	public void setCenterYOffset(int offset){
		centerYOffset = offset;
		setupDim();
	}
	
	
	
	public int getHoverIndex() {
		return hoverIndex;
	}

	public void setHoverIndex(int hoverIndex) {
		this.hoverIndex = hoverIndex;
	}

	public long getLastHover() {
		return lastHover;
	}

	public void setLastHover(long lastHover) {
		this.lastHover = lastHover;
	}
	

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	

	public Rect getBackHoverRect() {
		return backHoverRect;
	}

	public Rect getForwardHoverRect() {
		return forwardHoverRect;
	}


	/**
	 * Cancel edit mode - should be called from a "done" button or onBackPressed() in the activity
	 */
	public void cancelEditMode(){
		if(editMode){
     		editMode = false;
     		stopJiggling(); 
     		hideAllDelete();
     		//TODO placeAddIcon();
     	}
	}
	
	public void startEditMode(){
		if(!editMode){
    		editMode = true;
    		startJiggling();
    		showAllDelete();
    		//TODO removeAddIcon();
    	}
	}
	
	/**
	 * Tests whether or not the draggable icon grid is being edited or not
	 * 
	 * @return boolean - whether or not the draggable icon grid is being edited or not
	 */
	public boolean isEditMode(){
		return editMode;
	}
	
	/**
	 * Gets the bounds for the grid square at position "pos"
	 * 
	 * @param pos - the position of the draggable icon 
	 * @return Rect - the bounds for the grid square at postion "pos"
	 */
	public Rect getGridRect(int pos){	
		if(grid == null){
			setupDim();
		}		
		return grid[pos];
	}
	
	/**
	 * Gets the current number of draggable icons in the grid 
	 * 
	 * @return the current number of draggable icons in the grid
	 */
	public int getNumIcons(){
		return icons.size();
	}
	
	/**
	 * Sets the add listener for adding new icons
	 * 
	 * @param addListener - a onclicklistener for adding new icons
	 */
	/*TODO public void setAddListener(OnClickListener addListener){
		if(addIcon == null){
			placeAddIcon(addListener);
		}else{
			addIcon.setAddListener(addListener);
		}
		
	}*/
	/**
	 * Gets a list of the current icon's keys on the grid
	 * 
	 * @return list of keys
	 */
	public List<String> getCurrentIcons(){
		List<String> currentIcons = new ArrayList<String>();
		for(DraggableIcon icon : icons){
			if(icon != null)
				currentIcons.add(icon.getText());
		}
		return currentIcons;
	}

	public Point getCenterPoint(){
		return center;
	}
	

	
	
	////////////////////////////////////////////////////
	//              Private Utilities                //
	///////////////////////////////////////////////////
	
	public DraggableGallery getDraggableGallery() {
		return draggableGallery;
	}

	public void setDraggableGallery(DraggableGallery draggableGallery) {
		this.draggableGallery = draggableGallery;
	}

	/**
	 * Reorders icons from start to end using a transform animation
	 * 
	 * @param start - first draggable icon to move 
	 * @param end - last draggable icon to move
	 */
	private void reorderIcons(int start, int end){
		for(int i = start; i < end; i++){
			slide(icons.get(i),false);		
		}

	}
		
	/**
	 * Swap draggable icon at i1 with draggable icon at i2
	 * 
	 * @param i1 - index of the first draggable icon
	 * @param i2 - index of the second draggable icon
	 */
	public void swapIcon(int i1, int i2){
		if(icons.get(i1) != null){
			icons.get(i1).setGridIndex(i2,true);
		}
		if(icons.get(i2) != null){
			icons.get(i2).setGridIndex(i1,true);
		}
		DraggableIcon tempIcon = icons.get(i1);
		icons.set(i1, icons.get(i2));
		icons.set(i2, tempIcon);	
	}
	
	/**
	 * Places the add Icon at the end of the grid if there is an empty space.
	 */
	/*TODO private void placeAddIcon(OnClickListener addListener){
		if(numIcons < 9){
			if(addIcon == null){
				addIcon = new AddIcon(this.getContext(),this,numIcons,addListener,longListener);
			}else{
				addIcon.setGridIndex(numIcons,true);
			}
			addView(addIcon);
		}
	}
	private void placeAddIcon(){
		placeAddIcon(null);
	}
	
	private void removeAddIcon(){
		if(addIcon != null){
			addIcon.clearAnimation();
			removeView(addIcon);
		}
	}*/
	
	/**
	 * Finds the index of a draggable icon that the param is overlapping the most. 
	 * 
	 * @param icon - Draggable icon that is currently being dragged 
	 * @return int - the index of a draggable icon that the param is overlapping the most.
	 */
	public int findOverlappingIcon(DraggableIcon icon){
		int index = -1;
		int area = 0;
		for(int i = 0; i < icons.size(); i++){
			Rect testRect = translateRect(grid[i]);
			int overlap = findAreaOfOverlap(testRect,icon.getBounds());
			if(Rect.intersects(testRect,icon.getBounds())  && overlap > area){
				area = overlap;
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Finds the area of intersection of two rectangles 
	 * 
	 * @param rect1 - the first rectangle
	 * @param rect2 - the second rectangle
	 * @return - the area of intersection
	 */
	private int findAreaOfOverlap(Rect rect1, Rect rect2){
		int left = Math.max(rect1.left, rect2.left);
		int right = Math.min(rect1.right,rect2.right);
		int top = Math.max(rect1.top, rect2.top);
		int bottom = Math.min(rect1.bottom, rect2.bottom);
		int width = right - left;
		int height = bottom - top;
		return width * height;
	}
	
	/**
	 * Checks to see if the draggable icon param is overlapping an icon. If so slide icons out of the way.
	 * 
	 * @param icon - the icon currently being dragged
	 */
	public boolean hover(DraggableIcon icon){
		int index = findOverlappingIcon(icon);
		int currentIndex = icon.getGridIndex();
		
		
		if(index != -1 && index != currentIndex && hoverIndex != index && System.currentTimeMillis() - lastHover > 500){
			lastHover = System.currentTimeMillis();
			hoverIndex = index;
			

			
			int i = hoverIndex;
			while(i != currentIndex){
				if(currentIndex > hoverIndex){
					slide(icons.get(i),true);
					i = nextIndex(i);
				}else{
					slide(icons.get(i),false);
					i = previousIndex(i);
				}
			}
			icon.setGridIndex(hoverIndex, false);
			icons.set(hoverIndex,icon);
			return true;
		}	
		else{
			return false;
		}
	}
	
	//TODO make more dynamic for more than two frames
	public boolean checkHoverEdge(DraggableIcon icon){
		
		if(page == 0){
			if(Rect.intersects(forwardHoverRect,icon.getBounds())){
				return true;
			}
		}else if (page == 1){ 
			if(Rect.intersects(backHoverRect,icon.getBounds())){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	/**
	 * Finds the next index in the draggable icon grid 
	 * 
	 * @param index - the current index 
	 * @return int - the next index
	 */
	private int nextIndex(int index){
		if(++index == icons.size()){
			index = 0;
		}
		return index;
	}
	
	/**
	 * Finds the previous index in the draggable icon grid 
	 * 
	 * @param index - the current index 
	 * @return int - the previous index
	 */
	private int previousIndex(int index){
		if(--index == -1){
			index = icons.size() -1;
		}
		return index;
	}
	
	/**
	 * Set up the dimensions of the draggable icon grid
	 */
	private void setupDim(){
		
		int squareWidth = maxWidth / 3;

		grid = new Rect[9];
		
		// Added this condition as all are getting merged in OS 2.1
		if((Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_MR1)) {
			grid[0] = new Rect((2 * -squareWidth), (2 * (-squareWidth + centerYOffset)) ,0 , centerYOffset);
			grid[1] = new Rect(0,(2 * (-squareWidth+ centerYOffset)), squareWidth, centerYOffset);
			grid[2] = new Rect((2 * squareWidth),(2 * (-squareWidth + centerYOffset)), (squareWidth * 2), centerYOffset);
			
			grid[3] = new Rect((2 * -squareWidth), (2 * centerYOffset), 0, (2 * (squareWidth + centerYOffset)));
			grid[4] = new Rect(0, (2 * centerYOffset), squareWidth, (2 * (squareWidth + centerYOffset)));
			grid[5] = new Rect((2 * squareWidth), (2 * centerYOffset), (squareWidth * 2), (2 * (squareWidth + centerYOffset)));
			
			grid[6] = new Rect((2 * -squareWidth), (2 * (squareWidth + centerYOffset)), 0, (squareWidth * 2) + centerYOffset);
			grid[7] = new Rect(0, (2 * (squareWidth + centerYOffset)), squareWidth, ((squareWidth * 2) + centerYOffset));
			grid[8] = new Rect((2 * squareWidth), (2 * (squareWidth + centerYOffset)), (squareWidth * 2), ((squareWidth * 2) + centerYOffset));

		} else {
			grid[0] = new Rect(-squareWidth, (-squareWidth + centerYOffset), 0, centerYOffset);
			grid[1] = new Rect(0, (-squareWidth + centerYOffset), squareWidth, centerYOffset);
			grid[2] = new Rect(squareWidth, (-squareWidth + centerYOffset), (squareWidth * 2), centerYOffset);
			
			grid[3] = new Rect(-squareWidth, centerYOffset, 0, (squareWidth + centerYOffset));
			grid[4] = new Rect(0, centerYOffset, squareWidth, (squareWidth + centerYOffset));
			grid[5] = new Rect(squareWidth, centerYOffset, (squareWidth * 2), (squareWidth + centerYOffset));
			
			grid[6] = new Rect(-squareWidth, (squareWidth + centerYOffset), 0, (squareWidth * 2) + centerYOffset);
			grid[7] = new Rect(0, (squareWidth + centerYOffset), squareWidth, (squareWidth * 2) + centerYOffset);
			grid[8] = new Rect(squareWidth, (squareWidth + centerYOffset), (squareWidth * 2), ((squareWidth * 2) + centerYOffset));
		}
	}
	
	
	/**
	 * Translate a views rect defined in relationship to its parent into a actual on screen coordinate rect
	 * 
	 * @param rect - the rect relative to it's parent
	 * @return rect - the actual on screen rect
	 */
	private Rect translateRect(Rect rect){
		int width = rect.right - rect.left;
		int height = rect.bottom - rect.top;
		
		Rect newRect = new Rect();
		newRect.left = rect.left + center.x - (width/2);
		newRect.right = rect.right + center.x - (width/2);
		newRect.top = rect.top + center.y - (height/2);
		newRect.bottom = rect.bottom + center.y - (height/2);
		return newRect;
	}
	
	/**
	 * Show the delete button for all draggable icons in the grid
	 */
	private void showAllDelete(){
		for(int i = 0; i < icons.size(); i++){
			if(icons.get(i) != null){
				icons.get(i).showDelete();
			}
		}
	}
	
	/**
	 * Hide the delete button for all draggable icons in the grid
	 */
	private void hideAllDelete(){
		for(int i = 0; i < icons.size(); i++){
			if(icons.get(i) != null){
				icons.get(i).hideDelete();
			}
		}
	}
	
 
     /////////////////////////////////////////////////////
     //                 ANIMATIONS                     //
     ////////////////////////////////////////////////////
     
     /**
      * Cause all draggable icons in the grid to start to jiggle 
      */
     private void startJiggling() {
    	 for(int i = 0; i < icons.size(); i++){
    		 if(icons.get(i) != null){
	    		 if(i%2==0){
	    			 jiggleLeft(icons.get(i));
	    		 }else{
	    			 jiggleRight(icons.get(i));
	    		 }
    		 }
    	 }
 	}
     
    /**
     * Cause all draggable icons in the grid to stop jiggling
     */
    private void stopJiggling(){
    	for(int i = 0; i < icons.size(); i++){
    		if(icons.get(i) != null){
    			icons.get(i).clearAnimation();
    		}
    	}
    }
    
    /**
     * Cause an individual draggable icon to start to jiggle based on its position in the grid
     * 
     * @param v - the view to jiggle
     */
    public static void startJiggling(DraggableIcon v){
    	if(v.getGridIndex() % 2 == 0){
    		jiggleLeft(v);
		}else{
			jiggleRight(v);
		}
    }
     
    /**
     * Cause a view to begin jiggling to the left. When finished the view will jiggle to the right and repeat until clearAnimation is called
     * 
     * @param target - the view to jiggle 
     */
	private static void jiggleLeft(final View target) {
		final Animation jiggleLeft = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_left);
		final Animation jiggleRight = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_right);
		target.startAnimation(jiggleLeft);
		jiggleLeft.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleRight);
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationStart(Animation animation) {
			}
		});
		jiggleRight.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleLeft);
			}
			
			public void onAnimationRepeat(Animation animation) {
			}
	
			public void onAnimationStart(Animation animation) {
			}
		});
	}
 	
	/**
     * Cause a view to begin jiggling to the right. When finished the view will jiggle to the left and repeat until clearAnimation is called
     * 
     * @param target - the view to jiggle 
     */
 	private static void jiggleRight(final View target) {
 		final Animation jiggleLeft = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_left);
 		final Animation jiggleRight = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_right);
 		target.startAnimation(jiggleRight);
 		jiggleLeft.setAnimationListener(new AnimationListener() {
 			public void onAnimationEnd(Animation animation) {
 				target.startAnimation(jiggleRight);
 			}
 			public void onAnimationRepeat(Animation animation) {
 			}
 			public void onAnimationStart(Animation animation) {
 			}
 		});
 		jiggleRight.setAnimationListener(new AnimationListener() {
 			public void onAnimationEnd(Animation animation) {
 				target.startAnimation(jiggleLeft);
 			}
 			
 			public void onAnimationRepeat(Animation animation) {
 			}

 			public void onAnimationStart(Animation animation) {
 			}
 		});
 	}
 	
 	/**
 	 * Animate a draggable icon from its current position to the next position in the grid
 	 * 
 	 * @param target - the draggable icon to animate
 	 * @param forward - whether the draggable icon is moving forward in the grid or not.
 	 */
 	private void slide(final DraggableIcon target, final boolean forward){
 		if (target == null) {
 			return;
 		}
 		Rect fromRect = grid[target.getGridIndex()];
 		Rect toRect = grid[forward?nextIndex(target.getGridIndex()):previousIndex(target.getGridIndex())];
 		
 		final Animation slide = new TranslateAnimation(Animation.ABSOLUTE,0,
 													   Animation.ABSOLUTE,toRect.left - fromRect.left,
 													  Animation.ABSOLUTE,0,
													   Animation.ABSOLUTE,toRect.top - fromRect.top);
 		slide.setDuration(200);
 		target.startAnimation(slide);
 		
 		slide.setAnimationListener(new AnimationListener() {
 			public void onAnimationEnd(Animation animation) {
 				if(forward){
 					target.setGridIndex(nextIndex(target.getGridIndex()),true);
 				}else{
 					target.setGridIndex(previousIndex(target.getGridIndex()),true);
 				}
 				icons.set(target.getGridIndex(),target); 
 				startJiggling(target);
 			}
 			public void onAnimationRepeat(Animation animation) {
 			}
 			public void onAnimationStart(Animation animation) {
 			}
 		});
 	}
 	
 	/**
 	 * Animates a draggable icon to grow and become slightly opaque 
 	 * 
 	 * @param target - the draggable icon to animate
 	 */
 	public static void grow(final DraggableIcon target){
 		if(target instanceof AddIcon){
 			return;
 		}
 		target.clearAnimation();
 		final Animation grow = AnimationUtils.loadAnimation(target.getContext(), R.anim.grow);
 		grow.setFillEnabled(true);
 		grow.setFillAfter(true);
 		target.startAnimation(grow);
 	}
 	
 	/**
 	 * Animate a draggable icon back to its original size and opacity
 	 * 
 	 * @param target - the draggable icon to animate
 	 */
 	public static void shrink(final DraggableIcon target){
 		final Animation shrink = AnimationUtils.loadAnimation(target.getContext(), R.anim.shrink);
 		shrink.setFillEnabled(true);
 		shrink.setFillAfter(true);
 		target.startAnimation(shrink);
 		shrink.setAnimationListener(new AnimationListener() {
 			public void onAnimationEnd(Animation animation) {
 				startJiggling(target);
 			}
 			public void onAnimationRepeat(Animation animation) {
 			}
 			public void onAnimationStart(Animation animation) {
 			}
 		});
 	}
 	

}
