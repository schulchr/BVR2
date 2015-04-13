package com.example.bvr;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.bvr.R;

public class GridActivity extends Activity {
	/** Hold a reference to our GLSurfaceView */
	private GridGLSurfaceView mGLSurfaceView;
	private GridRenderer mRenderer;
	private String filename;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		Intent intent = getIntent();
		
		mGLSurfaceView = (GridGLSurfaceView) findViewById(R.id.gl_surface_view);
		
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		// Set the renderer to our demo renderer, defined below.
		mRenderer = new GridRenderer(this, mGLSurfaceView);
		mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);
		mRenderer.setFilename(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));
		
		
		final Handler handler = new Handler();
		
		handler.post(new Runnable(){
			
			@Override
			public void run()
			{
				setFPS(mRenderer.frames);
				mRenderer.frames = 0;
				
				setGridNum(mRenderer.loadedPoint);
				
				handler.postDelayed(this,1000);
			}
		});
		
	}

	
	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		mGLSurfaceView.onPause();
	}
	
	public void radioClick(View v)
	{		
		switch(v.getId()){
			case R.id.alphaButton:
					mRenderer.setVariable(0);
				break;
			case R.id.stepSizeButton:
					mRenderer.setVariable(4);
				break;
			case R.id.rotateButton:
					mRenderer.setVariable(1);
				break;
			case R.id.numStepsButton:
					mRenderer.setVariable(5);
				break;
			case R.id.minButton:
					mRenderer.setVariable(2);
				break;
			case R.id.maxButton:
					mRenderer.setVariable(3);
				break;
		}		
	}
	
	public void directionClick(View v)
	{
		float factor = 0.2f;
		switch(v.getId()){
		case R.id.ImageButton_up:
			mRenderer.nextPoint = GridRenderer.gridCamera.nextGridPoint(mRenderer.loadedPoint, GridRenderer.gridPoints[mRenderer.loadedPoint], 1);
		break;	
		case R.id.ImageButton_down:
				mRenderer.nextPoint = GridRenderer.gridCamera.nextGridPoint(mRenderer.loadedPoint, GridRenderer.gridPoints[mRenderer.loadedPoint], -1);
			break;	
		}		
	}
	
	public void setFPS(int fps)
	{
		TextView tv = (TextView) findViewById(R.id.fpsText);
		String str = "FPS: ";
		tv.setText(str + Integer.toString(fps));
	}
	
	public void setGridNum(int gridNum)
	{
		TextView tv = (TextView) findViewById(R.id.gridNumText);
		String str = "Grid#: ";
		tv.setText(str + Integer.toString(gridNum));
		
		//Make sure the gridCamera is instantiated before query of its location
		if(mRenderer.gridCamera != null)
		{
			tv = (TextView) findViewById(R.id.xText);
			str = "X: ";
			tv.setText(str + Float.toString(mRenderer.gridCamera.loc[0]));
			
			tv = (TextView) findViewById(R.id.yText);
			str = "Y: ";
			tv.setText(str + Float.toString(mRenderer.gridCamera.loc[1]));
			
			tv = (TextView) findViewById(R.id.zText);
			str = "Z: ";
			tv.setText(str + Float.toString(mRenderer.gridCamera.loc[2]));
		}
	}
}