import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {

	private int port;
	private DatagramSocket udpListener;
	private Node node;
	private int pingCounter;

	public Server(int port, Node node) throws SocketException {
		this.port = port;
		this.node = node;
		this.udpListener = new DatagramSocket(port);
		this.pingCounter = 0;
	}

	public void run() {
		while (true) {
			try {
				byte buffer[] = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				udpListener.receive(packet);
				String content = new String(packet.getData()).trim();
				if (content.startsWith("ping")) {
					// System.out.println(pingCounter++ + ". " + "Pong! " +
					// node.getId());
					byte pong[] = ("pong," + node.getId()).getBytes();
					DatagramPacket sendPacket = new DatagramPacket(pong,
							pong.length, packet.getAddress(), packet.getPort());
					DatagramSocket pongSocket = new DatagramSocket();
					pongSocket.send(sendPacket);
					int id = Integer.parseInt(content.split(",")[1]);
				} else {
					System.out.println("Distance vector received!");
					String vectorString[] = (new String(packet.getData())
							.trim()).split(",");
					int source = Integer.parseInt(vectorString[0]);
					String vector[] = new String[vectorString.length - 1];
					for (int i = 1; i < vectorString.length; i++) {
						vector[i - 1] = vectorString[i];
					}
					DistanceVector distanceVector = createVector(vector);
					System.out.println("Created vector: " + distanceVector);
					node.updateVector(distanceVector, source);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public DistanceVector createVector(String vectorString[]) {
		DistanceVector vector = new DistanceVector();
		for (String pair : vectorString) {
			String values[] = pair.split(";");
			int id = Integer.parseInt(values[0]
					.substring(1, values[0].length()));
			int distance = Integer.parseInt(values[1].substring(0, values[1]
					.length() - 1));
			VectorPair newPair = new VectorPair(id, distance);
			vector.append(newPair);
		}
		return vector;
	}

}
