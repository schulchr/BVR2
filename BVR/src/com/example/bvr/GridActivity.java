package com.example.bvr;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
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
		boolean checked = ((RadioButton) v).isChecked();
		
		switch(v.getId()){
			case R.id.alphaButton:
					findViewById(R.id.rotateButton).setSelected(false);
					findViewById(R.id.rotateButton).animate();
					findViewById(R.id.alphaButton).setSelected(true);
					findViewById(R.id.stepSizeButton).setSelected(false);
					findViewById(R.id.numStepsButton).setSelected(false);
					findViewById(R.id.minButton).setSelected(false);
					findViewById(R.id.maxButton).setSelected(false);	
					mRenderer.setVariable(0);
				break;
			case R.id.stepSizeButton:
					findViewById(R.id.rotateButton).setSelected(false);
					findViewById(R.id.alphaButton).setSelected(false);
					findViewById(R.id.stepSizeButton).setSelected(true);
					findViewById(R.id.numStepsButton).setSelected(false);
					findViewById(R.id.minButton).setSelected(false);
					findViewById(R.id.maxButton).setSelected(false);
					mRenderer.setVariable(4);
				break;
			case R.id.rotateButton:
					findViewById(R.id.rotateButton).setSelected(true);
					findViewById(R.id.alphaButton).setSelected(false);
					findViewById(R.id.stepSizeButton).setSelected(false);
					findViewById(R.id.numStepsButton).setSelected(false);
					findViewById(R.id.minButton).setSelected(false);
					findViewById(R.id.maxButton).setSelected(false);
					mRenderer.setVariable(1);
				break;
			case R.id.numStepsButton:
					findViewById(R.id.rotateButton).setSelected(false);
					findViewById(R.id.alphaButton).setSelected(false);
					findViewById(R.id.stepSizeButton).setSelected(false);
					findViewById(R.id.numStepsButton).setSelected(true);
					findViewById(R.id.minButton).setSelected(false);
					findViewById(R.id.maxButton).setSelected(false);
					mRenderer.setVariable(5);
				break;
			case R.id.minButton:
					findViewById(R.id.rotateButton).setSelected(false);
					findViewById(R.id.alphaButton).setSelected(false);
					findViewById(R.id.stepSizeButton).setSelected(false);
					findViewById(R.id.numStepsButton).setSelected(false);
					findViewById(R.id.minButton).setSelected(true);
					findViewById(R.id.maxButton).setSelected(false);
					mRenderer.setVariable(2);
				break;
			case R.id.maxButton:
					findViewById(R.id.rotateButton).setSelected(false);
					findViewById(R.id.alphaButton).setSelected(false);
					findViewById(R.id.stepSizeButton).setSelected(false);
					findViewById(R.id.numStepsButton).setSelected(false);
					findViewById(R.id.minButton).setSelected(false);
					findViewById(R.id.maxButton).setSelected(true);
					mRenderer.setVariable(3);
				break;
		}
	}
}