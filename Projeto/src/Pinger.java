package Projeto.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Pinger extends Thread {

	ArrayList<NodeDescriptor> nodes;
	ArrayList<NodeDescriptor> actives;
	Node node;

	public Pinger(Node node) {
		this.node = node;
		this.nodes = new ArrayList<NodeDescriptor>();
		this.actives = new ArrayList<NodeDescriptor>();
	}

	public synchronized void addNode(NodeDescriptor node) {
		if (!this.nodes.contains(node)) {
			this.nodes.add(node);
		}
	}

	public synchronized void ping(NodeDescriptor node) {
		String ip = node.getIp();
		int port = node.getPort();
		try {
			InetAddress ipAddress = InetAddress.getByName(ip);
			byte ping[] = ("ping," + node.getId()).getBytes();
			DatagramSocket wait = new DatagramSocket();
			DatagramPacket pingPacket = new DatagramPacket(ping,
					ping.length, ipAddress, port);
			// System.out.println(">>>>>>>>>> Ping? " + node.getId());
			wait.send(pingPacket);
			byte pong[] = new byte[12];
			DatagramPacket pongPacket = new DatagramPacket(pong,
					pong.length, ipAddress, port);
			wait.setSoTimeout(500);
			try {
				wait.receive(pongPacket);
			} catch (SocketTimeoutException e) {
				// System.out.println("Timeout! Pong not received!");
				if (this.actives.contains(node)) {
					this.actives.remove(node);
					this.node.deactivateNode(node);
				}
				wait.close();
				return;
			}
			String responseId = (new String(pongPacket.getData()).trim())
					.split(",")[1];
			if (!this.actives.contains(node)) {
				this.actives.add(node);
				this.node.activateNode(node);
			}
			// System.out.println("<<<<<<<<<< Pong! " + responseId);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void pingAll() {
		for (NodeDescriptor nodeDescriptor : this.nodes) {
			this.ping(nodeDescriptor);
		}
	}

    @Override
	public void run() {
		while (true) {
			try {
				this.pingAll();
				Thread.sleep(5000); // Can cause performance problems
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
