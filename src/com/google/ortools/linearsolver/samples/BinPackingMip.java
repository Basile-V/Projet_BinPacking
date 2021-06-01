package com.google.ortools.linearsolver.samples;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/** Bin packing problem. */
public class BinPackingMip {
    static class DataModel {
        public final double[] weights;
        public final int numItems;
        public final int numBins;
        public final int binCapacity;

        public DataModel(double[] weights, int binCapacity){
            this.weights = weights;
            this.numItems = weights.length;
            this.numBins = weights.length;
            this.binCapacity = binCapacity;
        }
    }

    public static void main(String[] args) throws Exception {
        Loader.loadNativeLibraries();
        File file = new File("files/binpack1d_00.txt");
        if(args.length >= 1)
            file = new File(args[0]);
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(file));
        String line;
        int number = 0;
        line = reader.readLine();
        String[] header = line.split(" ");
        int binCapacity = Integer.parseInt(header[0]);
        double[] weight = new double[Integer.parseInt(header[1])];
        while ((line = reader.readLine()) != null) {
            weight[number++] = Double.parseDouble(line);
        }
        System.out.println(weight);
        DataModel data = new DataModel(weight , binCapacity);

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            return;
        }

        MPVariable[][] x = new MPVariable[data.numItems][data.numBins];
        for (int i = 0; i < data.numItems; ++i) {
            for (int j = 0; j < data.numBins; ++j) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }
        MPVariable[] y = new MPVariable[data.numBins];
        for (int j = 0; j < data.numBins; ++j) {
            y[j] = solver.makeIntVar(0, 1, "");
        }

        double infinity = java.lang.Double.POSITIVE_INFINITY;
        for (int i = 0; i < data.numItems; ++i) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
            for (int j = 0; j < data.numBins; ++j) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }
        // The bin capacity contraint for bin j is
        //   sum_i w_i x_ij <= C*y_j
        // To define this constraint, first subtract the left side from the right to get
        //   0 <= C*y_j - sum_i w_i x_ij
        //
        // Note: Since sum_i w_i x_ij is positive (and y_j is 0 or 1), the right side must
        // be less than or equal to C. But it's not necessary to add this constraint
        // because it is forced by the other constraints.

        for (int j = 0; j < data.numBins; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, infinity, "");
            constraint.setCoefficient(y[j], data.binCapacity);
            for (int i = 0; i < data.numItems; ++i) {
                constraint.setCoefficient(x[i][j], -data.weights[i]);
            }
        }

        MPObjective objective = solver.objective();
        for (int j = 0; j < data.numBins; ++j) {
            objective.setCoefficient(y[j], 1);
        }
        objective.setMinimization();

        final MPSolver.ResultStatus resultStatus = solver.solve();

        // Check that the problem has an optimal solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Number of bins used: " + objective.value());
            double totalWeight = 0;
            for (int j = 0; j < data.numBins; ++j) {
                if (y[j].solutionValue() == 1) {
                    System.out.println("\nBin " + j + "\n");
                    double binWeight = 0;
                    for (int i = 0; i < data.numItems; ++i) {
                        if (x[i][j].solutionValue() == 1) {
                            System.out.println("Item " + i + " - weight: " + data.weights[i]);
                            binWeight += data.weights[i];
                        }
                    }
                    System.out.println("Packed bin weight: " + binWeight);
                    totalWeight += binWeight;
                }
            }
            System.out.println("\nTotal packed weight: " + totalWeight);
        } else {
            System.err.println("The problem does not have an optimal solution.");
        }
    }
    private BinPackingMip() {}
}