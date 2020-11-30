package utils;

import java.util.Arrays;
import java.util.Map;

public class MatrixUtils implements MatrixUtilsInterface{

    @Override
    public int[][] generateIdentityMatrix(Integer dimension)
    {
        //Function to generate identity matrix of size dimension x dimension, with given dimension

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
        //Function to generate generating matrix by joining given identity and another matrix, returns joined matrix

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
        //Function to generate random binary matrix of size rows x columns, returns generating matrix

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
        //Function to multiply given vector with given matrix, returns new matrix

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
        //Function to transpose matrix. Given matrix of dimensions n x k, returns new matrix of dimensions k x n
        //  rows switched with columns

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
    public int[][] generateControlMatrix(int[][] matrix)
    {
        //Generate control matrix -> transpose given matrix, join with new identity matrix
        //     return new matrix.

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
            hMatrix = generateIdentityMatrix(matrix.length);
            return hMatrix;
        }
    }

    @Override
    public int[][] joinMatrices(int[][] oneMatrix, int[][] twoMatrix) {
        //Join two given matrixes and return new matrix

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
        //Function to generate vector of given size in zeros, just in one 1 in i place
        //  return vector of zeros with 1 in ith place

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
        //Sum two given vectors and return summed one

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
        //Multiply given matrix with vector and return multiplied matrix

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

    @Override
    public int[] sendVectorThroughChanel(int[] vector, double probability) {
        //Function which takes vector and corruption probability
        //  generate random number and if it is less than given probability
        //  change element in vector return new vector with changed values

        int[] encryptedVector = vector;

        for(int i=0; i<vector.length; i++)
        {
            double randomNumber = Math.random();
            if(randomNumber < probability)
            {
                encryptedVector[i] = (vector[i] + 1) % 2;
            } else encryptedVector[i] = vector[i];
        }

        return encryptedVector;
    }

    @Override
    public int[] decodeVector(int[] encryptedVector, Map<String, Integer> syndromeMap, int[][] controlMatrix, int matrixColumnNumb, int matrixRowNumb) {
        //Function to decode given vector, using guven control matrix and syndromes, k and n.
        //      Return decoded vector.

        int[] r = encryptedVector;
        for (int i = 0; i < r.length; i++) {

            //Finding syndrome of vector
            int[] syndrome = multiplyMatrixWithCode(controlMatrix, r);

            //Finding weight of syndrome
            int weight = syndromeMap.get(Arrays.toString(syndrome).replaceAll("\\[|\\]|,|\\s", ""));

            //If weight is equal 0 => break
            if (weight == 0) {
                break;
            } else {
                //If weight is not 0, create new vectors with 1's in different places add them with vector and check if weight is lower
                //      if it is then save new vector.

                int[] eVector = generateVectorWithOneinI(i, r.length);
                int[] ePlusRVector = addVectors(r, eVector);

                int[] syndromeRVector = multiplyMatrixWithCode(controlMatrix, ePlusRVector);
                int weightRVector = syndromeMap.get(Utils.intArrayToString(syndromeRVector));
                if (weightRVector < weight) {
                    r = ePlusRVector;
                }
            }
        }

        int vectorToReturnLength = matrixColumnNumb - (matrixColumnNumb-matrixRowNumb);
        int[] vectorToReturn = new int[vectorToReturnLength];
        for (int j=0; j<vectorToReturnLength; j++)
        {
            vectorToReturn[j] = r[j];
        }
        return vectorToReturn;
    }
}
