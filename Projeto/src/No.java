import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;


public class No {

	private int id;
	private int porta;
	private String ip;
	private VetorDistancia vetorDistancia;
	private ArrayList<DescritorNo> vizinhos;
	private ArrayList<DescritorNo> ativos;
	private HashMap<Integer, VetorDistancia> vetoresDistancia;
	private Servidor servidor;
	private HashMap<Integer, Integer> distancias;
	Pinger pinger;

	public No(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPorta(port);
		this.setIp(ip);
		this.vetorDistancia = new VetorDistancia();
		vetoresDistancia = new HashMap<Integer, VetorDistancia>();
		this.vizinhos = new ArrayList<DescritorNo>();
		this.ativos = new ArrayList<DescritorNo>();
		this.servidor = new Servidor(port, this);
		this.distancias = new HashMap<Integer, Integer>();
		this.pinger = new Pinger(this);
		this.vetorDistancia.adicionar(new VetorPar(id, 0));
		this.distancias.put(id, 0);
	}
	
	public void atualizarHistorico(VetorDistancia dv, int fonte) {
		vetoresDistancia.remove(fonte);
		vetoresDistancia.put(fonte, dv);
	}
	
	public void recalcularVetor() {
		VetorDistancia novoVetor = new VetorDistancia();
		novoVetor.adicionar(new VetorPar(id, 0));
		if(vetoresDistancia.keySet().size() > 0) {
			for(int i = 0; i < vetoresDistancia.keySet().toArray().length; i++) {
				VetorDistancia porNo = vetoresDistancia.get(vetoresDistancia.keySet().toArray()[i]);
				for(int j = 0; j < porNo.size(); j++) {
					if((porNo.get(j).getId()) != id) {
						if(novoVetor.getParPorID((porNo.get(j).getId())) == null){
							VetorPar toAdd = new VetorPar(porNo.get(j).getId(), porNo.get(j).getDistancia());
							toAdd.setDistancia(distancias.get(vetoresDistancia.keySet().toArray()[i])+toAdd.getDistancia());
							if(toAdd.getDistancia() > FileManager.getDiametro()) {
								toAdd.setDistancia(FileManager.getDiametro());
							}
							novoVetor.adicionar(toAdd);
						} else {
							VetorPar par = porNo.get(j);
							VetorPar par2 = novoVetor.getParPorID(par.getId());
							if(par2.getId() == par.getId()) {
								if((par.getDistancia()+distancias.get(vetoresDistancia.keySet().toArray()[i])) < par2.getDistancia()) {
									if (par.getDistancia()+distancias.get(vetoresDistancia.keySet().toArray()[i]) > FileManager.getDiametro()) {
										novoVetor.setDistanciaPorID(par2.getId(), FileManager.getDiametro());
									} else {
										novoVetor.setDistanciaPorID(par2.getId(), par.getDistancia()+distancias.get(vetoresDistancia.keySet().toArray()[i]));
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
			System.out.println("New distance vector: " + vetorDistancia);
			this.notificarVetor();
		}
	}
	
	public void removerElemento(int id) {
		vetoresDistancia.remove(id);
	}
	
	public boolean vetorAlterado(VetorDistancia novo) {
		int controle = 0;
		if(this.vetorDistancia.size() != novo.size()) {
			return true;
		}
		for(int i = 0; i < this.vetorDistancia.size(); i++) {
			for(int j = 0; j < novo.size(); j++) {
				if(novo.get(j).getId() == vetorDistancia.get(i).getId()) {
					if(!novo.get(j).equals(vetorDistancia.get(i))) {
						controle++;
					}
				}
			}
		}
		return (controle != 0);
	}

	public boolean ehVizinho(int id) {
		for (DescritorNo no : this.vizinhos) {
			if (no.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public void notificarVetor() {
		for (DescritorNo no : this.vizinhos) {
			System.out.println("Notifying for " + no);
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

	public void start() {
		this.servidor.start();
		this.pinger.start();
	}

	public void adicionarVizinho(DescritorNo no, int distancia) {
		this.distancias.put(no.getId(), distancia);
		this.vizinhos.add(no);
		this.pinger.adicionarNo(no);
	}

	public void atualizarVizinho(VetorDistancia vetorNovo, int idFonte) {
		if (vetorDistancia.mesclar(vetorNovo, idFonte, distancias.get(idFonte),
				this)) {
			System.out.println("New distance vector: " + vetorDistancia);
			this.notificarVetor();
		}
	}

	public void desativarNo(DescritorNo no) {
		vetorDistancia.getParPorID(no.getId()).setDistancia(Integer.MAX_VALUE);
//		distanceVector.removeById(node.getId());
		System.out.println("New distance vector: " + this.vetorDistancia);
		this.ativos.remove(no);
		this.notificarVetor();
	}

	public void ativarNo(DescritorNo no) {
		VetorPar pair = new VetorPar(no.getId(), this.distancias.get(no
				.getId()));
		if (!this.vetorDistancia.contem(no.getId())) {
			this.vetorDistancia.adicionar(pair);
			System.out.println("New distance vector: " + this.vetorDistancia);
			this.notificarVetor();
		}
		if (!this.ativos.contains(no)) {
			this.ativos.add(no);
		}
	}
	
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
