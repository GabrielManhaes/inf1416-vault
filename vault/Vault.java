package vault;

import java.util.Scanner;
import utils.Utils;
import database.Database;

public class Vault {

    public static String current = "";
    private static Vault vault;

    private Vault() {
        Database.getInstance();
        Database.connect();
        Database.addEntry(1001);
    }

    public static Vault getInstance() {

        if(vault == null) {
            vault = new Vault();
        }

        return vault;
    }

    public static void firstStep() {
        Database.addEntry(2001);
        Scanner scanner = new Scanner(System.in);
        String loginName = "";
        boolean retry = true;

        while(retry) {
            System.out.println("Login:");
            current = scanner.nextLine();
            boolean isValid = Utils.validateEmail(current);
            if(isValid) {
                boolean userExists = Database.userExists(current);
                if (userExists) {
                    boolean isBlocked = Database.userBlocked(current);
                    if(isBlocked) {
                        System.out.println("\nAccess blocked, try again later!\n");
                        Database.addEntry(2004, current);
                    } else {
                        retry = false;
                        Database.addEntry(2003, current);
                    }
                } else {
                    System.out.println("\nEmail does not exist in the database, try again!\n");
                    Database.addEntry(2005, current);
                }
            } else {
                System.out.println("\nInvalid email, try again!\n");
            }
        }
        Database.addEntry(2002);
    }

    public static void secondStep() {
    }

    public static void showMenu(String selected) {
        Scanner scanner = new Scanner(System.in);
        String certPath = "";
        String groupId = "";
        String password = "";
        String confirmPassword = "";
        String path = "";

        System.out.println("Login: " + current);
        System.out.println("Grupo: " + Database.getGroup(current));
        System.out.println("Nome: " + Database.getName(current) + "\n");
        System.out.println("Total de acessos do usuário: " + "\n");
        
        switch(selected) {
            case "":
                Database.addEntry(5001, current);
                System.out.println("Menu Principal:" + "\n");
                System.out.println("1 – Cadastrar um novo usuário");
                System.out.println("2 – Alterar senha pessoal e certificado digital do usuário");
                System.out.println("3 – Consultar pasta de arquivos secretos do usuário");
                System.out.println("4 – Sair do Sistema");
                showMenu(scanner.nextLine());
                break;
            case "1":
                Database.addEntry(5002, current);
                Database.addEntry(6001, current);
                System.out.println("Formulário de Cadastro:" + "\n");
                System.out.println("– Caminho do arquivo do certificado digital: ");
                certPath = scanner.nextLine();
                // Checar direito
                if (certPath.equals("")) {
                    Database.addEntry(6004, current);
                    Database.addEntry(6006, current);
                }
                System.out.println("– Grupo: (1) Administrador | (2) Usuário");
                groupId = scanner.nextLine();
                if (!groupId.equals("1") && !groupId.equals("2")){
                    Database.addEntry(6006, current);
                    System.out.println("Grupo inválido!");
                    scanner.nextLine();
                    showMenu("");
                }
                System.out.println("– Senha pessoal: ");
                password = scanner.nextLine();
                System.out.println("– Confirmação senha pessoal: ");
                confirmPassword = scanner.nextLine();
                if (!password.equals(confirmPassword)) {
                    Database.addEntry(6003, current);
                    Database.addEntry(6006, current);
                    System.out.println("As senhas digitadas não são iguais.");
                    scanner.nextLine();
                    showMenu("");
                }
                Database.addEntry(6002, current);
                Database.addEntry(6005, current);
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(6007, current);
                showMenu("");
                break;
            case "2":
                Database.addEntry(5003, current);
                Database.addEntry(7001, current);
                System.out.println("Caminho do arquivo do certificado digital: ");
                certPath = scanner.nextLine();
                if (certPath.equals("")) {
                    Database.addEntry(7003, current);
                    Database.addEntry(7005, current);
                }
                System.out.println("– Senha pessoal: ");
                password = scanner.nextLine();
                System.out.println("– Confirmação senha pessoal: ");
                confirmPassword = scanner.nextLine();
                if (!password.equals(confirmPassword)) {
                    Database.addEntry(7002, current);
                    Database.addEntry(7005, current);
                    System.out.println("As senhas digitadas não são iguais.");
                    scanner.nextLine();
                    showMenu("");
                }
                Database.addEntry(7004, current);
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(7006, current);
                showMenu("");
                break;
            case "3":
                Database.addEntry(5004, current);
                Database.addEntry(8001, current);
                System.out.println("Caminho da pasta: " + "\n");
                path = scanner.nextLine();
                if (path.equals("")) {
                    Database.addEntry(8004, current);
                }
                // Listar
                // falta 8005 em diante
                Database.addEntry(8003, current);
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(8002, current);
                showMenu("");
                break;
            case "4":
                Database.addEntry(5005, current);
                Database.addEntry(9001, current);
                System.out.println("Saída do sistema: " + "\n");
                // 9002?
                System.out.println("Pressione '0' para voltar ao menu inicial ou qualquer outra tecla para sair.");
                if (scanner.nextLine().equals("0")) {
                    Database.addEntry(9004, current);
                    showMenu("");
                }
                Database.addEntry(1002);
                Database.addEntry(9003, current);
                break;
            default:
                showMenu("");
                break;
        }
    }
}

