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
	
	
	/**
	 * Takes a gridpoint and determines whether or not the given gridpoint is inside the view volume of the camera.
	 */
	public boolean isInsideView(GridGridpoint point)
	{
		//Create the vector to compare to view volume vectors
		float[] ap = new float[3];
		ap[0] = point.x - a[0];
		ap[1] = point.y - a[1];
		ap[2] = point.z - a[2];
		
		//Take the dot products and determine whether or not the point in question is inside
		float APAB = dotProd(ap, ab);
		float ABAB = dotProd(ab, ab);
		float APAD = dotProd(ap, ad);
		float ADAD = dotProd(ad, ad);
		float APAE = dotProd(ap, ae);
		float AEAE = dotProd(ae, ae);
		
		if ((0 <= APAB && APAB <= ABAB) && (0 <= APAD && APAD <= ADAD) && (0 <= APAE && APAE <= AEAE))
			return true;		
		
		return false;
	}
	
	/**
	 * Will update the view volume for the current view. 
	 */
	float[] perpDirY = new float[3];
	float[] perpDirX = new float[3];
	public void updateViewVolume()
	{
				
		//4 vertices that describe the view volume
		float[] b = new float[3];
		float[] d = new float[3];
		float[] e = new float[3];
		
		//normalize the camera direction
		float camDirLength = (float) Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1] + dir[2] * dir[2]);
		dir[0] /= camDirLength;
		dir[1] /= camDirLength;
		dir[2] /= camDirLength;
		
		//Find vectors perpendicular to the camera
		float up[] = new float[3];//up vector
		up[0] = 0;
		up[1] = 1;
		up[2] = 0;
		
		perpDirX = crossProd(up, dir);
		perpDirY = crossProd(dir,perpDirX);
		
		//update the 4 points that define the view volume
		//Consult the notebook to get a better visual representation of what's going on
		
		a[0] = loc[0] + dir[0] * near + perpDirX[0] * left + -perpDirY[0] * bottom;
		a[1] = loc[1] + dir[1] * near + perpDirX[1] * left + -perpDirY[1] * bottom;
		a[2] = loc[2] + dir[2] * near + perpDirX[2] * left + -perpDirY[2] * bottom;
		
		b[0] = loc[0] + dir[0] * far + perpDirX[0] * left + -perpDirY[0] * bottom;
		b[1] = loc[1] + dir[1] * far + perpDirX[1] * left + -perpDirY[1] * bottom;
		b[2] = loc[2] + dir[2] * far + perpDirX[2] * left + -perpDirY[2] * bottom;
		
		d[0] = loc[0] + dir[0] * near - perpDirX[0] * right + -perpDirY[0] * bottom;
		d[1] = loc[1] + dir[1] * near - perpDirX[1] * right + -perpDirY[1] * bottom;
		d[2] = loc[2] + dir[2] * near - perpDirX[2] * right + -perpDirY[2] * bottom;
		
		e[0] = loc[0] + dir[0] * near + perpDirX[0] * left + perpDirY[0] * top;
		e[1] = loc[1] + dir[1] * near + perpDirX[1] * left + perpDirY[1] * top;
		e[2] = loc[2] + dir[2] * near + perpDirX[2] * left + perpDirY[2] * top;
		
		//Create the 3 vectors defining the volume
		ab[0] = b[0] - a[0];
		ab[1] = b[1] - a[1];
		ab[2] = b[2] - a[2];
		
		ad[0] = d[0] - a[0];
		ad[1] = d[1] - a[1];
		ad[2] = d[2] - a[2];
		
		ae[0] = e[0] - a[0];
		ae[1] = e[1] - a[1];
		ae[2] = e[2] - a[2];
		
	}
	
	/**
	 * Will update the location of the camera and also make the direction point to the center, for now.
	 */
	public void updateLocationForward(float factor)
	{
		float dx, dy ,dz; 
		
		dx = dir[0] * factor + loc[0];
		if(dx > 1)
			dx = 1.0f;
		if(dx < -1)
			dx = -1.0f;
		
		dy = dir[1] * factor + loc[1];
		if(dy > 1)
			dy = 1.0f;
		if(dy < -1)
			dy = -1.0f;
		
		dz = dir[2] * factor + loc[2];
		if(dz > 1)
			dz = 1.0f;
		if(dz < -1)
			dz = -1.0f;
		
		loc[0] = dx;
		loc[1] = dy;
		loc[2] = dz;
		
	}
	public void updateLocationRight(float factor)
	{
		float dx, dy ,dz; 
		
		dx = perpDirX[0] * factor + loc[0];
		if(dx > 1)
			dx = 1.0f;
		if(dx < -1.0f)
			dx = -1.0f;
		
		dy = perpDirX[1] * factor + loc[1];
		if(dy > 1)
			dy = 1.0f;
		if(dy < -1)
			dy = -1.0f;
		
		dz = perpDirX[2] * factor + loc[2];
		if(dz > 1)
			dz = 1.0f;
		if(dz < -1)
			dz = -1.0f;
		
		loc[0] = dx;
		loc[1] = dy;
		loc[2] = dz;
	}
	
	
	/**
	 * Updates the direction that the camera is looking at.
	 */
	public void updateDirection(float x, float y, float z)
	{
		dir[0] = x;
		dir[1] = y;
		dir[2] = z;
	}
	
	public float dotProd(float[] a, float[] b)
	{
		float dot = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
		if(-0.0001 < dot && dot < 0)
			return 0;
		return dot;
	}
	
	public float[] crossProd(float[] a, float[] b)
	{
		float[] result = new float[3];		
		
		result[0] = a[1]*b[2] - a[2]*b[1];		
		result[1] = a[2]*b[0] - a[0]*b[2];		
		result[2] = a[0]*b[1] - a[1]*b[0];
		
		return result;
	}
	
	public int nextGridPoint(int currentPoint, GridGridpoint point, int forward)
	{
		int nextInt = currentPoint;
		
		//this.updateDirection(0.0f, 1.0f, 0.0f);
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