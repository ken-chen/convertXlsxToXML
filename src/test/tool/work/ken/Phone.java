package tool.work.ken;

import java.util.List;

public class Phone {

	private String model;
	private String software_version;
	
	private List<Phone_Capability> phoneCapabilities;
	public List<Phone_Capability> getPhoneCapability() {
		return phoneCapabilities;
	}

	public void setPhoneCapability(List<Phone_Capability> phoneCapability) {
		this.phoneCapabilities = phoneCapability;
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		
		this.model = model;
	}
	public String getSoftwareVersion() {
		return software_version;
	}
	
	public void setSoftwareVersion(String software_version) {
		this.software_version = software_version;
	}
}
