package tool.work.ken;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author kchen
 * 
 * This class revert the xlsx to the xml file
 *
 */
public class XlsxConvertToxml {
	private String spreadSheetName;
	private String outputXMLName;
	
	@Before
	public void setUp() throws Exception {
		//please update xls if there is new here
		spreadSheetName = "/bluetooth_dataentry.xlsx";
		outputXMLName= "bluetooth_data.xml";
	}
	
	@Test 
	public void convertXLSToXML(){
			
			InputStream excel = XlsxConvertToxml.class.getResourceAsStream(spreadSheetName);
			XSSFWorkbook workbook = null;
			try {
				//Load an HSSF document from the input stream
				workbook = new XSSFWorkbook(excel);
			} catch (IOException ioEx) {
				System.out.println("can not Load an HSSF document from the input stream" + ioEx);
			}
			
			//Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			workbook.setMissingCellPolicy(HSSFRow.CREATE_NULL_AS_BLANK);
			
			//Get iterator to all the rows in current sheet
			Set<Manufacturer>  manufacturers = getManufacrurers(sheet);
			Set<Manufacturer>  manufacturerXMLS =  new LinkedHashSet<Manufacturer>();
			
			System.out.println("manufacturers size is " + manufacturers.size());
			
			for(Manufacturer manufacturer: manufacturers){
				Manufacturer manu = getSingleManufacrurerWithDeatils(sheet,manufacturer.getName());
				manufacturerXMLS.add(manu);
			}
			 
			//write to the XML File
			printTOtheXMLFile(manufacturerXMLS,outputXMLName);
			 
		}
	
	

	private void printTOtheXMLFile(Set<Manufacturer> manufacturerXMLS,String filename) {
		FileOutputStream fos = null;
		  try {
			    fos = new FileOutputStream( "./src/test/resources/"+filename);
			    
			    fos.write("<?xml version=\"1.0\"?>".getBytes("UTF-8"));
			    StringBuilder sb = new StringBuilder();
			    sb.append("<bluetooth ");
			    sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			    sb.append("xmlns=\"http://holden.com.au/schemas/bluetooth\" ");
			    sb.append("xsi:schemaLocation=\"http://holden.com.au/schemas/bluetooth bluetooth.xsd\" ");
			    sb.append(">");
			    
			    //headings
			    sb.append("<headings>");
			    sb.append("<mapping heading=\"Simplified\" sortOrder=\"1\" description=\"Call Handling\" />");
			    sb.append("<mapping heading=\"Simplified\" sortOrder=\"2\" description=\"Enhanced voice control\" />");
			    sb.append("<mapping heading=\"Simplified\" sortOrder=\"3\" description=\"Phonebook Access\" />");
			    sb.append("<mapping heading=\"Simplified\" sortOrder=\"4\" description=\"Audio Streaming\" />");
			    sb.append("<mapping heading=\"Simplified\" sortOrder=\"5\" description=\"SMS Text Messaging\" />");
			    sb.append("</headings>");
			   
			    fos.write(sb.toString().getBytes("UTF-8"));  
			    //write the body now
			    fos.write(convertToXml(manufacturerXMLS).getBytes("UTF-8"));

			    
		} catch (Exception e) {
			 System.err.println("Error in XML Write: " + e.getMessage());
		} finally{
		    if(fos != null){
		        try{
		            fos.close();
		        }catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}
	
	/**
	 * @param sheet
	 * @return
	 */
	private Set<Manufacturer> getManufacrurers(XSSFSheet sheet) {
				
		Set<Manufacturer>  manufacturers = new LinkedHashSet<Manufacturer>();
				
		String currentManName = "";
		
		int totalRows = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < totalRows; i++) {

			XSSFRow row = null; 
			row=sheet.getRow(i);
			
			String manufacturerName = getCellValueByIndex(row, 0);

			if(!currentManName.equalsIgnoreCase(manufacturerName)){
				
				Manufacturer manu = new Manufacturer();
				manu.setName(manufacturerName);
				manufacturers.add(manu);
				
				currentManName = manufacturerName;
			}
			
		}


		return manufacturers;
	}
	

	/**
	 * @param sheet
	 * @param name
	 * @return
	 */
	private Manufacturer getSingleManufacrurerWithDeatils(XSSFSheet sheet,String name) {
		
		Manufacturer manu = new Manufacturer();
		
		List<Phone> phones = new ArrayList<Phone>();
		
		int totalRows = sheet.getPhysicalNumberOfRows();

		for (int i = 1; i < totalRows; i++) {
			
			List<Phone_Capability> pcs =  new ArrayList<Phone_Capability>();
			
			XSSFRow row = null; 
			row=sheet.getRow(i);
			
			String manufacturerName = getCellValueByIndex(row, 0);
			
			if(name.equalsIgnoreCase(manufacturerName)){
				String model = getCellValueByIndex(row, 1);
				String softwareVersion = getCellValueByIndex(row, 2);
				String callHandling = getCellValueByIndex(row, 3);
				String enhancedVoiceControl = getCellValueByIndex(row, 4);
				String phonebookAccess = getCellValueByIndex(row, 5);
				String audioStreaming = getCellValueByIndex(row, 6);
				String smsTextMessaging = getCellValueByIndex(row, 7);
				
				Phone phone = getPhone(pcs, model, softwareVersion, callHandling,enhancedVoiceControl, phonebookAccess, audioStreaming, smsTextMessaging);
				phones.add(phone);				
				
				manu.setName(manufacturerName);			
			}
//				System.out.println("manufacturer is " + manufacturerName);
//				System.out.println("currentPhone is " + model);
//				System.out.println("softwareVersion is " + softwareVersion);
//				System.out.println("callHandling is " + callHandling);
//				System.out.println("enhancedVoiceControl is " + enhancedVoiceControl);
//				System.out.println("phonebookAccess is " + phonebookAccess);
//				System.out.println("audioStreaming is " + audioStreaming);
//				System.out.println("smsTextMessaging is " + smsTextMessaging);
			
		}

		
		manu.setPhone(phones);
		return manu;
	}

	/**
	 * @param pcs
	 * @param model
	 * @param softwareVersion
	 * @param callHandling
	 * @param enhancedVoiceControl
	 * @param phonebookAccess
	 * @param audioStreaming
	 * @param smsTextMessaging
	 * @return
	 */
	private Phone getPhone(List<Phone_Capability> pcs, String model,
			String softwareVersion, String callHandling,
			String enhancedVoiceControl, String phonebookAccess,
			String audioStreaming, String smsTextMessaging) {
		
		Phone ph = new Phone();
		
		pcs.add(getPhoneCapability("Call Handling",callHandling));
		pcs.add(getPhoneCapability("Enhanced voice control",enhancedVoiceControl));
		pcs.add(getPhoneCapability("Phonebook Access",phonebookAccess));	
		pcs.add(getPhoneCapability("Audio Streaming",audioStreaming));	
		pcs.add(getPhoneCapability("SMS Text Messaging",smsTextMessaging));	
		
		ph.setPhoneCapability(pcs);
		ph.setModel(model);
		ph.setSoftwareVersion(softwareVersion);
		
		return ph;
		
	}

	/**
	 * @param code
	 * @param capable
	 * @return
	 */
	private Phone_Capability getPhoneCapability(String code, String capable) {
		Phone_Capability pc = new Phone_Capability();
		if (StringUtils.isNotEmpty(capable)){
			pc.setCode(code);
			pc.setCapable(capable);
			 
		}
		
		return pc;
	}
	/**
	 * 
	 */
	private final static XStream xstreamForXml = new XStream(new DomDriver());
	static {
		xstreamForXml.setMode(XStream.NO_REFERENCES);
		
		xstreamForXml.alias("manufacturer", Manufacturer.class);
		xstreamForXml.useAttributeFor(Manufacturer.class, "name");
		
		xstreamForXml.alias("phone-capability", Phone_Capability.class);
		xstreamForXml.useAttributeFor(Phone_Capability.class, "code");
		xstreamForXml.useAttributeFor(Phone_Capability.class, "capable");
		
		xstreamForXml.alias("phone", Phone.class);
		xstreamForXml.useAttributeFor(Phone.class, "model");
		xstreamForXml.useAttributeFor(Phone.class, "software_version");
		xstreamForXml.aliasField("software-version", Phone.class, "software_version");
	}

	/**
	 * @param object
	 * @return
	 */
	private static String convertToXml(Object object) {
		
		String result = xstreamForXml.toXML(object);
		result = result.replaceAll("<phones>", "");
		result = result.replaceAll("</phones>", "");
		result = result.replaceAll("<phoneCapabilities>", "");
		result = result.replaceAll("</phoneCapabilities>", "");
		result = result.replaceAll("<manufacturers>", "");
		result = result.replaceAll("</manufacturers>", "");
		result = result.replaceAll("<linked-hash-set>", "");
		result = result.replaceAll("</linked-hash-set>", "");
		return result;
	}

	
	/**
	 * @param row
	 * @param index
	 * @return
	 */
	private static String getCellValueByIndex(XSSFRow row, int index) {
		Cell cell = row.getCell(index);
		if (cell != null) {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				return cell.getStringCellValue();
			
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				return "" + cell.getNumericCellValue();
			}
		}
		return null;
	}


}
