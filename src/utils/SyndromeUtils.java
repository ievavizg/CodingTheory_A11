package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SyndromeUtils {

    int rowsNumber;
    int kNumber;
    int nNumber;
    int base = 2;

    int[][] controlMatrix;

    BinaryVectors binaryVectors = new BinaryVectors();
    MatrixUtilsInterface matrixUtils = new MatrixUtils();


    public SyndromeUtils(int kNumber, int nNumber, int[][] controlMatrix)
    {
        this.kNumber = kNumber;
        this.nNumber = nNumber;
        this.rowsNumber = (int) Math.pow(base,nNumber-kNumber);
        this.controlMatrix = controlMatrix;
    }

    public Map<String, Integer> getSyndromeMap()
    {
        Map<String,Integer> syndromeMap = new HashMap<>();

        do
        {
            //take list of all possible binary vectors of size nNumber
            Map<String,Integer> possibleBinaryVectors = binaryVectors.generateBinaryVectorsOfSizeN(nNumber);

            //go through binarVectors list depending on current weight, starting by 0
            for(int weight=0;weight<nNumber; weight++) {

                Map<String,Integer> temporaryBinaryVectors = new HashMap<>();
                for (Map.Entry<String, Integer> element : possibleBinaryVectors.entrySet()) {
                    if (element.getValue().equals(weight)) {
                        temporaryBinaryVectors.put(element.getKey(),element.getValue());
                        //TODO:kai visus sudeda break, pries tai paskaiciuoti kiek turi rows buti temp mape
                    }
                }

                for (Map.Entry<String, Integer> element : temporaryBinaryVectors.entrySet()) {
                    //pasiimam elementa pagal kuri weight dabar tikrinam ir kiekviena tikrinam ar tinka
                    //paverciam stringa i array

                    int[] vectorFromMap = Utils.stringToIntegerArray(element.getKey());

                    //patikrinti ar tinka (sudauginti H kart vektorius) ir jei gauto nera map'e ideti
                    int[] syndrome = matrixUtils.multiplyMatrixWithCode(controlMatrix, vectorFromMap);

                    if(!(syndromeMap.containsKey(Arrays.toString(syndrome).replaceAll("\\[|\\]|,|\\s", "")))) {
                        syndromeMap.put(Arrays.toString(syndrome).replaceAll("\\[|\\]|,|\\s", ""), weight);
                        possibleBinaryVectors.remove(element);
                    }

                    //check if syndromeTable is full
                    if (syndromeMap.size() == rowsNumber)
                        break;
                }
                if (syndromeMap.size() == rowsNumber)
                    break;
            }
        } while (syndromeMap.size() != rowsNumber);

        return syndromeMap;
    }

}
