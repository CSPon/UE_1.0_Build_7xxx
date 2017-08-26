package ue.disp;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Toolkit;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import ue.disp.gfx.GraphicsI;
import ue.disp.screen.ScreenI;

public abstract class ScreenM implements ScreenI, GraphicsI
{
	private DisplayMode display;
	private String title;
	private boolean fullscreen;
	
	@Override
	public void initialize(final short width, final float ratio)
	{
		title = "";
		fullscreen = false;
		if(ratio > 1 || ratio < 0)
		{
			float newRatio = (Toolkit.getDefaultToolkit().getScreenSize().height) / (Toolkit.getDefaultToolkit().getScreenSize().width);
			display = new DisplayMode(width, (short) (width * newRatio));
		}
		else display = new DisplayMode(width, (short) (width * ratio));
	}
	
	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@Override
	public void setFullScreen(boolean fullscreen)
	{
		this.fullscreen = fullscreen;
		if(this.fullscreen)
		{
			try
			{
				for(DisplayMode mode : Display.getAvailableDisplayModes())
				{
					if(mode.isFullscreenCapable() && this.fullscreen)
					{
						display = mode;
						break;
					}
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	@Override
	public void create()
	{
		try
		{
			Display.setInitialBackground(0.0f, 0.0f, 0.0f);
			Display.setVSyncEnabled(false);
			Display.setDisplayMode(display);
			Display.setFullscreen(fullscreen);
			Display.setTitle(title);
			Display.setLocation(8, 8);
			Display.create();
			
			setGraphics(display);
		}
		catch(Exception err)
		{
			err.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public short getWidth()
	{
		return (short) this.display.getWidth();
	}
	
	@Override
	public short getHeight()
	{
		return (short) this.display.getHeight();
	}
	
	@Override
	public void destroy()
	{
		disableGraphics();
		Display.destroy();
	}
	
	/**
	 * Sets up basic openGL settings for this application.
	 */
	public void setGraphics(DisplayMode display)
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, display.getWidth(), display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		glEnable(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_LINEAR);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}
	
	/**
	 * Disables openGL settings.
	 */
	public void disableGraphics()
	{
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		glDisable(GL_DEPTH_TEST);
	}
}
