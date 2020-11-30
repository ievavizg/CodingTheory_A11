package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;
import utils.SyndromeUtils;
import utils.Utils;

import java.net.URL;
import java.util.*;

public class TextScenarioController implements Initializable {

    private int vectorLength;
    private int[][] generatingMatrix;
    private String textToEncrypt;
    private int[] encryptedVector;
    private String binaryTextToEncrypt;
    private List<String> binaryTextOfSizeKList;
    private List<int[]> encodedVectorsList;
    private boolean correctGeneratingMatrix;
    private StringBuilder zerosAddedToVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;

    private String generatingMatrixString;

    MatrixUtilsInterface matrixUtils = new MatrixUtils();

    @FXML
    private TextArea matrixTextArea;

    @FXML
    private Button generateRandomMatrixButton;

    @FXML
    private TextField kNumberTextArea;

    @FXML
    private TextField nNumberTextArea;

    @FXML
    private TextField probabilityNumberTextArea;

    @FXML
    private Button encodeVectorButton;

    @FXML
    private TextArea corruptedTextArea;

    @FXML
    private TextArea textTextArea;

    @FXML
    private TextArea decodedCorruptedTextArea;


    @FXML
    private TextField vectorLengthTextField;

    @FXML
    void generateRandomMatrixButtonOnAction(ActionEvent event) {
        // 1. Check if all values are correct:
        //  1.1. k >= n
        //  1.2. vectorLength = k
        // 2. If k=n, then generate only unitary matrix, else generate generating matrix with random matrix
        // 3.

        boolean isAlert = false;

        if(matrixColumnNumb < matrixRowNumb)
        {
            isAlert = true;
            Utils.createAlert("Netinkami parametrai", "Kodo ilgis negali būti mažesnis už dimensiją");
        } else {

            int[][] unitMa = matrixUtils.generateIdentityMatrix(matrixRowNumb);
            int[][] randomMa = matrixUtils.generateRandomMatrix(matrixRowNumb,matrixColumnNumb- matrixRowNumb);
            //TODO: find out what to do if n == k
            generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa,randomMa);
            Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
        }
    }

    @FXML
    void encodeVectorButtonOnAction(ActionEvent event) {

        // 1. Save text to binaries
        // 2.


        //Convert given text to binaries
        //Convert each char to binary?
        binaryTextToEncrypt = Utils.convertStringToBinary(textToEncrypt);

        //Save binaries of size k into the list
        zerosAddedToVector = new StringBuilder();
        binaryTextOfSizeKList = new ArrayList<>();
        do{
            if(binaryTextToEncrypt.length()> matrixRowNumb){
                binaryTextOfSizeKList.add(binaryTextToEncrypt.substring(0, matrixRowNumb));
                binaryTextToEncrypt = binaryTextToEncrypt.substring(matrixRowNumb, binaryTextToEncrypt.length());
            } else if(binaryTextToEncrypt.length() < matrixRowNumb) {
                do{
                    binaryTextToEncrypt += "0";
                    zerosAddedToVector.append("0");
                }while (!(binaryTextToEncrypt.length()== matrixRowNumb));
                binaryTextOfSizeKList.add(binaryTextToEncrypt);
                binaryTextToEncrypt = "";
            } else {
                binaryTextOfSizeKList.add(binaryTextToEncrypt);
                binaryTextToEncrypt = "";
            }
        }while (!binaryTextToEncrypt.isEmpty());


        correctGeneratingMatrix = checkIfGeneratingMatrixIsCorrect();

        if(correctGeneratingMatrix) {

            //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.

            encodedVectorsList = new ArrayList<>();

            for (String vector : binaryTextOfSizeKList) {
                //TODO: maybe save instantly as int[] array?
                int[] vectorAsArray = Utils.stringToIntegerArray(vector);
                encryptedVector = matrixUtils.multiplyCodeWithMatrix(vectorAsArray, generatingMatrix);
                encodedVectorsList.add(encryptedVector);
            }

            int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
            SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
            Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

            StringBuilder corruptedStringBuilderInBinary = new StringBuilder();
            StringBuilder decodedCorruptedStringBuilderInBinary = new StringBuilder();

            for (int[] vectorToDecode:encodedVectorsList) {

                int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);

                int[] corruptedVector = corruptedVectorBackToGivenVectorSize(encryptedVectorSent,matrixColumnNumb, matrixRowNumb);
                corruptedStringBuilderInBinary.append(Utils.intArrayToString(corruptedVector));

                int[] decodedCorruptedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb, matrixRowNumb);
                decodedCorruptedStringBuilderInBinary.append(Utils.intArrayToString(decodedCorruptedVector));
            }

            String corruptedText = "";
            String decodedCorrupted = "";

            if (zerosAddedToVector.length() != 0)
            {
                corruptedText = corruptedStringBuilderInBinary.toString().substring(0,corruptedStringBuilderInBinary.length()-zerosAddedToVector.length());
                decodedCorrupted =  decodedCorruptedStringBuilderInBinary.toString().substring(0,decodedCorruptedStringBuilderInBinary.length()-zerosAddedToVector.length());
            }

            String corruptedTextinBinary = Utils.binaryToText(corruptedText);
            corruptedTextArea.setText(corruptedTextinBinary);

            String decodedCorruptedText = Utils.binaryToText(decodedCorrupted);
            decodedCorruptedTextArea.setText(decodedCorruptedText);

        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Get vector length on every change and since k has to be equal to vector length set it.
        vectorLengthTextField.textProperty().addListener((obs, oldText, newText) -> {

            if(!newText.isEmpty()){
                vectorLength = Utils.tryParseToInteger(vectorLengthTextField.getText());
                Utils.checkIfIntegerBiggerThanZero(vectorLength);
            }
            kNumberTextArea.setText(newText);
        });

        //Get k (how many rows in generating matrix should be) on every change and since k has to be equal to vector length set it.
        kNumberTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                matrixRowNumb = Utils.tryParseToInteger(kNumberTextArea.getText());
                Utils.checkIfIntegerBiggerThanZero(matrixRowNumb);
            }
            vectorLengthTextField.setText(newText);
        });

        //Get n (how many columns in generating matrix should be) on every change
        nNumberTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                matrixColumnNumb = Utils.tryParseToInteger(nNumberTextArea.getText());
                Utils.checkIfIntegerBiggerThanZero(matrixColumnNumb);
            }
        });

        //Get corruptionProbability on every change
        probabilityNumberTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                corruptionProbability = Utils.tryParseToDouble(probabilityNumberTextArea.getText());
                Utils.checkIfDoubleBiggerOrEqualsZero(corruptionProbability);
            }
        });

        //Get text to decode on every change
        textTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                textToEncrypt = textTextArea.getText();
            }
        });
    }

    private boolean checkIfGeneratingMatrixIsCorrect(){
        boolean alert = false;

        if(matrixTextArea.getText().isEmpty())
        {
            Utils.createAlert("Neužpildyta generuojanti matrica",
                    "Prašome užpildyti generuojančios matricos lauką (gali būti be standartinio pavidalo matricos)");
            alert = true;
        } else if(!alert){
            int[][] scannedMatrix = Utils.textAreaToTwoDimensionalArray(matrixTextArea);

            if(scannedMatrix!=null){

                if (scannedMatrix.length != matrixRowNumb) {
                    Utils.createAlert("Neteisingai įvesta generuojanti matrica",
                            "Prašome užpildyti generuojančios matricos lauką tinkamai, eilučių skaičius turi būti lygus dimensijai");
                    alert = true;
                } else {
                    int columnLength = scannedMatrix[0].length;
                    for (int i = 0; i < scannedMatrix.length; i++) {
                        if (columnLength != scannedMatrix[i].length) {
                            Utils.createAlert("Neteisingai įvesta generuojanti matrica",
                                    "Nevienodi stulpelių ilgiai, prašome pasitikslinti įvestą / pakeistą generuojančią matricą");
                            alert = true;
                        }
                    }
                    if (!alert) {
                        if (columnLength == matrixColumnNumb) {
                            generatingMatrix = scannedMatrix;

                        } else if (columnLength == matrixColumnNumb - matrixRowNumb) {
                            int[][] unitMa = matrixUtils.generateIdentityMatrix(matrixRowNumb);
                            generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa, scannedMatrix);
                            Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
                            generatingMatrixString = matrixTextArea.toString();
                        } else {
                            Utils.createAlert("Neteisingai įvesta generuojanti matrica",
                                    "Prašome užpildyti generuojančios matricos lauką tinkamai, tai yra I | A matricos arba tik A matrica");
                            alert = true;
                        }
                    }
                }
            } else {
                alert = true;
            }
        }

        return (!alert);
    }


    public int[] corruptedVectorBackToGivenVectorSize(int[] encryptedVector, int matrixColumnNumb, int matrixRowNumb) {
        int vectorToReturnLength = matrixColumnNumb - (matrixColumnNumb-matrixRowNumb);
        int[] vectorToReturn = new int[vectorToReturnLength];
        for (int j=0; j<vectorToReturnLength; j++)
        {
            vectorToReturn[j] = encryptedVector[j];
        }
        return vectorToReturn;
    }
}
