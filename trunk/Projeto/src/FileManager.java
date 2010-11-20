import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class FileManager {
	
	private static HashMap<String, String[]> dados = new HashMap<String, String[]>();
	private static int diameter;
	
	public static int getPort(int id) {
		return Integer.parseInt(FileManager.dados.get(""+id)[0]);
	}
	
	public static String getIp(int id) {
		return FileManager.dados.get(""+id)[1];
	}
	
	public static HashMap<String, String[]> getDados() {
		return FileManager.dados;
	}
	
	public static void buildMap() {
		try {
	        BufferedReader in = new BufferedReader(new FileReader("roteador.config"));
	        String str;
	        while(in.ready()) {
	        	str = in.readLine();
	        	String [] valor = new String[2];
		        String [] splitStr = str.split(" ");
		        valor[0] = splitStr[1];
		        valor[1] = splitStr[2];
		        FileManager.dados.put(splitStr[0], valor);
	        }
		} catch (IOException e) {
			System.out.println("Arquivo n�o encontrado!");
		}
	}

	public static void setDiameter(int diameter) {
		FileManager.diameter = diameter;
	}

	public static int getDiameter() {
		return diameter;
	}
	
}
