package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.BinaryVectors;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;

import java.util.ArrayList;
import java.util.List;


public class MatrixController {

    private List<String> lineList = new ArrayList<String>();
    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;

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
        encryptedVector = matrixUtils.multiplyCodeWithMatrix(generatingMatrix, unencryptedVector);
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
        } else
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
        BinaryVectors binaryVectors = new BinaryVectors();
        List<String> vectors = binaryVectors.generateBinaryVectorsOfSizeN(5);

        for (String element : vectors)
        {
            System.out.println(element);
        }
    }
}
