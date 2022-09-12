import java.util.List;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Database {

    public int recordSize;
    public static final int memorySize = 500000000;
    public static final int poolSize = memorySize / blockSize;
    // remainder size
    public int availableSize;
    // used size
    public int usedSize;
    // size of each block
    public int blockSize = 200;
    public int block;
    public int recordsPerBlock = 2;
    public int totalNoOfBlocksAvail;
    public int totalNoOfBlocksUsed;
    public int currentUsedBlocks;
    public int totalNoOfRecords;
    public int totalBlockSize;
    public int totalRecordSize;
    public int recordCounter = 0;
    public int databaseSize = 0;
    private List<Block> memoryBlock;
    private Block blk;

    public Database(int poolSize, int blockSize) {
        this.poolSize = poolSize;
        this.freeSize = poolSize;
        this.block = poolSize;
        this.blockSize = blockSize;
        this.remaining = poolSize / blockSize;
        this.sizeUsed = 0;
        this.allocated = 0;
        listBlk = new ArrayList<>();
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

    // public void allocateRecord(Record rec) {
    // // String s = "tt0017626";
    // // float averageRating = 5.6f;
    // // int numVotes = 1024;
    // byte[] b = null;

    // try {

    // b = rec.getTConst().getBytes("UTF-8");
    // FloatBuffer fb = FloatBuffer.allocate(1);
    // fb.put(rec.getAverageRating());
    // IntBuffer ib = IntBuffer.allocate(1);
    // ib.put(rec.getNumVotes());
    // } catch (UnsupportedEncodingException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // recordSize = (Float.SIZE / 8) + (Integer.SIZE / 8) + 10;
    // this.recordsPerBlock = (int) Math.floor((double) blockSize / (double)
    // recordSize);

    // }

    public void deallocateBlock() {
        this.totalNoOfBlocksAvail++;
        this.totalNoOfBlocksUsed--;
        this.usedSize -= recordSize;
        this.totalRecordSize -= recordSize;
        this.recordCounter -= 2;
    }

    public void setRecord(int totalNoOfRecords) {
        this.totalNoOfRecords = totalNoOfRecords;
        this.block = (int) Math.ceil((double) this.totalNoOfRecords / (double) this.totalNoOfBlocks);
        this.recordCounter++;
    }

    public void printInformation(){
        System.out.println("overall memory size:" + memorySize + ' bytes');
        System.out.println('overall block size:' + memorySize + ' bytes');
        System.out.println('overall record size:' + memorySize + ' bytes');
        System.out.println('overall available size:' + memorySize + ' bytes');
        System.out.println('overall used size:' + memorySize + ' bytes');
        System.out.println('total number of blocks:' + memorySize + ' bytes');
        System.out.println('block size:' + memorySize + ' bytes');
        System.out.println('total number of records:' + memorySize + ' bytes');
        System.out.println('record size:' + memorySize + ' bytes');
    }

    public void printRecords(){
        for (int x=0; x< memoryBlock.size(); x++){
            for (int y=0; x< memoryBlock.get(x).getRecords().size(); y++){
                System.out.println("record in block" + x + '(T Const): ' + memoryBlock.get(x).getRecords().get(y).getTConst());
                System.out.println("record in block" + x + '(Average Rating):' + memoryBlock.get(x).getRecords().get(y).getAverageRating());
                System.out.println("record in block" + x + '(Number of Votes):' + memoryBlock.get(x).getRecords().get(y).getNumVotes());
            }
        }
    }

}