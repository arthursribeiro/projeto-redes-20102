import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

public class Node {

	private int id;
	private int port;
	private String ip;
	private DistanceVector distanceVector;
	private Vector<NodeDescriptor> neighbors;
	private Server server;

	public Node(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPort(port);
		this.setIp(ip);
		this.distanceVector = new DistanceVector();
		this.neighbors = new Vector<NodeDescriptor>();
		this.server = new Server(port, this);
	}
	
	public void notifyVector() {
		for (NodeDescriptor node : this.neighbors) {
			String ip = node.getIp();
			int port = node.getPort();
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress ipAddress = InetAddress.getByName(ip);
				byte vector[] = (id + "," + this.distanceVector.toString()).getBytes();
				DatagramPacket sendPacket = new DatagramPacket(vector, vector.length, ipAddress, port);
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
	}

	public void addNeighbor(NodeDescriptor node, int distance) {
		System.out.println(node.getId() + " " + distance);
		if (!this.neighbors.contains(node)) {
			this.neighbors.add(node);
			this.distanceVector.append(new VectorPair(node.getId(), distance));
		} else {
			VectorPair pair = distanceVector.getPairById(node.getId());
			pair.setDistance(distance);
		}
		this.notifyVector();
	}

	public void updateVector(DistanceVector newVector, int sourceId) {
		VectorPair pair = distanceVector.getPairById(sourceId);
		if (distanceVector.merge(newVector, pair.getDistance(), this)) {
			this.notifyVector();
		}
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
	
	public String toString() {
		return "Node: (" + id + ", " + ip + ", " + port + ")";
	}

}
