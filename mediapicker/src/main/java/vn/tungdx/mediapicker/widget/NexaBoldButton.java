/*******************************************************************************
 *  
 *  Created by iExemplar on 2016.
 *  
 *  Copyright (c) 2016 iExemplar. All rights reserved.
 *  
 *  
 *******************************************************************************/
package vn.tungdx.mediapicker.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;


public class NexaBoldButton extends AppCompatButton {


  public NexaBoldButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    if (!isInEditMode()) {
      setType(context);
    }
  }


  public NexaBoldButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) {
      setType(context);
    }
  }

  public NexaBoldButton(Context context) {
    super(context);
    if (!isInEditMode()) {
      setType(context);
    }
  }

  private void setType(Context context) {
    this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Nexa_Bold.ttf"));
  }
}
