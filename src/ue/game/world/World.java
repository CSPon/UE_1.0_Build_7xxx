package ue.game.world;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import ue.game.world.chunk.ChunkBlock;
import ue.game.world.chunk.UChunk;
import ue.game.world.tile.Block;
import ue.game.world.tile.terrain.Air;
import ue.game.world.tile.terrain.Dirt;
import ue.game.world.tile.terrain.Grass;
import ue.game.world.tile.terrain.Sand;
import ue.game.world.tile.terrain.Terrain;
import ue.game.world.tile.terrain.Water;

public abstract class World
{
	// World Constants
	public static final byte WORLD_ISO = Block.TYPE_ISO;
	public static final byte WORLD_TDV = Block.TYPE_TDV;
	public static final byte DRAW_DIRECT = 0x1;
	public static final byte DRAW_RANGED = 0x2;
	
	// Map Options
	private int map_size;
	private byte radius;
	
	// World Environment
	private byte max_water_level, max_level;
	
	// World generating
	private int[][] map;
	private long seed;
	
	// Actual rendering
	private int viewing_range;
	private UChunk[][] chunks;
	private UChunk center;
	private UChunk[] inRangeChunk;
	private int renderRange = 3;
	private byte DRAW_METHOD = DRAW_DIRECT;
	private byte VIEW_MODE = Block.TYPE_ISO;
	private boolean borderline = true;
	
	// Loading
	private boolean created; // created is to check if world is created, loaded is to check if world is pushed to GPU.
	private int currRow, currCol, count;
	private String status;
	
	// Terrain texture holding
	private Terrain[] terrain;
	
	public World(final long seed, final int size, final float water_mass, final byte max_level, final byte radius, final byte drawtype)
	{
		setTerrainOption(water_mass, max_level);
		
		this.VIEW_MODE = drawtype;
		this.radius = radius;
		
		map_size = size * UChunk.MAX_SIZE;
		map = new int[map_size][map_size];
		
		terrain = new Terrain[]{new Air(), new Water(), new Sand(), new Dirt(), new Grass()};
		
		viewing_range = (UChunk.MAX_SIZE * radius * 2);
		
		chunks = new UChunk[size][size];
		
		if(seed < 0)
			this.seed = System.currentTimeMillis();
		else
			this.seed = seed;
		
		// Below Experimental
		if(DRAW_METHOD == DRAW_RANGED)
		{
			int rangeSize = 1 + (renderRange * 2);
			inRangeChunk = new UChunk[rangeSize * rangeSize];
		}
		
		count = 0;
	}
	
	public void setDrawMethod(byte rule)
	{
		DRAW_METHOD = rule;
		if(DRAW_METHOD == DRAW_RANGED)
		{
			int rangeSize = 1 + (renderRange * 2);
			inRangeChunk = new UChunk[rangeSize * rangeSize];
		}
	}
	
	public void setEnvironment(final float water_mass, final byte max_level)
	{
		
	}
	
	private void setTerrainOption(final float water_mass, final byte max_level)
	{
		this.max_level = (byte) (max_level - 1);
		this.max_water_level = (byte) (Math.floor(water_mass / (1.0f / (float) this.max_level)));
		System.out.println("Water Level : " + max_water_level);
	}
	
	public void generateMap(int octave, float persistance)
	{
		int heighest = 0;
		new MapWizard(this.seed);
		float roughness = 0.4f + (0.2f * (persistance / 100f));
		float[][] gen = MapWizard.generate(map_size, map_size, octave, roughness);
		for(int x = 0; x < gen.length; x++)
			for(int y = 0; y < gen.length; y++)
			{
				map[x][y] = (int) Math.floor(gen[x][y] / (1.0f / max_level));
				if(heighest < map[x][y])
					heighest = map[x][y];
			}
		
		System.out.println("Highest : " + heighest);
	}
	
	private void create(final short width)
	{
		float percentage = ((float) count / ((float) chunks.length * (float) chunks.length)) * 100f;
		status = "Creating Chunk @ " + currRow + "," + currCol + " (" + (int) percentage +"%)";
		FloatBuffer buffer = BufferUtils.createFloatBuffer(UChunk.MAX_SIZE * UChunk.MAX_SIZE * 3 * 4 * 5 * 3);
		chunks[currRow][currCol] = new UChunk(currRow, currCol, width, map.length, radius, VIEW_MODE);
		chunks[currRow][currCol].applySeed(map, max_water_level, VIEW_MODE);
		chunks[currRow][currCol].applyHeight(map, VIEW_MODE, max_water_level, max_level);
		chunks[currRow][currCol].pushVBO(terrain, buffer);
		status += " (" + chunks[currRow][currCol].getMemoryAddress() + ")";
		buffer = null;
		
		currCol++;
		if(currCol > chunks.length - 1)
		{
			currCol = 0;
			currRow++;
			if(currRow > chunks.length - 1)
			{
				currRow = 0; currCol = 0;
				created = true;
				status = "Grid Overlay : " + borderline + "\n";
			}
		}
		count++;
	}
	
	public void render(int texturepack)
	{
		if(created)
		{
			if(DRAW_METHOD == DRAW_DIRECT)
			{
				for(UChunk[] row : chunks)
					for(UChunk chunk : row)
						if(chunk.isVisible())
							chunk.render(texturepack, borderline);
			}
			// Below Experimental
			else if(DRAW_METHOD == DRAW_RANGED)
			{
				for(int i = 0; i < inRangeChunk.length; i++)
				{
					if(inRangeChunk[i] != null)
						inRangeChunk[i].render(texturepack, borderline);
				}
			}
		}
	}
	
	public void updateLoad(final short width)
	{
		if(!created)
		{
			create(width);
		}
	}
	public void updateRender(final short width, final short height, final short cX, final short cY)
	{
		if(DRAW_METHOD == DRAW_RANGED)
		{
			center = getCenter(cX, cY);
			int centerX = (int) center.getRow();
			int centerY = (int) center.getCol();
			int count = 0;
			
			for(int x = centerX - renderRange; x <= centerX + renderRange; x++)
				for(int y = centerY - renderRange; y <= centerY + renderRange; y++)
				{
					if((x >= 0 && x < chunks.length) && (y >= 0 && y < chunks.length))
					{
						if(inRangeChunk[count] != null)
							inRangeChunk[count].setVisible(false);
						
						inRangeChunk[count] = chunks[x][y];
						inRangeChunk[count].setVisible(true);
					}
					else
						inRangeChunk[count] = null;
					
					count++;
				}
		}
		else if(DRAW_METHOD == DRAW_DIRECT)
		{
			for(UChunk[] row : chunks)
				for(UChunk chunk : row)
				{
					if((chunk.getXPos() < width + viewing_range && chunk.getXPos() > -viewing_range) && (chunk.getYPos() < height && chunk.getYPos() > -viewing_range))
						chunk.setVisible(true);
					else chunk.setVisible(false);
				}
		}
	}
	
	public void mouseMoved(short mX, short mY)
	{
		if(DRAW_METHOD == DRAW_RANGED)
		{				
			//status += "Center Chunk : " + (int) center.getRow() + "X" + (int) center.getCol() + "\n";
			for(UChunk chunk : inRangeChunk)
				if(chunk != null)
					chunk.gfxUpdate(mX, mY);
		}
		else if(DRAW_METHOD == DRAW_DIRECT)
		{
			for(UChunk[] row : chunks)
				for(UChunk chunk : row)
					if(chunk.isVisible())
						chunk.gfxUpdate(mX, mY);
		}
	}
	public void keyDown(short dX, short dY)
	{
		for(UChunk[] row : chunks)
			for(UChunk chunk : row)
				chunk.translate(dX, dY);
	}
	
	public UChunk getCenter(final short x, final short y)
	{
		if(center == null)
		{
			for(UChunk[] row : chunks)
				for(UChunk chunk : row)
				{
					chunk.gfxUpdate(x, y);
					if(chunk.getFocusedBlock() != null)
					{
						center = chunk;
						return chunk;
					}
				}
		}
		else if(center != null)
		{
			for(UChunk chunk : inRangeChunk)
			{
				if(chunk != null)
				{
					chunk.gfxUpdate(x, y);
					if(chunk.getFocusedBlock() != null)
					{
						if(chunk.getRow() != center.getRow() && chunk.getCol() != center.getCol())
							break;
						else
						{
							center = chunk;
							return chunk;
						}
					}
				}
			}
		}
		
		return center;
	}
	
	public String getStatus(){return this.status;}
	public long getSeed(){return this.seed;}
	public void setBorderline()
	{
		if(borderline)
			borderline = false;
		else borderline = true;
		status = "Grid Overlay : " + borderline + "\n";
	}
	
	public boolean isWorldLoaded()
	{
		if(created)
			return true;
		else return false;
	}
	
	public void cleanup()
	{
		for(UChunk[] row : chunks)
			for(UChunk chunk : row)
				chunk.clean();
	}
	
	public UChunk getChunk(int row, int col)
	{
		return chunks[row][col];
	}
	
	public UChunk getChunkAt(int row, int col)
	{
		row = (int) Math.floor(row / UChunk.MAX_SIZE);
		col = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		return chunks[row][col];
	}
	
	public float getChunkX(int row, int col)
	{
		return chunks[row][col].getXPos();
	}
	
	public float getChunkY(int row, int col)
	{
		return chunks[row][col].getXPos();
	}
	
	public float getChunkXAt(int row, int col)
	{
		int chunkrow = (int) Math.floor(row / UChunk.MAX_SIZE);
		int chunkcol = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		return chunks[chunkrow][chunkcol].getXPos();
	}
	
	public float getChunkYAt(int row, int col)
	{
		int chunkrow = (int) Math.floor(row / UChunk.MAX_SIZE);
		int chunkcol = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		return chunks[chunkrow][chunkcol].getYPos();
	}
	
	public ChunkBlock getBlockAt(int row, int col)
	{
		int chunkrow = (int) Math.floor(row / UChunk.MAX_SIZE);
		int chunkcol = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		int absRow = row - (chunkrow * UChunk.MAX_SIZE);
		int absCol = col - (chunkcol * UChunk.MAX_SIZE);
		
		return chunks[chunkrow][chunkcol].getBlockAbs(absRow, absCol);
	}
	
	public float getBlockXAt(int row, int col)
	{
		int chunkrow = (int) Math.floor(row / UChunk.MAX_SIZE);
		int chunkcol = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		int absRow = row - (chunkrow * UChunk.MAX_SIZE);
		int absCol = col - (chunkcol * UChunk.MAX_SIZE);
		
		return chunks[chunkrow][chunkcol].getBlockAbs(absRow, absCol).getXPos();
	}
	
	public float getBlockYAt(int row, int col)
	{
		int chunkrow = (int) Math.floor(row / UChunk.MAX_SIZE);
		int chunkcol = (int) Math.floor(col / UChunk.MAX_SIZE);
		
		int absRow = row - (chunkrow * UChunk.MAX_SIZE);
		int absCol = col - (chunkcol * UChunk.MAX_SIZE);
		
		return chunks[chunkrow][chunkcol].getBlockAbs(absRow, absCol).getTrueY();
	}
	
	// BELOW EXPERIMENTAL
	public void checkSurrounding()
	{
	}
}
