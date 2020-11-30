package utils;

import java.util.Map;

public interface MatrixUtilsInterface {

    int[][] generateIdentityMatrix(Integer dimension);

    int[][] generateGeneratingMatrix(int[][] identityMatrix, int[][] anotherMatrix);

    int[][] generateRandomMatrix(Integer rows, Integer columns);

    int[] multiplyCodeWithMatrix(int[] vector, int [][] matrix);

    int[][] transposeMatrix(int [][] matrix);

    int[][] generateControlMatrix(int [][] matrix);

    int[][] joinMatrices(int[][] oneMatrix, int[][] twoMatrix);

    int[] generateVectorWithOneinI(int i, int size);

    int[] addVectors(int[] vectorOne, int[] vectorTwo);

    int[] multiplyMatrixWithCode(int [][] matrix, int[] vector);

    int[] sendVectorThroughChanel(int[] vector, double probability);

    int[] decodeVector(int[] encryptedVector, Map<String,Integer> syndromeMap, int[][] controlMatrix, int matrixColumnNumb, int matrixRowNumb);
}
