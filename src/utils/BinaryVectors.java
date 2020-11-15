package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinaryVectors {

    List<String> vectorsList;

    // Function to save vectors
    public void saveInTheList(int arr[], int n)
    {

        vectorsList.add(Arrays.toString(arr).replaceAll("\\[|\\]|,|\\s", ""));

    }

    // Function to generate all binary strings
    public void generateAllBinaryStrings(int n,
                                         int arr[], int i)
    {
        if (i == n)
        {
            saveInTheList(arr, n);
            return;
        }

        //First assigning 0 in i'th position and try for others combinations in other positions.
        arr[i] = 0;
        generateAllBinaryStrings(n, arr, i + 1);

        //Then assigning 1 in i'th position and try for others combinations in other positions.
        arr[i] = 1;
        generateAllBinaryStrings(n, arr, i + 1);
    }

    public List<String> generateBinaryVectorsOfSizeN(int n)
    {
        vectorsList = new ArrayList<>();
        int[] arr = new int[n];

        generateAllBinaryStrings(n, arr, 0);

        return vectorsList;
    }
}
