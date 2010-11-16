import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Vector;

public class Node {

	private int id;
	private int port;
	private String ip;
	private DistanceVector distanceVector;
	private Vector<Node> neighbors;
	private Server server;

	public Node(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPort(port);
		this.setIp(ip);
		this.distanceVector = new DistanceVector();
		this.neighbors = new Vector<Node>();
		this.server = new Server(port, this);
	}
	
	public void notifyVector() {
		for (Node node : this.neighbors) {
			String ip = node.getIp();
			int port = node.getPort();
			try {
				DatagramSocket socket = new DatagramSocket(new InetSocketAddress(ip, port));
				byte vector[] = (id + "," + this.distanceVector.toString()).getBytes();
				socket.send(new DatagramPacket(vector, vector.length));
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		this.server.start();
	}

	public void addNeighbor(Node node, int distance) {
		if (!this.neighbors.contains(node)) {
			this.neighbors.add(node);
			this.distanceVector.append(new VectorPair(node.getId(), distance));
		} else {
			VectorPair pair = distanceVector.getPairById(node.getId());
			pair.setDistance(distance);
		}
	}

	public void updateVector(DistanceVector newVector, int sourceId) {
		VectorPair pair = distanceVector.getPairById(sourceId);
		distanceVector.merge(newVector, pair.getDistance());
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

}
