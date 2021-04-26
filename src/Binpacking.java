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
    boolean verbose;

    public Binpacking(String path, boolean verbose) {
        try {
            this.verbose = verbose;
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

    public void firstFit(int order){
        clearBins();
        if (order == 1)
            data = decreasingOrdering();
        if (order == 2)
            data = randomOrdering();

        for(var i = data.length-1; i >=0; i--) {
            boolean ajout = false;
            if (this.verbose) {
                System.out.println("");
                System.out.println("i : " + (data.length-1-i));
                System.out.println("Nouvel objet de taille " + data[i]);
            }
            for (int j = 0; j < nb_bin; j++)  {
                if (this.verbose) {
                    System.out.println("");
                    System.out.println("Bin " + (j + 1));
                }
                if (!this.bins[j].addObject(data[i])) {
                    if (this.verbose)
                        System.out.println("Impossible de l'ajouter");
                }
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
        if (this.verbose)
            printBins();
    }

    public void RecuitSimule(double initTemp, int n1, int n2, double mu){
        float p;
        double tk = initTemp;
        int delta;
        int bestScore = objectiveFunction();
        Bin[] lastSave;
        Bin[] bestBin = this.cloneBins();
        for (int k = 0; k < n1; k++){
            for(int l = 1; l < n2; l++){
                lastSave = this.cloneBins();
                int lastScore = objectiveFunction();
                Random random = new Random();
                int choice = random.nextInt(2);
                if(choice == 0){
                    this.relocateLoop();
                }else {
                    this.exchangeLoop();
                }
                int newScore = objectiveFunction();
                delta = newScore - lastScore;
                if(bestScore <= newScore){
                    bestScore = newScore;
                    bestBin = this.cloneBins();
                }
                else if(newScore < lastScore){
                    p = random.nextFloat();
                    if(p > Math.exp(-delta / tk)){
                        this.bins = lastSave;
                    }
                }
            }
            tk = mu * tk;
        }
        this.bins = bestBin;
    }


    public void OneItemPerBin() {
        clearBins();
        for (var i = 0; i < this.data.length ; i++) {
            if (this.verbose) {
                System.out.println("");
                System.out.println("i : " + i);
                System.out.println("Nouvel objet de taille " + this.data[i]);
            }
            addBin();
            this.bins[i].addObject(this.data[i]);
        }
        System.out.println("");
        System.out.println("Nombre total de bin : " + this.nb_bin);
        if (this.verbose)
            for (int i = 0; i < this.nb_bin; i++) {
                System.out.println("");
                System.out.println("Bin " + (i + 1) + " :");
                for (int j = 0; j < this.bins[i].nb_object; j++)
                    System.out.println(this.bins[i].objects[j]);
            }
    }

    public void addBin() {
        this.bins[nb_bin] = new Bin(this.bin_size, this.verbose);
        this.nb_bin += 1;
    }

    public void clearBins() {
        this.bins = new Bin[this.nb_item];
        this.nb_bin = 0;
    }

    public int[] decreasingOrdering() {
        int[] data = new int[this.data.length];
        for(var i = this.data.length-1; i >=0; i--)
            data[i] = this.data[i];

        Arrays.sort(data);
        return data;
    }

    public int[] randomOrdering() {
        int[] data = new int[this.data.length];
        int[] added = new int[this.data.length];
        for(var i = added.length-1; i >=0; i--)
            added[i] = -1;
        for(var i = this.data.length-1; i >= 0; i--) {
            Random r = new Random();
            int random = r.nextInt(this.data.length);
            boolean newPlace = false;
            while (!newPlace) {
                newPlace = true;
                Random ra = new Random();
                random = ra.nextInt(this.data.length);
                for(var j = 0; j < added.length; j++)
                    if (added[j] == random)
                        newPlace = false;

            }
            data[i] = this.data[random];
            added[i] = random;
        }
        return data;
    }

    public boolean relocate(int sourceBin, int itemNumber, int destinationBin) {
        if (this.bins[sourceBin].nb_object <= itemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + itemNumber + " dans le bin " + sourceBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[destinationBin].remaining_space < this.bins[sourceBin].objects[itemNumber]) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        int oldValue = objectiveFunction();
        if (this.bins[destinationBin].addObject(this.bins[sourceBin].objects[itemNumber])) {
            this.bins[sourceBin].removeObject(itemNumber);
            int newValue = objectiveFunction();
            if (newValue<oldValue) {
                relocate(destinationBin, this.bins[destinationBin].nb_object - 1, sourceBin);
            }
            clearEmptyBins();
            return  true;
        }

        return  false;
    }

    public int objectiveFunction (){
        int sum = 0;
        for (int i = 0; i < this.nb_bin; i++) {
            int square = 0;
            for (int j = 0; j < this.bins[i].nb_object; j++)
                square += this.bins[i].objects[j];
            sum += Math.pow(square,2);
        }
        return sum;
    }

    public boolean exchange(int sourceBin, int sourceItemNumber, int destinationBin, int destinationItemNumber) {
        if (this.bins[sourceBin].nb_object <= sourceItemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + sourceItemNumber + " dans le bin " + sourceBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[destinationBin].nb_object <= destinationItemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + destinationItemNumber + " dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[destinationBin].remaining_space + this.bins[destinationBin].objects[destinationItemNumber] < this.bins[sourceBin].objects[sourceItemNumber]) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[sourceBin].remaining_space + this.bins[sourceBin].objects[sourceItemNumber] < this.bins[destinationBin].objects[destinationItemNumber]) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        int oldValue = objectiveFunction();
        int size = this.bins[destinationBin].objects[destinationItemNumber];
        this.bins[destinationBin].removeObject(destinationItemNumber);
        this.bins[destinationBin].addObject(this.bins[sourceBin].objects[sourceItemNumber]);
        this.bins[sourceBin].removeObject(sourceItemNumber);
        this.bins[sourceBin].addObject(size);
        int newValue = objectiveFunction();
        if (newValue<oldValue) {
            exchange(sourceBin, this.bins[sourceBin].nb_object-1, destinationBin, this.bins[destinationBin].nb_object-1);
        }
        return  true;
    }

    public void printBins() {
        int somme = 0;
        for (int i = 0; i < this.nb_bin; i++) {
            System.out.println("");
            System.out.println("Bin " + (i+1) + " :");
            for (int j = 0; j < this.bins[i].nb_object; j++){
                System.out.println(this.bins[i].objects[j]);
                somme++;
            }
        }
        System.out.println("Somme: " + somme);
    }

    public Bin[] cloneBins() {
        Bin[] copy = new Bin[this.nb_bin];
        for (int i = 0; i < this.nb_bin; i++) {
            copy[i] = this.bins[i].clone();
        }
        return copy;
    }

    public void clearEmptyBins() {
        for (int i = 0; i < this.nb_bin; i++) {
            if (this.bins[i].nb_object == 0) {
                this.nb_bin -= 1;
                for (int j = i; j < this.nb_bin; j++)
                    this.bins[j] = this.bins[j+1];

            }
        }
    }

    public void relocateLoop(int times) {
        for (int i = 0; i < times; i++) {
            Random random = new Random();
            int source = random.nextInt(this.nb_bin);
            random = new Random();
            int destination = random.nextInt(this.nb_bin);
            random = new Random();
            int itemNumber = random.nextInt(this.bins[source].nb_object);
            if (!relocate(source, itemNumber, destination) & this.verbose)
                System.out.println("Echec de la relocation numéro "+ i);
        }
    }

    public void relocateLoop() {
        boolean reloc = false;
        while (!reloc) {
            Random random = new Random();
            int source = random.nextInt(this.nb_bin);
            random = new Random();
            int destination = random.nextInt(this.nb_bin);
            random = new Random();
            int itemNumber = random.nextInt(this.bins[source].nb_object);
            if (relocate(source, itemNumber, destination))
                reloc = true;
        }
    }

    public void exchangeLoop(int times) {
        for (int i = 0; i < times; i++) {
            Random random = new Random();
            int source = random.nextInt(this.nb_bin);
            random = new Random();
            int destination = random.nextInt(this.nb_bin);
            random = new Random();
            int sourceItemNumber = random.nextInt(this.bins[source].nb_object);
            random = new Random();
            int destinationItemNumber = random.nextInt(this.bins[destination].nb_object);
            if (!exchange(source, sourceItemNumber, destination, destinationItemNumber) & this.verbose)
                System.out.println("Echec de l'échange numéro "+ i);
        }
    }

    public void exchangeLoop() {
        boolean exchange = false;
        while (!exchange) {
            Random random = new Random();
            int source = random.nextInt(this.nb_bin);
            random = new Random();
            int destination = random.nextInt(this.nb_bin);
            random = new Random();
            int sourceItemNumber = random.nextInt(this.bins[source].nb_object);
            random = new Random();
            int destinationItemNumber = random.nextInt(this.bins[destination].nb_object);
            if (exchange(source, sourceItemNumber, destination, destinationItemNumber))
                exchange = true;
        }
    }


}
