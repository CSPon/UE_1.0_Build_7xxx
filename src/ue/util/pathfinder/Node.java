package ue.util.pathfinder;

/**
 * Node is specialized class object to be used in PathFinder class and other minor level.
 * @author Charlie Shin
 *
 */
public class Node
{
	public int f_val, g_val, h_val;
	public short row, colum;
	
	public Node parentNode;
	
	/**
	 * Creates a node with specified row and column position.<br>
	 * Constructor sets parent node as null.
	 * @param row
	 * @param colum
	 */
	public Node(int row, int colum)
	{
		this.row = (short)row;
		this.colum = (short)colum;
		
		f_val = 0; g_val = 0; h_val = 0;
		
		parentNode = null;
	}
	
	public void debug()
	{
		System.out.println();
		System.out.println("------Node Data-------");
		System.out.println("Node Location : " + row + " " + colum);
		System.out.println("G : " + g_val);
		System.out.println("H : " + h_val);
		System.out.println("F : " + f_val);
		System.out.println("--Parent--");
		if(parentNode != null)
		{
			System.out.println("Parent : " + parentNode.row + " " + parentNode.colum);
		}
		else
			System.out.println("No Parent");
		System.out.println("----------------------");
		System.out.println();
	}
	
	public int getRow(){return this.row;}
	public int getColum(){return this.colum;}
}
