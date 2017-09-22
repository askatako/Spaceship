package student;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import models.Node;
import models.NodeStatus;
import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;



/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {


	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * 
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */
	@Override
	public void rescue(RescueStage state) {
		// TODO : Find the missing spaceship
		//create a hash set of the visited location
		HashSet<NodeStatus> visitedLocations = new HashSet<NodeStatus>();

		//run depth first search to find the state
		doDFS2(state, state.currentLocation(), visitedLocations);

	}


	/**
	 * returns true if visited contains all the things in heap
	 * @param heap: heap of NodeStatuses 
	 * @param visited: hash set of visited nodes
	 * @return
	 */


	public static void doDFS2(RescueStage state, Long location, HashSet<NodeStatus> visitedNodes){
		
		//makes a stack of NodeStatus
		Stack<NodeStatus> stack = new Stack<NodeStatus>();
		
		//iterate through neighbors
		for(NodeStatus ns: state.neighbors())
		stack.push(ns);
		
		//Stack is not empty
		while(stack.size() !=0){
			NodeStatus popped = stack.pop();
			if (state.foundSpaceship()){
				return;
			}
			
			//node not visited
			if (!visitedNodes.contains(popped)){
				visitedNodes.add(popped);
				state.moveTo(popped.getId());
				
				//adds neighbors of unvisited node to the heap
				Heap<NodeStatus> reachableNeighbours = new Heap<>();
				for(NodeStatus ns: state.neighbors()){
					reachableNeighbours.add(ns, ns.getPingToTarget());
				}
				
				//adds neighbors of node to the stack
				while(reachableNeighbours.size() != 0){
					stack.push(reachableNeighbours.poll());
				}

				
			}
			
		}
		
	}



	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ScramState. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth
		
		int lives = 0;

		Queue<Node> path = getNewShortestPath(state.currentNode(), state.getEarth());

		HashSet<Node> visited = new HashSet<Node>();
		
		while(path.size() != 0){

			//Current node has speed upgrade
			if(state.currentNode().hasSpeedUpgrade() ){
				state.grabSpeedUpgrade();
			}
			
			Node nextNode = path.poll();
			if(lives > 0 || !nextNode.isHostile()){
				state.moveTo(nextNode);
				
				//add current node to the visited list
				visited.add(state.currentNode());

			}else{
				
				Node n = selectUnhostileNeighbour(state.currentNode(), visited);
				
				if(n != null){
					state.moveTo(n);
					visited.add(state.currentNode());
					path = getNewShortestPath(state.currentNode(), 
							state.getEarth());
				}else{
					state.moveTo(nextNode);
				}
			}
		}

	}

	/** Returns a path of nodes of the shortest path from Node n to node d
	 * */
	public Queue<Node> getNewShortestPath(Node n, Node d){
		List<Node>pathToExit = student.Paths.shortestPath(n, d);
		pathToExit.remove(0); //removes the first node which is n

		Queue<Node> path = new LinkedList<Node>();
		for(Node nextNode: pathToExit) {
			path.add(nextNode);
		}

		return path;
	}
	
	/**Selects an unhostile neighbor of n and returns it*/
	
	public static Node selectUnhostileNeighbour(Node n, HashSet<Node> visited){
		
		for (Node neighbor: n.getNeighbors().keySet()){
			if(!neighbor.isHostile() && !visited.contains(neighbor)){
				return neighbor;
			}
		}

		return null;
	}

}