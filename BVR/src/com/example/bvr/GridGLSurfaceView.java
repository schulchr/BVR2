package com.example.bvr;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class GridGLSurfaceView extends GLSurfaceView
{	

	private GridRenderer mRenderer;
	
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.0f;
	
	// Offsets for touch events	 
    private float mPreviousX;
    private float mPreviousY;
    
    private float mDensity;
        	
	public GridGLSurfaceView(Context context) 
	{
		super(context);			
	}
	
	public GridGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);		

		mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
	}

	boolean multiTouch = false;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{

		mScaleDetector.onTouchEvent(event);
		
		final int action = MotionEventCompat.getActionIndex(event);
		
		
		
		if (event != null)
		{			
			
			if(event.getPointerCount() > 1)
				multiTouch = true;
			
			if(action == MotionEvent.ACTION_UP && multiTouch){
				multiTouch = false;
				return true;
			}			

			float x = event.getX();
			float y = event.getY();
				
				if (event.getAction() == MotionEvent.ACTION_MOVE && !multiTouch)
				{
					
					
					if (mRenderer != null)
					{
						if(mRenderer.rotateTog){
							float deltaX = (x - mPreviousX) / mDensity / 2f;
							float deltaY = (y - mPreviousY) / mDensity / 2f;
							
							mRenderer.mDeltaX += deltaX;
							mRenderer.mDeltaY += deltaY;												
						}
						else
						{
							mRenderer.updateShaderValues((int) (y - mPreviousY));
						}
						
					}

				}	

				mPreviousX = x;
				mPreviousY = y;
				
				mRenderer.setZoom(mScaleFactor);
				
			return true;
		}
		else
		{
			return super.onTouchEvent(event);
		}		
	}

	// Hides superclass method.
	public void setRenderer(GridRenderer renderer, float density) 
	{
		mRenderer = renderer;
		mDensity = density;
		super.setRenderer(renderer);
	}
	
	private class ScaleListener 
			extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
		    mScaleFactor *= detector.getScaleFactor();
		
		    // Don't let the object get too small or too large.
		    mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
		
		    invalidate();
		    return true;
		}
}
}