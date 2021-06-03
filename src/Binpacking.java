import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Binpacking {
    ArrayList<Item> data;
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
            line = reader.readLine();
            String[] header = line.split(" ");
            this.bin_size = Integer.parseInt(header[0]);
            this.nb_item = Integer.parseInt(header[1]);
            this.nb_bin = 0;
            this.bins = new Bin[this.nb_item];
            this.data = new ArrayList<Item>();
            int nb_line = 0;
            int total_data = 0;
            Item item;
            while ((line = reader.readLine()) != null) {
                item = new Item(nb_line++,  Integer.parseInt(line));
                this.data.add(item);
                if (verbose) {
                    System.out.println(line);
                }
                total_data += Integer.parseInt(line);
            }
            System.out.println("nombre d'item : " + this.nb_item);
            System.out.println("taille des bin : " + this.bin_size);
            System.out.println("nombre de bin : " + this.nb_bin);

            int min_bin = total_data / this.bin_size;
            if(total_data % this.bin_size != 0){
                min_bin++;
            }
            System.out.println("Nombre minimal de bin : " + min_bin);
            this.min_nb_bin = min_bin;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void firstFit(int order){
        clearBins();
        if (order == 1)
            Collections.sort(data, new CompareWeight());
        if (order == 2)
            Collections.shuffle(this.data);

        for(var i = data.size() -1; i >=0; i--) {
            boolean ajout = false;
            if (this.verbose) {
                System.out.println("");
                System.out.println("i : " + (data.size() - 1 - i));
                System.out.println("Nouvel objet de taille " + data.get(i).getWeight());
            }
            for (int j = 0; j < nb_bin; j++)  {
                if (this.verbose) {
                    System.out.println("");
                    System.out.println("Bin " + (j + 1));
                }
                if (!this.bins[j].addObject(data.get(i))) {
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
                this.bins[this.nb_bin-1].addObject(data.get(i));
            }
        }
        if (this.verbose)
            printBins();
    }

    public void RecuitSimule(double initTemp, int n1, int n2, double mu){
        float p;
        double tk = initTemp;
        int delta;
        int bestScore = objectiveFunction();
        int newScore;
        Bin[] y;
        Bin[] xi;
        Random random = new Random();
        int choice;
        Bin[] bestBin = this.cloneBins();
        for (int k = 0; k < n1; k++){
            for(int l = 1; l < n2; l++){
                xi = this.cloneBins();
                choice = random.nextInt(2);
                if(choice == 0){
                    this.relocateLoop();
                }else {
                    this.exchangeLoop();
                }
                y = this.cloneBins();
                this.bins = xi;
                newScore = objectiveFunction(y);
                delta = newScore - objectiveFunction();
                if(delta >= 0){
                    this.bins = y;
                    if(newScore > bestScore){
                        bestBin = y;
                        bestScore = newScore;
                    }
                }
                else{
                    p = random.nextFloat();
                    if(p <= Math.exp(-delta / tk)){
                        this.bins = y;
                    }
                }
            }
            tk = mu * tk;
        }
        this.bins = bestBin;
        if (this.verbose) {
            printBins();
        }
    }

    public void TabuSearch(int tabuSize, int nbIter){
        int bestScore = objectiveFunction();
        int currentScore = objectiveFunction();
        Bin[] bestBin = this.cloneBins();
        Bin[] currentX = this.cloneBins();
        List<Integer> lastChange = new ArrayList<>();
        List<List<Integer>> T = new ArrayList<>();
        int bestVoisinScore;
        boolean acceptable;
        Bin[] bestVoisinBin;
        int newScore;
        int idItem1;
        int idItem2;
        for (int i = 0; i < nbIter; i++) {
            //RECHERCHE DE TOUS LES VOISINS
            bestVoisinScore = 0;
            bestVoisinBin = this.cloneBins();
            for (int bin1 = 0; bin1 < nb_bin; bin1++) {
                for (int bin2 = 0; bin2 < nb_bin; bin2++) {
                    for (int item1 = 0; item1 < bins[bin1].nb_object; item1++) {
                        idItem1 = this.bins[bin1].objects[item1].getWeight();
                        acceptable = acceptable(T, bin2, bin1, idItem1);
                        if (acceptable) {
                            this.bins = cloneBins(currentX);
                            this.nb_bin = this.bins.length;
                            if (relocate(bin1, item1, bin2)) {
                                newScore = objectiveFunction();
                                if (bestVoisinScore <= newScore) {
                                    bestVoisinScore = newScore;
                                    bestVoisinBin = this.cloneBins();
                                }
                                else {
                                    lastChange = new ArrayList<Integer>(Arrays.asList(bin2, bin1, idItem1));
                                }
                            }
                        }
                        this.bins = cloneBins(currentX);
                        this.nb_bin = this.bins.length;
                        for (int item2 = 0; item2 < bins[bin2].nb_object; item2++) {
                            if(!(bin1 == bin2 && item1 == item2)){
                                idItem2 = this.bins[bin2].objects[item2].getWeight();
                                acceptable = acceptable(T, bin1, idItem1, bin2, idItem2);
                                if (acceptable) {
                                    this.bins = cloneBins(currentX);
                                    this.nb_bin = this.bins.length;
                                    if (exchange(bin1, item1, bin2, item2)) {
                                        newScore = objectiveFunction();
                                        if (bestVoisinScore <= newScore) {
                                            bestVoisinScore = newScore;
                                            bestVoisinBin = this.cloneBins();
                                        }
                                        else {
                                            lastChange = new ArrayList<Integer>(Arrays.asList(bin1, idItem1, bin2, idItem2));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentX = bestVoisinBin;
            if (bestVoisinScore >= currentScore) {
                T.add(lastChange);
                if(T.size() > tabuSize){
                    T.remove(0);
                }
            }
            if (bestVoisinScore > bestScore) {
                bestScore = bestVoisinScore;
                bestBin = bestVoisinBin;
            }
        }
        this.bins = bestBin;
        this.nb_bin = bestBin.length;
        if (this.verbose) {
            printBins();
        }
    }

    public void OneItemPerBin() {
        clearBins();
        for (var i = 0; i < this.data.size() ; i++) {
            if (this.verbose) {
                System.out.println("");
                System.out.println("i : " + i);
                System.out.println("Nouvel objet de taille " + this.data.get(i));
            }
            addBin();
            this.bins[i].addObject(this.data.get(i));
        }
        if (this.verbose)
            printBins();
    }

    public boolean acceptable(List<List<Integer>> T, int bin1, int bin2, int item) {
        List<Integer> aled = new ArrayList<Integer>(Arrays.asList(bin1, bin2, item));
        return !T.contains(aled);
    }

    public boolean acceptable(List<List<Integer>> T, int bin1, int item1, int bin2, int item2) {
        List<Integer> aled1 = new ArrayList<Integer>(Arrays.asList(bin1, item1, bin2, item2));
        List<Integer> aled2 = new ArrayList<Integer>(Arrays.asList(bin2, item2, bin1, item1));
        return !(T.contains(aled1) || T.contains(aled2));
    }

    public void addBin() {
        this.bins[nb_bin] = new Bin(this.bin_size, this.verbose);
        this.nb_bin += 1;
    }

    public void clearBins() {
        this.bins = new Bin[this.nb_item];
        this.nb_bin = 0;
    }

    public boolean relocate(int sourceBin, int itemNumber, int destinationBin) {
        if (this.bins[sourceBin].nb_object <= itemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + itemNumber + " dans le bin " + sourceBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[destinationBin].remaining_space < this.bins[sourceBin].objects[itemNumber].getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[destinationBin].addObject(this.bins[sourceBin].objects[itemNumber])) {
            this.bins[sourceBin].removeObject(itemNumber);
            clearEmptyBins();
            return true;
        }

        return false;
    }

    public int objectiveFunction (){
        int sum = 0;
        int square;
        for (int i = 0; i < this.nb_bin; i++) {
            square = 0;
            for (int j = 0; j < this.bins[i].nb_object; j++)
                square += this.bins[i].objects[j].getWeight();
            sum += Math.pow(square,2);
        }
        return sum;
    }

    public int objectiveFunction (Bin[] data){
        int sum = 0;
        int square;
        for (int i = 0; i < data.length; i++) {
            square = 0;
            for (int j = 0; j < data[i].nb_object; j++)
                square += data[i].objects[j].getWeight();
            sum += Math.pow(square,2);
        }
        return sum;
    }

    public boolean exchange(int sourceBin, int sourceItemNumber, int destinationBin, int destinationItemNumber) {
        if (sourceBin == destinationBin) {
            return false;
        }
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
        if (this.bins[destinationBin].remaining_space + this.bins[destinationBin].objects[destinationItemNumber].getWeight() < this.bins[sourceBin].objects[sourceItemNumber].getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins[sourceBin].remaining_space + this.bins[sourceBin].objects[sourceItemNumber].getWeight() < this.bins[destinationBin].objects[destinationItemNumber].getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        Item item = this.bins[destinationBin].objects[destinationItemNumber];
        this.bins[destinationBin].removeObject(destinationItemNumber);
        this.bins[destinationBin].addObject(this.bins[sourceBin].objects[sourceItemNumber]);
        this.bins[sourceBin].removeObject(sourceItemNumber);
        this.bins[sourceBin].addObject(item);
        return  true;
    }

    public void printBins() {
        int somme = 0;
        for (int i = 0; i < this.nb_bin; i++) {
            System.out.println("");
            System.out.println("Bin " + (i+1) + " :");
            for (int j = 0; j < this.bins[i].nb_object; j++){
                System.out.println(this.bins[i].objects[j].getWeight());
                somme++;
            }
        }
        System.out.println("Somme: " + somme);
    }

    public Bin[] cloneBins() {
        Bin[] copy = new Bin[this.nb_bin];
        Bin bin;
        for (int i = 0; i < this.nb_bin; i++) {
            bin = new Bin(bin_size, verbose);
            for (int j = 0; j < this.bins[i].nb_object; j++) {
                bin.addObject(this.bins[i].objects[j]);
            }
            copy[i] = bin;
        }
        return copy;
    }

    public Bin[] cloneBins(Bin[] bins) {
        Bin[] copy = new Bin[bins.length];
        Bin bin;
        for (int i = 0; i < bins.length; i++) {
            bin = new Bin(bin_size, verbose);
            for (int j = 0; j < bins[i].nb_object; j++) {
                bin.addObject(bins[i].objects[j]);
            }
            copy[i] = bin;
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
        int source;
        int destination;
        Random random = new Random();
        int itemNumber;
        for (int i = 0; i < times; i++) {
            source = random.nextInt(this.nb_bin);
            destination = random.nextInt(this.nb_bin);
            itemNumber = random.nextInt(this.bins[source].nb_object);
            if (!relocate(source, itemNumber, destination) & this.verbose)
                System.out.println("Echec de la relocation numéro "+ i);
        }
    }

    public void relocateLoop() {
        boolean reloc = false;
        int nbTry = 0;
        Random random = new Random();
        int source;
        int destination;
        int itemNumber;
        while (!reloc && nbTry++ < 10000) {
            source = random.nextInt(this.nb_bin);
            destination = random.nextInt(this.nb_bin);
            itemNumber = random.nextInt(this.bins[source].nb_object);
            if (relocate(source, itemNumber, destination))
                reloc = true;
        }
    }

    public void exchangeLoop(int times) {
        Random random = new Random();
        int source;
        int destination;
        int sourceItemNumber;
        int destinationItemNumber;
        for (int i = 0; i < times; i++) {
            source = random.nextInt(this.nb_bin);
            destination = random.nextInt(this.nb_bin);
            sourceItemNumber = random.nextInt(this.bins[source].nb_object);
            destinationItemNumber = random.nextInt(this.bins[destination].nb_object);
            if (!exchange(source, sourceItemNumber, destination, destinationItemNumber) & this.verbose)
                System.out.println("Echec de l'échange numéro "+ i);
        }
    }

    public void exchangeLoop() {
        boolean exchange = false;
        int nbTry = 0;
        Random random = new Random();
        int source;
        int destination;
        int sourceItemNumber;
        int destinationItemNumber;
        while (!exchange&& nbTry++ < 10000) {
            source = random.nextInt(this.nb_bin);
            destination = random.nextInt(this.nb_bin);
            sourceItemNumber = random.nextInt(this.bins[source].nb_object);
            destinationItemNumber = random.nextInt(this.bins[destination].nb_object);
            if (exchange(source, sourceItemNumber, destination, destinationItemNumber))
                exchange = true;
        }
    }


}
