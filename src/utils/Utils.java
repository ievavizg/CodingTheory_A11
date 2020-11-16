package utils;

import java.util.Arrays;

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
}
