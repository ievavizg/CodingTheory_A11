package sample;

import com.sun.webkit.network.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;
import utils.SyndromeUtils;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextScenarioController {

    private int[][] generatingMatrix;
    private String textToEncrypt;
    private int[] encryptedVector;
    private String binaryTextToEncrypt;
    private List<String> binaryTextOfSizeKList;
    private List<int[]> encodedVectorsList;

    private int sizeOfVector;
    private int matrixColumnNumb;
    private double corruptionProbability;

    MatrixUtilsInterface matrixUtils = new MatrixUtils();

    @FXML
    private TextArea matrixTextArea;

    @FXML
    private Button matrixButton;

    @FXML
    private TextField kNumberTextArea;

    @FXML
    private TextField nNumberTextArea;

    @FXML
    private TextField probabilityNumberTextArea;

    @FXML
    private Button encodeVectorButton;

    @FXML
    private Button testBttn;

    @FXML
    private TextArea textTextArea;

    @FXML
    private TextArea encodedTextArea;

    @FXML
    private TextArea decodedTextArea;

    @FXML
    void encodeVectorButtonOnAction(ActionEvent event) {
        //TODO: sitas button veikia kaip issaugojimas generuojancios matricos!

        //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.

        encodedVectorsList = new ArrayList<>();

        for (String vector:binaryTextOfSizeKList) {
            //TODO: maybe save instantly as int[] array?
            int[] vectorAsArray = Utils.stringToIntegerArray(vector);
            encryptedVector = matrixUtils.multiplyCodeWithMatrix(vectorAsArray, generatingMatrix);
            encodedVectorsList.add(encryptedVector);
        }

        //TODO: list of arrays to string and set TextArea with string.
        StringBuilder encodedFullVector = new StringBuilder();
        for (int[] vector:encodedVectorsList) {
            encodedFullVector.append(Utils.intArrayToString(vector));
        }

        Utils.setTextToTextField(encodedFullVector.toString(),encodedTextArea);
    }

    @FXML
    void matrixButtonOnAction(ActionEvent event) {

        setValues();

        if(!matrixTextArea.getText().isEmpty()) {
            generatingMatrix = Utils.textAreaToTwoDimensionalArray(matrixTextArea);
        } else
        {
            int[][] unitMa = matrixUtils.generateIdentityMatrix(sizeOfVector);
            int[][] randomMa = matrixUtils.generateRandomMatrix(sizeOfVector,matrixColumnNumb-sizeOfVector);
            //TODO: find out what to do if n == k
            generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa,randomMa);
            Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
        }

        //Convert given text to binaries
        //Convert each char to binary?
        binaryTextToEncrypt = Utils.convertStringToBinary(textToEncrypt);

        //Save binaries of size k into the list
        binaryTextOfSizeKList = new ArrayList<>();
        do{
            if(binaryTextToEncrypt.length()>sizeOfVector){
                binaryTextOfSizeKList.add(binaryTextToEncrypt.substring(0, sizeOfVector));
                binaryTextToEncrypt = binaryTextToEncrypt.substring(sizeOfVector, binaryTextToEncrypt.length());
            } else if(binaryTextToEncrypt.length() < sizeOfVector) {
                do{
                    binaryTextToEncrypt += "0";
                }while (!(binaryTextToEncrypt.length()==sizeOfVector));
                binaryTextOfSizeKList.add(binaryTextToEncrypt);
                binaryTextToEncrypt = "";
            } else {
                binaryTextOfSizeKList.add(binaryTextToEncrypt);
                binaryTextToEncrypt = "";
            }
            }while (!binaryTextToEncrypt.isEmpty());

    }

    @FXML
    void testBttnOnAction(ActionEvent event) {

        int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
        SyndromeUtils syndromeUtils = new SyndromeUtils(sizeOfVector,matrixColumnNumb,controlMatrix);
        Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

        StringBuilder decodedStringBuilderInBinary = new StringBuilder();

        for (int[] vectorToDecode:encodedVectorsList) {

            int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
            int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,sizeOfVector);
            decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));
        }

        String decodedText = Utils.binaryToText(decodedStringBuilderInBinary.toString());

        decodedTextArea.setText(decodedText);
    }

    public void setValues()
    {
        if(!kNumberTextArea.getText().isEmpty()){
            this.sizeOfVector = Integer.parseInt(kNumberTextArea.getText());
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
        if(sizeOfVector<matrixColumnNumb)
        {
            //TODO: throw warning or error
        }
        if(!textTextArea.getText().isEmpty())
        {
            this.textToEncrypt = textTextArea.getText();
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
}
