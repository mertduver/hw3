import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    static String rootFolder = "C:\\Users\\mert\\IdeaProjects\\hw3\\src";
    static int MAX_SEQ_NUM = 5;
    static int MAX_SEQ_LENGTH = 1000;
    static String[][] blosum62;
    static String[][] pairwises;

    public static void main(String[] args) {
        // write your code here
        String[][] sequences = readFile("input.txt"); //sequences arrayinde proteinler isimleriyle beraber duruyor
        blosum62 = readBlosum("Blosum62.txt");
        print2DArray(blosum62);
        print2DArray(sequences);

        String[][] similarityMatrix = step1(sequences);
        printPairwises();
        print2DArray(similarityMatrix);

    }

    static String[][] step1(String[][] sequences) {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Enter k value:");
        int k = Integer.parseInt(myObj.nextLine());  // Read user input
        String[][] outData = new String[k][k];
        int numberOfPairWises = combinationWithTwo(k);
        pairwises = new String[numberOfPairWises][2];
        int p = 0;
        System.out.print("Enter the gap penalty:");
        int gapPenalty = Integer.parseInt(myObj.nextLine());  // Read user input
        for (int i = 0; i < k - 1; i++) {
            for (int j = i + 1; j < k; j++) {//loops for selecting pairs
                //L:left, U:up, D:diagonal, E:error, F:finished
                int verticalLength = sequences[i][1].length() + 1;
                int horizontalLength = sequences[j][1].length() + 1;
                char[][] backTrace = new char[verticalLength][horizontalLength];
                int[][] globalAlignment = new int[verticalLength][horizontalLength];

                //global alignment (dynamic programming)
                for (int v = 0; v < verticalLength; v++) {
                    for (int h = 0; h < horizontalLength; h++) {
                        if (v == 0 && h == 0) {
                            globalAlignment[v][h] = 0;
                            backTrace[v][h] = 'F';
                        } else if (v == 0) {
                            globalAlignment[v][h] -= gapPenalty;
                            backTrace[v][h] = 'L';
                        } else if (h == 0) {
                            globalAlignment[v][h] -= gapPenalty;
                            backTrace[v][h] = 'U';
                        } else {// here's the funny part begins :)
                            char maxChar = 'E';//error
                            int maxInt = Integer.MIN_VALUE;
                            //start to calculate and select the minimum one (left, up or diagonal(match mismatch))
                            if (globalAlignment[v][h - 1] - gapPenalty > maxInt) {
                                maxInt = globalAlignment[v][h - 1] - gapPenalty;
                                maxChar = 'L';
                            }
                            if (globalAlignment[v - 1][h] - gapPenalty > maxInt) {
                                maxInt = globalAlignment[v - 1][h] - gapPenalty;
                                maxChar = 'U';
                            }
                            int diagonalScore = globalAlignment[v - 1][h - 1] + getBlosum62Score(sequences[i][1].substring(v - 1,v), sequences[j][1].substring(h - 1,h));
                            if (diagonalScore > maxInt) {
                                maxInt = diagonalScore;
                                maxChar = 'D';
                            }
                            globalAlignment[v][h] = maxInt;
                            backTrace[v][h] = maxChar;
                        }
                    }
                }
                String verticalSequenceReverse = "";
                String horizontalSequenceReverse = "";
                int v = verticalLength - 1;
                int h = horizontalLength - 1;
                do {
                    if (backTrace[v][h] == 'D') {
                        verticalSequenceReverse += sequences[i][1].charAt(v - 1);
                        horizontalSequenceReverse += sequences[j][1].charAt(h - 1);
                        v--;
                        h--;
                    } else if (backTrace[v][h] == 'U') {
                        verticalSequenceReverse += sequences[i][1].charAt(v - 1);
                        horizontalSequenceReverse += "-";
                        v--;
                    } else if (backTrace[v][h] == 'L') {
                        horizontalSequenceReverse += sequences[j][1].charAt(h - 1);
                        verticalSequenceReverse += "-";
                        h--;
                    } else if (backTrace[v][h] == 'E') {
                        System.out.println("error at backtracking");
                    }

                } while (backTrace[v][h] != 'F');
                StringBuilder verticalSequence = new StringBuilder();
                verticalSequence.append(verticalSequenceReverse);
                verticalSequence.reverse();

                StringBuilder horizontalSequence = new StringBuilder();
                horizontalSequence.append(horizontalSequenceReverse);
                horizontalSequence.reverse();
                pairwises[p][0] = String.valueOf(verticalSequence);
                pairwises[p][1] = String.valueOf(horizontalSequence);
                p++;


                //calculating the similarity matrix;
                double exactMatches=0;
                for (int l = 0; l < verticalSequence.length(); l++) {
                    if (verticalSequence.charAt(l) == horizontalSequence.charAt(l)) {
                        exactMatches++;
                    }
                }
                double sim=exactMatches/verticalSequence.length();
                outData[i][j]= String.valueOf(sim);
            }
        }


        return outData;
    }

    //used for determining how many pairwise will be created
    static int combinationWithTwo(int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        return sum;
    }

    static int getBlosum62Score(String st1, String st2) {
        //assigned 24 because if the protein is not on our blosum list it should be considered as *(joker character)
        int index1 = 24, index2 = 24;
        for (int i = 0; i < blosum62.length; i++) {
            if (st1.equals(blosum62[0][i])) {
                index1 = i;
            }
            if (st2.equals(blosum62[0][i])) {
                index2 = i;
            }
        }
        int value = Integer.parseInt(blosum62[index1][index2]);
        return value;
    }

    static void printPairwises() {
        for (int i = 0; i < pairwises.length; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.println(pairwises[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    static void print2DArray(String[][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static String[][] readBlosum(String fileName) {
        String[][] outData = new String[25][25];
        outData[0][0] = "";
        try {
            File myObj = new File(rootFolder + "\\" + fileName);
            Scanner myReader = new Scanner(myObj);
            String line = myReader.nextLine();
            line = line.trim();
            line = line.replace("  ", " ");
            String[] splitted = line.split(" ");
            for (int i = 0; i < splitted.length; i++) {
                outData[0][i + 1] = splitted[i];
            }
            int i = 1;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine();
                line = line.trim();
                line = line.replace("  ", " ");
                splitted = line.split(" ");
                outData[i] = splitted;
                i++;
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
                String data = myReader.nextLine().trim();
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

