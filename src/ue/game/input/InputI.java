package ue.game.input;

public interface InputI
{
	/**
	 * Polls out input devices.
	 * By polling, program checks if there is an input which needs to be resolved or to be discarded.
	 */
	public void poll();
	
	/**
	 * Checks if there is any keyboard input that needs to be resolved.
	 * @return true if there is an input, false if not.
	 */
	public boolean hasKeyNext();
	
	/**
	 * Check if there is any mouse input that needs to be resolved.
	 * @return true if there is an input, false if not.
	 */
	public boolean hasMouseNext();
	
	/**
	 * Returns 256 keys based on their status(Pressed or Released).
	 * @return An array of boolean which based on 256 keyboard layout.
	 */
	public boolean[] getKeys();
	
	/**
	 * Gets current active button;
	 * @return 0 for left click, 1 for right click.
	 */
	public byte getMouseButton();
	
	/**
	 * Gets mouse position.
	 * @return short value of horizontal position of the mouse.
	 */
	public short getMouseX();
	
	/**
	 * Gets mouse position.
	 * @return short value of vertical position of the mouse.
	 */
	public short getMouseY();
	
	public short getMouseDeltaX();
	public short getMouseDeltaY();
	
	/**
	 * Gets delta x made by keyboard.
	 * @return byte value of horizontal change ranged between -1 to 1.
	 */
	public byte getKeyboardX();
	
	/**
	 * Gets delta y made by keyboard.
	 * @return byte value of vertical change ranged between -1 to 1.
	 */
	public byte getKeyboardY();
}
