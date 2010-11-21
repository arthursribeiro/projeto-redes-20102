import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class Node {

	private int id;
	private int port;
	private String ip;
	private DistanceVector distanceVector;
	private Vector<NodeDescriptor> neighbors;
	private Vector<NodeDescriptor> actives;
	private HashMap<Integer, DistanceVector> vetoresDistancia;
	private Server server;
	private HashMap<Integer, Integer> distances;
	Pinger pinger;

	public Node(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPort(port);
		this.setIp(ip);
		this.distanceVector = new DistanceVector();
		vetoresDistancia = new HashMap<Integer, DistanceVector>();
		this.neighbors = new Vector<NodeDescriptor>();
		this.actives = new Vector<NodeDescriptor>();
		this.server = new Server(port, this);
		this.distances = new HashMap<Integer, Integer>();
		this.pinger = new Pinger(this);
		this.distanceVector.append(new VectorPair(id, 0));
		this.distances.put(id, 0);
	}
	
	public void atualizarHistorico(DistanceVector dv, int source) {
		vetoresDistancia.remove(source);
		vetoresDistancia.put(source, dv);
	}
	
	public void recalcularVetor() {
		DistanceVector novoVetor = new DistanceVector();
		novoVetor.append(new VectorPair(id, 0));
		if(vetoresDistancia.keySet().size() > 0) {
			for(int i = 0; i < vetoresDistancia.keySet().toArray().length; i++) {
				DistanceVector byNode = vetoresDistancia.get(vetoresDistancia.keySet().toArray()[i]);
				for(int j = 0; j < byNode.size(); j++) {
					if((byNode.get(j).getId()) != id) {
						if(novoVetor.getPairById((byNode.get(j).getId())) == null){
							VectorPair toAdd = new VectorPair(byNode.get(j).getId(), byNode.get(j).getDistance());
							toAdd.setDistance(distances.get(vetoresDistancia.keySet().toArray()[i])+toAdd.getDistance());
							if(toAdd.getDistance() > FileManager.getDiameter()) {
								toAdd.setDistance(FileManager.getDiameter());
							}
							novoVetor.append(toAdd);
						} else {
							VectorPair par = byNode.get(j);
							VectorPair par2 = novoVetor.getPairById(par.getId());
							if(par2.getId() == par.getId()) {
								if((par.getDistance()+distances.get(vetoresDistancia.keySet().toArray()[i])) < par2.getDistance()) {
									if (par.getDistance()+distances.get(vetoresDistancia.keySet().toArray()[i]) > FileManager.getDiameter()) {
										novoVetor.setDistanceById(par2.getId(), FileManager.getDiameter());
									} else {
										novoVetor.setDistanceById(par2.getId(), par.getDistance()+distances.get(vetoresDistancia.keySet().toArray()[i]));
									}
								}
							}
						}
					}
				}
			}
		}
		if (changedVector(novoVetor)) {
			distanceVector = novoVetor;
			System.out.println("New distance vector: " + distanceVector);
			this.notifyVector();
		}
	}
	
	public void removeElementFromVector(int id) {
		vetoresDistancia.remove(id);
	}
	
	public boolean changedVector(DistanceVector novo) {
		int controle = 0;
		if(this.distanceVector.size() != novo.size()) {
			return true;
		}
		for(int i = 0; i < this.distanceVector.size(); i++) {
			for(int j = 0; j < novo.size(); j++) {
				if(novo.get(j).getId() == distanceVector.get(i).getId()) {
					if(!novo.get(j).equals(distanceVector.get(i))) {
						controle++;
					}
				}
			}
		}
		return (controle != 0);
	}

	public boolean isNeighbor(int id) {
		for (NodeDescriptor node : this.neighbors) {
			if (node.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public void notifyVector() {
		for (NodeDescriptor node : this.neighbors) {
			System.out.println("Notifying for " + node);
			String nodeIp = node.getIp();
			int nodePort = node.getPort();
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress ipAddress = InetAddress.getByName(nodeIp);
				byte vector[] = (id + "," + this.distanceVector.toString())
						.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(vector,
						vector.length, ipAddress, nodePort);
				socket.send(sendPacket);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		this.server.start();
		this.pinger.start();
	}

	public void addNeighbor(NodeDescriptor node, int distance) {
		this.distances.put(node.getId(), distance);
		this.neighbors.add(node);
		this.pinger.addNode(node);
	}

	public void updateVector(DistanceVector newVector, int sourceId) {
		if (distanceVector.merge(newVector, sourceId, distances.get(sourceId),
				this)) {
			System.out.println("New distance vector: " + distanceVector);
			this.notifyVector();
		}
	}

	public void deactivateNode(NodeDescriptor node) {
		distanceVector.getPairById(node.getId()).setDistance(Integer.MAX_VALUE);
//		distanceVector.removeById(node.getId());
		System.out.println("New distance vector: " + this.distanceVector);
		this.actives.remove(node);
		this.notifyVector();
	}

	public void activateNode(NodeDescriptor node) {
		VectorPair pair = new VectorPair(node.getId(), this.distances.get(node
				.getId()));
		if (!this.distanceVector.contains(node.getId())) {
			this.distanceVector.append(pair);
			System.out.println("New distance vector: " + this.distanceVector);
			this.notifyVector();
		}
		if (!this.actives.contains(node)) {
			this.actives.add(node);
		}
	}
	
	public int getDistance(int id) {
		return this.distances.get(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

    @Override
	public String toString() {
		return "Node: (" + id + ", " + ip + ", " + port + ")";
	}

	public DistanceVector getDistanceVector() {
		return distanceVector;
	}

}
