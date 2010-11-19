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
		return false;
	}

	public void append(VectorPair pair) {
		this.vector.add(pair);
	}
	
	public void removeById(int id) {
		VectorPair toRemove = this.getPairById(id);
		if (toRemove != null) {
			this.vector.remove(toRemove);
		}
	}

	public boolean merge(DistanceVector newVector, int source, int sourceDistance,
			Node owner) {
		boolean changed = false;
		System.out.println("New vector: " + newVector);
		for (int i = 0; i < newVector.size(); i++) {
			VectorPair pair = newVector.get(i);
			System.out.println("Pair: " + pair);
			if (owner.isNeighbor(pair.getId())) {
				System.out.println("neighbor... " + pair);
				continue;
			}
			if (pair.getId() == owner.getId()) {
				continue;
			}
			if (this.contains(pair.getId())) {
				VectorPair existingPair = this.getPairById(pair.getId());
				if (pair.getDistance() + sourceDistance < existingPair
						.getDistance()) {
					existingPair.setDistance(pair.getDistance()
							+ sourceDistance);
					changed = true;
				}
			} else {
				System.out.println("else " + newVector);
				this.append(new VectorPair(pair.getId(), pair.getDistance()
						+ sourceDistance));
				changed = true;
			}
		}
		return changed;
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < this.size(); i++) {
			result += this.vector.get(i);
			if (i < this.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

}
