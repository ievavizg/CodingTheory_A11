package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.*;

import java.util.*;


public class MatrixController {

    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;

    MatrixUtilsInterface matrixUtils = new MatrixUtils();

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
    private TextField decodedVectorTextField;

    @FXML
    private Button testBttn;

    @FXML
    void matrixButtonOnAction(ActionEvent event) {

        setValues();

        if(!matrixTextArea.getText().isEmpty()) {
            generatingMatrix = Utils.textAreaToTwoDimensionalArray(matrixTextArea);
        } else
        {
            int[][] unitMa = matrixUtils.generateIdentityMatrix(matrixRowNumb);
            int[][] randomMa = matrixUtils.generateRandomMatrix(matrixRowNumb,matrixColumnNumb-matrixRowNumb);
            //TODO: find out what to do if n == k
            generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa,randomMa);
            Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
        }

    }

    @FXML
    void encodeVectorButtonOnAction(ActionEvent event) {

        //TODO: sitas button veikia kaip issaugojimas generuojancios matricos!

        //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.
        encryptedVector = matrixUtils.multiplyCodeWithMatrix(unencryptedVector, generatingMatrix);
        setVectorTextField(encryptedVector, encodedVectorTextArea);
    }

    public void setVectorTextField(int[] vector, TextField textField)
    {
        StringBuilder vectorToDisplay = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            vectorToDisplay.append(vector[i]);
        }

        textField.setText(vectorToDisplay.toString());
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

        int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(encryptedVector,corruptionProbability);

        int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);

        setVectorTextField(decodedVector,decodedVectorTextField);

    }

}
