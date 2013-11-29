package tool.work.ken;

import java.util.ArrayList;
import java.util.List;

public class Manufacturer {

	private String name;
	private List<Phone> phones = new ArrayList<Phone>();
	
	public List<Phone> getPhone() {
		
		return phones;
	}

	public void setPhone(List<Phone> phone) {
		this.phones = phone;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
