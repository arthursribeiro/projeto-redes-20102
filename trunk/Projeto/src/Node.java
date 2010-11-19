package Projeto.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.ArrayList;


public class Node {

	private int id;
	private int port;
	private String ip;
	private DistanceVector distanceVector;
	private ArrayList<NodeDescriptor> neighbors;
	private Server server;
	private HashMap<Integer, Integer> distances;
	Pinger pinger;

	public Node(int id, int port, String ip) throws SocketException {
		this.setId(id);
		this.setPort(port);
		this.setIp(ip);
		this.distanceVector = new DistanceVector();
		this.neighbors = new ArrayList<NodeDescriptor>();
		this.server = new Server(port, this);
		this.distances = new HashMap<Integer, Integer>();
		this.pinger = new Pinger(this);
		this.distanceVector.append(new VectorPair(id, 0));
		this.distances.put(id, 0);
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
		System.out.println("Notifying " + this.distanceVector);
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
		distanceVector.removeById(node.getId());
		this.neighbors.remove(node);
		System.out.println("Removing node " + node
				+ " from distance vector of node " + this);
		System.out.println("New vector: " + this.distanceVector);
		this.notifyVector();
	}

	public void activateNode(NodeDescriptor node) {
		VectorPair pair = new VectorPair(node.getId(), this.distances.get(node
				.getId()));
		if (!this.distanceVector.contains(node.getId())) {
			this.distanceVector.append(pair);
			this.neighbors.add(node);
			System.out.println("Adding node " + node + " to distance vector");
			System.out.println("New vector: " + this.distanceVector);
			this.notifyVector();
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

}
