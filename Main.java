import vault.Vault;
import database.Database;
import java.io.IOException;
import javax.crypto.BadPaddingException;
import java.security.SignatureException;
import javax.naming.InvalidNameException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.cert.CertificateEncodingException;

class Main {
    public static void main(String[] args) throws
                                            CertificateEncodingException,
                                            IllegalBlockSizeException,
                                            NoSuchAlgorithmException,
                                            InvalidKeySpecException,
                                            NoSuchPaddingException,
                                            InvalidNameException,
                                            CertificateException,
                                            BadPaddingException,
                                            InvalidKeyException,
                                            SignatureException,
                                            IOException {
        String user;
        Vault.getInstance();

        System.out.println("\n#################### PRIMEIRA ETAPA DE AUTENTICAÇÃO ####################\n");
        user = Vault.firstStep();
        System.out.println("\n#################### SEGUNDA ETAPA DE AUTENTICAÇÃO ####################\n");
        user = Vault.secondStep(user);
        System.out.println("\n#################### TERCEIRA ETAPA DE AUTENTICAÇÃO ####################\n");
        Vault.thirdStep(user);
        Database.addEntry(5001, user);
        Vault.showMenu("-1");
        Database.addEntry(1002);
    }
}
