import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Classe que representa a escuta do No a uma porta, responsavel por receber pacotes
 * de ping e respondê-los, assim como receber vetores distancia e atualizar os do no
 * @author Grupo 10
 *
 */
public class Servidor extends Thread {

	private DatagramSocket udpListener;
	private No no;
	
	/**
	 * Cria um servidor relacionado ao No passado escutando na porta recebida
	 * @param porta	Porta a qual o servidor ficará escutando
	 * @param no	No ao qual o servidor pertence
	 * @throws SocketException
	 */
	public Servidor(int porta, No no) throws SocketException {
		this.no = no;
		this.udpListener = new DatagramSocket(porta);
	}

    @Override
	public void run() {
		while (true) {
			try {
				byte buffer[] = new byte[256];
				DatagramPacket pacote = new DatagramPacket(buffer,
						buffer.length);
				udpListener.receive(pacote);
				String conteudo = new String(pacote.getData()).trim();
				if (conteudo.startsWith("ping")) {
					// System.out.println(pingCounter++ + ". " + "Pong! " +
					// node.getId());
					byte pong[] = ("pong," + no.getId()).getBytes();
					DatagramPacket pacoteAEnviar = new DatagramPacket(pong,
							pong.length, pacote.getAddress(), pacote.getPort());
					DatagramSocket socketPong = new DatagramSocket();
					socketPong.send(pacoteAEnviar);
				} else {
//					System.out.println("Distance vector received!");
					String vetorString[] = (new String(pacote.getData())
							.trim()).split(",");
					int fonte = Integer.parseInt(vetorString[0]);
					String vetor[] = new String[vetorString.length - 1];
					for (int i = 1; i < vetorString.length; i++) {
						vetor[i - 1] = vetorString[i];
					}
					VetorDistancia distanceVector = createVector(vetor);
					no.atualizarHistorico(distanceVector, fonte);
					//node.updateVector(distanceVector, source);
					no.recalcularVetor();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * Cria um objeto VetorDistancia a partir de um vetor de strings com as informações
     * sobre as distâncias
     * @param vectorString	Vetor com dados sobre distâncias
     * @return	VetorDistancia correspondente
     */
	public VetorDistancia createVector(String vectorString[]) {
		VetorDistancia vetor = new VetorDistancia();
		for (String par : vectorString) {
			String valores[] = par.split(";");
			int id = Integer.parseInt(valores[0]
					.substring(1, valores[0].length()));
			int distancia = Integer.parseInt(valores[1].substring(0, valores[1]
					.length() - 1));
			VetorPar novoPar = new VetorPar(id, distancia);
			vetor.adicionar(novoPar);
		}
		return vetor;
	}

}
