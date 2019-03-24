package org.supplyhouse.service;

import java.io.IOException;
import java.util.List;

import org.supplyhouse.model.Supplier;

public interface SupplierService {
	
	public void readingFiles(String folderLocation, List<String> fileNameList) throws IOException;
	
	public void addInventoryFeed(Supplier supplier);
}
