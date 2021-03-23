import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Binpacking {
    int[] data;
    Bin[] bins;
    int bin_size;
    int nb_item;
    int nb_bin;
    int min_nb_bin;

    public Binpacking(String path) {
        try {
            File file = new File(path);
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            String line;
            int nb_line = 1;
            while ((line = reader.readLine()) != null) {

                if (nb_line == 1) {
                    String[] header = line.split(" ");
                    this.bin_size = Integer.parseInt(header[0]);
                    this.nb_item = Integer.parseInt(header[1]);
                    this.nb_bin = 0;
                    this.bins = new Bin[this.nb_item];
                    this.data = new int[this.nb_item];
                } else {
                    this.data[nb_line-2] = Integer.parseInt(line);
                }
                nb_line += 1;
            }
            System.out.println("nombre d'item : " + this.nb_item);
            System.out.println("taille des bin : " + this.bin_size);
            System.out.println("nombre de bin : " + this.nb_bin);
            int total_data = 0;
            for(int i=0; i < this.data.length; i++) {
                total_data += this.data[i];
                System.out.println(this.data[i]);
            }
            int min_bin = 1 + (total_data / this.bin_size);
            System.out.println("Nombre minimal de bin : " + min_bin);
            this.min_nb_bin = min_bin;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void firstFitDecreasing(int order){
        clearBins();
        if (order == 1)
            data = decreasing();
        if (order == 2)
            data = decreasing();

        for(var i = data.length-1; i >=0; i--) {
            boolean ajout = false;
            System.out.println("");
            System.out.println("i : " + (data.length-1-i));
            System.out.println("Nouvel objet de taille " + data[i]);
            if (this.nb_bin == 0)
                addBin();;
            for (int j = 0; j < nb_bin; j++)  {
                System.out.println("");
                System.out.println("Bin " + (j+1));
                if (!this.bins[j].addObject(data[i]))
                    System.out.println("Impossible de l'ajouter");
                else {
                    ajout = true;
                    break;
                }
            }
            if (!ajout){
                addBin();
                this.bins[this.nb_bin-1].addObject(data[i]);
            }
        }
        System.out.println("");
        System.out.println("Nombre total de bin : " + this.nb_bin);
        for (int i = 0; i < this.nb_bin; i++) {
            System.out.println("");
            System.out.println("Bin " + (i+1) + " :");
            for (int j = 0; j < this.bins[i].nb_object; j++)
                System.out.println(this.bins[i].objects[j]);
        }
    }

    public void OneItemPerBin() {
        clearBins();
        for (var i = 0; i < this.data.length ; i++) {
            System.out.println("");
            System.out.println("i : " + i);
            System.out.println("Nouvel objet de taille " + this.data[i]);
            addBin();
            this.bins[i].addObject(this.data[i]);
        }
        System.out.println("");
        System.out.println("Nombre total de bin : " + this.nb_bin);
        for (int i = 0; i < this.nb_bin; i++) {
            System.out.println("");
            System.out.println("Bin " + (i + 1) + " :");
            for (int j = 0; j < this.bins[i].nb_object; j++)
                System.out.println(this.bins[i].objects[j]);
        }
    }

    public void addBin() {
        this.bins[nb_bin] = new Bin(this.bin_size);
        this.nb_bin += 1;
    }

    public void clearBins() {
        this.bins = new Bin[this.nb_item];
        this.nb_bin = 0;
    }

    public int[] decreasing() {
        int[] data = new int[this.data.length];
        for(var i = this.data.length-1; i >=0; i--)
            data[i] = this.data[i];

        Arrays.sort(data);
        return data;
    }

    public int[] random() {
        int[] data = new int[this.data.length];
        int[] added = new int[this.data.length];
        Random r = new Random();
        int random = r.nextInt(this.data.length);
        for(var i = this.data.length-1; i >=0; i--) {
            while ((Arrays.asList(added).contains(random))) {
                Random ra = new Random();
                random = ra.nextInt(this.data.length);
            }
            data[i] = this.data[random];
            added[i] = random;
        }
        for(var i = this.data.length-1; i >=0; i--)
            System.out.println(data[i]);
        return data;
    }

}
