import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	final static int INSTANCES = 10;
	final static int MAX_DISTANCE = 30;


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

	public static void main(String[] args) throws IOException {

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

		double[][] expandedNodes = new double[MAX_DISTANCE/2][7];
		double[][] maxNodes = new double[MAX_DISTANCE/2][7];
		double[][] cpuTime = new double[MAX_DISTANCE/2][7];

		do{
			double[][] idAverages = new double[3][MAX_DISTANCE/2];
			double[][] ucsAverages = new double[3][MAX_DISTANCE/2];
			double[][] aStarMisAverages = new double[3][MAX_DISTANCE/2];
			double[][] aStarManAverages = new double[3][MAX_DISTANCE/2];
			double[][] idaStarMisAverages = new double[3][MAX_DISTANCE/2];
			double[][] idaStarManAverages = new double[3][MAX_DISTANCE/2];

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

				start = bean.getCurrentThreadCpuTime();
				solution = problem.IDAStarSearch(puzzle.getHeuristicNumMisplacedTiles());
				totalTime = bean.getCurrentThreadCpuTime() - start;
				idaStarMisAverages[0][arrayIndex] += State.getNumExpandedStates();
				idaStarMisAverages[1][arrayIndex] += problem.getMaxNodeCount();
				idaStarMisAverages[2][arrayIndex] += totalTime/1000000000.0;
				State.resetStats();

				start = bean.getCurrentThreadCpuTime();
				solution = problem.IDAStarSearch(puzzle.getHeuristicManhattanDistance());
				totalTime = bean.getCurrentThreadCpuTime() - start;
				idaStarManAverages[0][arrayIndex] += State.getNumExpandedStates();
				idaStarManAverages[1][arrayIndex] += problem.getMaxNodeCount();
				idaStarManAverages[2][arrayIndex] += totalTime/1000000000.0;
				State.resetStats();
			}

			expandedNodes[arrayIndex][0] = optimalLength; 
			expandedNodes[arrayIndex][1] = (idAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][2] = (ucsAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][3] = (aStarMisAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][4] = (aStarManAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][5] = (idaStarMisAverages[0][arrayIndex] / INSTANCES);
			expandedNodes[arrayIndex][6] = (idaStarManAverages[0][arrayIndex] / INSTANCES); 

			maxNodes[arrayIndex][0] = optimalLength; 
			maxNodes[arrayIndex][1] = (idAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][2] = (ucsAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][3] = (aStarMisAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][4] = (aStarManAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][5] = (idaStarMisAverages[1][arrayIndex] / INSTANCES);
			maxNodes[arrayIndex][6] = (idaStarManAverages[1][arrayIndex] / INSTANCES);

			cpuTime[arrayIndex][0] = optimalLength; 
			cpuTime[arrayIndex][1] = (idAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][2] = (ucsAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][3] = (aStarMisAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][4] = (aStarManAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][5] = (idaStarMisAverages[2][arrayIndex] / INSTANCES);
			cpuTime[arrayIndex][6] = (idaStarManAverages[2][arrayIndex] / INSTANCES);

			optimalLength +=2;
			System.out.println("Generating " + INSTANCES + " puzzles of " + optimalLength + " length...");

		}while(optimalLength <= MAX_DISTANCE);

		printTables(expandedNodes, maxNodes, cpuTime);
	}

	private static void printTables(double[][] expandedNodes, double[][] maxNodes, double[][] cpuTime) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("lastRun.txt")));
		String nodeFormat = "| %-12.1f | %-15.2f | %-15.2f | %-15.2f | %-15.2f | %-15.2f | %-15.2f |%n";
		String cpuFormat = "| %-12.1f | %-15.10f | %-15.10f | %-15.10f | %-15.10f | %-15.10f | %-15.10f |%n";

		bw.write(String.format("Tyler Sefcik and Ryan Ceresani%n"));
		bw.write(String.format("AI HW #8 including IDA*%n%n%n"));
		bw.write(String.format("Number of puzzle instances = " + INSTANCES +"%n%n"));
		bw.write(String.format("+======Average Expanded Nodes======+%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		bw.write(String.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |   IDAStarMis    |   IDAStarMan    |%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		for (int i = 0; i < expandedNodes.length; i++) {
			if(expandedNodes[i][1] == 0){
				nodeFormat = "| %-12.1f | %-15s | %-15s | %-15.2f | %-15.2f | %-15.2f | %-15.2f |%n";
				bw.write(String.format(nodeFormat, expandedNodes[i][0], "-", 
						"-", expandedNodes[i][3], expandedNodes[i][4], expandedNodes[i][5], expandedNodes[i][6]));
			} else {bw.write(String.format(nodeFormat, expandedNodes[i][0], expandedNodes[i][1], 
					expandedNodes[i][2], expandedNodes[i][3], expandedNodes[i][4], expandedNodes[i][5], expandedNodes[i][6]));
			}
			bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		}

		System.out.println();
		bw.write(String.format("+======Average Max Memory Nodes======+%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		bw.write(String.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |   IDAStarMis    |   IDAStarMan    |%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		for (int i = 0; i < maxNodes.length; i++) {
			if(maxNodes[i][1] == 0){
				nodeFormat = "| %-12.1f | %-15s | %-15s | %-15.2f | %-15.2f | %-15.2f | %-15.2f |%n";
				bw.write(String.format(nodeFormat, maxNodes[i][0], "-", 
						"-", maxNodes[i][3], maxNodes[i][4], maxNodes[i][5], maxNodes[i][6]));
			} else {
				bw.write(String.format(nodeFormat, maxNodes[i][0], maxNodes[i][1], maxNodes[i][2],
						maxNodes[i][3], maxNodes[i][4], maxNodes[i][5], maxNodes[i][6]));
			}
			bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		}


		System.out.println();
		bw.write(String.format("+======Average CPU Time======+%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		bw.write(String.format("| Path Length  |        ID       |       UCS       |    AStarMis     |    AStarMan     |   IDAStarMis    |   IDAStarMan    |%n"));
		bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		for (int i = 0; i < cpuTime.length; i++) {
			if(maxNodes[i][1] == 0){
				cpuFormat = "| %-12.1f | %-15s | %-15s | %-15.10f | %-15.10f | %-15.10f | %-15.10f |%n";
				bw.write(String.format(cpuFormat, cpuTime[i][0], "-", 
						"-", cpuTime[i][3], cpuTime[i][4], cpuTime[i][5], cpuTime[i][6]));
			} else {
				bw.write(String.format(cpuFormat, cpuTime[i][0], cpuTime[i][1], cpuTime[i][2],
						cpuTime[i][3], cpuTime[i][4], cpuTime[i][5], cpuTime[i][6]));
			}
			bw.write(String.format("+--------------+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+%n"));
		}
		bw.close();
	}
}
