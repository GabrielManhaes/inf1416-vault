import vault.Vault;

class Main {
    public static void main(String[] args) {
        boolean retry = true;

        Vault.getInstance();

        System.out.println("\n#################### STEP 1 ####################\n");
        Vault.firstStep();
        Vault.showMenu("");

    }
}
