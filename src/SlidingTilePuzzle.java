import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of sliding tile puzzles.
 * 
 * @author Vincent Cicirello
 * @version CSIS 4463
 */
public class SlidingTilePuzzle extends State {
	
	
	private int[][] tiles;
	private int x; // row of blank
	private int y; //column of blank
	
	
	/**
	 * Default is the 8 puzzle.
	 */
	public SlidingTilePuzzle() {
		this(3,3);
	}
	
	/**
	 * A square shaped sliding tile puzzle (number of rows and columns are the same).
	 * 
	 * @param n Number of rows and number of columns.
	 */
	public SlidingTilePuzzle(int n) {
		this(n,n);
	}
	
	/**
	 * General sliding tile puzzles.
	 * @param n Number of rows.
	 * @param m Number of columns.
	 */
	public SlidingTilePuzzle(int n, int m) {
		tiles = new int[n][m];
		int numTiles = n*m;
		int[] permutation = new int[numTiles];
		for (int i = 0; i < numTiles; i++) {
			permutation[i] = i;
		}
		Random gen = new Random();
		for (int i = numTiles-1; i > 0; i--) {
			int which = gen.nextInt(i+1);
			if (which == i) continue;
			permutation[i] ^= permutation[which];
			permutation[which] ^= permutation[i];
			permutation[i] ^= permutation[which];
		}
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				tiles[i][j] = permutation[k];
				if (tiles[i][j]==0) {
					x = i;
					y = j;
				}
				k++;
			}
		}
	}
	
	/**
	 * General sliding tile puzzle with specified optimal path length to goal.
	 * WARNING: This is potentially very slow for large puzzle sizes or large value of optimalPathLength.
	 * This is due to how it generates a random instance with that specified path length.  Specifically,
	 * it generates the set of all instances whose optimal path length is exactly that number through BFS, 
	 * and then picks one at random.  For either large dimension values or large optimalPathLength value
	 * or both, this will either run out of memory or take an excessively long time.
	 * 
	 * @param n Number of rows.
	 * @param m Number of columns.
	 * @param optimalPathLength Length of optimal path to goal.
	 */
	public SlidingTilePuzzle(int n, int m, int optimalPathLength) {
		tiles = new int[n][m];
		int numTiles = n*m;
		int k = 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				tiles[i][j] = k;
				k++;
				if (tiles[i][j]==numTiles) {
					x = i;
					y = j;
					tiles[i][j]=0;
				}
			}
		}
		HashMap<SlidingTilePuzzle,Integer> depth = new HashMap<SlidingTilePuzzle,Integer>();
		SlidingTilePuzzle goal = new SlidingTilePuzzle(this);
		depth.put(goal, 0);
		Queue<SlidingTilePuzzle> queue = new LinkedList<SlidingTilePuzzle>();
		queue.offer(goal);
		while (!queue.isEmpty() && depth.get(queue.peek()) < optimalPathLength) {
			SlidingTilePuzzle s = queue.poll();
			int d = depth.get(s) + 1;
			Collection<State> next = s.getSuccessors();
			for (State e : next) {
				if (!depth.containsKey(e)) {
					depth.put((SlidingTilePuzzle)e,d);
					queue.offer((SlidingTilePuzzle)e);
				}
			}
		}
		if (queue.isEmpty()) throw new IllegalArgumentException("Value of optimalPathLength is too high: " + optimalPathLength);
		Random gen = new Random();
		int which = gen.nextInt(queue.size());
		for (int i = 0; i < which; i++) queue.poll();
		SlidingTilePuzzle thisOne = queue.poll();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				tiles[i][j] = thisOne.tiles[i][j];
			}
		}
		x = thisOne.x;
		y = thisOne.y;
		resetStats();
	}
	
	/**
	 * Copies a state.
	 * @param other The state to copy.
	 */
	public SlidingTilePuzzle(SlidingTilePuzzle other) {
		tiles = new int[other.tiles.length][other.tiles[0].length];
		for (int i = 0; i < other.tiles.length; i++) {
			for (int j = 0; j < other.tiles[0].length; j++) {
				tiles[i][j] = other.tiles[i][j];
			}
		}
		x = other.x;
		y = other.y;
	}

	

	@Override
	public Collection<State> getSuccessors() {
		super.getSuccessors();
		ArrayList<State> neighbors = new ArrayList<State>(4);
		
		if (x > 0) {
			SlidingTilePuzzle up = new SlidingTilePuzzle(this);
			up.tiles[x][y] ^= up.tiles[x-1][y];
			up.tiles[x-1][y] ^= up.tiles[x][y];
			up.tiles[x][y] ^= up.tiles[x-1][y];
			up.x--;
			neighbors.add(up);
		}
		
		if (x < tiles.length - 1) {
			SlidingTilePuzzle down = new SlidingTilePuzzle(this);
			down.tiles[x][y] ^= down.tiles[x+1][y];
			down.tiles[x+1][y] ^= down.tiles[x][y];
			down.tiles[x][y] ^= down.tiles[x+1][y];
			down.x++;
			neighbors.add(down);
		}
		
		if (y > 0) {
			SlidingTilePuzzle left = new SlidingTilePuzzle(this);
			left.tiles[x][y] ^= left.tiles[x][y-1];
			left.tiles[x][y-1] ^= left.tiles[x][y];
			left.tiles[x][y] ^= left.tiles[x][y-1];
			left.y--;
			neighbors.add(left);
		}
		
		if (y < tiles[0].length - 1) {
			SlidingTilePuzzle right = new SlidingTilePuzzle(this);
			right.tiles[x][y] ^= right.tiles[x][y+1];
			right.tiles[x][y+1] ^= right.tiles[x][y];
			right.tiles[x][y] ^= right.tiles[x][y+1];
			right.y++;
			neighbors.add(right);
		}
		
		return neighbors;
	}
	
	
	/**
	 * 
	 * @return A heuristic function: number of misplaced tiles.
	 */
	public HeuristicFunction getHeuristicNumMisplacedTiles() {
		class NumMisplacedTiles implements HeuristicFunction {

			@Override
			public int h(State s) {
				SlidingTilePuzzle state = (SlidingTilePuzzle)s;
				int count = 0;
				int k = 1;
				int total = state.tiles.length * state.tiles[0].length;
				for (int i = 0; i < state.tiles.length; i++) {
					for (int j = 0; j < state.tiles[0].length; j++) {
						if (state.tiles[i][j] != k && k != total) count++;
						k++;
					}
				}
				return count;
			}
			
		}
		return new NumMisplacedTiles();
	}
	
	/**
	 * 
	 * @return A heuristic function: manhattan distance
	 */
	public HeuristicFunction getHeuristicManhattanDistance() {
		class ManhattanDistance implements HeuristicFunction {

			@Override
			public int h(State s) {
				SlidingTilePuzzle state = (SlidingTilePuzzle)s;
				int distance = 0;
				for (int i = 0; i < state.tiles.length; i++) {
					for (int j = 0; j < state.tiles[0].length; j++) {
						if (state.tiles[i][j] != 0) {
							int row = (state.tiles[i][j] - 1) / state.tiles[0].length;
							int col = (state.tiles[i][j] - 1) % state.tiles[0].length;
							distance += Math.abs(row-i) + Math.abs(col-j);
						}
					}
				}
				return distance;
			}
			
		}
		return new ManhattanDistance();
	}
	
	
	

	@Override
	public int getTransitionCost(State next) {
		return 1;
	}

	@Override
	public boolean isGoalState() {
		int k = 1;
		int total = tiles.length * tiles[0].length;
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] != k) return false;
				k = (k+1)%total;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (this.getClass()!= o.getClass()) return false;
		SlidingTilePuzzle other = (SlidingTilePuzzle)o;
		if (x != other.x || y != other.y || tiles.length != other.tiles.length || tiles[0].length != other.tiles[0].length) return false;
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j] != other.tiles[i][j]) return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				s += tiles[i][j] + "\t";
			}
			s += "\n";
		}
		return s;
	}

}
