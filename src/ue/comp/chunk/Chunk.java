package ue.comp.chunk;

import java.io.Serializable;

/**
 * <b>Chunk is currently constructable as 16 x 16 x 16 chunk. However, only first layer of chunk will be usable.</b><br>
 * Chunk is a 16 x 16 x 16 virtual cubic object which has capability of storing multiple data.<br>
 * Originally, chunk was exclusive private class for World.<br>
 * However, due to various usage of chunk object, I've created separate chunk class so it can be used as many other purpose.
 * @author Charlie Shin
 *
 */
public abstract class Chunk implements ChunkObject, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Object[][][] chunk;
	
	protected float row, col, xPos, yPos;
	
	public Chunk()
	{
		chunk = new Object[16][16][16];
	}

	@Override
	public Object[][][] getChunk()
	{
		return this.chunk;
	}

	@Override
	public Object[][] getLayer(int layer)
	{
		return this.chunk[layer];
	}

	@Override
	public Object getObjectAt(int row, int col)
	{
		return this.chunk[0][row][col];
	}

	@Override
	public void setObjectAt(int row, int col, Object obj)
	{
		this.chunk[0][row][col] = obj;
	}

	@Override
	public float getRow()
	{
		return this.row;
	}

	@Override
	public float getCol()
	{
		return this.col;
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

	@Override
	public void translate(float dX, float dY)
	{
		this.xPos += dX; this.yPos += dY;
	}
}
