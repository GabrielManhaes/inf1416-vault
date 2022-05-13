package utils;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger; 
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.SecureRandom;
import java.util.stream.IntStream;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
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
            
            // They are all valid
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

    public static boolean isPathValid(String path) {
        File file = new File(path);
        return file.exists();

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