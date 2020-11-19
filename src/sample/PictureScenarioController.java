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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class PictureScenarioController {

    List<String> binaryTextOfSizeKList;

    private int[][] generatingMatrix;
    private int[] unencryptedVector;
    private int[] encryptedVector;

    private int matrixRowNumb;
    private int matrixColumnNumb;
    private double corruptionProbability;
    private List<int[]> encodedVectorsList;

    private final MatrixUtilsInterface matrixUtils = new MatrixUtils();

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
        private TextArea encodedTextArea;

        @FXML
        private TextArea decodedTextArea;

        @FXML
        private Button importPicture;

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
        void importPictureButtonOnAction(ActionEvent event) throws IOException {

            //TODO: do i need this button?

            //Issaugom paveiksleli i stringbuilderi

            BufferedImage image = ImageIO.read(new File("/Users/ievaviz/Desktop/Fortest.bmp"));

            image.getHeight();
            image.getWidth();

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", byteStream);

            byte[] bytes = byteStream.toByteArray();

            StringBuilder sb = new StringBuilder();
            for(byte b : bytes) {
                sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }

            //TODO: isimti i Utils kazkuri, nes kartojasi
            //Save binaries of size k into the list
            binaryTextOfSizeKList = new ArrayList<>();
            String binaryTextToEncrypt = sb.toString();
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

            //TODO: visa do while isimti

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
            int[][] controlMatrix = matrixUtils.generateControlMatrix(generatingMatrix);
            SyndromeUtils syndromeUtils = new SyndromeUtils(matrixRowNumb,matrixColumnNumb,controlMatrix);
            Map<String,Integer> syndromeMap = syndromeUtils.getSyndromeMap();

            StringBuilder decodedStringBuilderInBinary = new StringBuilder();

            for (int[] vectorToDecode:encodedVectorsList) {

                int[] encryptedVectorSent = matrixUtils.sendVectorThroughChanel(vectorToDecode,corruptionProbability);
                int[] decodedVector = matrixUtils.decodeVector(encryptedVectorSent,syndromeMap,controlMatrix,matrixColumnNumb,matrixRowNumb);
                decodedStringBuilderInBinary.append(Utils.intArrayToString(decodedVector));
            }

            //String decodedText = Utils.binaryToText(decodedStringBuilderInBinary.toString());

            //decodedTextArea.setText(decodedText);

            byte[] byteArrray = decodedStringBuilderInBinary.toString().getBytes();

            //ByteArrayInputStream bis = new ByteArrayInputStream(byteArrray);
            //BufferedImage img = ImageIO.read(new ByteArrayInputStream(byteArrray));
            //ImageIO.write(img, "png", new File("/Users/ievaviz/Desktop/output.bmp") );

            //Sukuria damaged image
           // try (OutputStream out = new BufferedOutputStream(new FileOutputStream("/Users/ievaviz/Desktop/output.bmp"))) {
           //     out.write(byteArrray);
           // }

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

}
