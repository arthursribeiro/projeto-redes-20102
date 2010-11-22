import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Classe para representar um Nó (roteador) na topologia fornecida
 * 
 * @author Grupo 10
 * 
 */
public class No {

	private int id;
	private int porta;
	private String ip;
	private VetorDistancia vetorDistancia;
	private ArrayList<DescritorNo> vizinhos;
	private HashMap<Integer, VetorDistancia> vetoresDistancia;
	private Servidor servidor;
	private HashMap<Integer, Integer> distancias;
	Pinger pinger;

	/**
	 * Construtor da classe No. Recebe o identificador, a porta e o endereço IP
	 * do roteador
	 * 
	 * @param id
	 *            Identificador do roteador
	 * @param port
	 *            Porta do roteador
	 * @param ip
	 *            A String representando o endereço IP
	 * @throws SocketException
	 */
	public No(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPorta(port);
		this.setIp(ip);
		this.vetorDistancia = new VetorDistancia();
		vetoresDistancia = new HashMap<Integer, VetorDistancia>();
		this.vizinhos = new ArrayList<DescritorNo>();
		this.servidor = new Servidor(port, this);
		this.distancias = new HashMap<Integer, Integer>();
		this.pinger = new Pinger(this);
		this.vetorDistancia.adicionar(new VetorPar(id, 0));
		this.distancias.put(id, 0);
		for (String identificador : GerenciadorDeArquivos.getDados().keySet()) {
			if (Integer.parseInt(identificador) != this.id) {
				vetorDistancia.adicionar(new VetorPar(Integer
						.parseInt(identificador), GerenciadorDeArquivos
						.getDiametro() + 1));
			}
		}
	}

	public void atualizarHistorico(VetorDistancia dv, int fonte) {
		vetoresDistancia.remove(fonte);
		vetoresDistancia.put(fonte, dv);
	}

	/**
	 * Atualiza o vetor distância deste No.
	 */
	public void recalcularVetor() {
		VetorDistancia novoVetor = new VetorDistancia();
		novoVetor.adicionar(new VetorPar(id, 0));
		if (vetoresDistancia.keySet().size() > 0) {
			for (int i = 0; i < vetoresDistancia.keySet().toArray().length; i++) {
				VetorDistancia porNo = vetoresDistancia.get(vetoresDistancia
						.keySet().toArray()[i]);
				for (int j = 0; j < porNo.size(); j++) {
					if ((porNo.get(j).getId()) != id) {
						if (novoVetor.getParPorID((porNo.get(j).getId())) == null) {
							VetorPar toAdd = new VetorPar(porNo.get(j).getId(),
									porNo.get(j).getDistancia());
							toAdd.setDistancia(distancias.get(vetoresDistancia
									.keySet().toArray()[i])
									+ toAdd.getDistancia());
							if (toAdd.getDistancia() > GerenciadorDeArquivos
									.getDiametro()) {
								toAdd.setDistancia(GerenciadorDeArquivos
										.getDiametro() + 1);
							}
							novoVetor.adicionar(toAdd);
						} else {
							VetorPar par = porNo.get(j);
							VetorPar par2 = novoVetor.getParPorID(par.getId());
							if (par2.getId() == par.getId()) {
								if ((par.getDistancia() + distancias
										.get(vetoresDistancia.keySet()
												.toArray()[i])) < par2
										.getDistancia()) {
									if (par.getDistancia()
											+ distancias.get(vetoresDistancia
													.keySet().toArray()[i]) > GerenciadorDeArquivos
											.getDiametro()) {
										novoVetor.setDistanciaPorID(par2
												.getId(), GerenciadorDeArquivos
												.getDiametro());
									} else {
										novoVetor.setDistanciaPorID(par2
												.getId(), par.getDistancia()
												+ distancias
														.get(vetoresDistancia
																.keySet()
																.toArray()[i]));
									}
								}
							}
						}
					}
				}
			}
		}
		if (vetorAlterado(novoVetor)) {
			vetorDistancia = novoVetor;
			SimpleDateFormat df = new SimpleDateFormat(
					"'['dd/MM/yyyy '-' HH:mm:ss']'");
			System.out.print(df.format(new Date(System.currentTimeMillis())));
			System.out.println(" Novo vetor de distancia: "
					+ this.vetorDistancia.outputString());
			this.notificarVetor();
		}
	}

	/**
	 * Remove elemento com o Identificador fornecido do vetor distância
	 * 
	 * @param id
	 *            Identificador do elemento a ser removido
	 */
	public void removerElemento(int id) {
		vetoresDistancia.remove(id);
	}

	/**
	 * Identifica se o vetor distância deste No foi alterado em comparação com o
	 * vetor passado como parâmetro.
	 * 
	 * @param novo
	 *            Vetor a ser comparado com o atual
	 * @return True caso vetor atual seja diferente do recebido
	 */
	public boolean vetorAlterado(VetorDistancia novo) {
		int controle = 0;
		if (this.vetorDistancia.size() != novo.size()) {
			return true;
		}
		for (int i = 0; i < this.vetorDistancia.size(); i++) {
			for (int j = 0; j < novo.size(); j++) {
				if (novo.get(j).getId() == vetorDistancia.get(i).getId()) {
					if (!novo.get(j).equals(vetorDistancia.get(i))) {
						controle++;
					}
				}
			}
		}
		return (controle != 0);
	}

	/**
	 * Identifica se o No com o ID fornecido é vizinho deste
	 * 
	 * @param id
	 *            Identificador do No a ser verificado
	 * @return True caso o ID fornecido esteja na lista de vizinhos deste No
	 */
	public boolean ehVizinho(int id) {
		for (DescritorNo no : this.vizinhos) {
			if (no.getId() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Este No notifica o seu vetor a todos os seus vizinhos através do envio de
	 * um pacote UDP
	 */
	public void notificarVetor() {
		for (DescritorNo no : this.vizinhos) {
			SimpleDateFormat df = new SimpleDateFormat(
					"'['dd/MM/yyyy '-' HH:mm:ss']'");
			System.out.print(df.format(new Date(System.currentTimeMillis())));
			System.out.println(" Enviando vetor para " + no.getId());
			String nodeIp = no.getIp();
			int nodePort = no.getPorta();
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress enderecoIP = InetAddress.getByName(nodeIp);
				byte vector[] = (id + "," + this.vetorDistancia.toString())
						.getBytes();
				DatagramPacket pacoteAEnviar = new DatagramPacket(vector,
						vector.length, enderecoIP, nodePort);
				socket.send(pacoteAEnviar);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inicia o servidor e o pinger
	 */
	public void start() {
		SimpleDateFormat df = new SimpleDateFormat(
				"'['dd/MM/yyyy '-' HH:mm:ss']'");
		System.out.print(df.format(new Date(System.currentTimeMillis())));
		System.out.println(" Novo vetor de distancia: "
				+ this.vetorDistancia.outputString());
		this.servidor.start();
		this.pinger.start();
	}

	/**
	 * Adiciona o No passado como parâmentro para a lista de vizinhos deste No e
	 * adiciona a distância entre eles à lista de distâncias
	 * 
	 * @param no
	 *            O DescritorNo vizinho a ser adicionado
	 * @param distancia
	 *            A distância entre este No e o No vizinho
	 */
	public void adicionarVizinho(DescritorNo no, int distancia) {
		this.distancias.put(no.getId(), distancia);
		this.vizinhos.add(no);
		this.pinger.adicionarNo(no);
	}

	/**
	 * Ativa o No passado como parametro, atualizando sua distância para a
	 * distância do enlace direto. O No deve ser vizinho.
	 * 
	 * @param no
	 *            No vizinho a ser ativado
	 */
	public void ativarNo(DescritorNo no) {
		VetorPar par = this.vetorDistancia.getParPorID(no.getId());
		if (par.getDistancia() > GerenciadorDeArquivos.getDiametro()) {
			this.vetorDistancia.getParPorID(par.getId()).setDistancia(
					this.distancias.get(no.getId()));
			SimpleDateFormat df = new SimpleDateFormat(
					"'['dd/MM/yyyy '-' HH:mm:ss']'");
			System.out.print(df.format(new Date(System.currentTimeMillis())));
			System.out.println(" Novo vetor de distancia: "
					+ this.vetorDistancia.outputString());
			this.notificarVetor();
		}
	}

	/**
	 * Retorna a distância deste No para o No com o ID passado
	 * 
	 * @param id
	 *            Identificador do No ao qual se quer saber a distancia
	 * @return Distancia entre este no e o No com o ID recebido
	 */
	public int getDistancia(int id) {
		return this.distancias.get(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public int getPorta() {
		return porta;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	@Override
	public String toString() {
		return "Node: (" + id + ", " + ip + ", " + porta + ")";
	}

	public VetorDistancia getVetorDistancia() {
		return vetorDistancia;
	}

}
