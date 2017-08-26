package ue.game.world.chunk;

public interface UChunkLogic
{
	public String getMemoryAddress();
	
	public ChunkBlock getBlockAbs(final int absRow, final int absCol);
	
	public ChunkBlock getFocusedBlock();
	
	public ChunkBlock[][] getChunkBlocks();
}
