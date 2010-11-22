import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Classe responsavel por testar os Nos e identificar Nós ativos e desativados
 * @author Grupo 10
 *
 */
public class Pinger extends Thread {

	ArrayList<DescritorNo> nos;
	ArrayList<DescritorNo> ativos;
	No no;

	public Pinger(No no) {
		this.no = no;
		this.nos = new ArrayList<DescritorNo>();
		this.ativos = new ArrayList<DescritorNo>();
	}

	/**
	 * Adiciona o No recebido a lista de nos
	 * @param no	No a ser adicionado
	 */
	public synchronized void adicionarNo(DescritorNo no) {
		if (!this.nos.contains(no)) {
			this.nos.add(no);
		}
	}

	/**
	 * Envia um ping para o No informado e aguarda recebimento de resposta. Caso nao receba remove o No
	 * da lista de nos ativos. Caso receba e o No não esteja na lista de ativados então adiciona-o
	 * @param no	No a ser testado
	 */
	public synchronized void ping(DescritorNo no) {
		String ip = no.getIp();
		int porta = no.getPorta();
		try {
			InetAddress enderecoIP = InetAddress.getByName(ip);
			byte ping[] = ("ping," + no.getId()).getBytes();
			DatagramSocket espera = new DatagramSocket();
			DatagramPacket pacotePing = new DatagramPacket(ping, ping.length,
					enderecoIP, porta);
//			System.out.println("Enviando ping para " + no.getId());
			espera.send(pacotePing);
			byte pong[] = new byte[12];
			DatagramPacket pacotePong = new DatagramPacket(pong, pong.length,
					enderecoIP, porta);
			espera.setSoTimeout(500);
			try {
				espera.receive(pacotePong);
			} catch (SocketTimeoutException e) {
//				System.out.println("Timeout! Pong nao recebido!");
//				this.no.desativarNo(no);
				if (this.ativos.contains(no)) {
					this.ativos.remove(no);
					this.no.removerElemento(no.getId());
					this.no.recalcularVetor();
				}
				espera.close();
				return;
			}
//			System.out.println("Pong recebido!");
			if (!this.ativos.contains(no)) {
				this.ativos.add(no);
				this.no.ativarNo(no);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manda mensagem de ping para todos os Nos na lista de Nos do pinger
	 */
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
