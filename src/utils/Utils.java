package utils;

import javafx.scene.control.TextArea;

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

        List<String> lineList = new ArrayList<String>();
        int matrixColumnNumb = 0;

        for (String line : matrixTextArea.getText().split("\\n")) {
            lineList.add(line);
            matrixColumnNumb = line.length() - line.replace(" ", "").length() + 1;
        }

        int[][] matrix = new int[lineList.size()][matrixColumnNumb];
        int i = 0, j = 0;

        for (String newLine : lineList)
        {
            j = 0;

            for (String numberInString : newLine.split(" ")) {
                matrix[i][j] = Integer.parseInt(numberInString);
                j++;
            }
            i++;
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
}
