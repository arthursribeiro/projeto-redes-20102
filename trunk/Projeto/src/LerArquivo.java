import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class LerArquivo {
	
	public void imprimirConteudo() {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	            String str;
	            while (in.ready()) {
	                str = in.readLine();
	                System.out.println(str);
	            }
	            in.close();
	    } catch (IOException e) {
	    }
	}
	
	public DatagramSocket criarSocket() {
		try {
			boolean criou = false;
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	        String str;
	        DatagramSocket socket = null;
	        while(!criou && in.ready()) {
	        	str = in.readLine();
		        String [] splitStr = str.split(" ");
		        int porta = Integer.parseInt(splitStr[1]);
		        try {
		        	socket = new DatagramSocket(porta);
		        	criou = true;
		        	System.out.println("Criou socket na porta: " + porta);
		        } catch (SocketException e) {
		        	System.out.println("Nao conseguiu criar na porta: " + porta);
				}
	        }
	        in.close();
	        return socket;
	    } catch (IOException e) {
	    }
		return null;
	}
	
	public static void main(String[] args) {
		LerArquivo a = new LerArquivo();
		a.imprimirConteudo();
		DatagramSocket soc = a.criarSocket();
		soc.close();
		a.criarSocket();
		a.criarSocket();
	}
	
}
