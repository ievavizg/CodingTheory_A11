package utils;

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
}
