package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.BinaryVectors;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;
import utils.SyndromeUtils;

import java.util.*;


public class MatrixController {

    private List<String> lineList = new ArrayList<String>();
    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;

    @FXML
    private TextArea matrixTextArea;

    @FXML
    private Button matrixButton;

    @FXML
    private TextField nNumberTextArea;

    @FXML
    private TextField kNumberTextArea;

    @FXML
    private TextField vectorTextArea;

    @FXML
    private TextField probabilityNumberTextArea;

    @FXML
    private TextField encodedVectorTextArea;

    @FXML
    private Button testBttn;

    MatrixUtilsInterface matrixUtils = new MatrixUtils();


    @FXML
    void matrixButtonOnAction(ActionEvent event) {

        setValues();

        if(!matrixTextArea.getText().isEmpty()) {
            generatingMatrix = saveMatrixToArray(matrixTextArea);
        } else
        {
            int[][] unitMa = matrixUtils.generateIdentityMatrix(matrixRowNumb);
            int[][] randomMa = matrixUtils.generateRandomMatrix(matrixRowNumb,matrixColumnNumb-matrixRowNumb);
            //TODO: find out what to do if n == k
            generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa,randomMa);
            setMatrixTextArea(generatingMatrix);
        }

    }

    @FXML
    void encodeVectorButtonOnAction(ActionEvent event) {

        //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.
        encryptedVector = matrixUtils.multiplyCodeWithMatrix(unencryptedVector, generatingMatrix);
        setVectorTextArea(encryptedVector);
    }

    public int[][] saveMatrixToArray(TextArea matrixTextArea)
    {
        for (String line : matrixTextArea.getText().split("\\n")) {
            lineList.add(line);
            matrixColumnNumb = line.length() - line.replace(" ", "").length() + 1;
        }

        generatingMatrix = new int[lineList.size()][matrixColumnNumb];
        int i = 0, j = 0;

        for (String newLine : lineList)
        {
            j = 0;

            for (String numberInString : newLine.split(" ")) {
                generatingMatrix[i][j] = Integer.parseInt(numberInString);
                j++;
            }
            i++;
        }

        return generatingMatrix;
    }

    public void setMatrixTextArea(int[][] matrix)
    {
        StringBuilder matrixToDisplay = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
            {
                matrixToDisplay.append(matrix[i][j]);
                if (j+1 < matrix[i].length)
                    matrixToDisplay.append(" ");
            }
            matrixToDisplay.append(System.getProperty("line.separator"));
        }

        matrixTextArea.setText(matrixToDisplay.toString());
    }

    public void setVectorTextArea(int[] vector)
    {
        StringBuilder vectorToDisplay = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            vectorToDisplay.append(vector[i]);
        }

        encodedVectorTextArea.setText(vectorToDisplay.toString());
    }

    public void setValues()
    {
        if(!kNumberTextArea.getText().isEmpty()){
            this.matrixRowNumb = Integer.parseInt(kNumberTextArea.getText());
        } else
        {
            //TODO: throw warning or error
        }
        if(!nNumberTextArea.getText().isEmpty())
        {
            this.matrixColumnNumb = Integer.parseInt(nNumberTextArea.getText());
        } else
        {
            //TODO: throw warning or error
        }
        if(matrixRowNumb<matrixColumnNumb)
        {
            //TODO: throw warning or error
        }
        if(!vectorTextArea.getText().isEmpty())
        {
            //TODO: check if vector length is equal k
            this.unencryptedVector = vectorTextAreaToArray(vectorTextArea.getText());
        }else
        {
            //TODO: throw warning or error
        }
        if(!probabilityNumberTextArea.getText().isEmpty())
        {
            //TODO: add check if 0<=p<=1
            this.corruptionProbability = Double.parseDouble(probabilityNumberTextArea.getText());
        }else
        {
            //TODO: throw warning or error
        }
    }

    public int[] vectorTextAreaToArray(String vectorAsString)
    {
        int[] newVector = new int[vectorAsString.length()];

        for(int i=0; i<vectorAsString.length(); i++)
        {
            newVector[i] = (int)vectorAsString.charAt(i) - 48;
        }

        return newVector;
    }

    @FXML
    void testBttnOnAction(ActionEvent event) {

        //Testing Control Matrix
        /*
        int[][] nMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
        setMatrixTextArea(nMatrix);
         */

        //Testing all vectors of size n
        /*
        BinaryVectors binaryVectors = new BinaryVectors();
        Map<String,Integer> vectors = binaryVectors.generateBinaryVectorsOfSizeN(5);

        for (Map.Entry<String,Integer> vector : vectors.entrySet()) {
            System.out.print(vector.getKey() + ":");
            System.out.println(vector.getValue());
        }
        */

        //Testing Syndromes
        /*
        int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
        SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
        Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

        for (Map.Entry<String,Integer> vector : syndromeMap.entrySet()) {
            System.out.print(vector.getKey() + ":");
            System.out.println(vector.getValue());
        }
        */

        //Testing decoding
        int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
        SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
        Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

        int[] encryptedVectorSent = sendUnencryptedVector(encryptedVector,corruptionProbability);

        int[] decodedVector = decodeVector(encryptedVectorSent,syndromeMap,controlMatrix);

        System.out.println(Arrays.toString(decodedVector).replaceAll("\\[|\\]|,|\\s", ""));

    }

    public int[] decodeVector(int[] encryptedVector, Map<String,Integer> syndromeMap, int[][] controlMatrix){
        int[] r = encryptedVector;
        for(int i=0; i<r.length; i++){
            //randam vektoriaus sindroma
            int[] syndrome = matrixUtils.multiplyMatrixWithCode(controlMatrix, r);

            //randam sindromo svori
            int weight = syndromeMap.get(Arrays.toString(syndrome).replaceAll("\\[|\\]|,|\\s", ""));

            //jei svoris 0 break, otherwise
            if(weight==0){
                break;
            } else {
                int[] eVector = matrixUtils.generateVectorWithOneinI(i, r.length);
                int[] ePlusRVector = matrixUtils.addVectors(r,eVector);

                int[] syndromeRVector = matrixUtils.multiplyMatrixWithCode(controlMatrix, ePlusRVector);
                int weightRVector = syndromeMap.get(Arrays.toString(syndromeRVector).replaceAll("\\[|\\]|,|\\s", ""));
                if (weightRVector < weight)
                {
                    r = ePlusRVector;
                }
            }
        }

        int[] vectorToReturn = new int[matrixColumnNumb-matrixRowNumb];
        for (int j=0; j<matrixColumnNumb-matrixRowNumb; j++)
        {
            vectorToReturn[j] = r[j];
        }
        return vectorToReturn;
    }

    public int[] sendUnencryptedVector(int[] unencryptedVector, double probility){
        int[] encryptedVector = unencryptedVector;
        int min = 0, max = 1;

        for(int i=0; i<unencryptedVector.length; i++)
        {
            double randomNumber = Math.random();
            if(randomNumber < probility)
            {
                encryptedVector[i] = (unencryptedVector[i] + 1) % 2;
            } else encryptedVector[i] = unencryptedVector[i];
        }

        return encryptedVector;
    }
}
