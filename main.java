import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {

    public static void main(String args[]) throws FileNotFoundException {

        // creating File instance to reference text file in Java
        File text = new File("./data-sample.tsv");

        // Creating Scanner instance to read File in Java
        Scanner scnr = new Scanner(text);

        // Reading each line of the file using Scanner class
        int lineNumber = 1;
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            String[] values = line.split("\t");
            Record new_record = new Record(values[0], Float.parseFloat("5.1"), Integer.parseInt("6"));
            System.out.println("Record " + lineNumber + " :" + values[1]);
            new_record.printRecord();
            System.out.println("\n");
            lineNumber++;
        }
        Block block = new Block();
        Record rec = new Record("2314as", (float) 6.3, 4);
        Database database = new Database(50000000, 200);
        database.printInformation();
        System.out.println("\n");
        database.allocateBlock(block, rec);
        System.out.println("\n");
        database.printInformation();

    }
}
