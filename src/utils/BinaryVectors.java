package utils;

import java.util.*;

public class BinaryVectors {

    Map<String,Integer> vectorWeightMap;

    // Function to save vectors, find out its weight, save also
    public void saveInTheList(int arr[], int n)
    {
        int weight = 0;

        for (int i = 0; i < n; i++)
        {
            if(arr[i] == 1)
                weight++;

        }

        vectorWeightMap.put(Arrays.toString(arr).replaceAll("\\[|\\]|,|\\s", ""),weight);
    }

    // Function to generate all binary strings of size n
    public void generateAllBinaryStrings(int n, int arr[], int i)
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

    public Map<String,Integer> generateBinaryVectorsOfSizeN(int n)
    {
        //Main function that starts recursion

        vectorWeightMap = new HashMap<String,Integer> ();

        int[] arr = new int[n];

        generateAllBinaryStrings(n, arr, 0);

        return vectorWeightMap;
    }
}
