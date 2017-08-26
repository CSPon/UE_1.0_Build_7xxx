package ue.util.pathfinder;

import java.util.Vector;

/**
 * <b>CURRENT PATHFINDER VERSION IS OUT-OF-DATE. DO NOT USE THERE IS AN APPROPRIATE UPDATE.</b><br>
 * Jump-point search utility for faster pathfinding.
 * @author Charlie Shin
 *
 */
public class JumpPoint
{
	/**
	 * Goes through jump-point search algorithm to find optimized path, instead of checking every nodes in open list.
	 * @param level Matrix of byte representing heightmap.
	 * @param openList Vector of nodes(open list).
	 * @param closeList Vector of nodes(close list).
	 * @param current Current node to check.
	 * @param start Starting node. That is, the original starting point.
	 * @param end Final end point.
	 * @return Vector of nodes with optimized path.
	 */
	public static Vector<Node> jumpTest(byte[][] level, Vector<Node> openList, Vector<Node> closeList, Node current, Node start, Node end)
	{
		Vector<Node> successors = new Vector<Node>(0, 1);
		
		Vector<Node> adjacent = PathFinder.getAdjacentNode(level, openList, closeList, current);
		
		for(Node testNode : adjacent)
		{
			byte dX = getDirection(testNode.row, current.row);
			byte dY = getDirection(testNode.colum, current.colum);
			
			Node successor = jumpCheck(level, testNode, dX, dY, start, end);
			if(successor != null)
				successors.add(0, successor);
		}
		
		return successors;
	}
	
	/**
	 * Goes through jump check.<br>
	 * Compare to jumpTest(), this method utilizes only one node to get optimized path throughout the map.
	 * @param level Matrix of byte representing heightmap.
	 * @param testNode Testing node.
	 * @param dX Horizontal direction(-1 ~ 1).
	 * @param dY Vertical direction(-1 ~ 1).
	 * @param start Starting node. That is, the original starting point.
	 * @param target Final end point.
	 * @return A node that must go through pathfind check.
	 */
	public static Node jumpCheck(byte[][] level, Node testNode, byte dX, byte dY, Node start, Node target)
	{
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum) < 0)
			return null;
		if(PathFinder.compareNode(testNode, target))
			return testNode;
		if(dX != 0 && dY != 0)
		{
			if(horizontalCheck(level, testNode, target, dY) != null || verticalCheck(level, testNode, target, dX) != null)
			{
				if(diagonalCheck(level, testNode, target, dX, dY) != null)
					return diagonalCheck(level, testNode, target, dX, dY);
				else return testNode;
			}
			else if(diagonalCheck(level, testNode, target, dX, dY) != null)
				return diagonalCheck(level, testNode, target, dX, dY);
		}
		else if(dX == 0)
		{
			if(horizontalCheck(level, testNode, target, dY) != null)
			{
				if(diagonalCheck(level, testNode, target, (byte) 1, dY) != null || diagonalCheck(level, testNode, target, (byte) -1, dY) != null)
					return testNode;
				else return horizontalCheck(level, testNode, target, dY);
			}			
			//return horizontalCheck(level, testNode, target, dY);
		}
		else if(dY == 0)
		{
			if(verticalCheck(level, testNode, target, dX) != null)
			{
				if(diagonalCheck(level, testNode, target, dX, (byte) 1) != null || diagonalCheck(level, testNode, target, dX, (byte) -1) != null)
				{
					if(verticalCheck(level, testNode, target, dX) != null)
						return testNode;
					else return verticalCheck(level, testNode, target, dX);
				}
			}
			//return verticalCheck(level, testNode, target, dX);
		}
		
		return jumpCheck(level, new Node(testNode.row + dX, testNode.colum + dY), dX, dY, start, target);
	}
	
	/**
	 * Checks diagonal blocks for a path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dX
	 * @param dY
	 * @return A node to be added to open list.
	 */
	public static Node diagonalCheck(byte[][] level, Node testNode, Node targetNode, byte dX, byte dY)
	{
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row + dX, testNode.colum + dY) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row + dX, testNode.colum) < 0 && PathFinder.terrainCheck(level, testNode.row, testNode.colum + dY) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row - dX, testNode.colum) < 0 && PathFinder.terrainCheck(level, testNode.row, testNode.colum - dY) < 0)
			return null;
		else if(PathFinder.compareNode(testNode, targetNode))
			return testNode;
		else if(PathFinder.terrainCheck(level, testNode.row - dX, testNode.colum) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum - dY) < 0)
			return testNode;
		else if(PathFinder.terrainCheck(level, testNode.row + dX, testNode.colum) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum + dY) < 0)
			return testNode;
		
		return diagonalCheck(level, new Node(testNode.row + dX, testNode.colum + dY), targetNode, dX, dY);
	}
	
	/**
	 * Checks horizontal blocks for path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dir
	 * @return A node to be added to open list.
	 */
	public static Node horizontalCheck(byte[][] level, Node testNode, Node targetNode, byte dir)
	{
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row, testNode.colum + dir) < 0)
			return null;
		else if(PathFinder.compareNode(testNode, targetNode))
			return testNode;
		else if(PathFinder.terrainCheck(level, testNode.row - 1, testNode.colum) < 0 || PathFinder.terrainCheck(level, testNode.row + 1, testNode.colum) < 0)
			return testNode;
		
		return horizontalCheck(level, new Node(testNode.row, testNode.colum + dir), targetNode, dir);
	}
	
	/**
	 * Checks vertical blocks for path.
	 * @param level
	 * @param testNode
	 * @param targetNode
	 * @param dir
	 * @return A node to be added to open list.
	 */
	public static Node verticalCheck(byte[][] level, Node testNode, Node targetNode, byte dir)
	{
		if(PathFinder.terrainCheck(level, testNode.row, testNode.colum) < 0)
			return null;
		else if(PathFinder.terrainCheck(level, testNode.row + dir, testNode.colum) < 0)
			return null;
		else if(PathFinder.compareNode(testNode, targetNode))
			return targetNode;
		else if(PathFinder.terrainCheck(level, testNode.row, testNode.colum - 1) < 0 || PathFinder.terrainCheck(level, testNode.row, testNode.colum + 1) < 0)
			return testNode;
		
		return verticalCheck(level, new Node(testNode.row + dir, testNode.colum), targetNode, dir);
	}
	
	/**
	 * Gets direction of path.
	 * @param s Starting position.
	 * @param e End position.
	 * @return byte value representing one-dimensional direction.
	 */
	public static byte getDirection(int s, int e)
	{	
		if((s - e) >= 1)
			return 1;
		else if((s - e) <= -1)
			return -1;
		
		return 0;
	}
}
