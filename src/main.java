public class main {
    public static void main(String[] args)throws Exception
    {
        Binpacking bp = new Binpacking("files/file1.txt", false);
        bp.firstFit(1);
        bp.firstFit(2);
        bp.OneItemPerBin();
    }
}
