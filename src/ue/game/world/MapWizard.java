package ue.game.world;

import java.util.Random;

public class MapWizard
{
	private static Random random;
	
	public MapWizard(long seed)
	{
		random = new Random(seed);
	}
	
	public static float[][] generate(int x, int y, int octave, float persistance)
	{
		float[][] base = generateNoise(0, x, y);
		
		int width = base.length;
		int height = base[0].length;
	 
		float[][][] smoothNoise = new float[octave][][]; //an array of matrix
	 
		//generate smooth noise
		for (int i = 0; i < octave; i++)
		{
			smoothNoise[i] = generateSmoothNoise(base, i);
		}
	 
	    float[][] perlinNoise = new float[width][height];
	    float amplitude = 1.0f;
	    float totalAmplitude = 0.0f;
	 
	    //blend noise together
	    for (int pass = octave - 1; pass >= 0; pass--)
	    {
	       amplitude *= persistance;
	       //amplitude = (float) Math.pow(persistance, pass);
	       totalAmplitude += amplitude;
	 
	       for (int i = 0; i < width; i++)
	       {
	          for (int j = 0; j < height; j++)
	          {
	             perlinNoise[i][j] += smoothNoise[pass][i][j] * amplitude;
	          }
	       }
	    }
	 
	   //Normalization
	   for (int i = 0; i < width; i++)
	   {
	      for (int j = 0; j < height; j++)
	      {
	         perlinNoise[i][j] /= totalAmplitude;
	      }
	   }
	 
	   return perlinNoise;
	}
	
	public static float[][] generateWrap(float[] horizontal, float[] vertical, int x, int y, int octave, float persistance)
	{
		float[][] base = generateNoise(1, x, y);
		
		for(int i = 0; i < base.length; i++)
		{
			base[0][i] = horizontal[i];
			base[i][0] = vertical[i];
		}
		
		int width = base.length;
		int height = base[0].length;
	 
		float[][][] smoothNoise = new float[octave][][]; //an array of matrix
	 
		//generate smooth noise
		for(int i = 0; i < octave; i++)
		{
			smoothNoise[i] = generateSmoothNoise(base, i);
		}
	 
	    float[][] perlinNoise = new float[width][height];
	    float amplitude = 1.0f;
	    float totalAmplitude = 0.0f;
	 
	    //blend noise together
	    for(int pass = octave - 1; pass >= 0; pass--)
	    {
	       amplitude *= persistance;
	       totalAmplitude += amplitude;
	 
	       for (int i = 0; i < width; i++)
	       {
	          for (int j = 0; j < height; j++)
	          {
	             perlinNoise[i][j] += smoothNoise[pass][i][j] * amplitude;
	          }
	       }
	    }
	 
	   //Normalization
	   for (int i = 0; i < width; i++)
	   {
	      for (int j = 0; j < height; j++)
	      {
	         perlinNoise[i][j] /= totalAmplitude;
	      }
	   }
	 
	   return perlinNoise;
	}
	
	//First generates rough noise.
	private static float[][] generateNoise(int start, int width, int height)
	{
		float[][] noise = new float[width][height];
		
		for(int x = start; x < noise.length; x++)
			for(int y = start; y < noise[x].length; y++)
			{
				noise[x][y] = (float)random.nextDouble() % 1;
			}
		return noise;
	}
	
	//Rough noise will be smoothed
	private static float[][] generateSmoothNoise(float[][] base, int octave)
	{
		int width = base.length;
		int height = base[0].length;
		
		float[][] smoothnoise = new float[width][height];
		
		int sample_period = 1 << octave;
		float sample_freq = 1.0f / (float) sample_period;
		
		for(int x = 0;x < width; x++)
		{
			int sample_x0 = (int) (Math.floor(x / sample_period) * sample_period);
			int sample_x1 = (sample_x0 + sample_period) % width;
			float horiz_blend = (x - sample_x0) * sample_freq;
			
			for(int y = 0;y < height; y++)
			{
				int sample_y0 = (int) (Math.floor(y / sample_period) * sample_period);
				int sample_y1 = (sample_y0 + sample_period) % height;
				float vert_blend = (y - sample_y0) * sample_freq;
				
				float top = interpolate(base[sample_x0][sample_y0],
			            base[sample_x1][sample_y0], horiz_blend);
				
				float bottom = interpolate(base[sample_x0][sample_y1],
			            base[sample_x1][sample_y1], horiz_blend);
				
				smoothnoise[x][y] = interpolate(top, bottom, vert_blend);
			}
		}
		
		return smoothnoise;
	}
	
	//This method will use two points to calculate slope.
	private static float interpolate(float x0, float x1, float alpha)
	{
		return x0 * (1 - alpha) + alpha * x1;
	}
}
