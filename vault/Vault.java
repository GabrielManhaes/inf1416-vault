package vault;

import utils.Utils;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import database.Database;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

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

    public static String firstStep() {
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
                        System.out.println("\nAcesso bloqueado, tente novamente mais tarde!\n");
                        Database.addEntry(2004, current);
                    } else {
                        retry = false;
                        Database.addEntry(2003, current);
                    }
                } else {
                    System.out.println("\nEmail não existe no banco de dados, try again!\n");
                    Database.addEntry(2005, current);
                }
            } else {
                System.out.println("\nEmail inválido, tente novamente!\n");
            }
        }
        Database.addEntry(2002);
        return current;
    }

    public static void secondStep(String current) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        StringBuilder selectedNumbers1 = new StringBuilder();
        StringBuilder selectedNumbers2 = new StringBuilder();
        int count = 0;
        int error = 0;
        String asterisk = " *";
        boolean done = false;
        while(!done) {
            StringBuilder numbers = new StringBuilder();
            StringBuilder number1 = new StringBuilder();
            StringBuilder number2 = new StringBuilder();
            numbers.append("0123456789");

            System.out.println("\nDigite sua senha:" + String.join("", Collections.nCopies(count, asterisk)));
            for (int i = 0; i < 5; i++) {
                int randomIndex = Utils.getRandomNumber(0, numbers.length());
                number1.append(numbers.toString().charAt(randomIndex));
                numbers.deleteCharAt(randomIndex);
                randomIndex = Utils.getRandomNumber(0, numbers.length());
                number2.append(numbers.toString().charAt(randomIndex));
                numbers.deleteCharAt(randomIndex);
                System.out.println(i+1 + " - " + number1.charAt(i) + " ou " + number2.charAt(i));
            }
            System.out.println(6 + " - LIMPAR");
            System.out.println(7 + " - OK");
            String choice = scanner.nextLine();
            if (!choice.equals("7")) {
                if (choice.equals("6")) {
                    selectedNumbers1 = new StringBuilder();
                    selectedNumbers2 = new StringBuilder();
                    count = 0;
                } else {
                    selectedNumbers1.append(number1.charAt(Integer.parseInt(choice) - 1));
                    selectedNumbers2.append(number2.charAt(Integer.parseInt(choice) - 1));
                    count++;
                }
            } else {
                List<String> permutations = new ArrayList<>();
                permutations = Utils.generatePermutations(selectedNumbers1, selectedNumbers2, count);
                for(int i = 0; i < permutations.size(); i++) {
                    StringBuilder salted = new StringBuilder();
                    salted.append(permutations.get(i));
                    salted.append(Database.getSalt(current));
                    if(Database.validatePassword(current, Utils.hashSHA1(salted.toString()))) {
                        done = true;
                        break;
                    }
                }
                if (!done) {
                    if (count < 8 || count > 10) {
                        System.out.println("\nSenha precisa ter de 8 a 10 caracteres!\n");
                    } else {
                        count = 0;
                        error++;
                        selectedNumbers1 = new StringBuilder();
                        selectedNumbers2 = new StringBuilder();
                        System.out.println("\nSenha incorreta!\n");
                    }
                }
                if (error == 3) {
                    Database.blockUser(current);
                    System.out.println("\nUsuário bloqueado por excesso de tentativas!\n");
                    count = 0;
                    error = 0;
                    selectedNumbers1 = new StringBuilder();
                    selectedNumbers2 = new StringBuilder();
                    current = firstStep();
                }
            }
        }
    }

    public static void showMenu(String selected) throws IOException, CertificateEncodingException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        String certificate = "";
        String certPath = "";
        String name = "";
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
                if (Utils.isPathValid(certPath)) {
                    certificate = "-----BEGIN CERTIFICATE-----\n" + 
                                Base64.getEncoder()
                                      .encodeToString(Utils.getCertificateFromPath(certPath)
                                      .getEncoded())
                                      .toString()
                                      .replaceAll(".{64}",  "$0\n")
                                + "\n-----END CERTIFICATE-----";
                } else {
                    Database.addEntry(6004, current);
                    System.out.println("Caminho para o certificado inválido!");
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
                if (password.length() < 8 || password.length() > 10){
                    System.out.println("A senha precisa ter de 8 a 10 caracteres.");
                    scanner.nextLine();
                    showMenu("");
                }
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
                String salt = Utils.getNextSalt();
                StringBuilder salted = new StringBuilder();
                salted.append(password);
                salted.append(salt);
                Database.createUser("user01@inf1416.puc-rio.br", "Usuario 01", certificate, Utils.hashSHA1(salted.toString()), groupId, salt);
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
                if (Utils.isPathValid(certPath)) {
                    certificate = "-----BEGIN CERTIFICATE-----\n" + 
                                Base64.getEncoder()
                                      .encodeToString(Utils.getCertificateFromPath(certPath)
                                      .getEncoded())
                                      .toString()
                                      .replaceAll(".{64}",  "$0\n")
                                + "\n-----END CERTIFICATE-----";
                } else {
                    Database.addEntry(7003, current);
                    System.out.println("Caminho para o certificado inválido!");
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
                salted = new StringBuilder();
                salt = Database.getSalt(current);
                salted.append(password);
                salted.append(salt);
                Database.setPassword(current, Utils.hashSHA1(salted.toString()));
                Database.setCertificate(current, certificate);
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

