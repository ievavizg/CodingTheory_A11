package sample;

import com.sun.webkit.network.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.MatrixUtils;
import utils.MatrixUtilsInterface;
import utils.SyndromeUtils;
import utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Map;

public class PictureScenarioController {

 //   List<String> binaryTextOfSizeKList;

    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;
    private List<int[]> encodedVectorsList;

    private StringBuilder encodedVectorStringBuilder;
    private StringBuilder fileBytesStringBuilder;

    private final MatrixUtilsInterface matrixUtils = new MatrixUtils();

    private StringBuilder decodedStringBuilderInBinary;

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
        void encodeVectorButtonOnAction(ActionEvent event) {
            //TODO: sitas button veikia kaip issaugojimas generuojancios matricos!

            //Vektoriaus uzkodavimas, tai generuojancios matricos ir vektoriaus sandauga.

          //  encodedVectorsList = new ArrayList<>();



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

            decodedStringBuilderInBinary = new StringBuilder();

            //<------->
            //DECODING

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



            /*
            for (String vector:binaryTextOfSizeKList) {
                //TODO: maybe save instantly as int[] array?
                int[] vectorAsArray = Utils.stringToIntegerArray(vector);
                encryptedVector = matrixUtils.multiplyCodeWithMatrix(vectorAsArray, generatingMatrix);
                encodedVectorsList.add(encryptedVector);
            }

             */

            /*
            //TODO: list of arrays to string and set TextArea with string.
            StringBuilder encodedFullVector = new StringBuilder();
            for (int[] vector:encodedVectorsList) {
                encodedFullVector.append(Utils.intArrayToString(vector));
            }


             */
           // Utils.setTextToTextField(encodedFullVector.toString(),encodedTextArea);
        }

        @FXML
        void importPictureButtonOnAction(ActionEvent event) throws IOException {

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

                byte[] fileBytes = Files.readAllBytes(file.toPath());

                fileBytesStringBuilder = new StringBuilder();
                for(byte b : fileBytes) {
                    fileBytesStringBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
                }


                /*
                //TODO: isimti i Utils kazkuri, nes kartojasi
                //Save binaries of size k into the list
              //  binaryTextOfSizeKList = new ArrayList<>();
                String binaryTextToEncrypt = fileBytesString.toString();
                do{
                    if(binaryTextToEncrypt.length()>matrixRowNumb){

                        binaryTextOfSizeKList.add(binaryTextToEncrypt.substring(0, matrixRowNumb));


                        binaryTextToEncrypt = binaryTextToEncrypt.substring(matrixRowNumb, binaryTextToEncrypt.length());
                    } else if(binaryTextToEncrypt.length() < matrixRowNumb) {
                        do{
                            binaryTextToEncrypt += "0";
                        }while (!(binaryTextToEncrypt.length()==matrixRowNumb));
                        binaryTextOfSizeKList.add(binaryTextToEncrypt);
                        binaryTextToEncrypt = "";
                    } else {
                        binaryTextOfSizeKList.add(binaryTextToEncrypt);
                        binaryTextToEncrypt = "";
                    }
                }while (!binaryTextToEncrypt.isEmpty());

                 */

                //TODO: visa do while isimti

            }
        }

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
        void testBttnOnAction(ActionEvent event) throws IOException {

            byte[] data = decodedStringBuilderInBinary.toString().getBytes();

            System.out.println(decodedStringBuilderInBinary);
        //    String base64String = Base64.getEncoder().encodeToString(data);

        //    byte[] dataOther = Base64.getDecoder().decode(decodedStringBuilderInBinary.toString());


         //   byte[] imgByteArray = Base64.decodeBase64(origin);
         //   FileOutputStream imgOutFile = new FileOutputStream("C:\\Workspaces\\String_To_Image.jpg");
         //   imgOutFile.write(dataOther);
         //   imgOutFile.close();



            //System.out.println(decodedStringBuilderInBinary.toString());

            /*
            for (int[] vectorToDecode:encodedVectorsList) {



                int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
                int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);
                decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));
            }

             */

            //String decodedText = Utils.binaryToText(decodedStringBuilderInBinary.toString());

            //decodedTextArea.setText(decodedText);


            /*

            byte[] data = decodedStringBuilderInBinary.toString().getBytes();

            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage bImage2 = ImageIO.read(bis);
            ImageIO.write(bImage2, "bmp", new File("/Users/ievaviz/Desktop/output1.bmp") );
            System.out.println("image created");


             */
            //ByteArrayInputStream bis = new ByteArrayInputStream(byteArrray);
            //BufferedImage img = ImageIO.read(new ByteArrayInputStream(byteArrray));
            //ImageIO.write(img, "bmp", new File("/Users/ievaviz/Desktop/output.bmp") );

            //Sukuria damaged image
            //try (OutputStream out = new BufferedOutputStream(new FileOutputStream("/Users/ievaviz/Desktop/output.bmp"))) {
            //    out.write(byteArrray);
            //}

            //} System.out.println("image created");

            /*
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrray);
            BufferedImage image = ImageIO.read(bais);
            File outputfile = new File("saved.png");
            ImageIO.write(image , "png", new File("\\Users\\ievaviz\\Desktop\\output.png"));
            ImageIO.write(image, "png", outputfile); // Write the Buffered Image into an output file

             */

            /*
            // convert byte[] back to a BufferedImage
            InputStream is = new ByteArrayInputStream(byteArrray);
            BufferedImage newBi = ImageIO.read(is);


             */

          //  BufferedImage img=new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
          //  img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(srcbuf, srcbuf.length), new Point() ) );


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
        if(!probabilityNumberTextArea.getText().isEmpty())
        {
            //TODO: add check if 0<=p<=1
            this.corruptionProbability = Double.parseDouble(probabilityNumberTextArea.getText());
        }else
        {
            //TODO: throw warning or error
        }
    }

    public void binaryVectorStringEncode(String vector){
        //TODO: maybe save instantly as int[] array?
        int[] vectorAsArray = Utils.stringToIntegerArray(vector);
        encryptedVector = matrixUtils.multiplyCodeWithMatrix(vectorAsArray, generatingMatrix);
        //encodedVectorsList.add(encryptedVector);
        encodedVectorStringBuilder.append(Utils.intArrayToString(vectorAsArray));
    }

    public void decodeVectorString(String vector){

    }

}
