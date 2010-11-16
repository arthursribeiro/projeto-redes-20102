import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		int id;
		if (args.length > 1) {
			id = Integer.parseInt(args[1]);
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.print("Por favor, insira o numero do ID do roteador desejado: ");
			id = sc.nextInt();
		}
	}

}
