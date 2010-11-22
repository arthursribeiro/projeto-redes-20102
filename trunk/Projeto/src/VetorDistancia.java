import java.util.ArrayList;

/**
 * Classe responsavel por representar os vetores distancia dos Nos
 * @author Grupo 10
 *
 */
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

	/**
	 * Procura e retorna o VetorPar que tem o ID fornecido
	 * @param id	ID do VetorPar buscado
	 * @return	VetorPar com ID fornecido
	 */
	public VetorPar getParPorID(int id) {
		for (VetorPar par : this.lista) {
			if (par.getId() == id) {
				return par;
			}
		}
		return null;
	}
	
	/**
	 * Define a distância para o VetorPar com ID fornecido
	 * @param id		ID do VetorPar
	 * @param distancia	Valor da distância a ser atribuida
	 */
	public void setDistanciaPorID(int id, int distancia) {
		for(int i = 0; i < this.lista.size(); i++) {
			if(this.lista.get(i).getId() == id) {
				this.lista.get(i).setDistancia(distancia);
			}
		}
	}

	/**
	 * Retorna a posição do VetorPar fornecido
	 * @param par	VetorPar o qual deseja-se saber o índice
	 * @return	Índice do VetorPar dado
	 */
	public int indiceDe(VetorPar par) {
		return this.lista.indexOf(par);
	}

	/**
	 * Verifica se este VetorDistancia contém o VetorPar com ID fornecido
	 * @param id	ID do VetorPar a ser verificado existência
	 * @return	True caso VetorPar esteja contido neste VetorDistancia, False caso contrário
	 */
	public boolean contem(int id) {
		for (VetorPar par : this.lista) {
			if (par.getId() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adiciona o VetorPar dado a este VetorDistancia
	 * @param pair
	 */
	public void adicionar(VetorPar pair) {
		this.lista.add(pair);
	}

	/**
	 * Remove o VetorPar com o ID fornecido deste VetorDistancia
	 * @param id	ID do VetorPar a ser removido
	 */
	public void removerPorID(int id) {
		VetorPar toRemove = this.getParPorID(id);
		if (toRemove != null) {
			this.lista.remove(toRemove);
		}
	}

	/**
	 * Atualiza os valores deste VetorDistancia a partir dos valores do novo
	 * VetorDistancia da fonte fornecida
	 * @param novoVetor			Vetor da fonte com os novos valores
	 * @param fonte				ID do No fonte
	 * @param distanciaFonte	Distancia do dono deste VetorDistancia para a fonte
	 * @param dono				ID do dono deste VetorDistancia 
	 * @return
	 */
	public boolean mesclar(VetorDistancia novoVetor, int fonte,
			int distanciaFonte, No dono) {
		boolean modificado = false;
		for (int i = 0; i < novoVetor.size(); i++) {
			VetorPar par = novoVetor.get(i);
			if (par.getId() == dono.getId()) {
				continue;
			}
			if (this.contem(par.getId())) {
				VetorPar parExistente = this.getParPorID(par.getId());
				if (par.getDistancia() == Integer.MAX_VALUE) {
					if (parExistente.getDistancia() != Integer.MAX_VALUE) {
						parExistente.setDistancia(Integer.MAX_VALUE);
						modificado = true;
					}
				} else if (par.getDistancia() + distanciaFonte < parExistente
						.getDistancia()) {
					parExistente.setDistancia(par.getDistancia()
							+ distanciaFonte);
					modificado = true;
				}
			} else {
				if (par.getDistancia() + distanciaFonte <= GerenciadorDeArquivos
						.getDiametro()) {
					this.adicionar(new VetorPar(par.getId(), par.getDistancia()
							+ distanciaFonte));
					modificado = true;
				}
			}
		}
		return modificado;
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
	
	public String outputString() {
		String result = "";
		for (int i = 0; i < this.size(); i++) {
			VetorPar par = this.lista.get(i);
			if (par.getDistancia() > GerenciadorDeArquivos.getDiametro()) {
				result += "<" + par.getId() + ";INALCANCAVEL>";
			} else {
				result += par;
			}
			if (i < this.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

}
