import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Vector;

public class Node {

	private int id;
	private int port;
	private String ip;
	private DistanceVector distanceVector;
	private Vector<NodeDescriptor> neighbors;
	private Server server;
	private HashMap<Integer, Integer> distances;

	public Node(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPort(port);
		this.setIp(ip);
		this.distanceVector = new DistanceVector();
		this.neighbors = new Vector<NodeDescriptor>();
		this.server = new Server(port, this);
		this.distances = new HashMap<Integer, Integer>();
	}

	public void removeNeighbor(NodeDescriptor node) {
		this.neighbors.remove(node);
		this.distanceVector.removeById(node.getId());
	}
	
	public void pingNeighbors() {
		Vector<NodeDescriptor> toRemove = new Vector<NodeDescriptor>();
		for (NodeDescriptor node : this.neighbors) {
			String ip = node.getIp();
			int port = node.getPort();
			try {
				InetAddress ipAddress = InetAddress.getByName(ip);
				byte ping[] = ("ping," + node.getId()).getBytes();
				DatagramSocket wait = new DatagramSocket();
				DatagramPacket pingPacket = new DatagramPacket(ping,
						ping.length, ipAddress, port);
				System.out.println(">>>>>>>>>> ping? " + node.getId());
				wait.send(pingPacket);
				byte pong[] = new byte[12];
				DatagramPacket pongPacket = new DatagramPacket(pong,
						pong.length, ipAddress, port);
				wait.setSoTimeout(1000);
				try {
					wait.receive(pongPacket);
				} catch (SocketTimeoutException e) {
					System.out.println("Timeout! Pong not received!");
					wait.close();
					toRemove.add(node);
					continue;
				}
				String responseId = (new String(pongPacket.getData()).trim())
						.split(",")[1];
				System.out.println("<<<<<<<<<< Pong! " + responseId);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		for (NodeDescriptor node : toRemove) {
			this.removeNeighbor(node);
		}
	}

	public void notifyVector() {
		this.pingNeighbors();
		for (NodeDescriptor node : this.neighbors) {
			String ip = node.getIp();
			int port = node.getPort();
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress ipAddress = InetAddress.getByName(ip);
				byte vector[] = (id + "," + this.distanceVector.toString())
						.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(vector,
						vector.length, ipAddress, port);
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
		this.distances.put(node.getId(), distance);
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
		if (distanceVector.merge(newVector, sourceId, distances.get(sourceId), this)) {
			System.out.println("New distance vector: " + distanceVector);
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
