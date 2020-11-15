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

                for (Map.Entry<String, Integer> element : possibleBinaryVectors.entrySet()) {
                    if (element.getValue().equals(weight)) {
                        //pasiimam elementa pagal kuri weight dabar tikrinam ir kiekviena tikrinam ar tinka
                        //paverciam stringa i array

                        int[] vectorFromMap = Utils.stringToIntegerArray(element.getKey());

                        //patikrinti ar tinka (sudauginti H kart vektorius) ir jei gauto nera map'e ideti
                        int[] syndrome = matrixUtils.multiplyCodeWithMatrix(controlMatrix, vectorFromMap);

                        boolean found = false;

                        for (Map.Entry<String, Integer> entry : syndromeMap.entrySet()) {
                            if (entry.getKey().equals(syndrome.toString())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            syndromeMap.put(Arrays.toString(syndrome).replaceAll("\\[|\\]|,|\\s", ""), weight);
                        }

                        //check if syndromeTable is full
                        if (syndromeMap.size() == rowsNumber)
                            break;
                    }
                }
                if (syndromeMap.size() == rowsNumber)
                    break;
            }
        } while (syndromeMap.size() != rowsNumber);

        return syndromeMap;
    }

}
