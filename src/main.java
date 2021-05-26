public class main {
    public static void main(String[] args)throws Exception
    {
        Binpacking bp = new Binpacking("files/file1.txt", false);
        //bp.firstFit(1);
        //bp.firstFit(2);
        bp.OneItemPerBin();
        bp.printBins();
        //bp.RecuitSimule(70.00, 50, 10, 0.95);
        bp.TabuSearch(5, 100);
        System.out.println("nbbins = " + bp.nb_bin);
        bp.printBins();
    }
}
