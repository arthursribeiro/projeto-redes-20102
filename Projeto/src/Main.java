import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class Main {

	public Node criarNo(int id) {
		Node no = null;
	    boolean valido = false;
	    for(String chave : FileManager.getDados().keySet()) {
		    int identificador = Integer.parseInt(chave);
		    if (identificador != id) {
		    	continue;
		    }
		    valido = true;
		    int porta = Integer.parseInt(FileManager.getDados().get(chave)[0]);
		    String ip = FileManager.getDados().get(chave)[1];
		    try {
		    	no = new Node(id, porta, ip);		        	
		    	System.out.println("Criou socket na porta: " + porta);
		    	return no;
		    } catch (SocketException e) {
		    	System.out.println("Nao conseguiu socket UDP na porta: " + porta + ". Ja esta criado?");
		    }
	    }
		if(!valido)
	      	System.out.println("Identificador invalido, tente outro.");
	    return no;
	}
	
	public void adicionarVizinhos(Node no) {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("enlaces.config"));
	        String str;
	        while(in.ready()) {
	        	str = in.readLine();
		        String [] splitStr = str.split(" ");
		        if(Integer.parseInt(splitStr[0]) == no.getId()) {
		        	NodeDescriptor desc = new NodeDescriptor(Integer.parseInt(splitStr[1]), FileManager.getDados().get(splitStr[1])[1], Integer.parseInt(FileManager.getDados().get(splitStr[1])[0]));
		        	no.addNeighbor(desc, Integer.parseInt(splitStr[2]));
		        } else if(Integer.parseInt(splitStr[1]) == no.getId()) {
		        	NodeDescriptor desc = new NodeDescriptor(Integer.parseInt(splitStr[0]), FileManager.getDados().get(splitStr[0])[1], Integer.parseInt(FileManager.getDados().get(splitStr[0])[0]));
		        	no.addNeighbor(desc, Integer.parseInt(splitStr[2]));
		        }
	        }
		} catch (IOException e){
			
		}
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		FileManager.setDiameter(20);
		FileManager.buildMap();
		int id;
		if (args.length > 1) {
			id = Integer.parseInt(args[1]);
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.print("Por favor, insira o numero do ID do roteador desejado: ");
			id = sc.nextInt();
			Node no = m.criarNo(id);
			m.adicionarVizinhos(no);
			no.start();
		}
	}

}
