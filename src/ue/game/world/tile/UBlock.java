package ue.game.world.tile;

import org.lwjgl.util.vector.Vector3f;

public abstract class UBlock implements Block
{
	protected float xPos, yPos, terrainlevel, radius;
	
	private Vector3f[] vertices, borderlines;
	
	protected boolean flat, focused;
	
	/**
	 * Sets radius(Or height) of this tile.<br>
	 * Radius of each tile must be constant since terrain level is depends on the radius.<br>
	 * This method must be called before createVectors() method.
	 * @param radius integer value of tile's radius.
	 */
	protected abstract void setRadius(int radius);
	
	@Override
	public void createVectors(byte type)
	{
		if(type == Block.TYPE_ISO)
		{
			vertices = new Vector3f[4];
			
			vertices[0] = new Vector3f(-(radius * 2), 0, 0);
			vertices[1] = new Vector3f(0, -radius, 0);
			vertices[2] = new Vector3f((radius * 2), 0, 0);
			vertices[3] = new Vector3f(0, radius, 0);
			
			borderlines = new Vector3f[8];
			
			borderlines[0] = new Vector3f(-(radius * 2), 0, 0); borderlines[1] = new Vector3f(0, -radius, 0);
			borderlines[2] = new Vector3f(0, -radius, 0); 		borderlines[3] = new Vector3f((radius * 2), 0, 0);
			borderlines[4] = new Vector3f((radius * 2), 0, 0); 	borderlines[5] = new Vector3f(0, radius, 0);
			borderlines[6] = new Vector3f(0, radius, 0); 		borderlines[7] = new Vector3f(-(radius * 2), 0, 0);
		}
		else if(type == Block.TYPE_TDV)
		{
			vertices = new Vector3f[4];
			
			vertices[0] = new Vector3f(-radius, -radius, 0);
			vertices[1] = new Vector3f(radius, -radius, 0);
			vertices[2] = new Vector3f(radius, radius, 0);
			vertices[3] = new Vector3f(-radius, radius, 0);
			
			borderlines = new Vector3f[8];
			
			borderlines[0] = new Vector3f(-radius, -radius, 0); borderlines[1] = new Vector3f(radius, -radius, 0);
			borderlines[2] = new Vector3f(radius, -radius, 0); 	borderlines[3] = new Vector3f(radius, radius, 0);
			borderlines[4] = new Vector3f(radius, radius, 0); 	borderlines[5] = new Vector3f(-radius, radius, 0);
			borderlines[6] = new Vector3f(-radius, radius, 0); 		borderlines[7] = new Vector3f(-radius, -radius, 0);
		}
	}

	@Override
	public float getXPos()
	{
		return this.xPos;
	}

	@Override
	public float getYPos()
	{
		return this.yPos;
	}
	
	/**
	 * Gets actual height of the tile.
	 * @return float value.
	 */
	public float getTrueY()
	{
		return getYPos() - getHeight();
	}

	@Override
	public void setXPos(float xPos)
	{
		this.xPos = xPos;
	}

	@Override
	public void setYPos(float yPos)
	{
		this.yPos = yPos;
	}

	@Override
	public void applyPosition(byte type, boolean reset)
	{
		if(reset)
			createVectors(type);
		
		if(type == Block.TYPE_ISO)
		{
			for(int i = 0; i < 4; i++)
				vertices[i].translate(xPos, yPos - (radius * terrainlevel), 0);
			for(int i = 0; i < 8; i++)
				borderlines[i].translate(xPos, yPos - (radius * terrainlevel), 0);
		}
		else if(type == Block.TYPE_TDV)
		{
			for(int i = 0; i < 4; i++)
				vertices[i].translate(xPos, yPos, 0);
			for(int i = 0; i < 8; i++)
				borderlines[i].translate(xPos, yPos, 0);
		}
	}

	@Override
	public void translatePosition(byte dX, byte dY)
	{
		for(int i = 0; i < 4; i++)
			vertices[i].translate(dX, dY, 0);
		for(int i = 0; i < 8; i++)
			borderlines[i].translate(dX, dY, 0);
	}

	@Override
	public float getTerrainLevel()
	{
		return this.terrainlevel;
	}

	@Override
	public void setTerrainLevel(float terrainlevel)
	{
		this.terrainlevel = terrainlevel;
	}

	@Override
	public void adjustTerrain(byte[] edges)
	{
		if(edges[0] != 0 || edges[1] != 0 || edges[2] != 0 || edges[3] != 0)
			flat = false;
		else flat = true;
		
		vertices[0].translate(0, -(radius * edges[0]), 0);
		vertices[1].translate(0, -(radius * edges[1]), 0);
		vertices[2].translate(0, -(radius * edges[2]), 0);
		vertices[3].translate(0, -(radius * edges[3]), 0);
		
		borderlines[0].translate(0, -(radius * edges[0]), 0); borderlines[1].translate(0, -(radius * edges[1]), 0);
		borderlines[2].translate(0, -(radius * edges[1]), 0); borderlines[3].translate(0, -(radius * edges[2]), 0);
		borderlines[4].translate(0, -(radius * edges[2]), 0); borderlines[5].translate(0, -(radius * edges[3]), 0);
		borderlines[6].translate(0, -(radius * edges[3]), 0); borderlines[7].translate(0, -(radius * edges[0]), 0);
	}

	@Override
	public float[] getVertices()
	{
		return new float[]{vertices[0].getX(), vertices[0].getY(), vertices[0].getZ(),
				vertices[1].getX(), vertices[1].getY(), vertices[1].getZ(),
				vertices[2].getX(), vertices[2].getY(), vertices[2].getZ(),
				vertices[3].getX(), vertices[3].getY(), vertices[3].getZ()};
	}

	@Override
	public float[] getBorderlines()
	{
		return new float[]{borderlines[0].x, borderlines[0].y, borderlines[0].z,
				borderlines[1].x, borderlines[1].y, borderlines[1].z,
				borderlines[2].x, borderlines[2].y, borderlines[2].z,
				borderlines[3].x, borderlines[3].y, borderlines[3].z,
				borderlines[4].x, borderlines[4].y, borderlines[4].z,
				borderlines[5].x, borderlines[5].y, borderlines[5].z,
				borderlines[6].x, borderlines[6].y, borderlines[6].z,
				borderlines[7].x, borderlines[7].y, borderlines[7].z};
	}

	@Override
	public boolean isFlat()
	{
		return this.flat;
	}

	@Override
	public boolean isCollide(float tX, float tY, float mX, float mY)
	{	
		int dist = (int) Math.sqrt(Math.pow((tX - mX), 2) + Math.pow((tY - mY), 2));
		if(dist <= radius)
		{
			focused = true;
			return true;
		}
		else
		{
			focused = false;
			return false;
		}
	}
	
	@Override
	public boolean isFocused()
	{
		return this.focused;
	}

	/**
	 * Gets true height of this tile. However, this is not the y-position of the tile.
	 * @return integer value of tile height.
	 */
	public float getHeight()
	{
		return (radius * terrainlevel);
	}
	
	/**
	 * Cleans up vectors to conserve memory.<br>
	 * Method only cleans out vectors only. Positions and height is saved.
	 */
	public void cleanUp()
	{
		vertices = null; borderlines = null;
	}
}
