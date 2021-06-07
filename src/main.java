import com.google.ortools.linearsolver.samples.BinPackingMip;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args)
    {
        long debut;
        long fin;
        int somme;
        int nbFirstFitRandom = 100;
        int nbRecuit = 100;
        int initChoice;
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez saisir le nom du fichier avec l'extention:");
        String fileName = "files/" +sc.nextLine();
        boolean verbose = false;
        Binpacking bp = new Binpacking(fileName, verbose);

            //------------     First Fit Decreasing   ------------

        System.out.println("\nFirst Fit Decreasing");
        debut = System.currentTimeMillis();
        bp.firstFit(1);
        fin = System.currentTimeMillis() - debut;
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");


        //------------     First Fit Random   ------------

        System.out.println("\nFirst Fit Random - Efficacité en temps");
        debut = System.currentTimeMillis();
        bp.firstFit(2);
        fin = System.currentTimeMillis() - debut;
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");

        System.out.println("\nFirst Fit Random - Efficacité en nb bins");
        somme = 0;
        for (int i = 0; i < nbFirstFitRandom; i++) {
            bp.firstFit(2);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + (double) somme/nbFirstFitRandom);


        //------------     One item per bin   ------------

        System.out.println("\nOne item per bin");
        debut = System.currentTimeMillis();
        bp.OneItemPerBin(1);
        fin = System.currentTimeMillis() - debut;
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");


        //------------     Recuit Simulé   ------------

        System.out.println("\nRecuit simulé - Efficacité en temps");
        System.out.println("\nTabu Search -  Efficacité en temps");
        System.out.println("Veuillez selectionner comment vous voulez initialiser les bins:");
        System.out.println("- Un item par bin rangé par ordre decroissant (entrer 1)");
        System.out.println("- Un item par bin rangé aléatoirement (entrer 2)");
        System.out.println("- First Fit Decreasing (entrer 3)");
        System.out.println("- First Fit Random (entrer 4)");
        initChoice = sc.nextInt();
        if(initChoice == 1){
            bp.OneItemPerBin(1);
        }else if(initChoice == 3){
            bp.firstFit(1);
        }else if(initChoice == 4){
            bp.firstFit(2);
        }else {
            if(initChoice != 2 ){
                System.out.println("Choix impossible, deuxième choix selectionné par default");
            }
            bp.OneItemPerBin(2);
        }
        System.out.println("Veuillez saisir la température initiale:");
        int initTemp = sc.nextInt();
        System.out.println("Veuillez saisir le nombre de changement de température:");
        int n1 = sc.nextInt();
        System.out.println("Veuillez saisir le nombre de changement à une température:");
        int n2 = sc.nextInt();

        System.out.println("Veuillez saisir la μ : (mettre une virgule pas de point)");
        double mu = sc.nextDouble();
        System.out.println("l'algo est en train de tourner");
        debut = System.currentTimeMillis();
        bp.RecuitSimule(initTemp, n1, n2, mu);
        fin = System.currentTimeMillis() - debut;
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin+"ms");


        System.out.println("\nRecuit simulé - Efficacité en nb bins");
        somme = 0;
        for (int i = 0; i < nbRecuit; i++) {
            bp.OneItemPerBin(1);
            bp.RecuitSimule(initTemp, n1, n2, mu);
            System.out.println("nbbins = " + bp.nb_bin);
            somme += bp.nb_bin;
        }
        System.out.println("Nombre moyen de bins = " + (double) somme/nbRecuit);


            //------------     Tabou   ------------

        System.out.println("\nTabu Search -  Efficacité en temps");
        System.out.println("Veuillez selectionner comment vous voulez initialiser les bins:");
        System.out.println("- Un item par bin rangé par ordre decroissant (entrer 1)");
        System.out.println("- Un item par bin rangé aléatoirement (entrer 2)");
        System.out.println("- First Fit Decreasing (entrer 3)");
        System.out.println("- First Fit Random (entrer 4)");
        initChoice = sc.nextInt();
        if(initChoice == 1){
            bp.OneItemPerBin(1);
        }else if(initChoice == 3){
            bp.firstFit(1);
        }else if(initChoice == 4){
            bp.firstFit(2);
        }else {
            if(initChoice != 2 ){
                System.out.println("Choix impossible, deuxième choix selectionné par default");
            }
            bp.OneItemPerBin(2);
        }
        System.out.println("Veuillez saisir la taille du tableau:");
        int tabuSize = sc.nextInt();
        System.out.println("Veuillez saisir le nombre d'itération:");
        int nbIter = sc.nextInt();
        System.out.println("l'algo est en train de tourner");
        debut = System.currentTimeMillis();
        bp.TabuSearch(tabuSize, nbIter);
        fin = System.currentTimeMillis() - debut;
        bp.printBins();
        System.out.println("nbbins = " + bp.nb_bin);
        System.out.println("L'optimisation à durée " + fin + "ms");


        //------------   Solution Optimale   ------------

        System.out.println("\nSolution optimale");
        String[] file = {fileName};
        try {
            debut = System.currentTimeMillis();
            BinPackingMip.main(file);
            fin = System.currentTimeMillis() - debut;
            System.out.println("\nL'optimisation du fichier " + fileName + " à durée " + fin+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sc.close();
    }
}
