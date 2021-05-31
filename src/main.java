public class main {
    public static void main(String[] args)
    {
        long debut;
        long fin;
        int somme;
        int nbFirstFitRandom = 100;
        int nbRecuit = 100;
        int nbTabu = 10;
        int TabuIter = 10;
        Binpacking bp = new Binpacking("files/binpack1d_00.txt", false);
        boolean verbose = false;

        //------------     First Fit Decreasing   ------------
/*
        System.out.println("\nFirst Fit Decreasing");
        debut = System.currentTimeMillis();
        bp.firstFit(1);
        fin = System.currentTimeMillis() - debut;
        if (verbose)
            bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");


        //------------     First Fit Random   ------------

        System.out.println("\nFirst Fit Random - Efficacité en temps");
        debut = System.currentTimeMillis();
        bp.firstFit(2);
        fin = System.currentTimeMillis() - debut;
        if (verbose)
            bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");

        System.out.println("\nFirst Fit Random - Efficacité en nb bins");
        somme = 0;
        for (int i = 0; i < nbFirstFitRandom; i++) {
            bp.firstFit(2);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + (double) somme/nbFirstFitRandom);


        //------------     One item per bin   ------------

        System.out.println("\nOne item per bin");
        debut = System.currentTimeMillis();
        bp.OneItemPerBin();
        fin = System.currentTimeMillis() - debut;
        if (verbose)
            bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");


        //------------     Recuit Simulé   ------------

        System.out.println("\nRecuit simulé - Efficacité en temps");
        bp.OneItemPerBin();
        debut = System.currentTimeMillis();
        bp.RecuitSimule(140, 120, 100, 0.85);
        fin = System.currentTimeMillis() - debut;
        if (verbose)
            bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");
*/

        System.out.println("\nRecuit simulé - Efficacité en nb bins");
        somme = 0;
        for (int i = 0; i < nbRecuit; i++) {
            bp.OneItemPerBin();
            bp.RecuitSimule(100, 50, 10, 0.85);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + (double) somme/nbRecuit);

/*
        //------------     Tabou   ------------

        System.out.println("\nTabu Search -  Efficacité en temps");
        bp.OneItemPerBin();
        debut = System.currentTimeMillis();
        bp.TabuSearch(10, TabuIter);
        fin = System.currentTimeMillis() - debut;
        if (verbose)
            bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");
*/
    }
}
