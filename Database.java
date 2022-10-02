import java.util.List;
import java.util.ArrayList;

public class Database {
    // for each record - should be 1 float, 1 int, 1 string
    public int recordSize;
    // overall disk size = 500 MB
    public int diskSize;
    // remainder size of disk
    public int availableSize;
    // used size of disk
    public int usedSize;
    // size of each block
    public int blockSize;
    // number of records within a block
    public int recordsPerBlock;
    public int totalNoOfBlocks;
    public int totalNoOfBlocksAvail;
    public int totalNoOfBlocksUsed;
    public int totalNoOfRecords;
    public int totalBlockSize;
    public int totalRecordSize;
    public int recordCounter = 0;
    // Block number based on index in Memory Block
    private List<Block> memoryBlock;
    private Block blk;

    public Database(int diskSize, int blockSize) {
        // disk,individual block size, total number of blocks
        this.diskSize = diskSize;
        this.blockSize = blockSize;

        this.totalNoOfBlocks = (int) diskSize / blockSize;
        this.totalNoOfBlocksAvail = (int) diskSize / blockSize;
        this.usedSize = 0;
        this.totalNoOfRecords = 0;
        this.totalNoOfBlocksUsed = 0;
        this.totalBlockSize = 0;
        this.totalRecordSize = 0;
        this.availableSize = diskSize;
        this.recordSize = (Float.SIZE / 8) + (Integer.SIZE / 8) + 10;
        this.recordsPerBlock = (int) blockSize / recordSize;
        memoryBlock = new ArrayList<>();
    }

    public int allocateRecordToBlock(Block newBlk, Record rec) {
        blk = newBlk;
        // ADD NEW RECORD INTO BLOCK
        if (blk.getNumberOfRecords() < this.recordsPerBlock) {
            blk.getRecords().add(rec);
            this.totalNoOfRecords++;
            this.usedSize += recordSize;
            this.availableSize -= recordSize;
            this.totalRecordSize += recordSize;
            return 1;
        }

        else {
            return 0;
        }
    }

    public void allocateBlock(Block newBlk) {
        this.totalNoOfBlocksAvail--;
        this.totalNoOfBlocksUsed++;
        memoryBlock.add(newBlk);
    }

    public void deallocateBlock(Block newBlk) {
        this.totalNoOfBlocksAvail++;
        this.totalNoOfBlocksUsed--;
        this.usedSize -= recordSize;
        this.availableSize += recordSize;
        this.totalRecordSize -= recordSize;
        memoryBlock.remove(newBlk);
        System.out.println("Deallocated  " + newBlk + " to Memory");
        // pop block from memory
    }

    public void printInformation() {
        System.out.println("Overall memory size:" + diskSize + " bytes");
        System.out.println("Overall block size:" + blockSize + " bytes");
        System.out.println("Each record size:" + recordSize + " bytes");
        System.out.println("Overall available size:" + availableSize + " bytes");
        System.out.println("Overall used size:" + usedSize + " bytes");
        System.out.println("Total number of blocks:" + totalNoOfBlocks);
        System.out.println("Total number of blocks avail:" + totalNoOfBlocksAvail);
        System.out.println("Total number of blocks used:" + totalNoOfBlocksUsed);
        System.out.println("Total number of records:" + totalNoOfRecords);
        System.out.println("Record size:" + totalRecordSize + " bytes");
    }

    public void printRecords() {
        for (int x = 0; x < memoryBlock.size(); x++) {
            for (int y = 0; y < memoryBlock.get(x).getRecords().size(); y++) {
                System.out.println("record " + y + " in block " + x + " (T Const): " +
                        memoryBlock.get(x).getRecords().get(y).getTConst());
                System.out.println("record " + y + " in block " + x + " (Average Rating):" +
                        memoryBlock.get(x).getRecords().get(y).getAverageRating());
                System.out.println("record " + y + " in block " + x + " (Number of Votes):" +
                        memoryBlock.get(x).getRecords().get(y).getNumVotes());
            }
        }
    }

}