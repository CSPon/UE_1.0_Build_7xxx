package ue;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3i;

import org.lwjgl.opengl.Display;

import ue.disp.UDisplay;
import ue.game.UGame;
import ue.game.world.World;

/**
 * Uniform Engine Version 1.0, Build 7xxx
 * @author Charlie Shin
 *
 */
public abstract class UniformEngine
{
	public static final byte WORLD_ISO = World.WORLD_ISO;
	public static final byte WORLD_TDV = World.WORLD_TDV;
	public static final byte DRAW_RULE_DIRECT = World.DRAW_DIRECT;
	public static final byte DRAW_RULE_RANGED = World.DRAW_RANGED;
	
	public static final String VERSION = "Uniform 1 Engine (v.1.0.7000)";
	
	private UDisplay display;
	private UGame game;
	
	/**
	 * Creates screen and displays it.<br><br>
	 * In order to have specified screen, parameter must contains 4 parameters<br>
	 * <b>First</b> parameter determines full screen mode. This is boolean value.<br>
	 * <b>Second</b> parameter determines screen width. This is short value.<br>
	 * <b>Third</b> parameter determines screen ratio(ie. 16:9). This is float value.<br>
	 * <b>Fourth</b> parameter determines desired frame rate(or target frame rate). This is byte value.<br><br>
	 * If there is no specified parameters, screen will be adjusted to 1024*576 with 60fps, full screen disabled.
	 * <b>TIPS ON HOW TO SET SCREEN RATIO</b><br>
	 * To set screen ratio, divide height ratio by width ratio.<br>
	 * For example, 16:9 screen ratio will be determined by dividing 9 by 16(0.5625).<br>
	 * @param args arguments passed from .bat file.
	 */
	public void init(String[] args)
	{
		if(args.length > 0 && args.length < 5)
		{
			boolean fullscreen = Boolean.valueOf(args[0]);
			short width = Short.valueOf(args[1]);
			float ratio = Float.valueOf(args[2]);
			byte rate = Byte.valueOf(args[3]);
			
			display = new UDisplay(width, ratio);
			display.setTitle(VERSION);
			display.setFullScreen(fullscreen);
			
			game = new UGame(display.getWidth(), display.getHeight());
			game.setTargetRate(rate);
			
		}
		else
		{
			display = new UDisplay((short) 1024, 0.5625f);
			display.setTitle(VERSION);
			display.setFullScreen(false);
			
			game = new UGame(display.getWidth(), display.getHeight());
			game.setTargetRate((byte) 60);
		}
	}
	
	/**
	 * Performs internal process which developer does not need to perform. 
	 */
	protected void run()
	{
		while(game.isRunning())
		{
			preRender();
			
			if(isThreadAvailable())
			{
				new Logic().run();
			}
			else logic();
			
			input();
			update();
			render();
			
			postRender();
		}
		cleanup();
		display.destroy();
		System.exit(0);
	}
	
	/**
	 * Updates keyboard/mouse related variables.<br>
	 * Call this method before render() method.
	 */
	private void input()
	{
		game.poll();
		keyboard();
		mouse();
	}
	
	/**
	 * Updates graphics-related variables.<br>
	 * Call this method before render() method.
	 */
	public abstract void update();
	
	/**
	 * Updates internal calculations.<br>
	 * <b>IMPORTATN!</b> Do not use this method to run graphics-related updates.
	 */
	public abstract void logic();
	
	/**
	 * Handles keyboard actions.<br>
	 * Game developers must handle this part since Uniform Engine only supports rendering.
	 */
	public abstract void keyboard();
	
	/**
	 * Handles mouse actions.<br>
	 * Game developers must handle this part since Uniform Engine only supports rendering.
	 */
	public abstract void mouse();
	
	/**
	 * Cleans up before destroying screen.<br>
	 * Cleanup() method must be called before destroy() method and after run() loop.
	 */
	public abstract void cleanup();
	
	/**
	 * Renders graphics data into screen.
	 */
	public abstract void render();
	
	/**
	 * Creates screen.<br>
	 * Creates screen then starts the thread.
	 */
	public void create()
	{
		display.create();
		game.initInput();
		game.startClock();
		
		game.start();
	}
	
	/**
	 * Checks if there is another processor available for multi-threading.
	 * @return true if processor is available, false if not.
	 */
	public boolean isThreadAvailable()
	{
		if(Runtime.getRuntime().availableProcessors() > 1)
			return true;
		else return false;
	}
	
	/**
	 * Performs pre-render, such as screen cleaning and delta-calculations.
	 */
	public void preRender()
	{
		game.calcDelta();
		game.clear();
	}
	
	/**
	 * Performs post-render, such as frame sync and memory usage check.
	 */
	public void postRender()
	{
		Display.update();
		Display.sync(game.getRate());
		
		game.getFPS();
		game.getMemory();
	}
	
	/**
	 * Gets frame rate as String data.
	 * @return String data of frame rate.
	 */
	public String getFPS()
	{
		return game.getFPSString();
	}
	
	/**
	 * Gets memory usage as String data.
	 * @return String data of memory usage.
	 */
	public String getUsage()
	{
		return game.devMode();
	}
	
	/**
	 * Renders string onto the screen on desired position.
	 * @param string String to be rendered.
	 * @param x integer value of x position.
	 * @param y integer value of y position.
	 * @param size integer value of font size.
	 */
	public static void drawString(int fontid, Object string, int x, int y, int size)
	{
		String[] lines = string.toString().split("\n");
		
		for(int i = 0; i < lines.length; i++)
		{
			char[] line = lines[i].toCharArray();
			int accum = 0;
			
			glBindTexture(GL_TEXTURE_2D, fontid);
			for(char c : line)
			{
				glLoadIdentity();
				glTranslatef(x + accum, y + (i * size), 0);
				
				float startX = ((int)c % 16) * 8;
				float endX = startX + 8;
				
				float startY = ((int)c / 16) * 8;
				float endY = startY + 8;
				
				startX /= 128;
				startY /= 128;
				endX /= 128;
				endY /= 128;
				
				glBegin(GL_QUADS);			
					glTexCoord2f(startX, startY);
					glVertex3i(0, 0, 0);
					
					glTexCoord2f(endX, startY);
					glVertex3i(size, 0, 0);
					
					glTexCoord2f(endX, endY);
					glVertex3i(size, size, 0);
					
					glTexCoord2f(startX, endY);
					glVertex3i(0, size, 0);
				glEnd();
				
				accum += (size / 2);
			}
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	// From UDisplay
	public void setTitle(String title)
	{
		display.setTitle(title);
	}
	public void setFullScreen(boolean fullscreen)
	{
		display.setFullScreen(fullscreen);
	}
	public short getScrWidth()
	{
		return display.getWidth();
	}
	public short getScrHeight()
	{
		return display.getHeight();
	}
	
	// From UGame
	protected void stop()
	{
		game.stop();
	}
	public void renderPointer(float r, float g, float b)
	{
		game.renderPointer(r, g, b);
	}
	public UGame getInput()
	{
		return this.game;
	}
	
	private class Logic extends Thread
	{
		public void run()
		{
			logic();
		}
	}
}
