import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Classe respons�vel pela leitura e interpreta��o dos dados contidos nos 
 * arquivos de configura��o
 * @author Grupo 10
 */
public class GerenciadorDeArquivos {

	private static HashMap<String, String[]> dados = new HashMap<String, String[]>();
	private static int diametro;
	
	/**
	 * Retorna a porta do roteador com o id fornecido como parametro.
	 * @param id	Identificador do roteador
	 * @return		Porta do roteador especificado
	 */
	public static int getPorta(int id) {
		return Integer.parseInt(GerenciadorDeArquivos.dados.get(""+id)[0]);
	}
	
	/**
	 * Retorna o endere�o IP do roteador com o id fornecido como parametro.
	 * @param id	Identificador do roteador
	 * @return		Endere�o IP do roteador especificado
	 */
	public static String getIp(int id) {
		return GerenciadorDeArquivos.dados.get(""+id)[1];
	}
	
	/**
	 * Retorna o mapa com os dados recuperados pelo GerenciadorDeArquivos atrav�s do uso
	 * do m�todo {@link this#construirMapa() construirMapa}
	 * @return Mapa com dados lidos do arquivo roteador.config
	 */
	public static HashMap<String, String[]> getDados() {
		return GerenciadorDeArquivos.dados;
	}
	
	/**
	 * Constroi o mapa relacionando cada roteadores a suas respectivas portas
	 */
	public static void construirMapa() {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	        String str;
	        while(in.ready()) {
	        	str = in.readLine();
	        	String [] valor = new String[2];
		        String [] splitStr = str.split("\\s+");
		        valor[0] = splitStr[1];
		        valor[1] = splitStr[2];
		        GerenciadorDeArquivos.dados.put(splitStr[0], valor);
	        }
		} catch (IOException e) {
			System.out.println("Arquivo nao encontrado!");
		}
	}
	
	/**
	 * Define o valor do diametro a partir da leitura do distanciaMaxima.config
	 */
	public static void construirDistancia() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("distanciaMaxima.config"));
	        String str;
	        while(in.ready()) {
	        	str = in.readLine();
	        	diametro = Integer.parseInt(str);
	        }
		} catch (IOException e) {
			System.err.println("Arquivo com distancia nao foi encontrado");
		}
	}

	public static void setDiametro(int diametro) {
		GerenciadorDeArquivos.diametro = diametro;
	}

	public static int getDiametro() {
		return diametro;
	}
	
}
