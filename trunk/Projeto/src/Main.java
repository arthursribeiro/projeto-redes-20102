/**
 * UFCG - CEEI - DSC
 * Projeto da disciplina Redes de Computadores - 2010.2
 * 
 * Grupo 10:
 * 	- Arthur de Souza Ribeiro
 * 	- Danilo Araújo de Freitas
 * 	- Nicholas Alexander Diniz Rodrigues
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Classe Main <br>
 * Início da execução do código de um Nó
 * 
 * @author Grupo 10
 */
public class Main {

	/**
	 * Cria o nó relativo ao identificador passado como parâmetro.
	 * 
	 * @param id
	 *            Identificador do nó a ser criado
	 * @return Nó criado
	 * @throws Exception
	 *             Caso algo dê errado
	 */
	public No criarNo(int id) throws Exception {
		No no = null;
		boolean valido = false;
		for (String chave : GerenciadorDeArquivos.getDados().keySet()) {
			int identificador = Integer.parseInt(chave);
			if (identificador != id) {
				continue;
			}
			valido = true;
			int porta = Integer.parseInt(GerenciadorDeArquivos.getDados().get(
					chave)[0]);
			String ip = GerenciadorDeArquivos.getDados().get(chave)[1];
			try {
				no = new No(id, porta, ip);
				System.out.println("Criou socket na porta: " + porta);
				return no;
			} catch (SocketException e) {
				throw new Exception("Nao conseguiu socket UDP na porta: "
						+ porta
						+ ". Porta provavelmente em uso. Abortando programa");
			}
		}
		if (!valido)
			throw new Exception("Identificador invalido, tente outro.");
		return no;
	}

	/**
	 * Adiciona todos os vizinhos do No passado como parâmetro para sua lista de
	 * vizinhos
	 * 
	 * @param no
	 *            No cuja lista de vizinhos sera preenchida
	 */
	public void adicionarVizinhos(No no) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"enlaces.config"));
			String str;
			while (in.ready()) {
				str = in.readLine();
				String[] splitStr = str.split(" ");
				if (Integer.parseInt(splitStr[0]) == no.getId()) {
					DescritorNo desc = new DescritorNo(Integer
							.parseInt(splitStr[1]), GerenciadorDeArquivos
							.getDados().get(splitStr[1])[1], Integer
							.parseInt(GerenciadorDeArquivos.getDados().get(
									splitStr[1])[0]));
					no.adicionarVizinho(desc, Integer.parseInt(splitStr[2]));
				} else if (Integer.parseInt(splitStr[1]) == no.getId()) {
					DescritorNo desc = new DescritorNo(Integer
							.parseInt(splitStr[0]), GerenciadorDeArquivos
							.getDados().get(splitStr[0])[1], Integer
							.parseInt(GerenciadorDeArquivos.getDados().get(
									splitStr[0])[0]));
					no.adicionarVizinho(desc, Integer.parseInt(splitStr[2]));
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Início da execução do Programa. Os arquivos enlaces.config e
	 * roteador.config são lidos e é perguntado qual roteador deseja-se
	 * inicializar.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Main m = new Main();
		GerenciadorDeArquivos.construirDistancia();
		GerenciadorDeArquivos.construirMapa();
		int id;
		if (args.length > 0) {
			id = Integer.parseInt(args[0]);
		} else {
			Scanner sc = new Scanner(System.in);
			System.out
					.print("Por favor, insira o numero do ID do roteador desejado: ");
			id = sc.nextInt();
		}
		No no = null;
		try {
			no = m.criarNo(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Aperte enter para sair");
			System.in.read();
			System.exit(1);
		}
		m.adicionarVizinhos(no);
		no.start();
	}

}
