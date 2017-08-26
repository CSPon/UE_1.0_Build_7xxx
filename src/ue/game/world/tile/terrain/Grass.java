package ue.game.world.tile.terrain;

public class Grass extends Terrain
{
	private final float coordX = (1f % 16f) * 0.0625f;
	private float coordY = (float) (Math.floor(1f / 16f) * 0.0625f);
	
	private final float[] textures = {coordX, coordY, 0.0f, 
			coordX + 0.0625f, coordY, 0.0f,
			coordX + 0.0625f, coordY + 0.0625f, 0.0f,
			coordX, coordY + 0.0625f, 0.0f};
	
	private final float[] colors = {15f/255f, 123f/255f, 25f/255f,
			15f/255f, 123f/255f, 25f/255f,
			15f/255f, 123f/255f, 25f/255f,
			15f/255f, 123f/255f, 25f/255f};

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
		return Terrain.GRASS;
	}
}
