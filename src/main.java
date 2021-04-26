public class main {
    public static void main(String[] args)throws Exception
    {
        Binpacking bp = new Binpacking("files/binpack1d_31.txt", false);
        //bp.firstFit(1);
        //bp.firstFit(2);
        bp.OneItemPerBin();
        //bp.printBins();
        bp.relocateLoop(100);
        bp.exchangeLoop(100);
        bp.relocateLoop(100);
        bp.exchangeLoop(100);
        bp.relocateLoop(100);
        bp.exchangeLoop(100);
        bp.relocateLoop(100);
        bp.exchangeLoop(100);
        System.out.println("nbbins = " + bp.nb_bin);
        //bp.printBins();
    }
}
