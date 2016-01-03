package com.example.bvr;

public class GridDataCamera {
	
	enum gridType{
		//All the corners 
		BUL_CORNER,
		BUR_CORNER,
		BLL_CORNER,
		BLR_CORNER,
		TUL_CORNER,
		TUR_CORNER,
		TLL_CORNER,
		TLR_CORNER,

		//Edges that go up and down in the x and z positions (top face)
		TOP_POSZ_EDGE,
		TOP_POSX_EDGE,
		TOP_NEGZ_EDGE,
		TOP_NEGX_EDGE,

		//Edges that go up and down in the x and z positions (bottom face)
		BOT_POSZ_EDGE,
		BOT_POSX_EDGE,
		BOT_NEGZ_EDGE,
		BOT_NEGX_EDGE,

		//Edges that go up and down in the y direction
		SIDE_NN_EDGE,
		SIDE_NP_EDGE,
		SIDE_PP_EDGE,
		SIDE_PN_EDGE,

		//Points on a given plane
		POS_XY_PLANE,
		NEG_XY_PLANE,
		POS_YZ_PLANE,
		NEG_YZ_PLANE,
		POS_XZ_PLANE,
		NEG_XZ_PLANE,

		//Any interior point
		INTERIOR
	};
	
	float[] loc = new float[3]; //location of the camera
	float[] dir = new float[4]; //direction that the camera is looking at
	
	//View volume information of the camera
	float far, near;
	float left, right;
	float top, bottom;
	
	int height, depth, width;
	
	//vectors defining the view volume
	float[] a = new float[3];//need this for creating a vector for a given grid point
	float[] ab = new float[3];
	float[] ad = new float[3];
	float[] ae = new float[3];
	
	public GridDataCamera(float f, float n, float l, float r, float t, float b)
	{
		loc[0] =  0;
		loc[1] =  0;
		loc[2] =  1;
		
		dir[0] =  0;
		dir[1] =  0;
		dir[2] = -1;
		
		far = f;
		near = n;
		left = l;
		right = r;
		top = t;
		bottom = b;		
	}
	
	public int nextGridPoint(int currentPoint, GridGridpoint point, int forward)
	{
		int nextInt = currentPoint;
		
		float[] dir1 = new float[3];
		dir1[0] = dir[0] * forward;
		dir1[1] = dir[1] * forward;
		dir1[2] = dir[2] * forward;
		
		//make sure direction of camera is normalized
		double length = Math.sqrt(dir1[0] * dir1[0] + dir1[1] * dir1[1] + dir1[2] * dir1[2]);
		dir1[0] /= length;
		dir1[1] /= length;
		dir1[2] /= length;
		
		//Phi is the angle from the X axis
		double phi = Math.atan2(dir1[1],  dir1[0]);
		
		//Theta is the angle from the z axis
		double theta = Math.acos(dir1[2]);
		
		theta = Math.toDegrees(theta);
		phi   = Math.toDegrees(phi);
		
		/*
		 * Here the offset for the current gridpoint will be done
		 */
		int offset = 0;
		boolean ic = false;
		//decide whether or not this point can be moved in a certain direction
		boolean nx = true, ny = true, nz = true, px = true, py = true, pz = true;
		
		//determine if it can go which way in the y direction
		switch(point.type)
		{
		//top corners
		case TUL_CORNER:			
		case TUR_CORNER:
		case TLL_CORNER:
		case TLR_CORNER:
		//top edges
		case TOP_POSZ_EDGE:
		case TOP_NEGZ_EDGE:
		case TOP_POSX_EDGE:
		case TOP_NEGX_EDGE:
		//the top face
		case POS_XZ_PLANE:
			ny = true;
			py = false;
			break;
			
		//bottom corners
		case BUL_CORNER:			
		case BUR_CORNER:
		case BLL_CORNER:
		case BLR_CORNER:
		//bottom edges
		case BOT_POSZ_EDGE:
		case BOT_NEGZ_EDGE:
		case BOT_POSX_EDGE:
		case BOT_NEGX_EDGE:
		//the bottom face
		case NEG_XZ_PLANE:
			ny = false;
			py = true;
			break;
			
		}
		
		//determine if it can go which way in the z direction
		switch(point.type)
		{
		//top corners
		case TUL_CORNER:			
		case TUR_CORNER:
		case BUL_CORNER:
		case BUR_CORNER:
		//edges
		case TOP_POSZ_EDGE:
		case BOT_POSZ_EDGE:
		case SIDE_NP_EDGE:
		case SIDE_PP_EDGE:
			
		//the top face
		case POS_XY_PLANE:
			nz = true;
			pz = false;
			break;			
			
		//top corners
		case TLL_CORNER:			
		case TLR_CORNER:
		case BLL_CORNER:
		case BLR_CORNER:
		//edges
		case TOP_NEGZ_EDGE:
		case BOT_NEGZ_EDGE:
		case SIDE_NN_EDGE:
		case SIDE_PN_EDGE:
			
		//the top face
		case NEG_XY_PLANE:
			nz = false;
			pz = true;
			break;			
		}
		
		//determine if it can go which way in the x direction
		switch(point.type)
		{
		//top corners
		case TUR_CORNER:			
		case TLR_CORNER:
		case BLR_CORNER:
		case BUR_CORNER:
		//edges
		case TOP_POSX_EDGE:
		case BOT_POSX_EDGE:
		case SIDE_PN_EDGE:
		case SIDE_PP_EDGE:
			
		//the top face
		case POS_YZ_PLANE:
			nx = true;
			px = false;
			break;			
			
		//top corners
		case TUL_CORNER:			
		case TLL_CORNER:
		case BLL_CORNER:
		case BUL_CORNER:
		//edges
		case TOP_NEGX_EDGE:
		case BOT_NEGX_EDGE:
		case SIDE_NN_EDGE:
		case SIDE_NP_EDGE:
			
		//the top face
		case NEG_YZ_PLANE:
			nx = false;
			px = true;
			break;			
		}
		
		//Check the theta for the offset of Z will be applied
		if(theta > 112.5)
		{
			if(nz)
				offset -= width * height;
		}
		if(theta < 67.5)
		{
			if(pz)
				offset += width * height;
		}
		
		//Check the theta to see if the x/y/xy offset is applied
		if(theta < 157.5 && theta > 27.5)
		{
			ic = true;
		}
	
		//If ic is true (include the x or y or x/y direction in the offset)
		if(ic)
		{
			double absPhi = Math.abs(phi);
			if(absPhi < 67.5)
			{
				//just add in the x direction
				if(px)
					offset += 1;
			}
			
			if(absPhi > 112.5)
			{
				//just add in the x direction
				if(nx)
					offset -= 1;
			}
			
			if(absPhi > 22.5 && absPhi < 157.5)
			{
				//just add in the x direction
				if(phi >= 0)
				{
					if(py)
						offset += width;
				}
				else
				{
					if(ny)
						offset -= width;
				}
			}
		}
		
		
		return nextInt + offset;
	}
}