package org.supplyhouse.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.supplyhouse.service.SupplierService;

@Controller
public class SupplierController {
	
	@Autowired
	private SupplierService supplierService;
	
	private static final Logger LOG = Logger.getLogger(SupplierController.class.getName());
	
	// Folder location where the supplier inventory feeds files are stored
	final String folderLocation = "/Users/abhishekshakwala/Desktop/SupplyHouse/";

	/**
	 * supplierJobScheduler is a scheduler method which will get executed at midnight on
	 * daily basis to read the supplier inventory feeds and update the database record.
	 * This will be completely automated job that will be scheduled daily as specified in cron expression. 
	 * 
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void supplierJobScheduler() throws IOException {
		
		readFolder();
		
		LOG.info("Logger Name: " + LOG.getName() + " Executed Successfully.");
	}
	
	@RequestMapping("/")
	public String applicationStarter(ModelMap m) {
		m.addAttribute("data", "Welcome to SupplyHouse");
		return "jsonTemplate";
	}
	
	
	/**
	 * readFolder creates a File object with specified folder location of supplier files
	 * and calls readFiles method passing the File object.
	 * 
	 */
	public void readFolder() throws IOException {
		File folder = new File(folderLocation);
		readFiles(folder);
	}
	
	
	/**
	 * readFiles method takes the File object and will extract list of files present in the folder
	 * and maintains a list which will add the filename present in the folder object. Once the complete
	 * list of files is obtained then it calls the supplier service readingFiles method and passes 
	 * folder location and list of file name.
	 * 
	 * @param folder It is the File object which specifies the folder location to read files.
	 * 
	 */
	public void readFiles(File folder) throws IOException {
		List<String> fileNameList = new ArrayList<String>();
		File[] fileNames = folder.listFiles();
		for(File file : fileNames) {
			if(file.isDirectory()) {
				readFiles(file);
			} else {
				fileNameList.add(file.getName());
			}
		}
		supplierService.readingFiles(folderLocation, fileNameList);
	}
}
