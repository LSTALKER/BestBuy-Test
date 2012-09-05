package com.bestbuy.android.ui.draggable;



import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;

/**
 * DraggableIcon
 * 
 * a custom LinearLayout that contains an image and text.
 * 
 * Has the ability to be deleted dragged and locked into place. 
 * 
 * @author Aaron Goldberg
 */
public class DraggableIcon extends LinearLayout{
	
	private View layout;
	private int gridIndex;
	private boolean removable;
	protected DraggableIconFrame parentGrid; 

	public DraggableIcon(Context context, DraggableIconFrame grid, int resourceId, String text, int gridIndex, boolean removable,
			final OnTouchListener dragListener, final OnClickListener clickListener, final OnLongClickListener longClickListener) {
		super(context);
		
		this.removable = removable;
		parentGrid = grid;	
		setGridIndex(gridIndex,true);
		setupView(context, resourceId,text);
	
		final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());
		
		//Ensure listeners only operate when grid is in the correct edit mode
		setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(gestureDetector.onTouchEvent(event)){
					return true;
				}
				 if(parentGrid.isEditMode()){
					 if(dragListener != null){
						 return dragListener.onTouch(v, event);
					 }
				 }
				 return false;
			}
		});	
		setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	           if(!parentGrid.isEditMode()){
	        	   if(clickListener != null){
	        		   clickListener.onClick(v);   
	        	   }        	   
	           }
	        }
		});	
		setOnLongClickListener(new OnLongClickListener() {
	        public boolean onLongClick(View v) {
	           if(!parentGrid.isEditMode()){
	        	   if(longClickListener != null){
	        		   return longClickListener.onLongClick(v);
	        	   }
	           }
	           return false;
	        }
		});	
		
	
		
	}
	
	/**
	 * Setup the draggable icon's view
	 * 
	 * @param context - the context the view in running in, through which it can access the current theme, resources, etc.
	 * 
	 * @param resourceId - resource id of the draggable icon's drawable
	 * @param text - string to display on the draggable icon.
	 */
	private void setupView(Context context, int resourceId, String text){
		layout = View.inflate(context, R.layout.draggable_icon, null); 
		
		((ImageView)layout.findViewById(R.id.draggable_icon_image)).setBackgroundResource(R.drawable.btn_grid_bg);
		((ImageView)layout.findViewById(R.id.draggable_icon_image)).setImageResource(resourceId);
    	((TextView)layout.findViewById(R.id.draggable_icon_text)).setText(text);
    	((ImageView)layout.findViewById(R.id.draggable_icon_delete)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		parentGrid.deleteIcon(getGridIndex(),true);
         	}      
 		});	
 	
		addView(layout);
	}

	/**
	 * Set the index in the draggable icon grid of this draggable icon with the option to move to that position.
	 * 
	 * @param gridIndex - the new grid index
	 * @param move - whether or not to move
	 */
	public void setGridIndex(int gridIndex,boolean move){
		this.gridIndex = gridIndex;
		if(move){
			FrameLayout.LayoutParams par = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
	    	par.topMargin = parentGrid.getGridRect(gridIndex).top;
			par.leftMargin = parentGrid.getGridRect(gridIndex).left;
			setLayoutParams(par);
		}
	}
	
	/**
	 * Gets the current position in the draggable icon grid
	 * 
	 * @return int - the current position
	 */
	public int getGridIndex(){
		return gridIndex;
	}
	
	/**
	 * Get the bounds of the draggable icon as a Rect in its current position (dragging position).
	 * 
	 * @return rect - the draggable icons bounds
	 */
	public Rect getBounds(){
		return new Rect(getLeft(),getTop(),getRight(),getBottom());
	}
	
	/**
	 * Get the text of draggable icon 
	 * 
	 * @return string - the text of the draggable icon
	 */
	public String getText(){
		return ((TextView)layout.findViewById(R.id.draggable_icon_text)).getText().toString();
	}
	
	/**
	 * Show the delete button
	 */
	public void showDelete(){
		if(removable)
			((ImageView)layout.findViewById(R.id.draggable_icon_delete)).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide the delete button
	 */
	public void hideDelete(){
		((ImageView)layout.findViewById(R.id.draggable_icon_delete)).setVisibility(View.GONE);
	}
	
	public DraggableIconFrame getParentGrid(){
		return parentGrid;
	}
	
	public void setParentGrid(DraggableIconFrame parentGrid){
		this.parentGrid = parentGrid;
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	if(parentGrid.getDraggableGallery() != null){
        		if(!parentGrid.getDraggableGallery().isEditMode())
        			return parentGrid.getDraggableGallery().onFling(e1, e2, velocityX, velocityY);
        	}
        	return false;
        }

    }

}

