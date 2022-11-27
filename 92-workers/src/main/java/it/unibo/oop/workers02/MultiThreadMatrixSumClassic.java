package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadMatrixSumClassic implements SumMatrix{

    private int nthread;


    public MultiThreadMatrixSumClassic(final int nthread) {
        if(nthread < 1) {
             throw new IllegalArgumentException();   
        }
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res = 0;

        public Worker(double[][] matrix, final int startpos, final int nelem) {
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            for (int i = startpos; i < startpos + nelem && i < matrix.length; i++ ) {
                for (final double cur : matrix[i]) {
                    res += cur;
                }
            }
        }

        public double getResult() {
            return res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (Worker w : workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
    
}
