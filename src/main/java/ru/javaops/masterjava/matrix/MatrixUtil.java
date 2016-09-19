package ru.javaops.masterjava.matrix;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixResult = new int[matrixSize][matrixSize];
        final int threadCount = Runtime.getRuntime().availableProcessors();
        final int maxIndex = matrixSize * matrixSize;
        final int cellsInThread =  maxIndex / threadCount;
        final int[][] matrixBFinal = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBFinal[i][j] = matrixB[j][i];
            }
        }

        Set<Callable<Boolean>> threads = new HashSet<>();
        int fromIndex = 0;
        for (int i = 1; i <= threadCount; i++) {
            final int toIndex = i == threadCount ? maxIndex : fromIndex + cellsInThread;
            final int firstIndexFinal = fromIndex;
            threads.add(() -> {
                for (int j = firstIndexFinal; j < toIndex; j++) {
                    final int row = j / matrixSize;
                    final int col = j % matrixSize;

                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[row][k] * matrixBFinal[col][k];
                    }
                    matrixResult[row][col] = sum;
                }
                return true;
            });
            fromIndex = toIndex;
        }
        executor.invokeAll(threads);
        return matrixResult;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
