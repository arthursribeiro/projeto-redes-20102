import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Main {

	public static Node criarNo(int id) {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	        String str;
	        Node no = null;
	        boolean valido = false;
	        while(in.ready()) {
	        	str = in.readLine();
		        String [] splitStr = str.split(" ");
		        int identificador = Integer.parseInt(splitStr[0]);
		        if (identificador != id) {
		        	continue;
		        }
		        valido = true;
		        int porta = Integer.parseInt(splitStr[1]);
		        String ip = splitStr[2];
		        try {
		        	no = new Node(id, porta, ip);		        	
		        	System.out.println("Criou socket na porta: " + porta);
		        	return no;
		        } catch (SocketException e) {
		        	System.out.println("Nao conseguiu socket UDP na porta: " + porta + ". Ja esta criado?");
				}
	        }
	        in.close();
	        if(!valido)
	        	System.out.println("Identificador invalido, tente outro.");
	        return no;
	    } catch (IOException e) {
	    	System.out.println("Arquivo não encontrado!");
	    }
	    return null;
	}
	
	public static void main(String[] args) {
		int id;
		if (args.length > 1) {
			id = Integer.parseInt(args[1]);
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.print("Por favor, insira o numero do ID do roteador desejado: ");
			id = sc.nextInt();
			Node no = criarNo(id);	
		}
	}

}
