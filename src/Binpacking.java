import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Binpacking {
    ArrayList<Item> data;
    List<Bin> bins;
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
            this.bins = new ArrayList<Bin>();
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
            System.out.println("Borne indérieure de bin : " + min_bin);
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
        for(var i = 0; i < this.data.size(); i++) {
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
                if (!this.bins.get(j).addObject(data.get(i))) {
                    if (this.verbose)
                        System.out.println("Impossible de l'ajouter");
                }
                else {
                    ajout = true;
                    break;
                }
            }
            if (!ajout){
                addBin(i);
                this.bins.get(this.nb_bin-1).addObject(data.get(i));
            }
        }
        if (this.verbose)
            printBins();
    }

    public void RecuitSimule(double initTemp, int n1, int n2, double mu){
        double p;
        double tk = initTemp;
        int delta;
        int bestScore = objectiveFunction();
        int newScore;
        int fxi;
        List<Bin> xi;
        Random random = new Random();
        int choice;
        List<Bin> bestBin = new ArrayList<>(this.bins);
        for (int k = 0; k < n1; k++){
            for(int l = 1; l < n2; l++){
                fxi = objectiveFunction();
                xi = cloneBins();
                choice = random.nextInt(2);
                if(choice == 0){
                    this.relocateLoop();
                }else {
                    this.exchangeLoop();
                }
                newScore = objectiveFunction();
                delta = newScore - fxi;
                if(delta >= 0){
                    if(newScore > bestScore){
                        bestBin = cloneBins();
                        bestScore = newScore;
                    }
                }
                else{
                    p = random.nextFloat();
                    if(p < Math.exp((-delta) / tk)){
                        this.bins = xi;
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
        List<Bin> bestBin = this.cloneBins();
        List<Bin> currentX = this.cloneBins();
        List<Integer> lastChange = new ArrayList<>();
        List<List<Integer>> T = new ArrayList<>();
        int bestVoisinScore;
        boolean acceptable;
        List<Bin> bestVoisinBin;
        int newScore;
        int idItem1;
        int idItem2;
        int idBin1;
        int idBin2;
        for (int i = 0; i < nbIter; i++) {
            //RECHERCHE DE TOUS LES VOISINS
            bestVoisinScore = 0;
            bestVoisinBin = this.cloneBins();
            for (int bin1 = 0; bin1 < nb_bin; bin1++) {
                for (int bin2 = 0; bin2 < nb_bin; bin2++) {
                    for (int item1 = 0; item1 < bins.get(bin1).getnbObject(); item1++) {
                        idItem1 = this.bins.get(bin1).getObjects().get(item1).getWeight();
                        idBin2 = this.bins.get(bin2).getId();
                        idBin1 = this.bins.get(bin1).getId();
                        acceptable = acceptable(T, idBin2, idBin1, idItem1);
                        if (acceptable) {
                            this.bins = cloneBins(currentX);
                            this.nb_bin = this.bins.size();
                            if (relocate(bin1, item1, bin2)) {
                                newScore = objectiveFunction();
                                if (bestVoisinScore <= newScore) {
                                    bestVoisinScore = newScore;
                                    bestVoisinBin = this.cloneBins();
                                }
                                else {
                                    lastChange = new ArrayList<Integer>(Arrays.asList(idBin2, idBin1, idItem1));
                                }
                            }
                        }
                        this.bins = cloneBins(currentX);
                        this.nb_bin = this.bins.size();
                        for (int item2 = 0; item2 < bins.get(bin2).getnbObject(); item2++) {
                            if(!(bin1 == bin2 && item1 == item2)){
                                idItem2 = this.bins.get(bin2).getObjects().get(item2).getWeight();
                                acceptable = acceptable(T, idBin1, idItem1, idBin2, idItem2);
                                if (acceptable) {
                                    this.bins = cloneBins(currentX);
                                    this.nb_bin = this.bins.size();
                                    if (exchange(bin1, item1, bin2, item2)) {
                                        newScore = objectiveFunction();
                                        if (bestVoisinScore <= newScore) {
                                            bestVoisinScore = newScore;
                                            bestVoisinBin = this.cloneBins();
                                        }
                                        else {
                                            lastChange = new ArrayList<Integer>(Arrays.asList(idBin1, idItem1, idBin2, idItem2));
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
        this.nb_bin = bestBin.size();
        if (this.verbose) {
            printBins();
        }
    }

    public void OneItemPerBin(int order) {
        clearBins();
        if (order == 1)
            Collections.sort(data, new CompareWeight());
        if (order == 2)
            Collections.shuffle(this.data);
        for (var i = 0; i < this.data.size() ; i++) {
            if (this.verbose) {
                System.out.println("");
                System.out.println("i : " + i);
                System.out.println("Nouvel objet de taille " + this.data.get(i).getWeight());
            }
            addBin(i);
            this.bins.get(i).addObject(this.data.get(i));
        }
        if (this.verbose)
            printBins();
    }

    public boolean acceptable(List<List<Integer>> T, int bin1, int bin2, int item) {
        List<Integer> aled = new ArrayList<Integer>(Arrays.asList(bin1, bin2, item));
        return !(T.contains(aled) || bin1 == bin2);
    }

    public boolean acceptable(List<List<Integer>> T, int bin1, int item1, int bin2, int item2) {
        List<Integer> aled1 = new ArrayList<Integer>(Arrays.asList(bin1, item1, bin2, item2));
        List<Integer> aled2 = new ArrayList<Integer>(Arrays.asList(bin2, item2, bin1, item1));
        return !(T.contains(aled1) || T.contains(aled2));
    }

    public void addBin(int id) {
        this.bins.add(new Bin(this.bin_size, this.verbose, id));
        this.nb_bin += 1;
    }

    public void clearBins() {
        this.bins = new ArrayList<Bin>();
        this.nb_bin = 0;
    }

    public boolean relocate(int sourceBin, int itemNumber, int destinationBin) {
        if(sourceBin == destinationBin){
            return false;
        }
        if (this.bins.get(sourceBin).getnbObject() <= itemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + itemNumber + " dans le bin " + sourceBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins.get(destinationBin).remaining_space < this.bins.get(sourceBin).getObjects().get(itemNumber).getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins.get(destinationBin).addObject(this.bins.get(sourceBin).getObjects().get(itemNumber))) {
            this.bins.get(sourceBin).removeObject(itemNumber);
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
            for (int j = 0; j < this.bins.get(i).getnbObject(); j++)
                square += this.bins.get(i).getObjects().get(j).getWeight();
            sum += Math.pow(square,2);
        }
        return sum;
    }

    public boolean exchange(int sourceBin, int sourceItemNumber, int destinationBin, int destinationItemNumber) {
        if (sourceBin == destinationBin) {
            return false;
        }
        if (this.bins.get(sourceBin).getnbObject() <= sourceItemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + sourceItemNumber + " dans le bin " + sourceBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins.get(destinationBin).getnbObject() <= destinationItemNumber) {
            if (this.verbose) {
                System.out.println("Il n'y a pas d'item " + destinationItemNumber + " dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins.get(destinationBin).remaining_space + this.bins.get(destinationBin).getObjects().get(destinationItemNumber).getWeight() < this.bins.get(sourceBin).getObjects().get(sourceItemNumber).getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        if (this.bins.get(sourceBin).remaining_space + this.bins.get(sourceBin).getObjects().get(sourceItemNumber).getWeight() < this.bins.get(destinationBin).getObjects().get(destinationItemNumber).getWeight()) {
            if (this.verbose) {
                System.out.println("Il n'y a pas de place suffisante dans le bin " + destinationBin);
                System.out.println("");
            }
            return false;
        }
        Item item = this.bins.get(destinationBin).objects.get(destinationItemNumber);
        this.bins.get(destinationBin).removeObject(destinationItemNumber);
        this.bins.get(destinationBin).addObject(this.bins.get(sourceBin).getObjects().get(sourceItemNumber));
        this.bins.get(sourceBin).removeObject(sourceItemNumber);
        this.bins.get(sourceBin).addObject(item);
        return  true;
    }

    public void printBins() {
        int somme = 0;
        for (int i = 0; i < this.nb_bin; i++) {
            System.out.println("");
            System.out.println("Bin " + (i+1) + " :");
            for (int j = 0; j < this.bins.get(i).getnbObject(); j++){
                System.out.println(this.bins.get(i).getObjects().get(j).getWeight());
                somme++;
            }
        }
        System.out.println("Somme: " + somme);
    }

    public List<Bin> cloneBins() {
        List<Bin> copy = new ArrayList<Bin>();
        Bin bin;
        for (int i = 0; i < this.nb_bin; i++) {
            bin = new Bin(bin_size, verbose, this.bins.get(i).getId());
            for (int j = 0; j < this.bins.get(i).getnbObject(); j++) {
                bin.addObject(this.bins.get(i).getObjects().get(j));
            }
            copy.add(bin);
        }
        return copy;
    }

    public List<Bin> cloneBins(List<Bin> bins) {
        List<Bin> copy = new ArrayList<Bin>();
        Bin bin;
        for (int i = 0; i < bins.size(); i++) {
            bin = new Bin(bin_size, verbose, bins.get(i).getId());
            for (int j = 0; j < bins.get(i).getnbObject(); j++) {
                bin.addObject(bins.get(i).getObjects().get(j));
            }
            copy.add(bin);
        }
        return copy;
    }

    public void clearEmptyBins() {
        for (int i = 0; i < this.nb_bin; i++) {
            if (this.bins.get(i).getnbObject() == 0) {
                this.nb_bin -= 1;
                this.bins.remove(i);
            }
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
            itemNumber = random.nextInt(this.bins.get(source).getnbObject());
            if (relocate(source, itemNumber, destination))
                reloc = true;
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
            sourceItemNumber = random.nextInt(this.bins.get(source).getnbObject());
            destinationItemNumber = random.nextInt(this.bins.get(destination).getnbObject());
            if (exchange(source, sourceItemNumber, destination, destinationItemNumber))
                exchange = true;
        }
    }


}
