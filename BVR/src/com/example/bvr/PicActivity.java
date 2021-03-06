package com.example.bvr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.example.bvr.GridDataCamera.gridType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PicActivity extends Activity {
	Button button, next, prev;
	ImageView image;
	String filename;
	int tw, th, td; //texture dimensions
	int curZ = 0; //current image slice we're on
	int rx1, ry1, rx2, ry2; //coordinates for a rectangle to draw on the image
	int left, right, top, bottom;
	int touchCount = 0;
	boolean drawRect = false;
	int zJump = 25;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
 
		//get the filename from the invoking activity
		Intent intent = getIntent();
		filename = intent.getStringExtra(GridActivity.EXTRA_MESSAGE);
				
		filename = filename.substring(0, filename.length() - 4);
		
		addListenerOnButton();
		
		//curZ = 0; 
		
		//Get the dimensions of the texture
		readSizeFile();
		
		//initialize the first image to be posted
		setImage();
	}
 
	
	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
	}
	
	
	public void addListenerOnButton() {
 
		image = (ImageView) findViewById(R.id.imageView1);
 
		button = (Button) findViewById(R.id.button1);
		next = (Button) findViewById(R.id.button2);
		prev = (Button) findViewById(R.id.button3);
		
		button.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View arg0) {
				int z = 0;
				
				if(curZ - 128 < 0)
					z = 0;
				else if(curZ + 128 > td)
					z = td - 256;
				else
					z = curZ - 128;
				
				
				String result = Float.toString(left) + " " + Float.toString(th - bottom) + " " + Float.toString(z) + " " + Float.toString(bottom - top)
						+ " " + Float.toString(right - left) + " 256" + " " + Float.toString(tw)+ " " + Float.toString(th)+ " " + Float.toString(td);
						
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", result);
				setResult(1, returnIntent);
				finish();
			}
 
		});
		
		
		next.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				if(curZ + zJump > td)
					curZ = td;
				else
					curZ += zJump;


				setImage();
			}
 
		});
		
		prev.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View arg0) {
				if(curZ - zJump < 0)
					curZ = td;
				else
					curZ -= zJump;
				
				setImage();
			}
 
		});
		
		image.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN){
	           
	            		rx2 = (int) event.getX();
	            		ry2 = (int) event.getY();
	            		
	            		touchCount = 0;
	            		drawRect = true;
	            		setImage();
	            	            	
	            }
	            return true;
	        }
	    });
 
	}
	
	//
	// Read in the GRID file to populate the grid points
	//
	public void readSizeFile() {
		// Read in the .grid file for data information
		try {
			String fn = filename;
			
			fn = fn + "size";
			
			File file = new File(fn);
			FileReader fileReader;
			
				fileReader = new FileReader(file);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;

			// grab the dimensions of the grid.

			line = bufferedReader.readLine();
			
			String[] split = line.split(" ");
			tw = Integer.parseInt(split[0]);
			th = Integer.parseInt(split[1]);
			td = Integer.parseInt(split[2]);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setImage()
	{

		byte[] bytes = new byte[tw * th];

		try {
			RandomAccessFile file = new RandomAccessFile(filename+"raw", "r");
			//RandomAccessFile file = new RandomAccessFile(filename+"raw", "r");
			//RandomAccessFile file = new RandomAccessFile(filename+"raw", "r");
			int offset = (tw * th * curZ);
					
			file.seek(offset);			
			file.read(bytes);
			
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create the bitmap
		Bitmap bmap = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888);
		
		int count = 0;
		for(int y = 0; y < th; y++)
		{
			for(int x = 0; x < tw; x++)
			{
				int c = bytes[count++];				
				int color = (0xFF << 24) | (c << 16) | (c << 8) | c;
				bmap.setPixel(x, th - 1 - y, Color.argb(255, c, c, c));
			}
		}
		
		Canvas tempCanvas = new Canvas(bmap);
		
		Paint rectPaint = new Paint(0);
		rectPaint.setColor(0x55FF0000);
		if(drawRect)
		{
			
			left = rx2 - 128;
			right = rx2 + 128;
			
			top = ry2 - 128;
			bottom = ry2 + 128;
			
			if(left < 0)
			{
				right -= left;
				left = 0;
			}
			
			if(right > image.getWidth())
			{
				left -= right - image.getWidth();
				right = image.getWidth() - 1;
			}
			
			if(top < 0)
			{
				bottom -= top;
				top = 0;
			}
			if(bottom > image.getHeight())
			{
				top   -= bottom - image.getHeight();
				bottom = image.getHeight() - 1;
			}
			
			tempCanvas.drawRect(left, top, right, bottom, rectPaint);
			drawRect = false;
			rx1 = 0;
			ry1 = 0;
			ry2 = 0;
			rx2 = 0;
		}
		
	    //ColorMatrix cm = new ColorMatrix();
	    //cm.setSaturation(0);
	    //ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		
	    //image.setColorFilter(f);
	    
		image.setImageDrawable(new BitmapDrawable(getResources(), bmap));
		
		
	}
 
}
