/**
 * Classe que armazena informações sobre Nos.
 * 
 * @author Grupo 10
 */
public class DescritorNo {

	private int id;
	private String ip;
	private int porta;

	public DescritorNo(int id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.porta = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public String toString() {
		return "(" + id + ", " + ip + ", " + porta + ")";
	}

}
