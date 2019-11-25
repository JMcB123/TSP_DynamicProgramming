//A point (city) in the TSP problem
public class Vertex {
	//id number of the city
	private int id;
	//x-coordinate of the city
	private int x_coordinate;
	//y-coordinate of the city
	private int y_coordinate;
	
	//Constructor
	public Vertex() {
		this.id = 0;
		this.x_coordinate = 0;
		this.y_coordinate = 0;
	}

	//Constructor to deep copy a vertex object
	public Vertex(Vertex other) {
		this.id = other.getId();
		this.x_coordinate = other.getX_Coordinate();
		this.y_coordinate = other.getY_Coordinate();
	}
	
	//Constructor
	public Vertex(int id, int x_coord, int y_coord) {
		this.id = id;
		this.x_coordinate = x_coord;
		this.y_coordinate = y_coord;
	}
	
	// Get and Set Id
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	
	// Get and Set X coordinate
	public int getX_Coordinate() { return this.x_coordinate; }
	public void setX(int x) { this.x_coordinate = x; }

	// Get and Set Y coordinate
	public int getY_Coordinate() { return this.y_coordinate; }
	public void setY(int y) { this.y_coordinate = y; }
	
	/*
	 * Calculate the distance between two vertices and return it
	 * 
	 * @param vertex: the vertex to which the distance is being calculated
	 * 
	 * @return distance between the vertices
	 */
	public double distanceTo(Vertex vertex) {
		//Distance in x plane equals the difference in the x-values of both vertices
		int xDistance = this.getX_Coordinate() - vertex.getX_Coordinate();
		//Distance in y plane equals the difference in the y-values of both vertices
		int yDistance = this.getY_Coordinate() - vertex.getY_Coordinate();
		//Pythagoras Theorem
		double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
		
		return distance;
	}
}
