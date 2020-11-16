package utils;

public class MatrixUtils implements MatrixUtilsInterface{

    @Override
    public int[][] generateIdentityMatrix(Integer dimension)
    {
        int[][] unitaryMatrix = new int[dimension][dimension];
        for (int i = 0; i<dimension; i++)
        {
            for (int j=0; j<dimension; j++)
            {
                if (i!=j)
                    unitaryMatrix[i][j] = 0;
                else unitaryMatrix[i][j] = 1;
            }
        }

        return unitaryMatrix;
    }

    @Override
    public int[][] generateGeneratingMatrix(int[][] identityMatrix, int[][] anotherMatrix) {
        int rows = identityMatrix.length;
        int columns = identityMatrix.length + anotherMatrix[0].length;
        int[][] generatingMatrix = new int[rows][columns];

        for(int i=0; i<rows; i++)
        {
            for(int j=0; j<columns; j++)
            {
                if(j < identityMatrix.length)
                {
                    generatingMatrix[i][j] = identityMatrix[i][j];
                } else
                {
                    generatingMatrix[i][j] = anotherMatrix[i][j-identityMatrix.length];
                }
            }
        }

        return generatingMatrix;
    }

    public int[][] generateRandomMatrix(Integer rows, Integer columns)
    {
        int[][] randomMatrix = new int[rows][columns];

        int max = 1, min = 0;

        for (int i = 0; i<rows; i++)
        {
            for (int j=0; j<columns; j++)
            {
                randomMatrix[i][j] = (int)(Math.random() * (max - min + 1) + min);
            }
        }

        return randomMatrix;
    }

    @Override
    public int[] multiplyCodeWithMatrix(int[] vector, int [][] matrix)
    {
        int rows = matrix.length;
        int columns = matrix[0].length;
        int sum = 0;

        int[] newMatrix = new int[columns];

        if (vector.length == matrix.length) {

            for (int j = 0; j < columns; j++) {

                sum = 0;
                for (int i = 0; i < rows; i++) {
                    sum += matrix[i][j] * vector[i];
                }
                newMatrix[j] = sum % 2;
            }
        }else {
            //TODO exception maybe?
        }

        return newMatrix;
    }

    @Override
    public int[][] transposeMatrix(int[][] matrix) {
        int[][] transposedMatrix = new int[matrix[0].length][matrix.length];
        int tRow = 0,tColumn = 0;

        for(int i=0; i<matrix.length; i++)
        {
            tRow = 0;
            for(int j=0; j<matrix[0].length; j++)
            {
                transposedMatrix[tRow][tColumn] = matrix[i][j];
                tRow++;
            }
            tColumn++;
        }
        return transposedMatrix;
    }

    @Override
    public int[][] generateControlMatrix(int[][] matrix) {
        //identity matrix yra n x n, kiek eiluciu tiek.
        //stulpeliu sk. db eiluciu sk, ir eiluciu sk. db stulp. sk.
        int[][] hMatrix;
        int[][] matrixToTranspose;
        if(matrix[0].length != matrix.length)
        {
            //get matrix to transpose
            matrixToTranspose = new int[matrix.length][matrix[0].length-matrix.length];
            for(int i = 0; i<matrixToTranspose.length; i++)
            {
                for(int j = 0; j<matrixToTranspose[0].length; j++)
                {
                   matrixToTranspose[i][j] = matrix[i][matrix.length + j];
                }
            }

            int[][] tMatrix = transposeMatrix(matrixToTranspose);
            hMatrix = joinMatrices(tMatrix,generateIdentityMatrix(tMatrix.length));
            return hMatrix;
        } else
        {
            //TODO - ka daryti kai k = n?
        }
        //reikia issiimti A matrica

        return new int[0][];
    }

    @Override
    public int[][] joinMatrices(int[][] oneMatrix, int[][] twoMatrix) {
        if(oneMatrix.length == twoMatrix.length)
        {
            int rows = oneMatrix.length;
            int columns = oneMatrix[0].length+twoMatrix[0].length;
            int[][] joinedMatrix = new int[rows][columns];

            for(int i=0; i<rows; i++)
            {
                for(int j=0; j<columns; j++)
                {
                    if(j < oneMatrix[0].length)
                    {
                        joinedMatrix[i][j] = oneMatrix[i][j];
                    } else {
                        joinedMatrix[i][j] = twoMatrix[i][j-oneMatrix[0].length];
                    }
                }
            }

            return joinedMatrix;
        }
        return new int[0][];
    }

    @Override
    public int[] generateVectorWithOneinI(int i, int size) {
        int[] vector = new int[size];

        for(int n =0; n<size; n++)
        {
            if(i==n)
                vector[n] = 1;
            else vector[n] = 0;
        }

        return vector;
    }

    @Override
    public int[] addVectors(int[] vectorOne, int[] vectorTwo) {
        int[] addedVectors = new int[vectorOne.length];
        for(int i=0; i<vectorOne.length; i++)
        {
            int sum = vectorOne[i] + vectorTwo[i];
            addedVectors[i] = sum % 2;
        }

        return addedVectors;
    }

    @Override
    public int[] multiplyMatrixWithCode(int[][] matrix, int[] vector) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        int sum = 0;

        int[] newMatrix = new int[rows];

        if (vector.length == matrix[0].length) {

        for (int i = 0; i < rows; i++) {
            sum = 0;
            for (int j = 0; j < columns; j++) {
                    sum += matrix[i][j] * vector[j];
                }
                newMatrix[i] = sum % 2;
            }
        }else {
            //TODO exception maybe?
        }

        return newMatrix;
    }

}
