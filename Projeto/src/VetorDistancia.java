import java.util.ArrayList;

public class VetorDistancia {

	private ArrayList<VetorPar> lista;

	public VetorDistancia() {
		this.lista = new ArrayList<VetorPar>();
	}

	public int size() {
		return this.lista.size();
	}

	public VetorPar get(int indice) {
		return this.lista.get(indice);
	}

	public VetorPar getParPorID(int id) {
		for (VetorPar par : this.lista) {
			if (par.getId() == id) {
				return par;
			}
		}
		return null;
	}
	
	public void setDistanciaPorID(int id, int distance) {
		for(int i = 0; i < this.lista.size(); i++) {
			if(this.lista.get(i).getId() == id) {
				this.lista.get(i).setDistancia(distance);
			}
		}
	}

	public int indiceDe(VetorPar par) {
		return this.lista.indexOf(par);
	}

	public boolean contem(int id) {
		for (VetorPar par : this.lista) {
			if (par.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public void adicionar(VetorPar pair) {
		this.lista.add(pair);
	}

	public void removerPorID(int id) {
		VetorPar toRemove = this.getParPorID(id);
		if (toRemove != null) {
			this.lista.remove(toRemove);
		}
	}

	public boolean mesclar(VetorDistancia newVector, int source,
			int sourceDistance, No owner) {
		boolean changed = false;
		for (int i = 0; i < newVector.size(); i++) {
			VetorPar pair = newVector.get(i);
			if (pair.getId() == owner.getId()) {
				continue;
			}
			if (this.contem(pair.getId())) {
				VetorPar existingPair = this.getParPorID(pair.getId());
				if (pair.getDistancia() == Integer.MAX_VALUE) {
					if (existingPair.getDistancia() != Integer.MAX_VALUE) {
						existingPair.setDistancia(Integer.MAX_VALUE);
						changed = true;
					}
				} else if (pair.getDistancia() + sourceDistance < existingPair
						.getDistancia()) {
					existingPair.setDistancia(pair.getDistancia()
							+ sourceDistance);
					changed = true;
				}
			} else {
				if (pair.getDistancia() + sourceDistance <= FileManager
						.getDiametro()) {
					this.adicionar(new VetorPar(pair.getId(), pair.getDistancia()
							+ sourceDistance));
					changed = true;
				}
			}
		}
		return changed;
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < this.size(); i++) {
			result += this.lista.get(i);
			if (i < this.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

}
