package ue.game.world.tile.terrain;

public class Dirt extends Terrain
{
	private final float coordX = (2f % 16f) * 0.0625f;
	private final float coordY = (float) (Math.floor(2f / 16f) * 0.0625f);
	
	private final float[] textures = {coordX, coordY, 0.0f, 
			coordX + 0.0625f, coordY, 0.0f,
			coordX + 0.0625f, coordY + 0.0625f, 0.0f,
			coordX, coordY + 0.0625f, 0.0f};
	
	private final float[] colors = {1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f};

	@Override
	public float[] getTextures()
	{
		return this.textures;
	}

	@Override
	public float[] getColors()
	{
		return this.colors;
	}

	@Override
	public int getID()
	{
		return Terrain.DIRT;
	}
	
}
