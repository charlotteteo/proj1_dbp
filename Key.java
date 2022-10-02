import java.util.ArrayList;
import java.util.List;

public class Key {
	private float key;
	List<Record> rec;

	public Key(float key, Record value) {
		this.key = key;
		if (this.rec == null) {
			this.rec = new ArrayList<>();
		}
		this.rec.add(value);
	}

	public Key(float key) {
		this.key = key;
		this.rec = new ArrayList<>();
	}

	public float getKey() {
		return key;
	}

	public void setKey(float key) {
		this.key = key;
	}

	public List<Record> getRec() {
		return rec;
	}

	public void setrec(List<Record> rec) {
		this.rec = rec;
	}

	// print key information
	public String toString() {
		return "" + key + "";
	}
}
