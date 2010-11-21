import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Pinger extends Thread {

	ArrayList<DescritorNo> nos;
	ArrayList<DescritorNo> ativos;
	No no;

	public Pinger(No no) {
		this.no = no;
		this.nos = new ArrayList<DescritorNo>();
		this.ativos = new ArrayList<DescritorNo>();
	}

	public synchronized void adicionarNo(DescritorNo no) {
		if (!this.nos.contains(no)) {
			this.nos.add(no);
		}
	}

	public synchronized void ping(DescritorNo no) {
		String ip = no.getIp();
		int porta = no.getPorta();
		try {
			InetAddress enderecoIP = InetAddress.getByName(ip);
			byte ping[] = ("ping," + no.getId()).getBytes();
			DatagramSocket espera = new DatagramSocket();
			DatagramPacket pacotePing = new DatagramPacket(ping,
					ping.length, enderecoIP, porta);
			// System.out.println(">>>>>>>>>> Ping? " + node.getId());
			espera.send(pacotePing);
			byte pong[] = new byte[12];
			DatagramPacket pacotePong = new DatagramPacket(pong,
					pong.length, enderecoIP, porta);
			espera.setSoTimeout(500);
			try {
				espera.receive(pacotePong);
			} catch (SocketTimeoutException e) {
				// System.out.println("Timeout! Pong not received!");
				if (this.ativos.contains(no)) {
					this.ativos.remove(no);
					this.no.removerElemento(no.getId());
					//this.node.deactivateNode(node);
					this.no.recalcularVetor();
				}
				espera.close();
				return;
			}
			if (!this.ativos.contains(no)) {
				this.ativos.add(no);
				this.no.ativarNo(no);
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

	public synchronized void pingTodos() {
		for (DescritorNo descritorNo : this.nos) {
			this.ping(descritorNo);
		}
	}

    @Override
	public void run() {
		while (true) {
			try {
				this.pingTodos();
				Thread.sleep(5000); // Can cause performance problems
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
