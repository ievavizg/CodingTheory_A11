package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.*;

import java.net.URL;
import java.util.*;


public class MatrixController implements Initializable {

    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;
    private List<Integer> corruptedValues;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;

    private String generatingMatrixString;

    private boolean correctGeneratingMatrix;

    private int[] encryptedVectorSentThroughChannel;

    int vectorLength;

    MatrixUtilsInterface matrixUtils = new MatrixUtils();

    @FXML
    private TextArea matrixTextArea;

    @FXML
    private Button randomMatrixButton;

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
    private Button decodeButton;

    @FXML
    private TextField vectorLengthTextField;

    @FXML
    private TextArea mistakesTextArea;

    @FXML
    private TextField vectorToDecodeTextField;

    @FXML
    void generateRandomMatrixButtonOnAction(ActionEvent event) {
        // 1. Check if all values are correct:
        //  1.1. k >= n
        //  1.2. vectorLength = k
        // 2. If k=n, then generate only unitary matrix, else generate generating matrix with random matrix
        // 3. Generate random matrix by joining unitary and random matrix.

        boolean isAlert = false;

        if(matrixColumnNumb < matrixRowNumb)
        {
            isAlert = true;
            Utils.createAlert("Netinkami parametrai", "Kodo ilgis negali būti mažesnis už dimensiją");
        }
        //TODO: check if all values set
        else if(matrixRowNumb != unencryptedVector.length) {
            isAlert = true;
            Utils.createAlert("Netinkami parametrai", "Įvesto vektoriaus ilgis turi būti lygus dimensijai");
        } else {
            int[][] unitMa = matrixUtils.generateIdentityMatrix(matrixRowNumb);
            if(matrixRowNumb == matrixColumnNumb){
                generatingMatrix = unitMa;
            } else {
                int[][] randomMa = matrixUtils.generateRandomMatrix(matrixRowNumb,matrixColumnNumb-matrixRowNumb);
                generatingMatrix = matrixUtils.generateGeneratingMatrix(unitMa,randomMa);
            }
        }

        if (!isAlert)
        {
            Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
            generatingMatrixString = matrixTextArea.toString();
            correctGeneratingMatrix = true;
        }
    }

    @FXML
    void encodeVectorButtonOnAction(ActionEvent event) {

        //1. Check if generatingMatrixString is not empty
        //2. Check if generating matrix is as it should be
        //  2.1. rows = k
        //  2.2. columns = n
        //3. If matrix is correct -> encrypt vector by multiplying vector and generatingMatrix
        //4. Check if corruption probability is not null
        //5. Send through channel with corruption probability


        correctGeneratingMatrix = checkIfGeneratingMatrixIsCorrect();

        if(correctGeneratingMatrix) {
            encryptedVector = matrixUtils.multiplyCodeWithMatrix(unencryptedVector, generatingMatrix);
            Utils.setVectorTextField(encryptedVector, encodedVectorTextArea);

            if(corruptionProbability == 0.0){
                Utils.createAlert("Iškraipymo tikymybė neįvesta",
                        "Iškraipymo tikimybė buvo neįvesta, todėl ji laikoma 0, galite pakeisti ją ir siųsti vektorių per kanalą iš naujo");
            }
            sendVectorThroughChanel();
            Utils.setVectorTextField(encryptedVectorSentThroughChannel, vectorToDecodeTextField);
            Utils.setMistakesListToTextArea(corruptedValues,mistakesTextArea,". ");
        }
    }

    public void sendVectorThroughChanel() {

        //1. While looping through vector generate random number
        //      and check if it is less than corruption probability
        //      if yes -> change bit, else leave bit

        encryptedVectorSentThroughChannel = encryptedVector;
        int j = 0;
        corruptedValues = new ArrayList<>();

        for(int i=0; i<encryptedVector.length; i++)
        {
            double randomNumber = Math.random();
            if(randomNumber < corruptionProbability)
            {
                encryptedVectorSentThroughChannel[i] = (encryptedVector[i] + 1) % 2;
                corruptedValues.add(i + 1);
                j++;

            } else encryptedVectorSentThroughChannel[i] = encryptedVector[i];
        }

    }

    @FXML
    void decodeButtonOnAction(ActionEvent event) {


        //1. Check if vector from chanel is correct
        //2. Check if generating matrix was not changed!
        //3. Generate Syndrome Map
        //4. Decode vector


        if(!correctGeneratingMatrix){
            Utils.createAlert("Netinkama matrica",
                    "Generuojanti matrica arba neužpildyta arba buvo pakeista, todėl reikia iš naujo užkoduoti vektorių.");
        } else if(encryptedVectorSentThroughChannel.length != matrixColumnNumb)
        {
            Utils.createAlert("Netinkamas vektorius",
                    "Notint dekoduoti vektorių, jo ilgis turi būti lygus kodo ilgiui");
        } else {

            int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
            SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
            Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

            int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSentThroughChannel,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);

            Utils.setVectorTextField(decodedVector,decodedVectorTextField);
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

        //Get vector on every change
        vectorTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                unencryptedVector = Utils.stringToBinaryIntegerArrayWithAlert(vectorTextArea.getText());
            }
        });

        //Get generating matrix on every change
        matrixTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                generatingMatrixString = matrixTextArea.getText();
                correctGeneratingMatrix = false;
            }
        });

        vectorToDecodeTextField.textProperty().addListener((obs, oldText, newText) -> {
            if(!newText.isEmpty()) {
                encryptedVectorSentThroughChannel = Utils.stringToIntegerArray(vectorToDecodeTextField.getText());
            }
        });
    }

    private boolean checkIfGeneratingMatrixIsCorrect(){

        boolean alert = false;

        //Check if generating matrix are is empty
        if(matrixTextArea.getText().isEmpty())
        {
            Utils.createAlert("Neužpildyta generuojanti matrica",
                    "Prašome užpildyti generuojančios matricos lauką (gali būti be standartinio pavidalo matricos)");
            alert = true;
        } else if(!alert){
            int[][] scannedMatrix = Utils.textAreaToTwoDimensionalArray(matrixTextArea);

            if(scannedMatrix!=null){

                //Check if entered generating matrix rows match entered parameters
                if (scannedMatrix.length != matrixRowNumb) {
                    Utils.createAlert("Neteisingai įvesta generuojanti matrica",
                            "Prašome užpildyti generuojančios matricos lauką tinkamai, eilučių skaičius turi būti lygus dimensijai");
                    alert = true;
                } else {
                    //Check if entered generating matrix columns match entered parameters
                    int columnLength = scannedMatrix[0].length;
                    for (int i = 0; i < scannedMatrix.length; i++) {
                        if (columnLength != scannedMatrix[i].length) {
                            Utils.createAlert("Neteisingai įvesta generuojanti matrica",
                                    "Nevienodi stulpelių ilgiai, prašome pasitikslinti įvestą / pakeistą generuojančią matricą");
                            alert = true;
                        }
                    }
                    if (!alert) {
                        //If entered generating matrix rows and columns match entered parameters -> full matrix was entered
                        if (columnLength == matrixColumnNumb) {
                            generatingMatrix = scannedMatrix;
                        }
                        //If entered generating matrix rows match entered parameters and columns are entered columns - rows -> A part of matrix was entered
                        else if (columnLength == matrixColumnNumb - matrixRowNumb) {
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
}
