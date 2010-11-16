import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Server {
	
	private String ip;
	private int port;
	private DatagramSocket udpListener;

	public Server(String ip, int port) throws SocketException {
		this.ip = ip;
		this.port = port;
		this.udpListener = new DatagramSocket(port);
	}

	public void start() {
		while (true) {
			try {
				byte buffer[] = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				udpListener.receive(packet);
				System.out.println("Package received!");
				System.out.println(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
