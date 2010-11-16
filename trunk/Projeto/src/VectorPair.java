public class VectorPair {

	private int id;
	private int distance;

	public VectorPair(int id, int distance) {
		this.id = id;
		this.distance = distance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public String toString() {
		return "<" + id + ";" + distance + ">";
	}

}
