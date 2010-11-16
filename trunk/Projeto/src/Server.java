import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {

	private int port;
	private DatagramSocket udpListener;
	private Node node;

	public Server(int port, Node node) throws SocketException {
		this.port = port;
		this.node = node;
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
				String vectorString[] = buffer.toString().split(",");
				int source = Integer.parseInt(vectorString[0]);
				String vector[] = new String[vectorString.length - 1];
				for (int i = 1; i < vectorString.length; i++) {
					vector[i-1] = vectorString[i];
				}
				DistanceVector distanceVector = createVector(vector);
				node.updateVector(distanceVector, source);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DistanceVector createVector(String vectorString[]) {
		DistanceVector vector = new DistanceVector();
		for (String pair : vectorString) {
			String values[] = pair.split(";");
			int id = Integer.parseInt(values[0].substring(1, values[0].length()));
			int distance = Integer.parseInt(values[1].substring(0, values[1].length() - 1));
			VectorPair newPair = new VectorPair(id, distance);
			vector.append(newPair);
		}
		return vector;
	}

}
