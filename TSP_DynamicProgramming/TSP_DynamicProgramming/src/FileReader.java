import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//Parses the TSP file and returns all of its information
public class FileReader {
	//File object to store the TSP file
	private File file;
	
	//Constructor
	public FileReader(File file) {
		this.file = file;
	}

	/*
	 * Parse the file to retrieve all of the cities and their information and store them in Vertex objects
	 * 
	 * @param vertexCount: number of cities(vertices) in the TSP
	 * @return Vertex array containing each city from the TSP
	 */
	public Vertex[] getVerticesFromFile(int vertexCount) throws FileNotFoundException {
		Scanner fileParser = new Scanner(this.file);
		//initialise vertices with correct size
		Vertex[] vertices = new Vertex[vertexCount];
		Vertex currentPoint;
		//ID number of the city currently being read by the scanner
		int currentId;
		//X coordinate of the city currently being read by the scanner
		int currentX_coordinate;
		//Y coordinate of the city currently being read by the scanner
		int currentY_coordinate;
		//count the iterations of the while loop
		int counter = 0;
		
		//loop while there are still lines (cities) in the file that haven't been parsed
		while (fileParser.hasNextLine()) {
			//Update the ID, X coordinate and Y coordinate of the current city
			currentId = Integer.parseInt(fileParser.next());
			currentX_coordinate = Integer.parseInt(fileParser.next());
			currentY_coordinate = Integer.parseInt(fileParser.next());
			//Create a Vertex object with the current city's data
			currentPoint = new Vertex(currentId, currentX_coordinate, currentY_coordinate);
			//Add the Vertex to the vertices array
			vertices[counter] = currentPoint;
			//increment the loop counter
			counter++;
			//prevent empty line at the end of a file from triggering an error 
			if (fileParser.hasNextLine()) {
				fileParser.nextLine();
			}
		}
		//close the scanner stream
		fileParser.close();
		return vertices;
	}
	
	/*
	 * Parse the file and for every non-empty line, increment the vertexCount
	 * 
	 * @return the number of cities in the TSP
	 */
	public int getVertexCount() throws FileNotFoundException {
		//Create the Scanner object with the TSP data file
		Scanner fileParser = new Scanner(this.file);
		//Initialise vertexCount
		int vertexCount = 0;
		
		//loop while there are still lines (cities) in the file that haven't been parsed
		while (fileParser.hasNextLine()) {
			//increment vertexCount
			vertexCount++;
			//go to the next line (city) in the file
			fileParser.nextLine();
		}
		
		//file has been parsed so close the scanner
		fileParser.close();
		//return the number of vertices in the file
		return vertexCount;
	}
}
