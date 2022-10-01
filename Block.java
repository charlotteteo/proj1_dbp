import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Block {
	// list of records should be within block size
	private List<Record> records;

	public Block() {
		this.records = new ArrayList<>();
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		Iterator<Record> iterator = records.iterator();
		while (iterator.hasNext()) {
			this.records.add(iterator.next());
		}
	}

	public int getNumberOfRecords() {
		return records.size();
	}

	public void printRecords() {
		System.out.println("Records in Block:" + records);
	}
}
