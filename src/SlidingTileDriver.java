import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;

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

	final static int INSTANCES = 4;
	final static int MAX_DISTANCE = 18;


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
		int optimalLength = 2;

		SearchNode solution;
		SlidingTilePuzzle puzzle;
		SearchProblem problem;
		long start;
		long totalTime;

		ThreadMXBean bean = ManagementFactory.getThreadMXBean();

		double[][] expandedNodes = new double[MAX_DISTANCE/2][5];
		double[][] maxNodes = new double[MAX_DISTANCE/2][5];
		double[][] cpuTime = new double[MAX_DISTANCE/2][5];

		do{
			double[][] idAverages = new double[3][MAX_DISTANCE/2];
			double[][] ucsAverages = new double[3][MAX_DISTANCE/2];
			double[][] aStarMisAverages = new double[3][MAX_DISTANCE/2];
			double[][] aStarManAverages = new double[3][MAX_DISTANCE/2];

			int arrayIndex = optimalLength/2 - 1;

			for (int i = 0; i < INSTANCES; i++) {
				puzzle = new SlidingTilePuzzle(3,3,optimalLength);
				problem = new SearchProblem(puzzle);

				if(optimalLength <= 16){
					start = bean.getCurrentThreadCpuTime();
					solution = problem.iterativeDeepeningSearch();
					totalTime = bean.getCurrentThreadCpuTime() - start;
					idAverages[0][arrayIndex] += State.getNumExpandedStates();
					idAverages[1][arrayIndex] += problem.getMaxNodeCount();
					idAverages[2][arrayIndex] += totalTime/1000000000.0;
					State.resetStats();

					start = bean.getCurrentThreadCpuTime();
					solution = problem.uniformCostSearch();
					totalTime = bean.getCurrentThreadCpuTime() - start;
					ucsAverages[0][arrayIndex] += State.getNumExpandedStates();
					ucsAverages[1][arrayIndex] += problem.getMaxNodeCount();
					ucsAverages[2][arrayIndex] += totalTime/1000000000.0;
					State.resetStats();
				}

				start = bean.getCurrentThreadCpuTime();
				solution = problem.AStarSearch(puzzle.getHeuristicNumMisplacedTiles());
				totalTime = bean.getCurrentThreadCpuTime() - start;
				aStarMisAverages[0][arrayIndex] += State.getNumExpandedStates();
				aStarMisAverages[1][arrayIndex] += problem.getMaxNodeCount();
				aStarMisAverages[2][arrayIndex] += totalTime/1000000000.0;
				State.resetStats();

				start = bean.getCurrentThreadCpuTime();
				solution = problem.AStarSearch(puzzle.getHeuristicManhattanDistance());
				totalTime = bean.getCurrentThreadCpuTime() - start;
				aStarManAverages[0][arrayIndex] += State.getNumExpandedStates();
				aStarManAverages[1][arrayIndex] += problem.getMaxNodeCount();
				aStarManAverages[2][arrayIndex] += totalTime/1000000000.0;
				State.resetStats();
			}

			expandedNodes[arrayIndex][0] = optimalLength; 
			expandedNodes[arrayIndex][1] = (idAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][2] = (ucsAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][3] = (aStarMisAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][4] = (aStarManAverages[0][arrayIndex] / INSTANCES); 

			maxNodes[arrayIndex][0] = optimalLength; 
			maxNodes[arrayIndex][1] = (idAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][2] = (ucsAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][3] = (aStarMisAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][4] = (aStarManAverages[1][arrayIndex] / INSTANCES);

			cpuTime[arrayIndex][0] = optimalLength; 
			cpuTime[arrayIndex][1] = (idAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][2] = (ucsAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][3] = (aStarMisAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][4] = (aStarManAverages[2][arrayIndex] / INSTANCES);

			optimalLength +=2;
			System.out.println("Starting Optimal length gen " + optimalLength);

		}while(optimalLength <= MAX_DISTANCE);

		printTables(expandedNodes, maxNodes, cpuTime);

		// After constructing a SlidingTilePuzzle object above, construct a SearchProblem with it as the start state.

		// Needed to time code.



		//		System.out.println("Iterative Deepening");
		//		long start = bean.getCurrentThreadCpuTime();
		//		SearchNode solution = problem.iterativeDeepeningSearch();
		//		long totalTime = bean.getCurrentThreadCpuTime() - start;
		//		System.out.println("Time: " + (totalTime/1000000000.0));
		//		System.out.println("Expanded: " + State.getNumExpandedStates());
		//		System.out.println("Memory: " + problem.getMaxNodeCount());
		//
		//		if (solution != null) {
		//			System.out.println("Path length: " + solution.getPathLengthToNode());
		//			System.out.println("Path cost: " + solution.getG());
		//			printSolutionPath(solution);
		//		} else {
		//			System.out.println("No solution found");
		//		}
		//
		//
		//
		//		System.out.println("Uniform Cost Search");
		//		start = bean.getCurrentThreadCpuTime();
		//		solution = problem.uniformCostSearch();
		//		totalTime = bean.getCurrentThreadCpuTime() - start;
		//		System.out.println("Time: " + (totalTime/1000000000.0));
		//		System.out.println("Expanded: " + State.getNumExpandedStates());
		//		System.out.println("Memory: " + problem.getMaxNodeCount());
		//
		//		if (solution != null) {
		//			System.out.println("Path length: " + solution.getPathLengthToNode());
		//			System.out.println("Path cost: " + solution.getG());
		//			printSolutionPath(solution);
		//		} else {
		//			System.out.println("No solution found");
		//		}
		//
		//
		//		System.out.println("A* Search: Num Misplaced Tiles");
		//		start = bean.getCurrentThreadCpuTime();
		//		solution = problem.AStarSearch(puzzle.getHeuristicNumMisplacedTiles());
		//		totalTime = bean.getCurrentThreadCpuTime() - start;
		//		System.out.println("Time: " + (totalTime/1000000000.0));
		//		System.out.println("Expanded: " + State.getNumExpandedStates());
		//		System.out.println("Memory: " + problem.getMaxNodeCount());
		//
		//		if (solution != null) {
		//			System.out.println("Path length: " + solution.getPathLengthToNode());
		//			System.out.println("Path cost: " + solution.getG());
		//			printSolutionPath(solution);
		//		} else {
		//			System.out.println("No solution found");
		//		}
		//
		//
		//		System.out.println("A* Search: Manhattan Distance");
		//		start = bean.getCurrentThreadCpuTime();
		//		solution = problem.AStarSearch(puzzle.getHeuristicManhattanDistance());
		//		totalTime = bean.getCurrentThreadCpuTime() - start;
		//		System.out.println("Time: " + (totalTime/1000000000.0));
		//		System.out.println("Expanded: " + State.getNumExpandedStates());
		//		System.out.println("Memory: " + problem.getMaxNodeCount());
		//
		//		if (solution != null) {
		//			System.out.println("Path length: " + solution.getPathLengthToNode());
		//			System.out.println("Path cost: " + solution.getG());
		//			printSolutionPath(solution);
		//		} else {
		//			System.out.println("No solution found");
		//		}

	}

	private static void printTables(double[][] expandedNodes, double[][] maxNodes, double[][] cpuTime) {
		String nodeFormat = "| %-12.1f | %-15.2f | %-15.2f | %-15.2f | %-15.2f |%n";
		String cpuFormat = "| %-12.1f | %-15.10f | %-15.10f | %-15.10f | %-15.10f |%n";

		System.out.println("+======Expanded Nodes======+");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		System.out.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |%n");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		for (int i = 0; i < expandedNodes.length; i++) {
			if(expandedNodes[i][1] == 0){
				nodeFormat = "| %-12.1f | %-15s | %-15s | %-15.2f | %-15.2f |%n";
				System.out.format(nodeFormat, expandedNodes[i][0], "-", 
						"-", expandedNodes[i][3], expandedNodes[i][4]);
			} else {System.out.format(nodeFormat, expandedNodes[i][0], expandedNodes[i][1], 
					expandedNodes[i][2], expandedNodes[i][3], expandedNodes[i][4]);
			}
			System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		}

		System.out.println();
		System.out.println("+======Max Memory Nodes======+");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		System.out.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |%n");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		for (int i = 0; i < maxNodes.length; i++) {
			if(maxNodes[i][1] == 0){
				nodeFormat = "| %-12.1f | %-15s | %-15s | %-15.2f | %-15.2f |%n";
				System.out.format(nodeFormat, maxNodes[i][0], "-", 
						"-", maxNodes[i][3], maxNodes[i][4]);
			} else {
				System.out.format(nodeFormat, maxNodes[i][0], maxNodes[i][1], maxNodes[i][2],
						maxNodes[i][3], maxNodes[i][4]);
			}
			System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		}


		System.out.println();
		System.out.println("+======CPU Time======+");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		System.out.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |%n");
		System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		for (int i = 0; i < cpuTime.length; i++) {
			if(maxNodes[i][1] == 0){
				cpuFormat = "| %-12.1f | %-15s | %-15s | %-15.10f | %-15.10f |%n";
				System.out.format(cpuFormat, cpuTime[i][0], "-", 
						"-", cpuTime[i][3], cpuTime[i][4]);
			} else {
				System.out.format(cpuFormat, cpuTime[i][0], cpuTime[i][1], cpuTime[i][2],
						cpuTime[i][3], cpuTime[i][4]);
			}
			System.out.format("+--------------+-----------------+-----------------+-----------------+-----------------+%n");
		}

	}
}
