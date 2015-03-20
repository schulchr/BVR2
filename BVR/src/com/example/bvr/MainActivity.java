package com.example.bvr;

import java.io.File;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.example.bvr.MESSAGE";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);      
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    public void loadFiles(View view)
    {
    	//Launch an alert window to allow user to select the file they want to view
		String dirName = Environment.getExternalStorageDirectory().toString() + "/grid/";
		
		File dir = new File(dirName);
		
		String[] fileNames = dir.list();
		final ArrayList<String> txtFiles = new ArrayList<String>();
		
		for (int i=0; i < fileNames.length; i++)
		{
		    if(fileNames[i].matches(".*?[.]grid$"))
		    {
		    	txtFiles.add(fileNames[i]);
		    }
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Load a GRID file");
		
		CharSequence[] items = txtFiles.toArray(new CharSequence[txtFiles.size()]);
		
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				File sdcard = Environment.getExternalStorageDirectory();					
							    
			    //Send this text to the new activity
				Intent intent = new Intent(MainActivity.this, GridActivity.class);
				String filename = sdcard.getPath() + "/grid/" + txtFiles.get(which);
				intent.putExtra(EXTRA_MESSAGE, filename);
				startActivity(intent);									
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
    }
}
