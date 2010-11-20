import java.util.ArrayList;

public class DistanceVector {

	private ArrayList<VectorPair> list;

	public DistanceVector() {
		this.list = new ArrayList<VectorPair>();
	}

	public int size() {
		return this.list.size();
	}

	public VectorPair get(int index) {
		return this.list.get(index);
	}

	public VectorPair getPairById(int id) {
		for (VectorPair pair : this.list) {
			if (pair.getId() == id) {
				return pair;
			}
		}
		return null;
	}

	public int indexOf(VectorPair pair) {
		return this.list.indexOf(pair);
	}

	public boolean contains(int id) {
		for (VectorPair pair : this.list) {
			if (pair.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public void append(VectorPair pair) {
		this.list.add(pair);
	}

	public void removeById(int id) {
		VectorPair toRemove = this.getPairById(id);
		if (toRemove != null) {
			this.list.remove(toRemove);
		}
	}

	public boolean merge(DistanceVector newVector, int source,
			int sourceDistance, Node owner) {
		boolean changed = false;
		for (int i = 0; i < newVector.size(); i++) {
			VectorPair pair = newVector.get(i);
			if (owner.isNeighbor(pair.getId())) {
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
			result += this.list.get(i);
			if (i < this.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

}
