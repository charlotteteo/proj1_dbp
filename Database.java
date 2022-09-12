import java.util.List;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Database {
    // for each record - should be 1 float, 1 int, 1 string
    public int recordSize = (Float.SIZE / 8) + (Integer.SIZE / 8) + 10;
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
    private List<Block> memoryBlock;
    private Block blk;

    public Database(int diskSize, int blockSize) {
        this.diskSize = diskSize;
        this.blockSize = blockSize;
        this.totalNoOfBlocksAvail = (int) diskSize / blockSize;
        this.usedSize = 0;
        this.totalNoOfRecords = 0;
        this.totalNoOfBlocksUsed = 0;
        this.totalBlockSize = 0;
        this.totalRecordSize = 0;
        memoryBlock = new ArrayList<>();
    }

    public void allocateBlock(Block newBlk, Record rec) {

        // Block newBlk = new Block();
        blk = newBlk;
        blk.getRecords().add(rec);

        if (recordCounter % recordsPerBlock == 0) {
            recordCounter = 0;
            this.usedSize += blockSize;
            totalBlockSize += blockSize;
            memoryBlock.add(blk);
            this.totalNoOfBlocksAvail--;
            this.totalNoOfBlocksUsed++;
        }

        this.totalRecordSize += recordSize;
        this.recordCounter++;
    }

    public void deallocateBlock() {
        // Number of blocks remaining plus 1
        this.totalNoOfBlocksAvail++;

        // Number of blocks deallocated minus 1
        this.totalNoOfBlocksUsed--;

        this.usedSize -= recordSize;

        this.totalRecordSize -= recordSize;
    }

    public void printInformation() {
        System.out.println("overall memory size:" + diskSize + " bytes");
        System.out.println("overall block size:" + blockSize + " bytes");
        System.out.println("overall record size:" + recordSize + " bytes");
        System.out.println("overall available size:" + availableSize + " bytes");
        System.out.println("overall used size:" + usedSize + " bytes");
        System.out.println("total number of blocks:" + totalNoOfBlocks + "  bytes");
        System.out.println("total number of blocks avail:" + totalNoOfBlocksAvail + " bytes");
        System.out.println("total number of blocks used:" + totalNoOfBlocksUsed + " bytes");
        System.out.println("block size:" + totalBlockSize + " bytes");
        System.out.println("total number of records:" + totalNoOfRecords + " bytes");
        System.out.println("record size:" + totalRecordSize + " bytes");
    }

    public void printRecords() {
        for (int x = 0; x < memoryBlock.size(); x++) {
            for (int y = 0; x < memoryBlock.get(x).getRecords().size(); y++) {
                System.out.println("record in block" + x + "(T Const): " +
                        memoryBlock.get(x).getRecords().get(y).getTConst());
                System.out.println("record in block" + x + "(Average Rating):" +
                        memoryBlock.get(x).getRecords().get(y).getAverageRating());
                System.out.println("record in block" + x + "(Number of Votes):" +
                        memoryBlock.get(x).getRecords().get(y).getNumVotes());
            }
        }
    }

}