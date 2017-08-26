package ue.game;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class UGame extends GameM
{
	private boolean running;
	
	private byte rate;
	
	/**
	 * Font ID is a unique number which has been given to a font texture, which buffered in via TextureLoader class.<br>
	 */
	protected static int fontid;
	
	public UGame(final short width, final short height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void setTargetRate(final byte rate)
	{
		this.rate = rate;
	}
	
	public void initInput()
	{
		try
		{
			keys = new boolean[256];
			Keyboard.create();
			Keyboard.enableRepeatEvents(true);
			Mouse.create();
			Mouse.setGrabbed(true);
		}
		catch(Exception err)
		{
			err.printStackTrace();
			System.exit(1);
		}
	}
	
	public void start()
	{
		running = true;
	}
	
	public void stop()
	{
		running = false;
	}
	
	public boolean isRunning()
	{
		return this.running;
	}
	
	public void calcDelta()
	{
		calculateDelta();
	}
	
	public byte getRate()
	{
		return this.rate;
	}

	/**
	 * Renders default mouse pointer to screen using immediate method.<br>
	 * If there is any other mouse pointer to render, override this default method.
	 * @param r float value of red components.
	 * @param g float value of green components.
	 * @param b float value of blue components.
	 */
	public void renderPointer(float r, float g, float b)
	{
		glLoadIdentity();
		glTranslatef(mX, mY, 0);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		glColor3f(r, g, b);
		glBegin(GL_LINES);
		{
			for(int i = -1; i <=1; i++)
			{
				glVertex2i(i, -16); glVertex2i(i, 16);
				glVertex2i(-16, i); glVertex2i(16, i);
			}
		}
		glEnd();
	}
}
