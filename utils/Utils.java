package utils;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Base64;
import java.util.Scanner;
import database.Database;
import javax.crypto.Cipher;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.math.BigInteger; 
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.crypto.SecretKey;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.PublicKey;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.KeyFactory;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.util.stream.IntStream;
import java.security.MessageDigest;
import java.io.ByteArrayInputStream;
import java.security.SignatureException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;
import javax.crypto.NoSuchPaddingException;
import java.security.cert.CertificateFactory;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Utils{

    public static boolean validateEmail(String email) {
        String emailRegexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(emailRegexPattern)
            .matcher(email)
            .matches();
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static List<List<Integer>> subsets(int[] nums) {
        return subsets(nums, 0);
    }
    
    public static List<List<Integer>> subsets(int[] nums, int start) {
        List<List<Integer>> ret = new ArrayList<>();
        if (start == nums.length) {
            ret.add(new ArrayList<>());
        } else {
            List<List<Integer>> subsets = subsets(nums, start + 1);
            
            ret.addAll(subsets);
            
            for (List<Integer> subset : subsets) {
                List<Integer> clone = new ArrayList<>(subset);
                clone.add(nums[start]);
                ret.add(clone);
            }
        }
        return ret;
    }

    public static String generateOnePermutation(StringBuilder str1, StringBuilder str2, int index) {
        StringBuilder res = new StringBuilder();
        res.append(str1.toString()).setCharAt(index, str2.charAt(index));
        return res.toString();
    }

    public static List<String> generatePermutations(StringBuilder str1, StringBuilder str2, int len) {
        int[] indexes = IntStream.rangeClosed(0, len-1).toArray();
        List<List<Integer>> permutationSets = subsets(indexes);
        List<String> permutations = new ArrayList<>();
        for(int i = 0; i < permutationSets.size(); i++) {
            String permutation = str1.toString();
            for (int j = 0; j < permutationSets.get(i).size(); j++) {
                StringBuilder newstr = new StringBuilder();
                newstr.append(permutation);
                permutation = generateOnePermutation(newstr, str2, permutationSets.get(i).get(j));
            }
            permutations.add(permutation.toString());
        }
        return permutations;
    }

    public static String getNextSalt() throws NoSuchAlgorithmException {
        String chrs = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        String salt = secureRandom.ints(10, 0, chrs.length()).mapToObj(i -> chrs.charAt(i))
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        return salt;
    }

    public static String hashSHA1(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();

    }

    public static String readEncryptedFile(String path, String filename, String current, PrivateKey privateKey) throws
                                                                                                                    IllegalBlockSizeException,
                                                                                                                    NoSuchAlgorithmException,
                                                                                                                    NoSuchPaddingException,
                                                                                                                    CertificateException,
                                                                                                                    InvalidKeyException,
                                                                                                                    SignatureException,
                                                                                                                    IOException {
        byte[] encryptedBytes = Utils.getBytesFromPath(path + filename + ".enc");
        byte[] encryptedSeed = Utils.getBytesFromPath(path + filename + ".env");
        byte[] indexSignature = Utils.getBytesFromPath(path + filename + ".asd");
        byte[] decryptedBytes = {};
        byte[] decryptedSeed = {};
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        try {
            decryptedSeed = cipher.doFinal(encryptedSeed);
            Database.addEntry(8005, current);
        } catch (BadPaddingException e) {
            System.out.println("\nO usuário não tem acesso a este arquivo/diretório!\n");
            return "";
        }
        prng.setSeed(decryptedSeed);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56, prng);
        SecretKey desKey = keyGenerator.generateKey();
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, desKey);
        try {
            decryptedBytes = cipher.doFinal(encryptedBytes);
        } catch (BadPaddingException e) {
            Database.addEntry(8007, current);
            System.out.println("\nO usuário não tem acesso a este arquivo/diretório!\n");
            return "";
        }
        Signature signature = Signature.getInstance("SHA1withRSA");
        String certificateString = Database.getCertificate(current)
                                .replace("-----BEGIN CERTIFICATE-----", "")
                                .replace("-----END CERTIFICATE-----", "")
                                .replace("\n", "")
                                .trim();;
        byte encodedCertificate[] = Base64.getDecoder().decode(certificateString);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(encodedCertificate));
        PublicKey publicKey = certificate.getPublicKey();

        signature.initVerify(publicKey);
        signature.update(decryptedBytes);
        if (signature.verify(indexSignature)) {
            System.out.println("\nConfirmada a integridade e autenticidade do arquivo!\n");
            Database.addEntry(8006, current);
            return new String(decryptedBytes);
            
        } else {
            Database.addEntry(8008, current);
            System.out.println("\nNão foi possível verificar a integridade e autenticidade do arquivo de índice!\n");
        }
        return "";
    }

    public static String parseDataFromIndex(String index, String secretFilename, int match) {
        Matcher matcher = Pattern.compile("(.*" + secretFilename + ".*)\n").matcher(index);
        if (matcher.find()) {
            String expression = matcher.group(1).split(" ")[match];
            return expression;
        }
        return "";
    }

    public static void writeDecryptedFile(String index, String path, String secretFilename, String bytes) throws IOException {
        String filename = parseDataFromIndex(index, secretFilename, 1);
        if (!filename.equals("")) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + filename));
            writer.write(bytes);
            writer.close();
            System.out.println("\nArquivo decriptado e salvo com sucesso!\n");
        } else {
            System.out.println("\nNão foi possível encontrar o arquivo para ser escrito!\n");
        }
    }

    public static boolean isPathValid(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    public static byte[] getBytesFromPath(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static String getCertificateField(X509Certificate certificate, String field) {
		String regex = ".*" + field + "=([^,]*).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(certificate.getSubjectDN().toString());
        if( matcher.matches() ) {
        	return matcher.group(1);
        }
        return null;
    }

    public static X509Certificate getCertificateFromPath(String path) throws IOException {
        File file = new File(path);
        return readX509Certificate(file);
    }

    public static X509Certificate readX509Certificate(File file) throws IOException {
        return readX509Certificate(file.toURI().toURL());
    }

    public static X509Certificate readX509Certificate(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        try {
            CertificateFactory servercf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) servercf.generateCertificate(is);
        } catch (CertificateException e) {
            // We can assume certificates are valid is most cases
            throw new RuntimeException(e);
        } finally {
            is.close();
        }
    }
}