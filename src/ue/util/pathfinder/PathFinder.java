package ue.util.pathfinder;

import java.util.Vector;

/**
 * <b>CURRENT PATHFINDER VERSION IS OUT-OF-DATE. DO NOT USE THERE IS AN APPROPRIATE UPDATE.</b><br>
 * Default pathfinder utility.
 * @author Charlie Shin
 *
 */
public class PathFinder
{	
	/**
	 * Generates entire path from starting position to target position.<br>
	 * Both openList and closeList must be empty at the beginning.<br>
	 * @param level byte array containing differnt levels on different points
	 * @param openList Vectors of nodes selected as possible path node.
	 * @param closeList Vectors of nodes which have been used to check path.
	 * @param startNode Starting position.
	 * @param targetNode Final position(Exclusive).
	 * @return Vector of nodes to closest path to target node, target node is exclusive.
	 */
	public static Vector<Node> getPath3(byte[][] level, Vector<Node> openList, Vector<Node> closeList, Node startNode, Node targetNode)
	{
		if(openList.isEmpty())
		{
			openList.add(startNode);
		}
		else if(!openList.isEmpty())
		{
			Node parent;
			
			parent = getLowestF(openList, closeList);
			//parent = getLowestF(openList, closeList);
			//parent = openList.firstElement();
			//parent = openList.lastElement();
			
			closeList.add(parent);
			while(openList.remove(parent))
				openList.remove(parent);
			
			Vector<Node> successors = JumpPoint.jumpTest(level, openList, closeList, parent, startNode, targetNode);
			
			for(Node node : successors)
			{		
				if(!isInOpenList(openList, node))
				{
					node.g_val = getGValue(parent, node);
					node.h_val = (int) getHValue(node, targetNode);
					node.f_val = (node.g_val + node.h_val);
					
					node.parentNode = parent;
					
					openList.add(0, node);
				}
				else if(isInOpenList(openList, node))
				{
					if(parent.g_val + getGValue(parent, node) < parent.g_val)
					{
						node.parentNode = parent;
						node.g_val = getGValue(parent, node);
						node.f_val = node.g_val + node.h_val;
					}
					node.parentNode = parent;
					node.g_val = getGValue(parent, node);
				}
			}
		}
		
		if(isInCloseList(closeList, targetNode))
		{	
			Node reference = getNode(closeList, targetNode);
			
			Vector<Node> newPath = new Vector<Node>(0, 1);
			
			newPath.add(0, reference);
			
			while(reference.parentNode != null)
			{
				if(compareNode(reference.parentNode, startNode) || reference.parentNode == null)
					break;
				
				newPath.add(0, reference.parentNode);
				
				reference = reference.parentNode;
			}
			
			return newPath;
		}
		else if(closeList.size() > 0)
		{
			Node reference = closeList.lastElement();

			Vector<Node> newPath = new Vector<Node>(0, 1);
			
			newPath.add(0, reference);
			
			while(reference.parentNode != null)
			{	
				newPath.add(0, reference.parentNode);
				
				reference = reference.parentNode;
			}
			
			return newPath;
		}
		else if(openList.isEmpty())
		{
			return new Vector<Node>(0, 1);
		}
		
		return null;
	}
	
	/**
	 * Gets single node from a specified list.<br>
	 * Method uses row & column match to find a node. It does not use node.equals(node).
	 * @param list Vector of nodes.
	 * @param target target node.
	 * @return Target Node if found, null if not found.
	 */
	public static Node getNode(Vector<Node> list, Node target)
	{
		for(Node node : list)
			if(compareNode(node, target))
				return node;
		return null;
	}
	
	/**
	 * Compares two nodes and see if these two are identical.<br>
	 * Method uses row & column match to find a node. It does not use node.equals(node).
	 * @param a Node to compare.
	 * @param b Node to compare.
	 * @return True if nodes are identical, false if not.
	 */
	public static boolean compareNode(Node a, Node b)
	{
		if(a != null && b != null)
			if(a.row == b.row && a.colum == b.colum)
				return true;
		return false;
	}
	
	/**
	 * Checks if desired node is in open list.<br>
	 * Method uses row & column match to find a node. It does not use node.equals(node).
	 * @param open Vector of nodes(open list).
	 * @param testNode Node to find.
	 * @return True if node is in the open list, false if not.
	 */
	public static boolean isInOpenList(Vector<Node> open, Node testNode)
	{	
		for(Node node : open)
			if(compareNode(node, testNode))
				return true;
		return false;
	}
	
	/**
	 * Check if desired node is in closed list.<br>
	 * Method uses row & column match to find a node. It does not use node.equals(node).
	 * @param close Vector of nodes(close list).
	 * @param testNode Node to find.
	 * @return True if node is in the open list, false if not.
	 */
	public static boolean isInCloseList(Vector<Node> close, Node testNode)
	{	
		for(Node node : close)
			if(compareNode(node, testNode))
				return true;
		return false;
	}
	
	/**
	 * Gets the lowest cost of F value from selected list.
	 * @param nodes Vector of nodes to check lowest F value.
	 * @return A node with lowest F value within the list.
	 */
	public static Node getLowestF(Vector<Node> nodes)
	{
		Node lowest = nodes.firstElement();
		for(Node node : nodes)
			if((node.f_val <= lowest.f_val))
				lowest = node;
		
		return lowest;
	}
	
	/**
	 * Gets the lowest cost of F value from open list, and checks if this node is already in close list.<br>
	 * A node which already has been used(that is, is in close list) cannot be used again.
	 * @param open Vector of nodes(open list).
	 * @param close Vector of nodes(close list).
	 * @return A node with lowest F value within open list that has not yet been moved to close list.
	 */
	public static Node getLowestF(Vector<Node> open, Vector<Node> close)
	{		
		Node lowest = open.lastElement();
		for(Node node : open)
			if((node.f_val <= lowest.f_val) && !isInCloseList(close, node))
				//if(node.h_val < lowest.h_val)
					lowest = node;
		
		return lowest;
	}
	
	/**
	 * Gets the lowest cost of G value from open list, and checks if this node is already in close list.<br>
	 * A node which already had been used(that is, is in close list) cannot be used again.
	 * @param open Vector of nodes(open list).
	 * @param close Vector of nodes(close list).
	 * @return A node with lowest G value within open list that has not yet been moved to close list.
	 */
	public static Node getLowestG(Vector<Node> open, Vector<Node> close)
	{
		Node lowest = open.firstElement();
		for(Node node : open)
			if((node.g_val <= lowest.g_val) && !isInCloseList(close, node))
				lowest = node;
		
		return lowest;
	}
	
	/**
	 * Returns a node with closest distance based on center node.<br>
	 * Distance is measured in G cost.
	 * @param list List of nodes to be tested
	 * @param center Center node to be considered
	 * @return Node with closest distance. If there are multiple nodes with closest distance, last one will be returned.
	 */
	public static Node getNextClosest(Vector<Node> list, Node center)
	{
		Vector<Node> closest = new Vector<Node>(0, 1);
		
		Node start = list.firstElement();
		int g = getGValue(center, start);
		
		for(Node node : list)
		{
			if(getGValue(center, node) <= g)
			{
				g = getGValue(center, node);
				closest.add(0, node);
			}
		}
		
		System.out.println(g);
		
		return closest.firstElement();
	}
	
	/**
	 * Gets surrounding nodes from selected node, regardless of terrain type.
	 * @param level byte matrix that represents heightmap of the map.
	 * @param close Vector of nodes(close list).
	 * @param center A node which has been selected as center node.
	 * @return Vector of nodes surrounding the center node.
	 */
	public static Vector<Node> getForcedAdjacentNode(byte[][] level, Vector<Node> close, Node center)
	{
		Vector<Node> adjacent = new Vector<Node>(0, 1);
		
		for(int row = center.row - 1; row < center.row + 2; row++)
			for(int col = center.colum - 1; col < center.colum + 2; col++)
			{
				if(terrainCheck(level, row, col) >= 0 && !compareNode(new Node(row, col), center))
				{
					if(isInCloseList(close, new Node(row, col)))
						adjacent.add(0, getNode(close, new Node(row, col)));
				}
			}
		
		return adjacent;
	}

	/**
	 * Gets surrounding nodes from selected node, excluding impassable node.
	 * @param level A matrix of byte representing heighmap of the map.
	 * @param open Vector of nodes(open list).
	 * @param close Vector of nodes(close list).
	 * @param center A node which has been selected as center node.
	 * @return Vector of nodes surrounding the center node.
	 */
	public static Vector<Node> getAdjacentNode(byte[][] level, Vector<Node> open, Vector<Node> close, Node center)
	{
		Vector<Node> list = new Vector<Node>(0, 1);
		
		for(int row = center.row - 1; row < center.row + 2; row++)
			for(int col = center.colum - 1; col < center.colum + 2; col++)
			{
				if(terrainCheck(level, row, col) >= 0 && !compareNode(new Node(row, col), center))
				{
					if(!isInCloseList(close, new Node(row, col)) && !isInOpenList(open, new Node(row, col)))
					{
						Node newNode = new Node(row, col);
						list.add(0, newNode);
					}
				}
			}
		
		return list;
	}
	
	/**
	 * Gets G value based on distance method.
	 * @param start Start node.
	 * @param end End node.
	 * @return Integer value representing distance between two nodes.
	 */
	public static int getGValue(Node start, Node end)
	{	
		return (int) (Math.sqrt(Math.pow(start.row - end.row, 2) + Math.pow(start.colum - end.colum, 2)) * 10);
	}
	
	/**
	 * Returns heuristic value
	 * It first attempts Diagonal heuristic value. If fails however, it returns Manhattan heuristic value.
	 * @param start Start node.
	 * @param end End node.
	 * @return Heuristic value in float.
	 */
	public static float getHValue(Node start, Node end)
	{
		int xDist = Math.abs(start.row - end.row);
		int yDist = Math.abs(start.colum - end.colum);
		
		if(xDist > yDist)
			return (14 * yDist) + (10 * (xDist - yDist));
		else
			return (14 * xDist) + (10 * (yDist - xDist));
		
		//return (Math.abs(start.row - end.row) + Math.abs(start.colum - end.colum)) * 10;
	}
	
	public static float getHValue2(Node start, Node end)
	{
		float dX = Math.abs(start.row - end.row);
		float dY = Math.abs(start.colum - end.colum);
		
		return (float) (Math.sqrt((dX * dX) + (dY * dY)) * 10);
	}
	
	/**
	 * Gets total score(G + H).
	 * @param parent Target node's parent node. Parent node defines direction of target node.
	 * @param target The node to check.
	 * @return float value of G + H.
	 */
	public static float getScore(Node parent, Node target)
	{
		float g = getGValue(parent, target);
		float h = getHValue(parent, target);
		
		return g + h;
	}
	
	/**
	 * Checks if terrain is impassable or not.<br>
	 * If checking range is out of bound or impassable, it returns -1.
	 * @param level A matrix representing the heightmap of the map.
	 * @param row Row position of the map.
	 * @param col Column position of the map.
	 * @return byte value representing height of the block.
	 */
	public static byte terrainCheck(byte[][] level, int row, int col)
	{
		if(row > level.length - 1 || row < 0)
			return -1;
		else if(col > level.length - 1 || col < 0)
			return -1;
		
		return level[row][col];
	}
}
