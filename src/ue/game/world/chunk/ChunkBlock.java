package ue.game.world.chunk;

import java.io.Serializable;

import ue.game.world.tile.UBlock;

public class ChunkBlock extends UBlock implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int row, col, relRow, relCol;
	
	private boolean isWater;
	
	/**
	 * Block type ID.<br>
	 * Block's type is determined via this variable. BLOCK_ID_HEX and BLOCK_ID are only there to locate memory address.
	 */
	private byte blockTypeID;
	
	/**
	 * Seed value of this particular block.
	 */
	private float keptSeed;
	
	/**
	 * Four corners.
	 */
	private byte[] edge;
	
	/**
	 * WorldBlock constructor generates block's unique ID, then generates vertices based on the block's position.
	 * Adjusting blocks must be called after this.
	 * @param xPos Integer value of horizontal position.
	 * @param yPos Integer value of vertical position.
	 * @param terrainlevel Integer value of terrain level.
	 * @param radius integer value of size of the tile.
	 */
	public ChunkBlock(byte type, int xPos, int yPos, int terrainlevel, int radius)
	{
		blockTypeID = -1;
		
		edge = new byte[4];
		
		setXPos(xPos);
		setYPos(yPos);
		
		setTerrainLevel(terrainlevel);
		setRadius(radius);
		
		createVectors(type);
		applyPosition(type, false);
	}

	@Override
	protected void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	/**
	 * Gets this tile's row position on the chunk.
	 * @return Integer value of this tile's absolute row position.
	 */
	public int getAbsoluteRow()
	{
		return this.row;
	}
	/**
	 * Gets this tile's column position on the chunk.
	 * @return Integer value of this tile's absolute column position.
	 */
	public int getAbsoluteCol()
	{
		return this.col;
	}
	
	/**
	 * Gets this tile's row position based on the world.
	 * @return Integer value of this tile's relative row position.
	 */
	public int getRelativeRow()
	{
		return this.relRow;
	}
	/**
	 * Gets this tile's column position based on the world.
	 * @return Integer value of this tile's relative column position.
	 */
	public int getRelativeCol()
	{
		return this.relCol;
	}
	
	/**
	 * Sets absolute position of this block.
	 * @param row Integer value of absolute row.
	 * @param col Integer value of absolute column.
	 */
	public void setAbsoluteLocation(final int row, final int col)
	{
		this.row = row; this.col = col;
	}
	
	/**
	 * Sets relative position of this block.
	 * @param relRow Integer value of relative row.
	 * @param relCol Integer value of relative column.
	 */
	public void setRelativeLocation(final int relRow, final int relCol)
	{
		this.relRow = relRow; this.relCol = relCol;
	}
	
	/**
	 * Sends data of this block.
	 * @return String data containing position, height, , memory address, and flatness of this block.
	 */
	public String debug()
	{
		return "Position : " + relRow + "," + relCol + "(" + getMemoryAddress() + ")\n" +
				"Terrain Level : " + (int)terrainlevel + "\n" + 
				"Is flat? : " + flat;
	}
	
	/**
	 * Checks if this block is water.
	 * @return true if this block is water, false if not.
	 */
	public boolean isWater(){return this.isWater;}
	/**
	 * Sets this block's type.<br>
	 * Although this method sets this block's type as water, water type ID must be plugged in to blockID.
	 * @param isWater boolean which sets block's type.
	 */
	public void isWater(boolean isWater){this.isWater = isWater;}
	
	/**
	 * Gets block ID.<br>
	 * This returns block's type ID.<br>
	 * @return byte value of block's ID.
	 */
	public byte getBlockID(){return this.blockTypeID;}
	/**
	 * Sets block ID.<br>
	 * This sets block's type ID, not the block's memory address ID.
	 * @param blockTypeID byte value of block ID.
	 */
	public void setBlockID(byte blockTypeID){this.blockTypeID = blockTypeID;}
	
	/**
	 * Gets memory address of this block.
	 * @return Memory address of block in hexadecimal.
	 */
	public String getMemoryAddress(){return this.toString().split("@")[1];}
	
	/**
	 * Gets seed value of this block.
	 * @return float value.
	 */
	public float getSeed(){return this.keptSeed;}
	/**
	 * Sets seed value of this block.
	 * @param keptSeed float value of seed value.
	 */
	public void setSeed(final float keptSeed){this.keptSeed = keptSeed;}
	
	/**
	 * Gets edge data.
	 * @return Byte array of edge data.
	 */
	public byte[] getEdges()
	{
		return this.edge;
	}
	
	/**
	 * Sets edge data based on adjacent blocks.
	 * @param adjacent Adjacent blocks.
	 * @param max_water_level Maximum water level for check.
	 */
	public void setEdges(final int[] adjacent, final byte max_water_level)
	{
		if(adjacent[5] > -1 && adjacent[5] > terrainlevel)
		{
			edge[0] = 1;
			edge[3] = 1;
		}
		else if(adjacent[5] > -1 && adjacent[5] < terrainlevel && adjacent[5] <= max_water_level)
		{
			edge[0] = -1;
			edge[3] = -1;
		}
		else
		{
			if(adjacent[2] > -1 && adjacent[2] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[2] > -1 && adjacent[2] < terrainlevel && adjacent[2] <= max_water_level)
				edge[0] = -1;
			else if(adjacent[5] > -1 && adjacent[5] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[5] > -1 && adjacent[5] < terrainlevel && adjacent[5] <= max_water_level)
				edge[0] = -1;
			else if(adjacent[1] > -1 && adjacent[1] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[1] > -1 && adjacent[1] < terrainlevel && adjacent[1] <= max_water_level)
				edge[0] = -1;
			
			if(adjacent[8] > -1 && adjacent[8] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[8] > -1 && adjacent[8] < terrainlevel && adjacent[8] <= max_water_level)
				edge[3] = -1;
			else if(adjacent[5] > -1 && adjacent[5] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[5] > -1 && adjacent[5] < terrainlevel && adjacent[5] <= max_water_level)
				edge[3] = -1;
			else if(adjacent[7] > -1 && adjacent[7] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[7] > -1 && adjacent[7] < terrainlevel && adjacent[7] <= max_water_level)
				edge[3] = -1;
		}
		
		if(adjacent[1] > -1 && adjacent[1] > terrainlevel)
		{
			edge[0] = 1;
			edge[1] = 1;
		}
		else if(adjacent[1] > -1 && adjacent[1] < terrainlevel && adjacent[1] <= max_water_level)
		{
			edge[0] = -1;
			edge[1] = -1;
		}
		else
		{
			if(adjacent[2] > -1 && adjacent[2] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[2] > -1 && adjacent[2] < terrainlevel && adjacent[2] <= max_water_level)
				edge[0] = -1;
			else if(adjacent[5] > -1 && adjacent[5] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[5] > -1 && adjacent[5] < terrainlevel && adjacent[5] <= max_water_level)
				edge[0] = -1;
			else if(adjacent[1] > -1 && adjacent[1] > terrainlevel)
				edge[0] = 1;
			else if(adjacent[1] > -1 && adjacent[1] < terrainlevel && adjacent[1] <= max_water_level)
				edge[0] = -1;
			
			if(adjacent[0] > -1 && adjacent[0] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[0] > -1 && adjacent[0] < terrainlevel && adjacent[0] <= max_water_level)
				edge[1] = -1;
			else if(adjacent[1] > -1 && adjacent[1] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[1] > -1 && adjacent[1] < terrainlevel && adjacent[1] <= max_water_level)
				edge[1] = -1;
			else if(adjacent[3] > -1 && adjacent[3] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[3] > -1 && adjacent[3] < terrainlevel && adjacent[3] <= max_water_level)
				edge[1] = -1;
		}
		
		if(adjacent[3] > -1 && adjacent[3] > terrainlevel)
		{
			edge[1] = 1;
			edge[2] = 1;
		}
		else if(adjacent[3] > -1 && adjacent[3] < terrainlevel && adjacent[3] <= max_water_level)
		{
			edge[1] = -1;
			edge[2] = -1;
		}
		else
		{
			if(adjacent[0] > -1 && adjacent[0] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[0] > -1 && adjacent[0] < terrainlevel && adjacent[0] <= max_water_level)
				edge[1] = -1;
			else if(adjacent[1] > -1 && adjacent[1] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[1] > -1 && adjacent[1] < terrainlevel && adjacent[1] <= max_water_level)
				edge[1] = -1;
			else if(adjacent[3] > -1 && adjacent[3] > terrainlevel)
				edge[1] = 1;
			else if(adjacent[3] > -1 && adjacent[3] < terrainlevel && adjacent[3] <= max_water_level)
				edge[1] = -1;
			
			if(adjacent[6] > -1 && adjacent[6] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[6] > -1 && adjacent[6] < terrainlevel && adjacent[6] <= max_water_level)
				edge[2] = -1;
			else if(adjacent[3] > -1 && adjacent[3] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[3] > -1 && adjacent[3] < terrainlevel && adjacent[3] <= max_water_level)
				edge[2] = -1;
			else if(adjacent[7] > -1 && adjacent[7] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[7] > -1 && adjacent[7] < terrainlevel && adjacent[7] <= max_water_level)
				edge[2] = -1;
		}
		
		if(adjacent[7] > -1 && adjacent[7] > terrainlevel)
		{
			edge[2] = 1;
			edge[3] = 1;
		}
		else if(adjacent[7] > -1 && adjacent[7] < terrainlevel && adjacent[7] <= max_water_level)
		{
			edge[2] = -1;
			edge[3] = -1;
		}
		else
		{
			if(adjacent[6] > -1 && adjacent[6] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[6] > -1 && adjacent[6] < terrainlevel && adjacent[6] <= max_water_level)
				edge[2] = -1;
			else if(adjacent[3] > -1 && adjacent[3] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[3] > -1 && adjacent[3] < terrainlevel && adjacent[3] <= max_water_level)
				edge[2] = -1;
			else if(adjacent[7] > -1 && adjacent[7] > terrainlevel)
				edge[2] = 1;
			else if(adjacent[7] > -1 && adjacent[7] < terrainlevel && adjacent[7] <= max_water_level)
				edge[2] = -1;
			
			if(adjacent[8] > -1 && adjacent[8] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[8] > -1 && adjacent[8] < terrainlevel && adjacent[8] <= max_water_level)
				edge[3] = -1;
			else if(adjacent[5] > -1 && adjacent[5] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[5] > -1 && adjacent[5] < terrainlevel && adjacent[5] <= max_water_level)
				edge[3] = -1;
			else if(adjacent[7] > -1 && adjacent[7] > terrainlevel)
				edge[3] = 1;
			else if(adjacent[7] > -1 && adjacent[7] < terrainlevel && adjacent[7] <= max_water_level)
				edge[3] = -1;
		}
	}
	
	public void cleanUp()
	{
		super.cleanUp();
	}
}
