import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;



/**
 * A Search Problem representation.
 * 
 * This version includes my solutions to parts of prior assignments.  I've also added some code that tracks
 * memory usage of the search algorithms (computed as maximum number of search states in memory).
 * 
 *  @author Vincent Cicirello
 *  @version CSIS 4463
 */
public class SearchProblem {

	final private State start;
	final private State goal;
	
	/**
	 * Use this constructor for search problems where
	 * there is a single start state and for which
	 * either there are multiple potential goal states
	 * or for which it is more convenient to not explicitly
	 * specify the goal state.
	 * 
	 * @param start The start state.
	 */
	public SearchProblem(State start) {
		this.start = start;
		goal = null;
	}
	
	/**
	 * Constructs a search problem with specified start and
	 * goal states.
	 * 
	 * @param start  The start state
	 * @param goal  The goal state
	 */
	public SearchProblem(State start, State goal) {
		this.start = start;
		this.goal = goal;
	}
	
	/*
	 * Helper method for checking if a State is the goal.
	 */
	private boolean goalCheck(State s) {
		return goal != null && s.equals(goal) || goal == null && s.isGoalState();
	}
	
		
	
	/**
	 * Uniform Cost Search
	 * 
	 * @return A SearchNode containing the Goal state and such that following the
	 * backpointers will enable recovering the path from the Start to the Goal.
	 * Obviously, the backpointers will give us that path backwards.
	 * Returns null if the Goal was not found.
	 */
	public SearchNode uniformCostSearch() {
		
		// not logically necessary.  here to track memory usage
		maxNodesInMemory = 1;
		State.resetStats();
		
		// HOMEWORK SOLUTION
		
		if (goalCheck(start)) 
			return new SearchNode(start);
		
		class MyComparator implements Comparator<SearchNode> {
			@Override
			public int compare(SearchNode o1, SearchNode o2) {
				return o1.getG()-o2.getG();
			}
		}
		PQ<SearchNode> frontier = new PQ<SearchNode>(new MyComparator());
		frontier.offer(new SearchNode(start));
		HashMap<State,Integer> generated = new HashMap<State,Integer>();
		generated.put(start,0);
		
		while (!frontier.isEmpty()) {
			SearchNode s = frontier.poll();
			if (goalCheck(s.getState())) {
				maxNodesInMemory = generated.size();
				return s;
			}
			Collection<State> succs = s.getState().getSuccessors();
			for (State e : succs) {
				if (!generated.containsKey(e)) {
					SearchNode eS = new SearchNode(e,s);
					generated.put(e, eS.getG());
					frontier.offer(eS);
				} else {
					SearchNode eS = new SearchNode(e,s);
					if (eS.getG() < generated.get(e)) {
						generated.put(e, eS.getG());
						frontier.offer(eS);
					}
				}
			}
		}
		maxNodesInMemory = generated.size();
		return null;
		
		
	}

	
	
	/**
	 * Depth First Search (path checking DFS)
	 * 
	 * @return A SearchNode containing the Goal state and such that following the
	 * backpointers will enable recovering the path from the Start to the Goal.
	 * Obviously, the backpointers will give us that path backwards.
	 * Returns null if the Goal was not found.
	 */
	public SearchNode dfs() {
		State.resetStats();
		return depthLimitedDFS(Integer.MAX_VALUE - 1);
	}
	
	/*
	 * Helper method for path-checking DFS.  Checks if a State is already in the current path.
	 */
	private boolean isOnPath(SearchNode pathEnd, State s) {
		
		while (pathEnd != null) {
			if (pathEnd.getState().equals(s)) return true;
			pathEnd = pathEnd.getBackpointer();
		}
		return false;
	}
	
	// field used to pass info from depthLimitedDFS to iterativeDeepening on whether the search space contains
	// anything beyond the prior depth limit
	private boolean didLimit;
	
	/**
	 * Depth Limited DFS
	 * 
	 * @param limit Limit on how deep into the search space the search goes
	 *  
	 * @return A SearchNode containing the Goal state and such that following the
	 * backpointers will enable recovering the path from the Start to the Goal.
	 * Obviously, the backpointers will give us that path backwards.
	 * Returns null if the Goal was not found.
	 */
	public SearchNode depthLimitedDFS(int limit) {
		// not logically necessary.  here to track memory usage
		maxNodesInMemory = 1;
				
		didLimit = false;
		if (goalCheck(start)) 
			return new SearchNode(start);
		
		Stack<SearchNode> frontier = new Stack<SearchNode>();
		frontier.push(new SearchNode(start));
		
		while (!frontier.isEmpty()) {
			SearchNode s = frontier.pop();
			int pathLength = s.getPathLengthToNode();
			if (pathLength >= limit) {
				didLimit = true;
				continue;
			}
			Collection<State> succs = s.getState().getSuccessors();
			for (State e : succs) {
				if (goalCheck(e)) {
					maxNodesInMemory = Math.max(maxNodesInMemory, frontier.size()+1+pathLength);
					return new SearchNode(e, s);
				}
				if (!isOnPath(s,e)) {
					frontier.push(new SearchNode(e, s));
					maxNodesInMemory = Math.max(maxNodesInMemory, frontier.size()+pathLength);
				}
			}
		}
		return null;
	}
	
	/**
	 * Iterative Deepening Search
	 * 
	 * @return A SearchNode containing the Goal state and such that following the
	 * backpointers will enable recovering the path from the Start to the Goal.
	 * Obviously, the backpointers will give us that path backwards.
	 * Returns null if the Goal was not found.
	 */
	public SearchNode iterativeDeepeningSearch() {
		long localNodeCount = 0;
		State.resetStats();
		didLimit = true;
		
		for (int L = 1; didLimit; L++) {
			SearchNode s = depthLimitedDFS(L);
			localNodeCount = Math.max(localNodeCount, maxNodesInMemory);
			if (s != null) {
				maxNodesInMemory = localNodeCount;
				return s;
			}
		}
		maxNodesInMemory = localNodeCount;
		return null;
	}
	
	/**
	 * A* Search.
	 * 
	 * @param h A heuristic function. 
	 * @return A SearchNode containing the Goal state and such that following the
	 * backpointers will enable recovering the path from the Start to the Goal.
	 * Obviously, the backpointers will give us that path backwards.
	 * Returns null if the Goal was not found.
	 */	
	public SearchNode AStarSearch(HeuristicFunction h) {
		
		// not logically necessary.  here to track memory usage
		maxNodesInMemory = 1;
		State.resetStats();
		
		if (goalCheck(start)) 
			return new SearchNode(start);
		
		// A* needs f values for the priority queue.
		// This subclass of SearchNode provides that.  Inner class since only needed here.
		class AStarNode extends SearchNode {
			private int f;
			public AStarNode(State state) {
				super(state);
				f = getG()+h.h(state);
			}
			public AStarNode(State state, AStarNode back) {
				super(state,back);
				f = getG()+h.h(state);
			}
			public int getF() { return f; }
		}
		
		// Comparator which compares the f values.  Needed for the priority queue.
		class MyComparator implements Comparator<AStarNode> {
			@Override
			public int compare(AStarNode o1, AStarNode o2) {
				return o1.getF()-o2.getF();
			}
		}
		
		PQ<AStarNode> frontier = new PQ<AStarNode>(new MyComparator());
		frontier.offer(new AStarNode(start));
		HashMap<State,Integer> generated = new HashMap<State,Integer>();
		generated.put(start,h.h(start));
		
		while (!frontier.isEmpty()) {
			AStarNode s = frontier.poll();
			if (goalCheck(s.getState())) {
				maxNodesInMemory = generated.size();
				return s;
			}
			Collection<State> succs = s.getState().getSuccessors();
			for (State e : succs) {
				if (!generated.containsKey(e)) {
					AStarNode eS = new AStarNode(e,s);
					generated.put(e, eS.getF());
					frontier.offer(eS);
				} else {
					AStarNode eS = new AStarNode(e,s);
					if (eS.getF() < generated.get(e)) {
						generated.put(e, eS.getF());
						frontier.offer(eS);
					}
				}
			}
		}
		maxNodesInMemory = generated.size();
		return null;
	}
	
	/**
	 * Gets the maximum number of nodes that the last executed search had in memory.
	 * @return
	 */
	public long getMaxNodeCount() {
		return maxNodesInMemory;
	}
	
	
	private long maxNodesInMemory;
	
	
	
	
	
}
