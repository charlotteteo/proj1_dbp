import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;

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
        Scanner scan = new Scanner(System.in);
        boolean exit = false;
        boolean optionSelected = false;
        int num = 0;

        Database database = null;
        while (!exit) {
            System.out.println("----SELECT OPTION----");
            System.out.println("1: Block size 200 Bytes");
            System.out.println("2: Block size 500 Bytes");
            System.out.println("3: Exit");
            int option = scan.nextInt();
            switch (option) {
                case 1:
                    // Initialise Database
                    num = 200;
                    database = new Database(100000000, 200);
                    exit = true;
                    optionSelected = true;
                    break;
                case 2:
                    num = 500;
                    database = new Database(100000000, 500);
                    exit = true;
                    optionSelected = true;
                    break;
                case 3:
                    scan.close();
                    exit = true;
                    break;
                default:
                    scan.close();
                    exit = true;
                    break;
            }
        }

        if (optionSelected == true) {

            // File text = new File("./data-2.tsv");
            File text = new File("./data-2.tsv");
            // File text = new File("./data-2.tsv");
            // Creating Scanner instance to read File in Java
            Scanner scnr = new Scanner(text);

            // Reading each line of the file using Scanner class

            String line = scnr.nextLine();
            int n = (int) (num - 8) / 12;
            BPlusTree tree = new BPlusTree(n);
            Block block = new Block();
            // keep track of not allocated block
            int blockCreated = 1;
            int blockAllocated = 0;
            while (scnr.hasNextLine()) {
                line = scnr.nextLine();
                String[] values = line.split("\t");
                Record new_record = new Record(values[0], Float.parseFloat(values[1].strip()),
                    Integer.parseInt(values[2].strip()));
                tree.insertKey(Integer.parseInt(String.valueOf(Integer.parseInt(values[2].strip()))),
                    new_record);
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
                // tree.insertKey(Float.parseFloat(String.valueOf(values[1])), new_record);
            }
            System.out.println("Reading file...");
            // database.printRecords();
            // database.printInformation();
            // tree.displayTreeInfo();
            System.out.println("File read");

            boolean exitExperiment = false;
            try {
                while (!exitExperiment) {
                    System.out.println("----SELECT EXPERIMENT----");
                    System.out.println("1: Display Database info");
                    System.out.println("2: Display B+ tree");
                    System.out.println("3: Retrieve numVotes = 500");
                    System.out.println("4: Retrieve numVotes where 30,000 <= numVotes <= 40,000");
                    System.out.println("5: Delete movies where numVotes = 1000 ");
                    System.out.println("6: Exit");
                    Scanner expScanner = new Scanner(System.in);
                    int expOption = expScanner.nextInt();

                    switch (expOption) {
                        case 1:
                            System.out.println("----Experiment 1----");
                            database.printInformation();
                            break;
                        case 2:
                            System.out.println("----Experiment 2----");
                            tree.displayTreeInfo();
                            System.out.println("The parameter n of the B+ tree = " + n);
                            tree.displayHeightInfo();
                            break;
                        case 3:
                            System.out.println("----Experiment 3----");
                            List<Record> searchValues = tree.searchKey(500);
                            tree.printIndexNodeAccess();
                            tree.printDataBlockAccess();
                            float sum = 0;
                            for (int j = 0; j < searchValues.size(); j++) {
                                sum = sum + searchValues.get(j).getAverageRating();
                            }
                            float average = sum / searchValues.size();
                            System.out.println("Average of averageRating: " + average);
                            break;
                        case 4:
                            System.out.println("----Experiment 4----");
                            // INSERT EXP 4 CODES/PRINT STATEMENTS
                            tree.searchKeyRange(30000, 40000);
                            tree.printIndexNodeAccess();
                            tree.printDataBlockAccess();

                            break;
                        case 5:
                            System.out.println("----Experiment 5----");
                            tree.deleteKey(1000);
							tree.displayTreeInfo();
							tree.displayHeightInfo();
							tree.displayUpdatedNodesInfo();
                            break;
                        case 6:
                            // Close all scanner at the end
                            expScanner.close();
                            scnr.close();
                            scan.close();
                            exitExperiment = true;
                            break;
                        default:
                            // Close all scanner at the end
                            expScanner.close();
                            scnr.close();
                            scan.close();
                            exitExperiment = true;
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
