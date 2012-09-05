package com.bestbuy.android.ui.draggable;



import android.content.Context;
import android.view.View;

import com.bestbuy.android.R;

/**
 * AddIcon
 * 
 * a special version of Draggable Icon that allows the developer a way to add more icons
 * 
 * @author Aaron Goldberg
 */
public class AddIcon extends DraggableIcon{


	public AddIcon(Context context, DraggableIconFrame grid, int gridIndex, final OnClickListener clickListener, final OnLongClickListener longClickListener) {
		super(context,grid,R.drawable.btn_add_icon,"Add Item",gridIndex,false,null,clickListener,longClickListener);
	}
	
	
	public void setAddListener(final OnClickListener addListener){
		setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	         if(!parentGrid.isEditMode()){
	        	   if(addListener != null){
	        		   addListener.onClick(v);   
	        	   }        	   
	           }
	        }
		});	
	}
}
