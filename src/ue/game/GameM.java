package ue.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ue.game.handle.HandlerI;
import ue.game.input.InputI;

public abstract class GameM implements HandlerI, InputI
{	
	protected int delta;
	
	protected boolean[] keys;
	protected byte kX, kY, mB;
	protected short mX, mY, mDx, mDy;
	
	// Frame Rate
	protected long lastfps, lastframe;
	protected long fps, max_fps, min_fps;
	protected String fpswatch;
	
	// Memory
	protected String memory;
	
	// Display Property
	protected short width, height;

	@Override
	public void poll()
	{
		Keyboard.poll();
		
		for(short i = 0; i < keys.length; i++)
			keys[i] = Keyboard.isKeyDown(i);
		
		kX = 0; kY = 0;
		
		if(keys[Keyboard.KEY_UP] || keys[Keyboard.KEY_W])
			kY = (byte) (1);
		if(keys[Keyboard.KEY_DOWN] || keys[Keyboard.KEY_S])
			kY = (byte) (-1);
		if(keys[Keyboard.KEY_LEFT] || keys[Keyboard.KEY_A])
			kX = (byte) (1);
		if(keys[Keyboard.KEY_RIGHT] || keys[Keyboard.KEY_D])
			kX = (byte) (-1);
		
		Mouse.poll();
		
		mX = (short) Mouse.getX();
		mY = (short) (height - Mouse.getY());
		mDx = (short) Mouse.getDX();
		mDy = (short) (height - Mouse.getDY());
		mB = (byte) Mouse.getEventButton();
	}

	@Override
	public boolean hasKeyNext()
	{
		return Keyboard.next();
	}

	@Override
	public boolean hasMouseNext()
	{
		return Mouse.next();
	}

	@Override
	public boolean[] getKeys()
	{
		return this.keys;
	}

	@Override
	public byte getMouseButton()
	{
		return this.mB;
	}

	@Override
	public short getMouseX()
	{
		return this.mX;
	}

	@Override
	public short getMouseY()
	{
		return this.mY;
	}

	@Override
	public short getMouseDeltaX()
	{
		return this.mDx;
	}

	@Override
	public short getMouseDeltaY()
	{
		return this.mDy;
	}

	@Override
	public byte getKeyboardX()
	{
		return this.kX;
	}

	@Override
	public byte getKeyboardY()
	{
		return this.kY;
	}

	@Override
	public void clear()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void update(int delta)
	{
		kX *= delta; kY *= delta;
	}

	@Override
	public long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	@Override
	public int calculateDelta()
	{
		long time = getTime();
		delta = (int) (time - lastframe);
		lastframe = time;
		
		return delta;
	}

	@Override
	public void startClock()
	{
		calculateDelta();
		lastfps = getTime();
	}

	@Override
	public void getFPS()
	{
		if(getTime() - lastfps > 1000)
		{
			fpswatch = "FPS: " + fps;
			if(fps <= min_fps)
				min_fps = fps;
			if(fps > max_fps)
			{
				min_fps = max_fps;
				max_fps = fps;
			}
			fps = 0;
			lastfps += 1000;
		}
		fps++;
	}

	@Override
	public String getFPSString()
	{
		return fpswatch;
	}

	@Override
	public void getMemory()
	{
		int maxMem = (int) (Runtime.getRuntime().maxMemory() / 1048576L);
		int totalMem = (int) (Runtime.getRuntime().totalMemory() / 1048576L);
		int freeMem = (int) (Runtime.getRuntime().freeMemory() / 1048576L);
		int usedMem = totalMem - freeMem;
		
		int usedPercentage = (int) (((float)usedMem / (float)totalMem) * 100);
		int freePercentage = (int) (((float)freeMem / (float)totalMem) * 100);
		int allocPercentage = (int) (((float)totalMem / (float)maxMem) * 100);
		
		memory = "Used Memory : " + usedMem + "MB (" + usedPercentage + "%) Free Memory : " + freeMem + "MB (" + freePercentage + "%)\n" +
				"Allocated : " + totalMem + "MB/" + maxMem + "MB (" + allocPercentage + "%)";
	}

	@Override
	public String devMode()
	{
		return memory;
	}
}
