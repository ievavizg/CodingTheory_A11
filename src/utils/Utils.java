package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static int[] stringToIntegerArray(String str)
    {
        int size = str.length();
        int[] intArray = new int[size];

        for(int i=0; i<size; i++) {
            intArray[i] = Character.digit(str.charAt(i), 10);
        }

        return intArray;
    }

    public static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();
    }

    public static String intArrayToString(int[] array){
        return Arrays.toString(array).replaceAll("\\[|\\]|,|\\s", "");
    }

    public static String binaryToText(String binary) {
        return Arrays.stream(binary.split("(?<=\\G.{8})"))/* regex to split the bits array by 8*/
                .parallel()
                .map(eightBits -> (char)Integer.parseInt(eightBits, 2))
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                ).toString();
    }

    public static int[][] textAreaToTwoDimensionalArray(TextArea matrixTextArea)
    {
        int[][] matrix = null;
        boolean alert = false;

        List<String> lineList = new ArrayList<String>();
        int matrixColumnNumb = 0;

        for (String line : matrixTextArea.getText().split("\\n")) {
            lineList.add(line);
            int length = line.length() - line.replace(" ", "").length() + 1;
            if(matrixColumnNumb == 0)
            {
                matrixColumnNumb = length;
            } else if(matrixColumnNumb != length){
                alert = true;
                createAlert("Netinkama matricos struktūra", "Nevienodas stulpelių skaičius matricoje");
            }
        }

        if(!alert) {
            matrix = new int[lineList.size()][matrixColumnNumb];
            int i = 0, j = 0;

            for (String newLine : lineList) {
                j = 0;

                for (String numberInString : newLine.split(" ")) {
                    matrix[i][j] = Integer.parseInt(numberInString);
                    j++;
                }
                i++;
            }
        }

        return matrix;
    }

    public static void setMatrixTextArea(int[][] matrix, TextArea textArea)
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

        textArea.setText(matrixToDisplay.toString());
    }

    public static void setTextToTextField(String text, TextArea textArea)
    {
        textArea.setText(text);
    }

    public static Integer tryParseToInteger(String text) {

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            createAlert("Neteisinga įvestis","Prašome įvesti skaičių, pvz. 2");
            return null;
        }
    }

    public static Double tryParseToDouble(String text) {

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            createAlert("Neteisinga įvestis","Prašome įvesti realų skaičių, pvz. 0.1");
            return null;
        }
    }

    public static void createAlert(String headerText, String contextText)
    {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(headerText);
        errorAlert.setContentText(contextText);
        errorAlert.showAndWait();
    }

    public static int[] stringToBinaryIntegerArrayWithAlert(String str)
    {
        int size = str.length();
        int[] intArray = new int[size];

        for(int i=0; i<size; i++) {
            if (Character.digit(str.charAt(i), 10) == 0 || Character.digit(str.charAt(i), 10) == 1)
            {
                intArray[i] = Character.digit(str.charAt(i), 10);
            } else createAlert("Klaida bandant tekstą paversti į dvejetainių skaičių masyvą",
                    "Prašome patikslinti įvestus duomenis, turėtų būti pvz. 10001");
        }

        return intArray;
    }

    public static void setVectorTextField(int[] vector, TextField textField)
    {
        StringBuilder vectorToDisplay = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            vectorToDisplay.append(vector[i]);
        }

        textField.setText(vectorToDisplay.toString());
    }

    public static void setMistakesListToTextArea(List<Integer> vector, TextArea textArea, String separator)
    {
        StringBuilder vectorToDisplay = new StringBuilder();

        if(!(vector == null)) {

            vectorToDisplay.append("Iš viso klaidų: " + vector.size() + "! \n" + "Pozicijose: ");
            for (Integer number:vector) {
                vectorToDisplay.append(number + separator);
            }
        } else {
            vectorToDisplay.append("Siunčiant kanalu klaidų nepadaryta");
        }

        textArea.setText(vectorToDisplay.toString());
    }
}
