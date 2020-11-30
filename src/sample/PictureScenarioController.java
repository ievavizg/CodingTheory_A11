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
    private StringBuilder corruptedStringBuilderInBinary;
    private boolean correctGeneratingMatrix;
    private WritableRaster raster;
    private int imageType;
    private boolean isCorrectGeneratingMatrix;

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
    private TextField pathToSaveCorruptedPicture;


    @FXML
        void encodeVectorButtonOnAction(ActionEvent event) throws IOException {

        isCorrectGeneratingMatrix = checkIfGeneratingMatrixIsCorrect();

        boolean allFieldsCorrect = true;

        if(pathToSaveCorruptedPicture.getText().isEmpty() || pathToSaveNewPicture.getText().isEmpty())
        {
            allFieldsCorrect = false;
        }

        if(isCorrectGeneratingMatrix && allFieldsCorrect) {

            //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.

            String binaryTextToEncrypt = fileBytesStringBuilder.toString();
            encodedVectorStringBuilder = new StringBuilder();
            do {
                if (binaryTextToEncrypt.length() > matrixRowNumb) {
                    binaryVectorStringEncode(binaryTextToEncrypt.substring(0, matrixRowNumb));
                    binaryTextToEncrypt = binaryTextToEncrypt.substring(matrixRowNumb, binaryTextToEncrypt.length());
                } else if (binaryTextToEncrypt.length() < matrixRowNumb) {
                    do {
                        binaryTextToEncrypt += "0";
                    } while (!(binaryTextToEncrypt.length() == matrixRowNumb));
                    binaryVectorStringEncode(binaryTextToEncrypt);
                    binaryTextToEncrypt = "";
                } else {
                    binaryVectorStringEncode(binaryTextToEncrypt);
                    binaryTextToEncrypt = "";
                }
            } while (!binaryTextToEncrypt.isEmpty());


            String encodedVectorString = encodedVectorStringBuilder.toString();

            //TODO: open encoded picture

            int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
            SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb, matrixColumnNumb, controlMatrix);
            Map<String, Integer> syndromeMap = syndromeUtils.getSyndromeMap();

            //<------->
            //DECODING

            decodedStringBuilderInBinary = new StringBuilder();
            corruptedStringBuilderInBinary = new StringBuilder();

            zerosAddedToVector = new StringBuilder();

            do {
                if (encodedVectorString.length() > matrixColumnNumb) {

                    int[] vectorToDecode = Utils.stringToIntegerArray(encodedVectorString.substring(0, matrixColumnNumb));

                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode, corruptionProbability);

                    int[] corruptedVector = corruptedVectorBackToGivenVectorSize(encryptedVectorSent, matrixColumnNumb, matrixRowNumb);
                    corruptedStringBuilderInBinary.append(Utils.intArrayToString(corruptedVector));

                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent, syndromeMap, controlMatrix, matrixColumnNumb, matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = encodedVectorString.substring(matrixColumnNumb, encodedVectorString.length());
                } else if (encodedVectorString.length() < matrixColumnNumb) {
                    do {
                        encodedVectorString += "0";
                        zerosAddedToVector.append("0");

                    } while (!(encodedVectorString.length() == matrixColumnNumb));

                    int[] vectorToDecode = Utils.stringToIntegerArray(encodedVectorString);
                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode, corruptionProbability);

                    int[] corruptedVector = corruptedVectorBackToGivenVectorSize(encryptedVectorSent, matrixColumnNumb, matrixRowNumb);
                    corruptedStringBuilderInBinary.append(Utils.intArrayToString(corruptedVector));

                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent, syndromeMap, controlMatrix, matrixColumnNumb, matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = "";
                } else {
                    int[] vectorToDecode = Utils.stringToIntegerArray(encodedVectorString);

                    int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode, corruptionProbability);
                    int[] corruptedVector = corruptedVectorBackToGivenVectorSize(encryptedVectorSent, matrixColumnNumb, matrixRowNumb);
                    corruptedStringBuilderInBinary.append(Utils.intArrayToString(corruptedVector));

                    int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent, syndromeMap, controlMatrix, matrixColumnNumb, matrixRowNumb);
                    decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));

                    encodedVectorString = "";
                }
            } while (!encodedVectorString.isEmpty());

            //<------->
            //SAVING ENCRYPTED IMAGE

            List<Integer> listEncrypted = new ArrayList<>();

            String encryptedVector = corruptedStringBuilderInBinary.toString().substring(0, corruptedStringBuilderInBinary.length() - zerosAddedToVector.length());

            for (String str : encryptedVector.split("(?<=\\G.{8})"))
                listEncrypted.add(Integer.parseInt(str, 2));

            byte[] dataEncrypted = new byte[listEncrypted.size()];

            int index = 0;
            Iterator<Integer> iterator = listEncrypted.iterator();

            while (iterator.hasNext()) {
                Integer i = iterator.next();
                dataEncrypted[index] = i.byteValue();
                index++;
            }

            byte[] destinationEncrypted = new byte[imageImportantBytes.length + listEncrypted.size()];
            System.arraycopy(imageImportantBytes, 0, destinationEncrypted, 0, imageImportantBytes.length);
            System.arraycopy(dataEncrypted, 0, destinationEncrypted, imageImportantBytes.length, dataEncrypted.length);

            //"/Users/ievaviz/Desktop/outputCorrupted.bmp"
            File fileOutputEncrypted = new File(pathToSaveCorruptedPicture.getText());

            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOutputEncrypted))) {
                out.write(destinationEncrypted);
            }


            Stage primaryStageEncrypted = new Stage();
            primaryStageEncrypted.setTitle("Iškraiptytas paveikslėlis");
            ImageView imageViewEncrypted = new ImageView(fileOutputEncrypted.toURI().toURL().toExternalForm());
            HBox hboxEncrypted = new HBox(imageViewEncrypted);

            Scene sceneEncrypted = new Scene(hboxEncrypted, 200, 100);
            primaryStageEncrypted.setScene(sceneEncrypted);
            primaryStageEncrypted.show();

            //<------->
            //SAVING DECODED IMAGE

            List<Integer> list = new ArrayList<>();

            String decodedVector = decodedStringBuilderInBinary.toString().substring(0, decodedStringBuilderInBinary.length() - zerosAddedToVector.length());

            for (String str : decodedVector.split("(?<=\\G.{8})"))
                list.add(Integer.parseInt(str, 2));

            byte[] data = new byte[list.size()];

            index = 0;
            Iterator<Integer> iteratorList = list.iterator();

            while (iteratorList.hasNext()) {
                Integer i = iteratorList.next();
                data[index] = i.byteValue();
                index++;
            }

            byte[] destination = new byte[imageImportantBytes.length + list.size()];
            System.arraycopy(imageImportantBytes, 0, destination, 0, imageImportantBytes.length);
            System.arraycopy(data, 0, destination, imageImportantBytes.length, data.length);

            File fileOutput = new File(pathToSaveNewPicture.getText());

            //"/Users/ievaviz/Desktop/output.bmp"
            //One way to save image
            if (!pathToSaveNewPicture.getText().isEmpty()) {
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOutput))) {
                    out.write(destination);
                }
            }

            //Another way
            //>>
            /*
            ByteArrayInputStream bis = new ByteArrayInputStream(destination);
            BufferedImage bImage2 = ImageIO.read(bis);
            ImageIO.write(bImage2, "jpg", fileOutput);
            System.out.println("image created");

             */
            //<<

            //Third way
            //>>
            /*
            ByteArrayInputStream bis = new ByteArrayInputStream(destination);
            BufferedImage image3 = new BufferedImage(raster.getWidth(), raster.getHeight(), imageType);
            image3 = ImageIO.read(bis);
            ImageIO.write(image3, "jpg", fileOutput);

             */
            //<<

            Stage primaryStage = new Stage();

            primaryStage.setTitle("Paveikslėlis po dekodavimo");

            ImageView imageView = new ImageView(fileOutput.toURI().toURL().toExternalForm());

            HBox hbox = new HBox(imageView);

            Scene scene = new Scene(hbox, 200, 100);
            primaryStage.setScene(scene);
            primaryStage.show();

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
                //>>
                /*
                BufferedImage bufferedImage = ImageIO.read(file);
                //int width = bufferedImage.getWidth();
                //int height = bufferedImage.getHeight();
                ColorModel cm = bufferedImage.getColorModel();
                imageType = bufferedImage.getType();

                raster = bufferedImage.getRaster();
                DataBufferByte data1 = (DataBufferByte) raster.getDataBuffer();

                byte[] fileBytes = data1.getData();

                 */

                //<<

                //Another way
                //>>

                /*
                BufferedImage originalImage=ImageIO.read(file);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", baos );
                byte[] fileBytes=baos.toByteArray();

                 */

                //<<

                imageImportantBytes = Arrays.copyOfRange(fileBytes, 0, 54);

                byte[] fileBytesToEncode = Arrays.copyOfRange(fileBytes,54,fileBytes.length);

                fileBytesStringBuilder = new StringBuilder();
                for(byte b : fileBytesToEncode) {
                    fileBytesStringBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
                }
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
        encodedVectorStringBuilder.append(Utils.intArrayToString(encryptedVector));
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

    public int[] corruptedVectorBackToGivenVectorSize(int[] encryptedVector, int matrixColumnNumb, int matrixRowNumb) {
        int vectorToReturnLength = matrixColumnNumb - (matrixColumnNumb-matrixRowNumb);
        int[] vectorToReturn = new int[vectorToReturnLength];
        for (int j=0; j<vectorToReturnLength; j++)
        {
            vectorToReturn[j] = encryptedVector[j];
        }
        return vectorToReturn;
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
