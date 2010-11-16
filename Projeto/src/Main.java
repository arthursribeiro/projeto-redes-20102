import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	HashMap<String, String[]> dados = new HashMap<String, String[]>();
	
	public void pegarDados() {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	        String str;
	        while(in.ready()) {
	        	str = in.readLine();
	        	String [] valor = new String[2];
		        String [] splitStr = str.split(" ");
		        valor[0] = splitStr[1];
		        valor[1] = splitStr[2];
		        this.dados.put(splitStr[0], valor);
	        }
		} catch (IOException e) {
			System.out.println("Arquivo não encontrado!");
		}
	}
	
	public Node criarNo(int id) {
		Node no = null;
	    boolean valido = false;
	    for(String chave : this.dados.keySet()) {
		    int identificador = Integer.parseInt(chave);
		    if (identificador != id) {
		    	continue;
		    }
		    valido = true;
		    int porta = Integer.parseInt(dados.get(chave)[0]);
		    String ip = dados.get(chave)[1];
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
		        	NodeDescriptor desc = new NodeDescriptor(Integer.parseInt(splitStr[1]), dados.get(splitStr[1])[1], Integer.parseInt(dados.get(splitStr[1])[0]));
		        	no.addNeighbor(desc, Integer.parseInt(splitStr[2]));
		        } else if(Integer.parseInt(splitStr[1]) == no.getId()) {
		        	NodeDescriptor desc = new NodeDescriptor(Integer.parseInt(splitStr[0]), dados.get(splitStr[0])[1], Integer.parseInt(dados.get(splitStr[0])[0]));
		        	no.addNeighbor(desc, Integer.parseInt(splitStr[2]));
		        }
	        }
		} catch (IOException e){
			
		}
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.pegarDados();
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
