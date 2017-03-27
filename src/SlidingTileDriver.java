import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

/**
 * Example driver to demonstrate SlidingTilePuzzle constructor, etc.
 * 
 * @author 
 *
 */
public class SlidingTileDriver {

	/**
	 * Prints (to System.out) the path from the start to the goal implied by the backpointers.
	 * @param goal The end of the path that must be printed.
	 */
	public static void printSolutionPath(SearchNode goal) {
		ArrayList<State> states = new ArrayList<State>();
		while (goal != null) {
			states.add(goal.getState());
			goal = goal.getBackpointer();
		}
		for (int i = states.size()-1; i >= 0; i--) {
			System.out.println(states.get(i));
		}
	}
	
	public static void main(String[] args) {
		
		// The constructor to SlidingTilePuzzle takes 3 parameters.
		// The first 2 are the dimensions (number of rows and columns), so for the 8-puzzle, you'd use 3,3.
		// For the 15 puzzle, you'd use 4,4.  It's not limited to square puzzles.
		// The 3rd parameter is used to control the difficulty.  Specifically, it is the length of the shortest path.
		// The example below will give you a start state that is 12 steps away from the goal state.
		SlidingTilePuzzle puzzle = new SlidingTilePuzzle(3,3,12);
		
		// After constructing a SlidingTilePuzzle object above, construct a SearchProblem with it as the start state.
		SearchProblem problem = new SearchProblem(puzzle);
				
		// Needed to time code.
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();	
		
		
		
		System.out.println("Iterative Deepening");
		long start = bean.getCurrentThreadCpuTime();
		SearchNode solution = problem.iterativeDeepeningSearch();
		long totalTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Time: " + (totalTime/1000000000.0));
		System.out.println("Expanded: " + State.getNumExpandedStates());
		System.out.println("Memory: " + problem.getMaxNodeCount());

		if (solution != null) {
			System.out.println("Path length: " + solution.getPathLengthToNode());
			System.out.println("Path cost: " + solution.getG());
			printSolutionPath(solution);
		} else {
			System.out.println("No solution found");
		}
		
		
		
		System.out.println("Uniform Cost Search");
		start = bean.getCurrentThreadCpuTime();
		solution = problem.uniformCostSearch();
		totalTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Time: " + (totalTime/1000000000.0));
		System.out.println("Expanded: " + State.getNumExpandedStates());
		System.out.println("Memory: " + problem.getMaxNodeCount());

		if (solution != null) {
			System.out.println("Path length: " + solution.getPathLengthToNode());
			System.out.println("Path cost: " + solution.getG());
			printSolutionPath(solution);
		} else {
			System.out.println("No solution found");
		}
				
		
		System.out.println("A* Search: Num Misplaced Tiles");
		start = bean.getCurrentThreadCpuTime();
		solution = problem.AStarSearch(puzzle.getHeuristicNumMisplacedTiles());
		totalTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Time: " + (totalTime/1000000000.0));
		System.out.println("Expanded: " + State.getNumExpandedStates());
		System.out.println("Memory: " + problem.getMaxNodeCount());

		if (solution != null) {
			System.out.println("Path length: " + solution.getPathLengthToNode());
			System.out.println("Path cost: " + solution.getG());
			printSolutionPath(solution);
		} else {
			System.out.println("No solution found");
		}

		
		System.out.println("A* Search: Manhattan Distance");
		start = bean.getCurrentThreadCpuTime();
		solution = problem.AStarSearch(puzzle.getHeuristicManhattanDistance());
		totalTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Time: " + (totalTime/1000000000.0));
		System.out.println("Expanded: " + State.getNumExpandedStates());
		System.out.println("Memory: " + problem.getMaxNodeCount());

		if (solution != null) {
			System.out.println("Path length: " + solution.getPathLengthToNode());
			System.out.println("Path cost: " + solution.getG());
			printSolutionPath(solution);
		} else {
			System.out.println("No solution found");
		}

	}
}
