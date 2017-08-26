package ue.game.world.tile;

public interface Block
{
	/**
	 * Isometric view angle.
	 */
	byte TYPE_ISO = 1;
	/**
	 * Top-down view angle.
	 */
	byte TYPE_TDV = 2;
	
	/**
	 * Creates vectors for vertices and get these vertices ready to modify.<br>
	 * This method can be called after setting x and y position of the block.<br>
	 * However, if did, position of the vectors must be reseted by calling applyPosition() method.
	 * @param type Block.TYPE_ISO or Block.TYPE_TDV
	 */
	public void createVectors(byte type);
	
	/**
	 * Gets horizontal position of this block
	 * @return integer value of horizontal position.
	 */
	public float getXPos();
	
	/**
	 * Gets vertical position of this block. Disregards terrain level.
	 * @return integer value of vertical position.
	 */
	public float getYPos();
	
	/**
	 * Sets horizontal position of this block.
	 * @param xPos integer value of horizontal position.
	 */
	public void setXPos(float xPos);
	
	/**
	 * Sets vertical position of this block. Disregards terrain level.
	 * @param yPos integer value of vertical position, disregarding terrain level.
	 */
	public void setYPos(float yPos);
	
	/**
	 * Applies current x and y position of this block to vertices so it can be positioned in right position.<br>
	 * This method can be called after createVectors() method. To do so, set reset parameter as false.<br>
	 * Method uses terrain level on y position so it can be placed in right height.<br>
	 * If resetting vertices, this method directly overrides createVectors() method.
	 * @param type Block.TYPE_ISO or Block.TYPE_TDV 
	 * @param reset true if applying position with new vertices, false if just applying position.
	 */
	public void applyPosition(byte type, boolean reset);
	
	/**
	 * Translates current position based on delta value of x and y.<br>
	 * This uses translate method to move vertices.<br>
	 * Method is to move vertices based on delta values, not to set positions.<br>
	 * To set position, refer to applyPosition(boolean) method.
	 * @param dX integer value of delta value of x
	 * @param dY integer value of delta value of y
	 */
	public void translatePosition(byte dX, byte dY);
	
	/**
	 * Gets block's altitude(or level).
	 * @return integer value of block's level.
	 */
	public float getTerrainLevel();
	/**
	 * Sets block's altitude(or level).
	 * @param terrainlevel integer value of block's level.
	 */
	public void setTerrainLevel(float terrainlevel);
	
	/**
	 * Adjusts tile's shape by translating each vertex's position.<br>
	 * Since this method applies translate to y position only, this method must be called after createVectors and applyPosition.<br>
	 * This method also sets if tile itself is flat or not.
	 * @param edges byte array size of 4 which contains vertex offsets(1 or -1).
	 */
	public void adjustTerrain(byte[] edges);
	
	/**
	 * Gets current vertices.<br>
	 * When called, method converts Vector3f values into x, y, and z position, creating an array size of 12.<br>
	 * (x, y, and z position, total 4 = (3 * 4) = 12)
	 * @return float array size of 12, containing x, y, and z value of each vertex.
	 */
	public float[] getVertices();
	
	/**
	 * Gets borderline vertices for rendering purpose.<br>
	 * When called, method converts Vector3f values into x, y, and z position, creating an array size of 24.<br>
	 * (x, y, and z position, total 8 = 3 * 8 = 24)
	 * @return float array with size of 24.
	 */
	public float[] getBorderlines();
	
	/**
	 * Checks if this block is flat; height of each vertex is equal.
	 * @return true if block is flat, false if not.
	 */
	public boolean isFlat();
	
	/**
	 * Checks if mouse cursor is within block's detection range.<br>
	 * Block uses it's radius value to check.
	 * @param tX integer value of block's horizontal position.
	 * @param tY integer value of block's vertical position.
	 * @param mX integer value of mouse's horizontal position.
	 * @param mY integer value of mouse's vertical position.
	 * @return true if cursor is within detection range, false if not.
	 */
	public boolean isCollide(float tX, float tY, float mX, float mY);
	
	/**
	 * Gets if this block is currently focused.<br>
	 * This method does not check if mouse cursor is within detection range.
	 * @return true if focused, false if not.
	 */
	public boolean isFocused();
}
