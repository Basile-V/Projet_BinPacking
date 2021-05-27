public class main {
    public static void main(String[] args)throws Exception
    {
        Binpacking bp = new Binpacking("files/binpack1d_00.txt", false);
        //bp.firstFit(1);
        //bp.firstFit(2);
        bp.OneItemPerBin();
        bp.printBins();
        //bp.RecuitSimule(25.00, 30, 10, 0.95);
        bp.TabuSearch(5, 5);
        System.out.println("nbbins = " + bp.nb_bin);
        bp.printBins();
    }
}
