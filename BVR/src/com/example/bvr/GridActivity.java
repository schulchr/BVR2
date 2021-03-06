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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bvr.R;

public class GridActivity extends Activity {
	/** Hold a reference to our GLSurfaceView */
	private GridGLSurfaceView mGLSurfaceView;
	private GridRenderer mRenderer;
	private String filename;

	public final static String EXTRA_MESSAGE = "com.example.bvr.MESSAGEGRID";
	
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
		filename = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		Button picture;
		
		picture = (Button) findViewById(R.id.pictureButton);
		
		picture.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				mRenderer.customLoaded = false;
				mRenderer.useCustom = true;
				Intent intent = new Intent(GridActivity.this, PicActivity.class);
				intent.putExtra(EXTRA_MESSAGE, filename);
				startActivityForResult(intent, 1);
			}
 
		});
		

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
		
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        	mRenderer.useCustom = true;
		        	mRenderer.loadedPoint = -5;
		        } else {
		            // The toggle is disabled
		        	mRenderer.useCustom = false;
		        	mRenderer.customLoaded = false;
		        	mRenderer.loadDownscaled(-1);
		        }
		    }
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 1)
		{
			if(resultCode == 1)
			{
				String result = data.getStringExtra("result");
				
				String[] split = result.split(" ");
				mRenderer.cXstart = (int) Float.parseFloat(split[0]);
				mRenderer.cYstart = (int) Float.parseFloat(split[1]);
				mRenderer.cZstart = (int) Float.parseFloat(split[2]);
				
				mRenderer.cW = (int) Float.parseFloat(split[3]);
				mRenderer.cH = (int) Float.parseFloat(split[4]);
				mRenderer.cD = (int) Float.parseFloat(split[5]);
				
				mRenderer.ctw = (int) Float.parseFloat(split[6]);
				mRenderer.cth = (int) Float.parseFloat(split[7]);
				mRenderer.ctd = (int) Float.parseFloat(split[8]);
				
				mRenderer.useCustom = true;
				mRenderer.customLoaded = false;
				
				ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
				toggle.setChecked(true);
			}
		}
	}
	
	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		//mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		//mGLSurfaceView.onPause();
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