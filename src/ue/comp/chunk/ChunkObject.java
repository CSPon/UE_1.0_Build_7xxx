package ue.comp.chunk;

public interface ChunkObject
{
	Object[][][] getChunk();
	Object[][]getLayer(int layer);
	Object getObjectAt(int row, int col);
	void setObjectAt(int row, int col, Object obj);
	
	float getRow();
	float getCol();
	
	float getXPos();
	float getYPos();
	
	void translate(float dX, float dY);
	
	void resetChunk();
}
