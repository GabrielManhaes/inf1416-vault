package vault;

import utils.Utils;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import database.Database;
import javax.crypto.Cipher;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import javax.crypto.SecretKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.Signature;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyFactory;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.io.ByteArrayInputStream;
import java.security.SignatureException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.naming.InvalidNameException;
import java.security.cert.X509Certificate;
import javax.crypto.NoSuchPaddingException;
import java.security.cert.CertificateFactory;
import javax.crypto.IllegalBlockSizeException;
import javax.security.auth.x500.X500Principal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.cert.CertificateEncodingException;

public class Vault {

    private static String current = "";
    private static PrivateKey privateKey;
    private static Vault vault;

    private Vault() {
        Database.getInstance();
        Database.connect();
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

    public static String secondStep(String current) throws NoSuchAlgorithmException {
        Database.addEntry(3001, current);
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
                        Database.addEntry(3003, current);
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
                if (error == 1) {
                    Database.addEntry(3004, current);
                } else if (error == 2) {
                    Database.addEntry(3005, current);
                } else if (error == 3) {
                    Database.addEntry(3006, current);
                    Database.blockUser(current);
                    Database.addEntry(3007, current);
                    System.out.println("\nUsuário bloqueado por excesso de tentativas!\n");
                    count = 0;
                    error = 0;
                    selectedNumbers1 = new StringBuilder();
                    selectedNumbers2 = new StringBuilder();
                    current = firstStep();
                }
            }
        }
        Database.addEntry(3002, current);
        return current;
    }

    public static String thirdStep(String current) throws
                                                        IllegalBlockSizeException,
                                                        NoSuchAlgorithmException,
                                                        InvalidKeySpecException,
                                                        NoSuchPaddingException,
                                                        CertificateException,
                                                        InvalidKeyException,
                                                        BadPaddingException,
                                                        SignatureException,
                                                        IOException {
        Scanner scanner = new Scanner(System.in);
        byte[] decryptedBytes = {};
        boolean done = false;
        int error = 0;
        Database.addEntry(4001, current);


        while(!done) {
            System.out.println("Caminho do arquivo da chave privada:");
            String path = scanner.nextLine();
            if(!Utils.fileExists(path)) {
                Database.addEntry(4004, current);
                System.out.println("Caminho para a chave inválido!");
                continue;
            }
            System.out.println("Frase secreta:");
            String secretPhrase = scanner.nextLine();

            byte[] encryptedBytes = Utils.getBytesFromPath(path);
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            prng.setSeed(secretPhrase.getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56, prng);
            SecretKey desKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            try {
                decryptedBytes = cipher.doFinal(encryptedBytes);
            } catch (BadPaddingException e) {
                Database.addEntry(4005, current);
                System.out.println("\nNão foi possível decriptar a chave privada!\n");
                error++;
                decryptedBytes = null;
            }
            if (decryptedBytes != null) {
                String PKCS8KeyB64 = new String(decryptedBytes)
                                        .replace("-----BEGIN PRIVATE KEY-----", "")
                                        .replace("-----END PRIVATE KEY-----", "")
                                        .replace("\n", "")
                                        .trim();
                byte[] PKCS8Key = Base64.getMimeDecoder().decode(PKCS8KeyB64);
                PKCS8EncodedKeySpec PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(PKCS8Key);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec);
                Signature signature = Signature.getInstance("SHA1withRSA");
                signature.initSign(privateKey);

                String certificateString = Database.getCertificate(current)
                                        .replace("-----BEGIN CERTIFICATE-----", "")
                                        .replace("-----END CERTIFICATE-----", "")
                                        .replace("\n", "")
                                        .trim();
                byte encodedCertificate[] = Base64.getDecoder().decode(certificateString);
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(encodedCertificate));
                PublicKey publicKey = certificate.getPublicKey();

                byte[] randomBytes = new byte[2048];
                SecureRandom.getInstanceStrong().nextBytes(randomBytes);

                signature.initSign(privateKey);
                signature.update(randomBytes);
                byte[] signatureBytes = signature.sign();
                signature.initVerify(publicKey);
                signature.update(randomBytes);

                if (signature.verify(signatureBytes)) {
                    Database.addEntry(4003, current);
                    System.out.println("\nAutenticado com sucesso!\n");
                    done = true;
                } else {
                    Database.addEntry(4006, current);
                    System.out.println("\nNão foi possível verificar o par de chaves!\n");
                    error++;
                }
            }
            if (error == 3) {
                Database.blockUser(current);
                Database.addEntry(4007, current);
                System.out.println("\nUsuário bloqueado por excesso de tentativas!\n");
                error = 0;
                current = firstStep();
                current = secondStep(current);
            }
        }
        Database.addEntry(4002, current);
        return current;
    }

    public static void showMenu(String selected) throws
                                                    CertificateEncodingException,
                                                    IllegalBlockSizeException,
                                                    NoSuchAlgorithmException,
                                                    NoSuchPaddingException,
                                                    InvalidNameException,
                                                    CertificateException,
                                                    BadPaddingException,
                                                    InvalidKeyException,
                                                    SignatureException,
                                                    IOException {
        Scanner scanner = new Scanner(System.in);
        String certificate_encoded = "";
        String confirmPassword = "";
        X509Certificate certificate;
        String certPath = "";
        String password = "";
        String groupId = "";
        String email = "";
        String name = "";
        String path = "";
        String dn = "";
        String CN = "";

        System.out.println("Login: " + current);
        System.out.println("Grupo: " + Database.getGroup(current));
        System.out.println("Nome: " + Database.getName(current) + "\n");
        
        List<String> options = Arrays.asList(
            "Cadastrar um novo usuário",
            "Alterar senha pessoal e certificado digital do usuário",
            "Consultar pasta de arquivos secretos do usuário",
            "Sair do Sistema"
        );
        Integer gid = Integer.parseInt(Database.getGroupId(current));
        switch(Integer.parseInt(selected) + gid - 1) {
            case -1:
                System.out.println("Total de acessos do usuário: " + Database.getCountLogins(current) + "\n");
                System.out.println("Menu Principal:" + "\n");
                for (int i = gid - 1 ; i < options.size(); i++) {
                        System.out.println(i+1 + " – " + options.get(i));
                }
                showMenu(scanner.nextLine());
                break;
            case 0:
                System.out.println("Total de acessos do usuário: " + Database.getCountLogins(current) + "\n");
                System.out.println("Menu Principal:" + "\n");
                for (int i = gid - 1 ; i < options.size(); i++) {
                    System.out.println(i + " – " + options.get(i));
                }
                showMenu(scanner.nextLine());
                break;
            case 1:
                Database.addEntry(5002, current);
                Database.addEntry(6001, current);
                System.out.println("Total de usuários do sistema: " + Database.getCountUsers() + "\n");
                System.out.println("Formulário de Cadastro:" + "\n");
                System.out.println("– Caminho do arquivo do certificado digital: ");
                certPath = scanner.nextLine();
                if (Utils.fileExists(certPath)) {
                    certificate = Utils.getCertificateFromPath(certPath);
                    certificate_encoded = "-----BEGIN CERTIFICATE-----\n" + 
                                Base64.getEncoder()
                                      .encodeToString(certificate.getEncoded())
                                      .toString()
                                      .replaceAll(".{64}",  "$0\n")
                                + "\n-----END CERTIFICATE-----";
                    email = Utils.getCertificateField(certificate, "EMAILADDRESS");
                    CN = Utils.getCertificateField(certificate, "CN");
                } else {
                    Database.addEntry(6004, current);
                    System.out.println("Caminho para o certificado inválido!");
                    scanner.nextLine();
                    showMenu("-1");
                    break;
                }
                System.out.println("– Grupo: (1) Administrador | (2) Usuário");
                groupId = scanner.nextLine();
                if (!groupId.equals("1") && !groupId.equals("2")){
                    Database.addEntry(6006, current);
                    System.out.println("Grupo inválido!");
                    scanner.nextLine();
                    showMenu("-1");
                    break;
                }
                System.out.println("– Senha pessoal: ");
                password = scanner.nextLine();
                if (password.length() < 8 || password.length() > 10){
                    System.out.println("A senha precisa ter de 8 a 10 caracteres.");
                    scanner.nextLine();
                    showMenu("-1");
                    break;
                }
                System.out.println("– Confirmação senha pessoal: ");
                confirmPassword = scanner.nextLine();
                if (!password.equals(confirmPassword)) {
                    Database.addEntry(6003, current);
                    Database.addEntry(6006, current);
                    System.out.println("As senhas digitadas não são iguais.");
                    scanner.nextLine();
                    showMenu("-1");
                    break;
                }
                Database.addEntry(6002, current);
                String salt = Utils.getNextSalt();
                StringBuilder salted = new StringBuilder();
                salted.append(password);
                salted.append(salt);
                Database.createUser(email, CN, certificate_encoded, Utils.hashSHA1(salted.toString()), groupId, salt);
                Database.addEntry(6005, current);
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(6007, current);
                showMenu("-1");
                break;
            case 2:
                Database.addEntry(5003, current);
                Database.addEntry(7001, current);
                System.out.println("Total de acessos do usuário: " + Database.getCountLogins(current) + "\n");
                System.out.println("Caminho do arquivo do certificado digital: ");
                certPath = scanner.nextLine();
                if (Utils.isPathValid(certPath)) {
                    if (Utils.fileExists(certPath)) {
                        certificate = Utils.getCertificateFromPath(certPath);
                        certificate_encoded = "-----BEGIN CERTIFICATE-----\n" + 
                                    Base64.getEncoder()
                                        .encodeToString(certificate.getEncoded())
                                        .toString()
                                        .replaceAll(".{64}",  "$0\n")
                                    + "\n-----END CERTIFICATE-----";
                        email = Utils.getCertificateField(certificate, "EMAILADDRESS");
                        CN = Utils.getCertificateField(certificate, "CN");
                    } else {
                        Database.addEntry(7003, current);
                        System.out.println("Caminho para o certificado inválido!");
                        scanner.nextLine();
                        showMenu("-1");
                        break;
                    }
                }
                System.out.println("– Senha pessoal: ");
                password = scanner.nextLine();
                if (password != "") {
                    if (password.length() < 8 || password.length() > 10){
                        System.out.println("A senha precisa ter de 8 a 10 caracteres.");
                        scanner.nextLine();
                        showMenu("-1");
                        break;
                    }
                    System.out.println("– Confirmação senha pessoal: ");
                    confirmPassword = scanner.nextLine();
                    if (!password.equals(confirmPassword)) {
                        Database.addEntry(7002, current);
                        Database.addEntry(7005, current);
                        System.out.println("As senhas digitadas não são iguais.");
                        scanner.nextLine();
                        showMenu("-1");
                        break;
                    }
                }
                Database.addEntry(7004, current);
                if (password != "") {    
                    salted = new StringBuilder();
                    salt = Database.getSalt(current);
                    salted.append(password);
                    salted.append(salt);
                    Database.setPassword(current, Utils.hashSHA1(salted.toString()));
                }
                if (certPath != "") {
                    Database.setCertificate(current, certificate_encoded);
                } 
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(7006, current);
                showMenu("-1");
                break;
            case 3:
                Database.addEntry(5004, current);
                Database.addEntry(8001, current);
                System.out.println("Total de consultas do usuário: " + Database.getCountUserQueries(current) + "\n");
                System.out.println("Caminho da pasta: " + "\n");
                path = scanner.nextLine();
                if (!Utils.isPathValid(path)) {
                    Database.addEntry(8004, current);
                    System.out.println("Caminho da pasta inválido!");
                    scanner.nextLine();
                    showMenu("-1");
                    break;
                }
                System.out.println("1 - Listar");
                System.out.println("2 - Voltar ao menu principal");
                String optionSelected = scanner.nextLine();

                if (optionSelected.equals("1")) {
                    String index = Utils.readEncryptedFile(path, "index", current, privateKey);
                    if (!index.equals("")) {
                        System.out.println("\nArquivos disponíveis:\n");
                        System.out.println(index);
                        Database.addEntry(8009, current);
                        System.out.println("\nDigite o nome do arquivo para ser consultado:\n");
                        String selectedFile = scanner.nextLine();
                        Database.addEntry(8010, current);
                        if (Database.getGroup(current).equals(Utils.parseDataFromIndex(index, selectedFile, 3))
                            || current.equals(Utils.parseDataFromIndex(index, selectedFile, 2))) {
                            String fileBytes = Utils.readEncryptedFile(path, selectedFile, current, privateKey);
                            if (!fileBytes.equals("")) {
                                Utils.writeDecryptedFile(index, path, selectedFile, fileBytes);
                            }
                        } else {
                            System.out.println("\nO usuário não tem acesso a este arquivo/diretório!\n");
                        }
                    }


                } else if (optionSelected.equals("2")) {
                    showMenu("-1");
                    break;
                }
                Database.addEntry(8003, current);
                System.out.println("Pressione qualquer botão para voltar ao menu inicial");
                scanner.nextLine();
                Database.addEntry(8002, current);
                showMenu("-1");
                break;
            case 4:
                Database.addEntry(5005, current);
                Database.addEntry(9001, current);
                System.out.println("Total de acessos do usuário: " + Database.getCountLogins(current) + "\n");
                System.out.println("Saída do sistema: " + "\n");
                System.out.println("Pressione '0' para voltar ao menu inicial ou qualquer outra tecla para sair.");
                if (scanner.nextLine().equals("0")) {
                    Database.addEntry(9004, current);
                    showMenu("-1");
                    break;
                }
                Database.addEntry(9003, current);
                break;
            default:
                showMenu("-1");
                break;
        }
    }
}

