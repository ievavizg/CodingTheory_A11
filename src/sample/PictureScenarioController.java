package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;
import utils.SyndromeUtils;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class PictureScenarioController implements Initializable {

    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;
    byte[] imageImportantBytes;
    private StringBuilder zerosAddedToVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private int vectorLength;
    private double corruptionProbability;
    private List<int[]> encodedVectorsList;

    private StringBuilder encodedVectorStringBuilder;
    private StringBuilder fileBytesStringBuilder;

    private final MatrixUtilsInterface matrixUtils = new MatrixUtils();

    private StringBuilder decodedStringBuilderInBinary;
    private boolean correctGeneratingMatrix;

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
        private Button importPicture;

        @FXML
        private Pane picturePane;

        @FXML
        private Pane picturePaneDecoded;

        @FXML
        private TextField vectorLengthTextField;

        @FXML
        private TextField pathToSaveNewPicture;

        @FXML
        private TextArea pictureBytes;

        @FXML
        void encodeVectorButtonOnAction(ActionEvent event) throws IOException {
            //TODO: sitas button veikia kaip issaugojimas generuojancios matricos!

            //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.

            String binaryTextToEncrypt = fileBytesStringBuilder.toString();
            encodedVectorStringBuilder = new StringBuilder();
            do{
                if(binaryTextToEncrypt.length()>matrixRowNumb){

                    binaryVectorStringEncode(binaryTextToEncrypt.substring(0, matrixRowNumb));

                    binaryTextToEncrypt = binaryTextToEncrypt.substring(matrixRowNumb, binaryTextToEncrypt.length());
                } else if(binaryTextToEncrypt.length() < matrixRowNumb) {
                    do{
                        binaryTextToEncrypt += "0";
                    }while (!(binaryTextToEncrypt.length()==matrixRowNumb));
                    binaryVectorStringEncode(binaryTextToEncrypt);
                    binaryTextToEncrypt = "";
                } else {
                    binaryVectorStringEncode(binaryTextToEncrypt);
                    binaryTextToEncrypt = "";
                }
            }while (!binaryTextToEncrypt.isEmpty());


            String encodedVectorString = encodedVectorStringBuilder.toString();

            int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
            SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
            Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

            //<------->
            //DECODING

            decodedStringBuilderInBinary = new StringBuilder();

            zerosAddedToVector = new StringBuilder();

            do{
                if(encodedVectorString.length()>matrixRowNumb){

                    int[] vectorToDecode =  Utils.stringToIntegerArray(encodedVectorString.substring(0, matrixRowNumb));

                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = encodedVectorString.substring(matrixRowNumb, encodedVectorString.length());
                } else if(encodedVectorString.length() < matrixRowNumb) {
                    do{
                        encodedVectorString += "0";
                        zerosAddedToVector.append("0");

                    }while (!(encodedVectorString.length()==matrixRowNumb));

                    int[] vectorToDecode =  Utils.stringToIntegerArray(encodedVectorString);

                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = "";
                } else {
                    int[] vectorToDecode =  Utils.stringToIntegerArray(encodedVectorString);

                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = "";
                }
            }while (!encodedVectorString.isEmpty());

            //<------->

            List<Integer> list = new ArrayList<>();

            String decodedVector = decodedStringBuilderInBinary.toString().substring(0,decodedStringBuilderInBinary.length()-zerosAddedToVector.length());

            for(String str : decodedVector.split("(?<=\\G.{8})"))
                list.add(Integer.parseInt(str, 2));

            byte[] data = new byte[list.size()];

            int index = 0;
            Iterator<Integer> iterator = list.iterator();

            while(iterator.hasNext())
            {
                Integer i = iterator.next();
                data[index] = i.byteValue();
                index++;
            }

            byte[] destination = new byte[imageImportantBytes.length + list.size()];
            System.arraycopy(imageImportantBytes, 0, destination, 0, imageImportantBytes.length);
            System.arraycopy(data, 0, destination, imageImportantBytes.length, data.length);

            System.out.println(Arrays.toString(destination));

            //"/Users/ievaviz/Desktop/output.bmp"
            if(!pathToSaveNewPicture.getText().isEmpty()){
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pathToSaveNewPicture.getText()))) {
                    out.write(destination);
                }
            }
        }

        @FXML
        void importPictureButtonOnAction(ActionEvent event) throws IOException {
            //1. Open file selector to allow user select a picture
            //2.

            picturePane.getChildren().clear();

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(picturePane.getScene().getWindow());

            if (file != null){
                Stage primaryStage = new Stage();

                primaryStage.setTitle("Paveikslėlis prieš kodavimą");

                ImageView imageView = new ImageView(file.toURI().toURL().toExternalForm());

                HBox hbox = new HBox(imageView);

                Scene scene = new Scene(hbox, 200, 100);
                primaryStage.setScene(scene);
                primaryStage.show();


                //Get bytes from picture using Files.readAllBytes
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                //Get bytes from picture using BufferedImage
                /*
                BufferedImage bufferedImage = ImageIO.read(file);
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                ColorModel cm = bufferedImage.getColorModel();

                WritableRaster raster = bufferedImage.getRaster();
                DataBufferByte data1 = (DataBufferByte) raster.getDataBuffer();

                byte[] fileBytes = data1.getData();
                 */

                imageImportantBytes = Arrays.copyOfRange(fileBytes, 0, 54*8);

                byte[] fileBytesToEncode = Arrays.copyOfRange(fileBytes,54*8,fileBytes.length);

                fileBytesStringBuilder = new StringBuilder();
                for(byte b : fileBytesToEncode) {
                    fileBytesStringBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
                }

                pictureBytes.setText(Arrays.toString(fileBytes));
            }
        }

        @FXML
        void randomMatrixButtonOnAction(ActionEvent event) {
            // 1. Check if all values are correct:
            //  1.1. k >= n
            //  1.2. vectorLength = k
            // 2. If k=n, then generate only unitary matrix, else generate generating matrix with random matrix
            // 3.

            boolean alert = false;

            if(matrixColumnNumb < matrixRowNumb)
            {
                alert = true;
                Utils.createAlert("Netinkami parametrai", "Kodo ilgis negali būti mažesnis už dimensiją");
            } else if(matrixRowNumb != Integer.parseInt(vectorLengthTextField.getText())) {
                alert = true;
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

            if (!alert)
            {
                Utils.setMatrixTextArea(generatingMatrix, matrixTextArea);
                correctGeneratingMatrix = true;
            }
        }

        @FXML
        void testBttnOnAction(ActionEvent event) throws IOException {

          //  byte[] data = Base64.getDecoder().dec;
        }

    public void binaryVectorStringEncode(String vector){
        //TODO: maybe save instantly as int[] array?
        int[] vectorAsArray = Utils.stringToIntegerArray(vector);
        encryptedVector = matrixUtils.multiplyCodeWithMatrix(vectorAsArray, generatingMatrix);
        //encodedVectorsList.add(encryptedVector);
        encodedVectorStringBuilder.append(Utils.intArrayToString(vectorAsArray));
    }

    public static boolean arrayToBMP(byte[] pixelData, int width, int height, File outputFile) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(pixelData, pixelData.length), null));
        return javax.imageio.ImageIO.write(img, "bmp", outputFile);
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
    }

}
