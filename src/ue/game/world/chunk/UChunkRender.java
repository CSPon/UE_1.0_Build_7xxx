package ue.game.world.chunk;

import java.nio.FloatBuffer;

import ue.game.world.tile.terrain.Terrain;

public interface UChunkRender
{
	/**
	 * Applies seed to each block in this chunk. However, this does not adjusts height.
	 * @param seed integer array of seed.
	 * @param max_water_level maximum water level.
	 * @param type Block.TYPE_ISO or Block.TYPE_TDV.
	 */
	void applySeed(int[][] seed, final byte max_water_level, final byte type);
	
	/**
	 * Applies height to each block in this chunk. Call this method after you apply seed to each block.
	 */
	void applyHeight(int[][] seed, final byte type, final byte max_water_level, final byte max_level);
	
	/**
	 * Renders this chunk.
	 * @param texture integer value of Texturepack's ID.
	 * @param borderlines true to render borderline, false if not.
	 */
	public void render(int texture, boolean borderlines);
	
	/**
	 * Renders borderline around each block on this chunk.
	 */
	public void renderBorder();
	
	/**
	 * Pushes Vertex Buffer Object to GPU.
	 * @param types An array of different types of blocks specified within engine.
	 * @param buffer FloatBuffer object to hold all buffered vertices.
	 */
	public void pushVBO(Terrain[] types, FloatBuffer buffer);
	/**
	 * Remaps Vertex Buffer Object.<br>
	 * Do not use this method to push VBO into GPU.
	 * @param types An array of ChunkBlock.
	 * @param buffer FloatBuffer object to hold all buffered vertices.
	 */
	public void remapVBO(Terrain[] types, FloatBuffer buffer);
	/**
	 * Cleans out VBO data.
	 */
	public void clean();
	
	/**
	 * Checks if this chunk is visible.
	 * @return true if this chunk is within window, false if not.
	 */
	public boolean isVisible();
	/**
	 * Sets chunk's visibility.
	 * @param visible boolean value of chunk's visibility.
	 */
	public void setVisible(boolean visible);
	
	/**
	 * Updates chunk.<br>
	 * Current version only checks for mouse collision.
	 * @param mX x-position of the mouse.
	 * @param mY y-position of the mouse.
	 */
	public void gfxUpdate(int mX, int mY);
}
