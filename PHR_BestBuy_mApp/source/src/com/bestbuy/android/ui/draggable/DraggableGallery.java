package com.bestbuy.android.ui.draggable;



import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.Home;

public class DraggableGallery extends Gallery {
	
	DraggableGalleryAdapter draggableGalleryAdapter;
	
	private DraggableIconFrame iconGrid0;
	private DraggableIconFrame iconGrid1;
	
	private boolean editMode = false;
	
	private int currentIndex = 0;
	
	private boolean flinging = false;
	private long firstEdgeHover;
	

	public DraggableGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DraggableGallery(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		draggableGalleryAdapter = new DraggableGalleryAdapter();
	    setAdapter(draggableGalleryAdapter);
	    
	    //TODO make our own item selected listener and always call super to make sure you set the current index... For now just make sure you have that line whenever you overwrite this. Why doesnt android have a better way to get the current position?
	    setOnItemSelectedListener(new OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
            	currentIndex = position;
            }
            public void onNothingSelected(AdapterView<?> adapter) {
            }
       });

	}
	
	
	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public void setCenterYOffset(int offset){
		iconGrid0.setCenterYOffset(offset);
		iconGrid1.setCenterYOffset(offset);
	}
	
	public void setMaxWidth(int width){
		iconGrid0.setMaxWidth(width);
		iconGrid1.setMaxWidth(width);
	}
	
	

	public boolean isFlinging() {
		return flinging;
	}

	public void setFlinging(boolean flinging) {
		this.flinging = flinging;
	}

	/**
	 * Cancel edit mode - should be called from a "done" button or onBackPressed() in the activity
	 */
	public void cancelEditMode(){
		if(editMode){
     		editMode = false;
     		iconGrid0.cancelEditMode();
     		iconGrid1.cancelEditMode(); 
     		Home.saveGridIds(0, iconGrid0.getCurrentIcons());
     		Home.saveGridIds(1, iconGrid1.getCurrentIcons());
     	}
	}
	
	public void startEditMode(){
		editMode = true;
		iconGrid0.startEditMode();
		iconGrid1.startEditMode();
	}
	
	/**
	 * Tests whether or not the draggable icon grid is being edited or not
	 * 
	 * @return boolean - whether or not the draggable icon grid is being edited or not
	 */
	public boolean isEditMode(){
		return editMode;
	}
	
	private DraggableIconFrame getCurrentFrame(){
		if(currentIndex == 0){
			return iconGrid0;
		}else{
			return iconGrid1;
		}
	}
	
	private DraggableIconFrame getNextFrame(){
		if(currentIndex == 1){
			return iconGrid0;
		}else{
			return iconGrid1;
		}
	}
	
	public void addIcon(int resourceId, String text,boolean removable,  OnClickListener clickListener, OnLongClickListener longListener){
		if (iconGrid0 != null && !iconGrid0.getCurrentIcons().contains(text) && iconGrid1 != null && !iconGrid1.getCurrentIcons().contains(text)) {
			if(iconGrid0.getNumIcons() < 8){
				iconGrid0.addIcon(resourceId, text, removable,dragListener, clickListener,longListener);
				
			}else if(iconGrid1.getNumIcons() < 8){
				iconGrid1.addIcon(resourceId, text, removable,dragListener, clickListener,longListener);
			}
		}
	}
	
	public void addIcon(int resourceId, String text,boolean removable,  OnClickListener clickListener,OnLongClickListener longListener, int page){
		if (iconGrid0 != null && !iconGrid0.getCurrentIcons().contains(text) && iconGrid1 != null && !iconGrid1.getCurrentIcons().contains(text)) {
			if(iconGrid0.getNumIcons() < 9 && page == 0){
				iconGrid0.addIcon(resourceId, text, removable,dragListener, clickListener,longListener);
			}else if(iconGrid1.getNumIcons() < 9 && page == 1){
				iconGrid1.addIcon(resourceId, text, removable,dragListener, clickListener,longListener);
			}
		}
	}
     
     /**
 	 * A generic touch listener used on all dragable icons to handle dragging/dropping/hovering
 	 */
 	private OnTouchListener dragListener = new OnTouchListener() {
 		public boolean onTouch(View v, MotionEvent event) {
 			if(flinging)
 				return false;
 			final DraggableIcon icon = (DraggableIcon)v;
			FrameLayout.LayoutParams par = (FrameLayout.LayoutParams) icon.getLayoutParams();
			
			final int x = (int) event.getRawX();
			final int y = (int) event.getRawY();
			
			icon.bringToFront();
			switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE: {
					
					//move
					par.topMargin = y -icon.getParentGrid().getCenterPoint().y - 40; 
					par.leftMargin = x - icon.getParentGrid().getCenterPoint().x - 10;
					icon.setLayoutParams(par);
					
					if(getCurrentFrame().checkHoverEdge(icon) && firstEdgeHover == 0){
						firstEdgeHover = System.currentTimeMillis();
					}else if(!getCurrentFrame().checkHoverEdge(icon)){
						firstEdgeHover = 0;
					}
					
					if(getCurrentFrame().checkHoverEdge(icon) && firstEdgeHover != 0 && System.currentTimeMillis() - firstEdgeHover > 500 && !flinging && (System.currentTimeMillis() - getCurrentFrame().getLastHover() > 250)){
						flinging = true;
						firstEdgeHover = 0;
						//BBYLog.i("HOME", "***********BEFORE FLING**********");
						//BBYLog.i("HOME", "FRAME 0: " + iconGrid0.getCurrentIcons());
						//BBYLog.i("HOME", "FRAME 1: " + iconGrid1.getCurrentIcons());
						onFling(null,null,currentIndex == 0?-2500:2500,0);
						//BBYLog.i("HOME", "***********AFTER FLING / BEFORE DELETE**********");
						//BBYLog.i("HOME", "FRAME 0: " + iconGrid0.getCurrentIcons());
						//BBYLog.i("HOME", "FRAME 1: " + iconGrid1.getCurrentIcons());
						
						getCurrentFrame().deleteIcon(icon.getGridIndex(),false);
						//BBYLog.i("HOME", "***********AFTER DELETE / BEFORE INSERT**********");
						//BBYLog.i("HOME", "FRAME 0: " + iconGrid0.getCurrentIcons());
						//BBYLog.i("HOME", "FRAME 1: " + iconGrid1.getCurrentIcons());
						DraggableIcon passIcon = getNextFrame().insertIcon(icon);
						//BBYLog.i("HOME", "***********AFTER INSERT / (BEFORE ADD PASS)**********");
						//BBYLog.i("HOME", "FRAME 0: " + iconGrid0.getCurrentIcons());
						//BBYLog.i("HOME", "FRAME 1: " + iconGrid1.getCurrentIcons());
						if(passIcon != null){
							getCurrentFrame().addIcon(passIcon);
							DraggableIconFrame.startJiggling(passIcon);
							//BBYLog.i("HOME", "***********AFTER ADD PASS**********");
							//BBYLog.i("HOME", "FRAME 0: " + iconGrid0.getCurrentIcons());
							//BBYLog.i("HOME", "FRAME 1: " + iconGrid1.getCurrentIcons());
						}

					}
					if(!flinging){
						if(getCurrentFrame().hover(icon)){
							firstEdgeHover = 0;
						}
					}

					break;
				}
				
				
				case MotionEvent.ACTION_UP: {
					DraggableIconFrame.shrink(icon);
					int newGridPos = getCurrentFrame().findOverlappingIcon(icon);
					if(newGridPos == -1 || System.currentTimeMillis() - getCurrentFrame().getLastHover() <= 250){
						icon.setGridIndex(icon.getGridIndex(),true);
					}else{
						getCurrentFrame().swapIcon(icon.getGridIndex(),newGridPos);
					}	
					break;
				}
				case MotionEvent.ACTION_DOWN: {
					icon.getParentGrid().setHoverIndex(-1);
					DraggableIconFrame.grow(icon);
					break;
				}
			}
			
			return true;
 		}
 	};
	
	
	
	 private class DraggableGalleryAdapter extends BaseAdapter {          
	        public int getCount() {
	           return 2; 
	        }
	        
	        public Object getItem(int position) {
	    		if(position == 0){
	    			return iconGrid0;
	    		}else{
	    			return iconGrid1;
	    		}
	        }

	        public long getItemId(int position) {
	           return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {       	
	        	View v = convertView;
				if (v == null) {
					
					LayoutInflater inflater = LayoutInflater.from(getContext());
					v = inflater.inflate(R.layout.draggable_gallery_frame, parent, false);
					DraggableIconFrame iconGrid = (DraggableIconFrame)v.findViewById(R.id.draggable_icon_frame);
					
			    	if(position == 0 && iconGrid0 == null){
			    		iconGrid0 = iconGrid;
			    		iconGrid0.setPage(0);
			    		iconGrid0.setDraggableGallery(DraggableGallery.this);
			    		
					}else if (position == 1 && iconGrid1 == null){
						iconGrid1 = iconGrid;
						iconGrid1.setPage(1);
						iconGrid1.setDraggableGallery(DraggableGallery.this);
					}
				}
	        	return v;
	       }
	        
	        
	   }
}
