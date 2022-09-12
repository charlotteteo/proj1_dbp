import java.util.ArrayList;
import java.util.List;

public class Key {
	private float key;
	List<Record> values;

	public Key(float key, Record value) {
		this.key = key;
		if (this.values == null) {
			this.values = new ArrayList<>();
		}
		this.values.add(value);
	}
	
	public Key(float key) {
		this.key = key;
		this.values = new ArrayList<>();
	}

	public float getKey() {
		return key;
	}

	public void setKey(float key) {
		this.key = key;
	}

	public List<Record> getValues() {
		return values;
	}

	public void setValues(List<Record> values) {
		this.values = values;
	}

    // print key information
	public String toString() {
		return "<KEY>[Key = " + key +  "] ";
	}
}
