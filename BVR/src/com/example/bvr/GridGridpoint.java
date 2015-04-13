package com.example.bvr;

import com.example.bvr.GridDataCamera.gridType;


public class GridGridpoint {
	float x, y, z; //location of the grid point
	gridType type;
	int[] textures;
	
	public GridGridpoint()
	{
		x = y = z = 0;
		textures = new int[8];
	}
}