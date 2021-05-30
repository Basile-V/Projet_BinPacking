public class main {
    public static void main(String[] args)throws Exception
    {
        Binpacking bp = new Binpacking("files/binpack1d_31.txt", false);
/*

        System.out.println("\nFirst Fit Decreasing");
        bp.firstFit(1);
        System.out.println("nbbins = " + bp.nb_bin);


        System.out.println("\nFirst Fit Random");
        bp.firstFit(2);
        System.out.println("nbbins = " + bp.nb_bin);


        System.out.println("\nOne item per bin");
        bp.OneItemPerBin();
        System.out.println("nbbins = " + bp.nb_bin);


        System.out.println("\nRecuit simulé");
        bp.RecuitSimule(25.00, 30, 10, 0.95);
        System.out.println("nbbins = " + bp.nb_bin);


        System.out.println("\nTabu Search");
        bp.OneItemPerBin();
        bp.TabuSearch(5, 5);
        System.out.println("nbbins = " + bp.nb_bin);


        System.out.println("\nFirst Fit Random");
        int somme = 0;
        for (int i = 0; i < 1000; i++) {
            bp.firstFit(2);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + somme/1000.00);


        System.out.println("\nRecuit simulé");
        int somme = 0;
        for (int i = 0; i < 100; i++) {
            bp.OneItemPerBin();
            bp.RecuitSimule(140, 120, 100, 0.85);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + somme/100.00);


        System.out.println("\nTabu Search");
        for (int i = 0; i < 10; i++) {
            bp.OneItemPerBin();
            bp.TabuSearch(10*i, 400);
            bp.printBins();
            System.out.println("nbbins = " + bp.nb_bin);
        }

 */

        System.out.println("\nTabu Search");
        bp.OneItemPerBin();
        bp.TabuSearch(10, 100);
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);

    }
}
