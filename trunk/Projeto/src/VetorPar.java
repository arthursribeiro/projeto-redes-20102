/**
 * Classe para representar um par do VetorDistancia
 * @author Grupo 10
 *
 */
public class VetorPar {

	private int id;
	private int distancia;

	public VetorPar(int id, int distancia) {
		this.id = id;
		this.distancia = distancia;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDistancia() {
		return distancia;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}
	
    @Override
	public String toString() {
		return "<" + id + ";" + distancia + ">";
	}
    
    public boolean equals(Object o) {
    	try {
    		VetorPar par = (VetorPar)o;
    		return this.id == par.getId() && this.distancia == par.getDistancia();
    	} catch (ClassCastException e) {
    		return false;
    	}
    }

}
