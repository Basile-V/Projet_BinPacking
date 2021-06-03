import java.util.ArrayList;
import java.util.List;

public class Bin implements Cloneable {
    int bin_size;
    List<Item> objects;
    int remaining_space;
    boolean isFull;
    boolean verbose;
    int id;

    public Bin(int size, boolean verbose, int id) {
        if (verbose)
            System.out.println("Creation d'un bin");
        this.bin_size = size;
        this.remaining_space = size;
        this.isFull = false;
        this.objects = new ArrayList<Item>();
        this.verbose = verbose;
        this.id = id;
    }

    public boolean addObject(Item item) {
        if (this.verbose)
            System.out.println("Ajout d'un objet de taille :" + item.getWeight());
        if (this.getRemainingSpace() >= item.getWeight()) {
            this.objects.add(item);
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.getnbObject());
            this.remaining_space -= item.getWeight();
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.getRemainingSpace() == 0)
                this.isFull = true;
            return true;
        } else
            return false;
    }

    public boolean removeObject(Item item) {
        if (this.verbose)
            System.out.println("Suppression de l'objet");
        if(this.getObjects().contains(item)){
            this.objects.remove(item);
            this.remaining_space += item.getWeight();
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.getnbObject());
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.remaining_space > 0)
                this.isFull = false;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeObject(int item) {
        if (this.verbose)
            System.out.println("Suppression de l'objet");
        if(this.getObjects().size() > item){
            this.remaining_space += this.getObjects().get(item).getWeight();
            this.objects.remove(item);
            if (this.verbose)
                System.out.println("Nombre d'objets :" + this.getnbObject());
            if (this.verbose)
                System.out.println("Espace restant :" + this.remaining_space);
            if (this.remaining_space > 0)
                this.isFull = false;
            return true;
        }
        else {
            return false;
        }
    }

    public int getId(){
        return this.id;
    }

    public int getRemainingSpace(){
        return this.remaining_space;
    }

    public int getnbObject(){
        return this.getObjects().size();
    }

    public List<Item> getObjects(){
        return this.objects;
    }
}
