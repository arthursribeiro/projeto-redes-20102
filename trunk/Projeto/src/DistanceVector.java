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
		for (int i = 0; i < newVector.size(); i++) {
			VectorPair pair = newVector.get(i);
			if (pair.getId() == owner.getId()) {
				if (!this.contains(source)) {
					this.append(new VectorPair(source, sourceDistance));
					changed = true;
				}
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
