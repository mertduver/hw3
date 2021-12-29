import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    static String rootFolder = "C:\\Users\\others\\IdeaProjects\\hw3\\src";
    static int MAX_SEQ_NUM = 100;
    static int MAX_SEQ_LENGTH = 1000;

    public static void main(String[] args) {
        // write your code here
        String[][] sequences = readFile("input.txt"); //sequences arrayinde proteinler isimleriyle beraber duruyor
        System.out.println(sequences[1][0]);
    }

    static String[][] readBlosum(String fileName) {
        String [][] outData= new String[25][25];
        try {
            File myObj = new File(rootFolder + "\\" + fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



        return outData;
    }

    public static String[][] readFile(String fileName) { //returns a two dimensional array that holds both the names and sequences of certain species
        String[][] output = new String[MAX_SEQ_NUM][2];
        int lineNum = 0;
        int seqNum = 0;
        try {
            File myObj = new File(rootFolder + "\\" + fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.toCharArray()[0] == '>') {
                    output[seqNum][0] = data;
                } else {
                    output[seqNum][1] = data;
                    seqNum++;
                }
               // System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return output;
    }
}

