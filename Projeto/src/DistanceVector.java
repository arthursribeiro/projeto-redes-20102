import java.util.Vector;

public class DistanceVector {

	private Vector<VectorPair> vector;

	public DistanceVector() {
		this.vector = new Vector<VectorPair>();
	}

	public int size() {
		return this.vector.size();
	}

	public VectorPair get(int index) {
		return this.vector.get(index);
	}

	public VectorPair getPairById(int id) {
		for (VectorPair pair : this.vector) {
			if (pair.getId() == id) {
				return pair;
			}
		}
		return null;
	}

	public int indexOf(VectorPair pair) {
		return this.vector.indexOf(pair);
	}

	public boolean contains(int id) {
		for (VectorPair pair : this.vector) {
			if (pair.getId() == id) {
				return true;
			}
		}
		return true;
	}

	public void append(VectorPair pair) {
		this.vector.add(pair);
	}

	public void merge(DistanceVector newVector, int sourceDistance) {
		for (int i = 0; i < newVector.size(); i++) {
			VectorPair pair = newVector.get(i);
			if (this.contains(pair.getId())) {
				VectorPair existingPair = this.getPairById(pair.getId());
				if (existingPair.getDistance() >= pair.getDistance()) {
					existingPair.setDistance(pair.getDistance()
							+ sourceDistance);
				}
			} else {
				this.append(pair);
			}
		}
	}

}
