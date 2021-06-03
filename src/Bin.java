public class Bin implements Cloneable {
    int bin_size;
    Item[] objects;
    int remaining_space;
    int nb_object;
    boolean isFull;
    boolean verbose;
    int id;

    public Bin(int size, boolean verbose) {
        if (verbose)
            System.out.println("Creation d'un bin");
        this.bin_size = size;
        this.remaining_space = size;
        this.isFull = false;
        this.objects = new Item[size];
        this.nb_object = 0;
        this.verbose = verbose;
        this.id = 0;
    }

    public boolean addObject(Item item) {
        if (this.verbose)
            System.out.println("Ajout d'un objet de taille :" + item.getWeight());
        if (this.remaining_space >= item.getWeight()) {
            this.objects[this.nb_object] = item;
            this.nb_object += 1;
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.nb_object);
            this.remaining_space -= item.getWeight();
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.remaining_space == 0)
                this.isFull = true;
            return true;
        } else
            return false;
    }

    public boolean removeObject(int number) {
        if (this.verbose)
            System.out.println("Suppression de l'objet");
        if (this.nb_object >= number) {
            Item removedObject = this.objects[number];
            for (int i = number; i < nb_object; i++)
                this.objects[i] = this.objects[i+1];
            this.nb_object -= 1;
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.nb_object);
            this.remaining_space += removedObject.getWeight();
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.remaining_space > 0)
                this.isFull = false;
            return true;
        } else
            return false;
    }

    public int getId(){
        return this.id;
    }
}
