import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;

public class main {

    public static void main(String args[]) throws FileNotFoundException {

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

            File text = new File("./data-2.tsv");
            Scanner scanner = new Scanner(text);

            String line = scanner.nextLine();
            int n = (int) (num - 8) / 12;
            BPlusTree tree = new BPlusTree(n);
            Block block = new Block();

            int blockCreated = 1;
            int blockAllocated = 0;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] values = line.split("\t");
                Record new_record = new Record(values[0], Float.parseFloat(values[1].strip()),
                        Integer.parseInt(values[2].strip()));
                tree.insertNodeKey(new_record, Integer.parseInt(String.valueOf(Integer.parseInt(values[2].strip()))));
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
                            database.displayInformation();
                            break;
                        case 2:
                            System.out.println("----Experiment 2----");
                            tree.printTree();
                            System.out.println("The parameter n of the B+ tree = " + n);
                            tree.printHeightInfo();
                            break;
                        case 3:
                            System.out.println("----Experiment 3----");
                            List<Record> searchValues = tree.searchNodeKey(500);
                            float sum = 0;
                            for (int j = 0; j < searchValues.size(); j++) {
                                System.out.print("TConst = ");
                                System.out.print(searchValues.get(j).getTConst() + " " + " AverageRating = " +
                                        searchValues.get(j).getAverageRating() + " " + "NumVotes = " +
                                        searchValues.get(j).getNumVotes() + " ");
                                System.out.print("\n");
                                sum = sum + searchValues.get(j).getAverageRating();
                            }
                            float average = sum / searchValues.size();
                            System.out.print("\n");
                            System.out.println("Average of averageRating: " + average);
                            tree.printIndexNodeAccess();
                            tree.printDataBlockAccess();
                            break;
                        case 4:
                            System.out.println("----Experiment 4----");
                            tree.searchNodeKeyRange(30000, 40000);
                            tree.printIndexNodeAccess();
                            tree.printDataBlockAccess();

                            break;
                        case 5:
                            System.out.println("----Experiment 5----");
                            tree.deleteNodeKey(1000);
                            tree.printTree();
                            tree.printHeightInfo();
                            tree.printUpdatedNodesInfo();
                            break;
                        case 6:
                            // Close all scanner at the end
                            expScanner.close();
                            scanner.close();
                            scan.close();
                            exitExperiment = true;
                            break;
                        default:
                            // Close all scanner at the end
                            expScanner.close();
                            scanner.close();
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
