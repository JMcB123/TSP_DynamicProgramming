import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

//Main class that runs the algorithm and outputs the result
public class DynamicProgramming {
	//path to the TSP files relative to the project
	static String relativeFilePath = "/src/TSP_Files/FinalTest4.txt";
	//TSP problem file
	static File file;
	//Number of cities in the TSP
	static int vertexCount;
	//Array of Vertex objects which represent the cities in the TSP
	static Vertex[] vertices;
	//matrix used to store distances from each Vertex to every other Vertex
	static double distanceMatrix[][];
	//Dynamic Programming array to store subpaths and their optimal solutions so they only need calculating once
	static double optimalSubPaths[][];
	//Stores traceable links to be able to construct a path taken by the optimal solution using the start vertex and bitmask
	static int[][] parent;
	//Arbitrary value chosen to represent invalid/empty positions in arrays
	//i.e. in parent array, empty means no path has been stored which goes from the current bitmask and position (indexes) to another vertex
	final static int EMPTY = -1;
	//start state for the system (bitmask ..0001, meaning city1 visited and no other cities visited)
	final static int START_STATE = 1;
	
	public static void main(String[] args) throws FileNotFoundException {
		//track the time taken for the solution to be found
		long startTime = System.nanoTime();
		long endTime;
		//distance of the solution will be stored here
		double answer;
		//Array for storing all vertices not yet visited in the current path
		Vertex[] notVisited;
		//Creating the filePath string using the project path and adding the path in the project directory
		String filePath = new File("").getAbsolutePath() + relativeFilePath;
		
		//Instantiate file object with the TSP txt file
	    file = new File(filePath);
		//Read in vertices from the .txt file
		readFile(file);
		//create the distance matrix with the distances between each vertex
		initDistanceMatrix();
		//initialise the DynamicProgramming cache array
		initOptimalSubPaths();
		//initialise the parent array (used for tracking the steps taken to form a circuit
		initParent();
		//print the matrix to the console
//		printMatrix(distanceMatrix);
		//initialise notVisited array with all applicable vertices
		notVisited = initNotVisited();
		System.out.println("Calculating Best Route...");
		//Running the DynamicProgramming algorithm
		answer = solve(notVisited, vertices[0], START_STATE);
		endTime = System.nanoTime();
		
		//Output distance of solution
		System.out.println("Distance of Shortest Tour: " + answer);
		System.out.println("Route taken: ");
		//Output path of solution
		printPath();
		//Output time taken to find solution
		outputTime(startTime, endTime);
	}
	
	/*
	 * Solves the TSP problem recursively with the use of Dynamic Programming
	 * 
	 * @param notVisited: an Array of Vertex's representing all the unvisited vertices so far in the path
	 * @param currentVertex: The Vertex that the path is currently at (has travelled to)
	 * @param bitmask: An integer representing the state of which vertices have been visited such that each bit of the binary representation refers to a city.
	 * 				   so in a TSP of 4 cities, 1111 = all cities visited and 0000 = no cities visited, and 0011 = city 1 and 2 visited, but 3 and 4 not visited.
	 * 
	 */
	public static double solve(Vertex[] notVisited, Vertex vertexCurrentlyAt, int bitmask) {
		//Store the best answer to a sub problem
		double bestAnswer = Double.MAX_VALUE;		
		
		//if all vertices have been visited, add distance from current(final) vertex back to the start vertex
		if (notVisited.length == 0) {
			return distanceMatrix[vertexCurrentlyAt.getId() - 1][0];
		}
		//if the optimal subpath has already been calculated and stored, return it
		else if (optimalSubPaths[bitmask][vertexCurrentlyAt.getId() - 1] != Double.valueOf(EMPTY)) {
			return optimalSubPaths[bitmask][vertexCurrentlyAt.getId() - 1];
		}
		//Calculation of sub problems takes place here
		else {
			//Denotes which vertex the for-each loop is currently checking
			int vertexLoopCounter = 0;
			//For each vertex, check if it has been visited, if it hasn't expand the problem by visiting that city
			for (Vertex currentVertex : vertices) {
				//If vertex, i, is in the notVisited array
				if (containsVertex(Arrays.asList(notVisited), currentVertex.getId())) {
					//Array to store all vertices not visited - the vertex at i
					Vertex[] newNotVisited = new Vertex[notVisited.length - 1];
					//counter for the number of vertices added to newNotVisited (used as index)
					int numOfVerticesAdded = 0;
					//for all previously unvisited vertices
					for (Vertex currentUnvisited : notVisited) {
						//if the vertex is not the vertex being travelled to
						if (currentUnvisited.getId() != currentVertex.getId()) {
							//store the unvisited vertex in the new array
							newNotVisited[numOfVerticesAdded] = currentUnvisited;
							numOfVerticesAdded++;
						}
					}
					//answer to the subpath
					double newAnswer = distanceMatrix[vertexCurrentlyAt.getId() - 1][vertexLoopCounter] + solve(newNotVisited, currentVertex, bitmask | (1<<vertexLoopCounter));
					//if new answer is lower than previous best
					if (bestAnswer > newAnswer) {
						//set best to be the newAnswer
						bestAnswer = newAnswer;
						//create a reference in parent between the starting vertex (currentVertex), destination vertex (i) and bitmask
						//so the optimal path can be determined by tracing the references through the parent array
						parent[vertexCurrentlyAt.getId() - 1][bitmask] = vertexLoopCounter;
					}
				}
				vertexLoopCounter++;
			}
		}
		//store the optimal solution to the subpath in the dynamic programming array so it doesn't have to be calculated again
		optimalSubPaths[bitmask][vertexCurrentlyAt.getId() - 1] = bestAnswer;
		//return the cost (distance)
		return optimalSubPaths[bitmask][vertexCurrentlyAt.getId() - 1];
	}
		
	/*
	 * Read the TSP problem file to obtain the number of vertices, and store each Vertex in the vertices array
	 * 
	 * @param file: a File object referencing the location of the TSP file
	 * 
	 */
	public static void readFile(File file) throws FileNotFoundException {
		//FileReader object
		FileReader reader = new FileReader(file);
		//parsing the file for the number of cities in the TSP and storing the result in vertexCount
		vertexCount = reader.getVertexCount();
		//initialising the vertices array with the correct size to store a Vertex representation of each city in the TSP
		vertices = new Vertex[vertexCount];
		//storing the Vertex objects in the vertices array by parsing the file with the FileReader object
		vertices = reader.getVerticesFromFile(vertexCount);
	}

	/*
	 * Initialise the DistanceMatrix with the correct size and fill it
	 * with the distances from each vertex to every other vertex such that,
	 * distance from vertices[city1Index] to vertices[city2Index] = distanceMatrix[city1Index][city2Index].
	 */
	public static void initDistanceMatrix() {
		//Vertex objects to temporarily store the vertices whose distance between is being stored
		Vertex currentVertex = new Vertex();
		Vertex neighbourVertex = new Vertex();
		//initialise distanceMatrix to the correct size
		distanceMatrix = new double[vertexCount][vertexCount];
		//for each vertex in the TSP
		for (int city1Index = 0; city1Index < vertexCount; city1Index++) {
			//store information of current Vertex 
			currentVertex = vertices[city1Index];
			//loop every vertex again so distance can be stored from city1 to every city.
			for (int city2Index = 0; city2Index < vertexCount; city2Index++) {
				//if city1 is the same as city2, then set the distance to empty (can't travel to where you already are)
				if (city1Index == city2Index) {
					distanceMatrix[city1Index][city2Index] = Double.valueOf(EMPTY);
				}
				else {
					//store information of vertex being travelled to
					neighbourVertex = vertices[city2Index];
					//store the distance from city1 to city2, in distanceMatrix[city1Index][city2Index]
					distanceMatrix[city1Index][city2Index] = currentVertex.distanceTo(neighbourVertex);
				}
			}
		}
	}
	
	/*
	 * Print a matrix to the console. Primarily used to output distanceMatrix, but can be used to show other matrices too
	 * @param matrix: The 2-D double array to be ouput to console
	 */
	static void printMatrix(double[][] matrix) {
		//loop through every value stored in the matrix
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				//output each value on the same line.
				System.out.print(matrix[row][column] + "  ");
			}
			//go to the next line
			System.out.println();
			System.out.println();
		}
	}
	
	/*
	 * Initialise the DP array to be the correct size using vertexCount, then fill every value to empty
	 */
	public static void initOptimalSubPaths() {
		//initialising dp array
		optimalSubPaths = new double[1<<vertexCount][vertexCount];
		
		//loop over every value in the array
		for (int row = 0; row < optimalSubPaths.length; row++) {
			for (int column = 0; column < optimalSubPaths[column].length; column++) {
				//set the value to empty to represent the solution to this position not being calculated yet
				optimalSubPaths[row][column] = Double.valueOf(EMPTY);
			}
		}
	}
	
	/*
	 * Initialise the notVisited array and fill it with every Vertex except the starting node
	 * 
	 * @return An array with the unvisited vertices 
	 */
	public static Vertex[] initNotVisited() {
		//Won't be storing starting node as it can be considered visited, so size is vertexCount - 1.
		Vertex[] notVisited = new Vertex[vertexCount - 1];
		//loop through every vertex starting from the second vertex (as first vertex is visited)
		for (int vertexIndex = 1; vertexIndex < vertexCount; vertexIndex++) {
			//store the Vertex in notVisited
			notVisited[vertexIndex - 1] = new Vertex(vertices[vertexIndex]);
		}
		return notVisited;
	}
	
	/*
	 * Initialise the parent array and fill with empty representation
	 */
	public static void initParent() {
		//initialise with correct size
		parent = new int[vertexCount][1<<vertexCount];
		//loop over every value
		for (int row = 0; row < parent.length; row++) {
			for (int column = 0; column < parent[row].length; column++) {
				//set value to equal empty, to represent no path links being stored yet
				parent[row][column] = EMPTY;
			}
		}
	}
	
	/*
	 * Test Whether or not a list of Vertex objects contains a Vertex of id equal to the idQuery.
	 * Used to test if a Vertex of id equal to idQuery, exists in the notVisited array (converted to list via Arrays.asList())
	 *  
	 * @param list: a list of Vertex objects where every vertex will be tested to see if its id matches the idQuery
	 * @param idQuery: The id being searched for
	 * 
	 * @return TRUE: if id is found in the list. FALSE: if the id is not found.
	 */
 	public static boolean containsVertex(List<Vertex> list, int idQuery){
	    return list.stream().anyMatch(vertex -> vertex.getId() == idQuery);
	}
 	
 	/*
 	 * Trace the parent array to piece together the path taken via the subpaths and output the path string once done
 	 */
	public static void printPath() {
		//String to store and output the path taken
		String path = "";
		//loop counter to prevent "->" being added after the last vertex of the path string
		//also used to end while loop
		int counter = 0;
		//current vertex in the path (used as 1st index of parent), starting vertex is always 1 (so index 0)
		int currentVertexIndex = 0;
		//bitmask of the current state of the path, starting vertex is always 1 (so bitmask ..0001)
		int currentMask = 1;
		
		//each loop adds the next vertex travelled to in the path
		while(counter < vertexCount) {
			//add the vertex to the path string (vertex id is equal to index + 1)
		    path += (currentVertexIndex + 1) + " -> ";
			//set the new current vertex to be equal to the value stored at the array index of the currentVertex and the bitmask
		    currentVertexIndex = parent[currentVertexIndex][currentMask];
		    //bitwise operation to change the bit of the city just added to path to a 1 from a 0
		    //(i.e. if bitmask was 0101 (meaning a 4 city TSP where city 1 and 3 are visited so far)
		    //if current vertex was city 2, 0101 OR 1<<2 = 0111
		    currentMask = currentMask | (1 << currentVertexIndex);
		    //increment loop counter
		    counter++;
		};
		//Add the starting point to the end of the path
		path += vertices[0].getId();
		//Output path
		System.out.println(path);
	}
	
	/*
	 * Output the Time Taken for the solution to be found
	 * 
	 * @param startTime: Time at which the program started
	 * @param endTime: Time at which the solution was found
	 */
	public static void outputTime(long startTime, long endTime) {
		//calculate total time elapsed
		long timeTaken = endTime - startTime;
		//convert time elapsed to seconds
		double timeInSeconds = timeTaken / Math.pow(10, 9);
		//Output
		System.out.println("Time Elapsed:");
		System.out.println("-------------");
		System.out.println(timeTaken + " nanoseconds.");
		System.out.println(timeInSeconds + " seconds.");
	}
}