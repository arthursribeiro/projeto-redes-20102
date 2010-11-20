
public class VectorPair implements Comparable<VectorPair> {

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
	
    @Override
	public String toString() {
		return "<" + id + ";" + distance + ">";
	}

	@Override
	public int compareTo(VectorPair pair) {
		return (id - pair.getId()) + (distance - pair.getDistance());
	}

}
