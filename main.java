import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {

    public static void main(String args[]) throws FileNotFoundException {

        // // creating File instance to reference text file in Java
        // File text = new File("./data-sample.tsv");

        // // Creating Scanner instance to read File in Java
        // Scanner scnr = new Scanner(text);

        // // Reading each line of the file using Scanner class

        // String line = scnr.nextLine();
        // while (scnr.hasNextLine()) {
        // line = scnr.nextLine();
        // String[] values = line.split("\t");
        // Record new_record = new Record(values[0],
        // Float.parseFloat(values[1].strip()),
        // Integer.parseInt(values[2].strip()));
        // // new_record.printRecord();
        // }
        // TESTING BLOCK
        // Block block = new Block();
        // Record rec = new Record("2314as", (float) 6.3, 4);
        // // Initialise Database
        // Database database = new Database(50000000, 200);
        // database.printInformation();
        // database.allocateRecordToBlock(block, rec);
        // Record rec1 = new Record("2314as", (float) 6.3, 4);
        // database.allocateRecordToBlock(block, rec1);
        // database.allocateBlock(block);
        // database.printInformation();
        // database.printRecords();

        // ALLOCATE RECORDS TO MEMORY

        // Initialise Database
        Database database = new Database(100000000, 200);
        database.printInformation();

        System.out.println("##############EXPERIMENT 1: ######################");
        File text = new File("./data-2.tsv");
        // File text = new File("./data-sample.tsv");

        // Creating Scanner instance to read File in Java
        Scanner scnr = new Scanner(text);

        // Reading each line of the file using Scanner class

        String line = scnr.nextLine();

        Block block = new Block();
        // keep track of not allocated block
        int blockCreated = 1;
        int blockAllocated = 0;
        while (scnr.hasNextLine()) {
            line = scnr.nextLine();
            String[] values = line.split("\t");
            Record new_record = new Record(values[0], Float.parseFloat(values[1].strip()),
                    Integer.parseInt(values[2].strip()));
            // add all records to block and add to memory
            if (database.allocateRecordToBlock(block, new_record) == 0) {
                blockAllocated++;
                database.allocateBlock(block);
                block = new Block();
                blockCreated++;
                database.allocateRecordToBlock(block, new_record);
            }
        }
        if (blockCreated != blockAllocated) {
            database.allocateBlock(block);
        }

        database.printRecords();
        database.printInformation();

    }
}
