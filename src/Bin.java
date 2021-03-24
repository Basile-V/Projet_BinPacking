public class Bin {
    int bin_size;
    int[] objects;
    int remaining_space;
    int nb_object;
    boolean isFull;
    boolean verbose;

    public Bin(int size, boolean verbose) {
        if (verbose)
            System.out.println("Creation d'un bin");
        this.bin_size = size;
        this.remaining_space = size;
        this.isFull = false;
        this.objects = new int[size];
        this.nb_object = 0;
        this.verbose = verbose;
    }

    public boolean addObject(int size) {
        if (this.verbose)
            System.out.println("Ajout d'un objet de taille :" + size);
        if (this.remaining_space >= size) {
            this.objects[this.nb_object] = size;
            this.nb_object += 1;
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.nb_object);
            this.remaining_space -= size;
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.remaining_space == 0)
                this.isFull = true;
            return true;
        } else
            return false;
    }
}
