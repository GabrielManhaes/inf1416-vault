import vault.Vault;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

class Main {
    public static void main(String[] args) throws IOException, CertificateEncodingException, NoSuchAlgorithmException{
        String user;
        Vault.getInstance();

        System.out.println("\n#################### PRIMEIRA ETAPA DE AUTENTICAÇÃO ####################\n");
        user = Vault.firstStep();
        System.out.println("\n#################### SEGUNDA ETAPA DE AUTENTICAÇÃO ####################\n");
        Vault.secondStep(user);
        // System.out.println("\n#################### TERCEIRA ETAPA DE AUTENTICAÇÃO ####################\n");
        // Vault.thirdStep(user);
        Vault.showMenu("");

    }
}
