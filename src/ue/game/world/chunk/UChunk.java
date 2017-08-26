package ue.game.world.chunk;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;

import java.io.Serializable;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import ue.comp.chunk.Chunk;
import ue.game.world.tile.Block;
import ue.game.world.tile.terrain.Terrain;

public class UChunk extends Chunk implements UChunkRender, UChunkLogic, Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int MAX_SIZE = 16;
	
	private int chunkid, borderlinesid;
	
	private boolean visible;
	
	public UChunk(int chunkRow, int chunkCol, short screenwidth, int map_size, byte radius, byte type)
	{
		chunk[0] = new ChunkBlock[MAX_SIZE][MAX_SIZE];
		
		this.row = chunkRow; this.col = chunkCol;
		
		if(type == Block.TYPE_ISO)
		{
			this.xPos = (screenwidth / 2) + ((chunkRow * MAX_SIZE) * radius * 2) - ((chunkCol * MAX_SIZE) * radius * 2);
			this.yPos = ((chunkCol * MAX_SIZE) * radius) + ((chunkRow * MAX_SIZE) * radius) - (((map_size) * (radius * 2)) / 4);
		}
		else if(type == Block.TYPE_TDV)
		{
			this.xPos = chunkRow * (MAX_SIZE * radius * 2);
			this.yPos = chunkCol * (MAX_SIZE * radius * 2);
		}
		
		for(int r = 0; r < MAX_SIZE; r++)
			for(int c = 0; c < MAX_SIZE; c++)
			{
				int x = 0, y = 0;
				if(type == Block.TYPE_ISO)
				{
					x = (r * radius * 2) - (c * radius * 2);
					y = (c * radius) + (r * radius);
				}
				else if(type == Block.TYPE_TDV)
				{
					x = r * (radius * 2);
					y = c * (radius * 2);
				}
				int relRow = (chunkRow * MAX_SIZE) + r;
				int relCol = (chunkCol * MAX_SIZE) + c;
				
				ChunkBlock block = new ChunkBlock(type, x, y, 0, radius);
				block.setAbsoluteLocation(r, c);
				block.setRelativeLocation(relRow, relCol);
				setObjectAt(r, c, block);
			}
	}
	
	@Override
	public void applySeed(int[][] seed, final byte max_water_level, final byte type)
	{
		for(int r = 0; r < MAX_SIZE; r++)
			for(int c = 0; c < MAX_SIZE; c++)
			{
				ChunkBlock b = (ChunkBlock) getObjectAt(r, c);
				
				int relRow = (int) ((row * MAX_SIZE) + r);
				int relCol = (int) ((col * MAX_SIZE) + c);
				
				if(seed[relRow][relCol] <= max_water_level)
				{
					b.setBlockID(Terrain.WATER);
					b.setTerrainLevel(max_water_level);
					b.isWater(true);
					b.applyPosition(type, true);
				}
				else
				{	
					if(seed[relRow][relCol] > max_water_level && seed[relRow][relCol] <= max_water_level + 1)
						b.setBlockID(Terrain.DIRT);
					else
						b.setBlockID(Terrain.GRASS);
					
					b.setTerrainLevel(seed[relRow][relCol]);
					b.applyPosition(type, true);
					
					int[] adjacent = new int[9];
					int counter = 0;
					for(int x = relRow - 1; x < relRow + 2; x++)
						for(int y = relCol - 1; y < relCol + 2; y++)
						{
							if(x >= 0 && x < seed.length && y >= 0 && y < seed.length)
								adjacent[counter] = seed[x][y];
							else adjacent[counter] = -1;
							counter++;
						}
					b.setEdges(adjacent, max_water_level);
				}
			}
	}

	@Override
	public void applyHeight(int[][] seed, final byte type, final byte max_water_level, final byte max_level)
	{
		for(int r = 0; r < MAX_SIZE; r++)
			for(int c = 0; c < MAX_SIZE; c++)
			{
				ChunkBlock b = (ChunkBlock) getObjectAt(r, c);
				
				int relRow = b.getRelativeRow();
				int relCol = b.getRelativeCol();
				
				if(!b.isWater())
				{	
					byte[] offset = b.getEdges();
					
					if(offset[0] == 1 && offset[1] == 1 && offset[2] == 1 && offset[3] == 1)
					{
						b.setTerrainLevel(b.getTerrainLevel() + 1);
						if(type == Block.TYPE_ISO)
							b.adjustTerrain(new byte[]{0, 0, 0, 0});
						b.applyPosition(type, true);
						seed[relRow][relCol] += 1;
						
						if(b.getTerrainLevel() > max_water_level && b.getTerrainLevel() <= max_water_level + 1)
							b.setBlockID(Terrain.DIRT);
						else
							b.setBlockID(Terrain.GRASS);
						
						b.isWater(false);
					}
					else if(offset[0] == -1 && offset[1] == -1 && offset[2] == -1 && offset[3] == -1)
					{
						b.setTerrainLevel(max_water_level);
						b.setBlockID(Terrain.WATER);
						if(type == Block.TYPE_ISO)
							b.adjustTerrain(new byte[]{0, 0, 0, 0});
						b.applyPosition(type, true);
						b.isWater(true);
						seed[relRow][relCol] = max_water_level;
					}
					else if(offset[0] == -1 || offset[1] == -1 || offset[2] == -1 || offset[3] == -1)
					{
						b.setBlockID(Terrain.SAND);
						if(type == Block.TYPE_ISO)
							b.adjustTerrain(offset);
						b.isWater(false);
					}
					else
					{
						if(type == Block.TYPE_ISO)
							b.adjustTerrain(offset);
						b.isWater(false);
					}
				}
				
				int[] adjacent = new int[9];
				int counter = 0;
				for(int x = relRow - 1; x < relRow + 2; x++)
					for(int y = relCol - 1; y < relCol + 2; y++)
					{
						if(x >= 0 && x < seed.length && y >= 0 && y < seed.length)
							adjacent[counter] = seed[x][y];
						else adjacent[counter] = -1;
						counter++;
					}
				b.setEdges(adjacent, max_water_level);
			}
	}

	@Override
	public final float getRow()
	{
		return this.row;
	}

	@Override
	public final float getCol()
	{
		return this.col;
	}

	@Override
	public final float getXPos()
	{
		return this.xPos;
	}

	@Override
	public final float getYPos()
	{
		return this.yPos;
	}

	@Override
	public void render(int texture, boolean borderlines)
	{
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		{
			glLoadIdentity();
			glTranslatef(xPos, yPos, 0);
			
			glBindTexture(GL_TEXTURE_2D, texture);
			glBindBuffer(GL_ARRAY_BUFFER, chunkid);
			
			glVertexPointer(3, GL_FLOAT, 12, 0);
			glColorPointer(3, GL_FLOAT, 12, 48);
			glTexCoordPointer(3, GL_FLOAT, 12, 96);
			
			glDrawArrays(GL_QUADS, 0, 12 * MAX_SIZE * MAX_SIZE);
			glBindTexture(GL_TEXTURE_2D, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
		
		if(borderlines)
			renderBorder();
	}

	@Override
	public void renderBorder()
	{
		glEnableClientState(GL_VERTEX_ARRAY);
		{
			glLoadIdentity();
			glTranslatef(xPos, yPos, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, borderlinesid);
			glVertexPointer(3, GL_FLOAT, 12, 0);
			
			glColor3f(0.0f, 0.0f, 0.0f);
			glDrawArrays(GL_LINES, 0, 8 * MAX_SIZE * MAX_SIZE);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glDisableClientState(GL_VERTEX_ARRAY);
	}

	@Override
	public void pushVBO(Terrain[] types, FloatBuffer buffer)
	{
		chunkid = glGenBuffers();
		borderlinesid = glGenBuffers();
		
		buffer.clear();
		
		for(Object[] row : getLayer(0))
			for(Object block : row)
			{
				ChunkBlock b = (ChunkBlock) block;
				for(float vertex : b.getVertices())
					buffer.put(vertex);
				for(float color : types[b.getBlockID()].getColors())
					buffer.put(color);
				for(float texture : types[b.getBlockID()].getTextures())
					buffer.put(texture);
			}
		
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, chunkid);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		buffer.clear();
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		buffer.clear();
		
		for(Object[] row : getLayer(0))
			for(Object block : row)
			{
				ChunkBlock b = (ChunkBlock) block;
				for(float vertex : b.getBorderlines())
					buffer.put(vertex);
			}
		
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, borderlinesid);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		buffer.clear();
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		resetChunk();
	}

	@Override
	public void remapVBO(Terrain[] types, FloatBuffer buffer)
	{
		glBindBuffer(GL_ARRAY_BUFFER, chunkid);
		buffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, null).order(ByteOrder.nativeOrder()).asFloatBuffer();
		{	
			for(Object[] row : getLayer(0))
				for(Object block : row)
				{
					ChunkBlock b = (ChunkBlock) block;
					for(float vertex : b.getVertices())
						buffer.put(vertex);
					for(float color : types[b.getBlockID()].getColors())
						buffer.put(color);
					for(float texture : types[b.getBlockID()].getTextures())
						buffer.put(texture);
				}
			
			buffer.flip();
			buffer.clear();
		}
		glUnmapBuffer(GL_ARRAY_BUFFER);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, borderlinesid);
		buffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, null).order(ByteOrder.nativeOrder()).asFloatBuffer();
		{	
			for(Object[] row : getLayer(0))
				for(Object block : row)
				{
					ChunkBlock b = (ChunkBlock) block;
					for(float vertex : b.getBorderlines())
						buffer.put(vertex);
				}
			
			buffer.flip();
			buffer.clear();
		}
		glUnmapBuffer(GL_ARRAY_BUFFER);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void clean()
	{
		glDeleteBuffers(chunkid);
		glDeleteBuffers(borderlinesid);
	}

	@Override
	public boolean isVisible()
	{
		return this.visible;
	}

	@Override
	public void setVisible(final boolean visible)
	{
		this.visible = visible;
	}

	@Override
	public void gfxUpdate(int mX, int mY)
	{	
		for(Object[] row : getLayer(0))
			for(Object block : row)
			{
				ChunkBlock b = (ChunkBlock) block;
				b.isCollide(xPos + b.getXPos(), yPos + b.getTrueY(), mX, mY);
			}
	}

	@Override
	public ChunkBlock getFocusedBlock()
	{
		for(Object[] row : getLayer(0))
			for(Object block : row)
			{
				ChunkBlock b = (ChunkBlock) block;
				if(b.isFocused())
					return b;
			}
		
		return null;
	}

	@Override
	public ChunkBlock getBlockAbs(int absRow, int absCol)
	{
		ChunkBlock b = (ChunkBlock) getObjectAt(absRow, absCol);
		return b;
	}

	@Override
	public ChunkBlock[][] getChunkBlocks()
	{
		return (ChunkBlock[][]) getLayer(0);
	}

	@Override
	public String getMemoryAddress()
	{
		return this.toString().split("@")[1];
	}

	@Override
	public void resetChunk()
	{
		for(Object[] row : getLayer(0))
			for(Object block : row)
			{
				ChunkBlock b = (ChunkBlock) block;
				b.cleanUp();
			}
	}
}
