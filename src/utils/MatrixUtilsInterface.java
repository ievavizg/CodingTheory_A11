package utils;

public interface MatrixUtilsInterface {

    int[][] generateIdentityMatrix(Integer dimension);

    int[][] generateGeneratingMatrix(int[][] identityMatrix, int[][] anotherMatrix);

    int[][] generateRandomMatrix(Integer rows, Integer columns);

    int[] multiplyCodeWithMatrix(int [][] matrix, int[] vector);

    int[][] transposeMatrix(int [][] matrix);

    int[][] generateControlMatrix(int [][] matrix);

    int[][] joinMatrices(int[][] oneMatrix, int[][] twoMatrix);
}
