package utils;

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
}
