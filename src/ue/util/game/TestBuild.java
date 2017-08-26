package ue.util.game;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import ue.UniformEngine;
import ue.game.world.UWorld;
import ue.util.tex.UTexture;

/**
 * TestBuild creates one world, either isometric or top-down view, then user can interact with basic map movement.
 * @author Charlie Shin
 *
 */
public class TestBuild extends UniformEngine
{
	private String devstat = "";
	private UTexture textures;
	private UWorld world;
	
	private boolean devmode;
	
	public static void main(String[] args)
	{
		new TestBuild(args, (byte) 1);
	}
	
	public TestBuild(String[] args, byte drawType)
	{
		init(args);
		create();
		
		textures = new UTexture(); textures.targetTexturePack("lib/tex/", "texturepack", "png", 16);
		textures.loadFont();
		world = new UWorld(0, 32, 0.5f, (byte) 20, (byte) 16, drawType);
		world.setDrawMethod(DRAW_RULE_RANGED);
		world.generateMap(8, 100f);
		
		run();
	}

	@Override
	public void cleanup()
	{
		textures.cleanup();
	}

	@Override
	public void keyboard()
	{
		if(world.isWorldLoaded())
		{
			byte dX = (byte) (getInput().getKeyboardX() * 10), dY = (byte) (getInput().getKeyboardY() * 10);
			world.keyDown(dX, dY);
		}
		
		while(getInput().hasKeyNext())
		{
			if(getInput().getKeys()[Keyboard.KEY_ESCAPE])
				stop();
			if(getInput().getKeys()[Keyboard.KEY_G])
				world.setBorderline();
			if(getInput().getKeys()[Keyboard.KEY_F11])
				if(!devmode)
					devmode = true;
				else devmode = false;
		}
	}

	@Override
	public void mouse()
	{
		if(world.isWorldLoaded())
			world.mouseMoved(getInput().getMouseX(), getInput().getMouseY());
	}

	@Override
	public void update()
	{	
		if(!world.isWorldLoaded())
		{
			world.updateLoad(getScrWidth());
			
			if(world.isWorldLoaded())
				world.getCenter((short) (getScrWidth() / 2), (short) (getScrHeight() / 2));
		}
		else
		{
			world.updateRender(getScrWidth(), getScrHeight(), (short) (getScrWidth() / 2), (short) (getScrHeight() / 2));
		}
		
		devstat = "*Developer Mode(Demonstration)*\n" +
				"" + getFPS() + "\n" + 
				"" + getUsage() + "\n";
		
		if(devmode)
			devstat += world.getStatus();
	}

	@Override
	public void render()
	{
		if(world.isWorldLoaded())
			world.render(textures.getTexturePack());
		
		renderPointer(1.0f, 1.0f, 1.0f);
		
		glColor3f(1.0f, 1.0f, 1.0f);
		drawString(textures.getFont(), devstat, 5, 5, 16);
	}

	@Override
	public void logic()
	{
		world.checkSurrounding();
	}
}
