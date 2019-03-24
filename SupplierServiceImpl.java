package org.supplyhouse.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.supplyhouse.dao.SupplierDAO;
import org.supplyhouse.model.Supplier;

public class SupplierServiceImpl implements SupplierService {

	@Autowired
	private SupplierDAO supplierDAO;
	
	/**
	 * readingFiles reads each file name from the list and extracts the extension and checks for condition
	 * if it is an excel file, a csv file or a text file and calls readExcel method for excel file and 
	 * readData method for csv and text format file.
	 * 
	 * @param folderLocation	folder location path where the supplier files are stored 
	 * @param fileNameList	list of file name present in the folder
	 * 
	 */
	@Override
	public void readingFiles(String folderLocation, List<String> fileNameList) throws IOException {
		// TODO Auto-generated method stub
		for (String file : fileNameList) {
			
			String extension = FilenameUtils.getExtension(file);
			
			if (extension.toLowerCase().equals("xlsx")) {
				readExcel(folderLocation, file);
			} else if (extension.toLowerCase().equals("csv")) {
				readData(folderLocation, file, ",");
			} else if (extension.toLowerCase().equals("txt")){
				readData(folderLocation, file, "\t");
			}
		}
	}
	

	/**
	 * readExcel reads the excel file from given folder location and file name creates a Workbook instance
	 * and here we assume that the data is present in the first sheet and finally it iterates over each 
	 * row in the sheet. It calls a helper function getHeaderMapping passing the list of columns and gets the 
	 * mapping of productID and quantity column index in the excel sheet. It reads the data and create a Supplier
	 * type object for the read data and passes the object to addInventoryFeed method. 
	 * 
	 * @param folderLocation	folder location path where the supplier file is stored 
	 * @param fileName	It is the excel format file name.
	 * 
	 */
	public void readExcel(String folderLocation, String fileName) throws IOException {
		System.out.println(fileName);
		try
        {
            FileInputStream file = new FileInputStream(new File(folderLocation + fileName));
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            Row col = rowIterator.next();
            
            Iterator<Cell> colCellIterator = col.cellIterator();
            
            List<String> colList = new ArrayList<>();
            
            while(colCellIterator.hasNext()) {
            	Cell colCell = colCellIterator.next();
            	colList.add(colCell.getStringCellValue());
            }
            
            HashMap<String, Integer> columnMapper = getHeaderMapping(colList);
            
            String supplierID = FilenameUtils.removeExtension(fileName);
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                
                String productID = "";
                
                int quantity = 0;
                
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    if(cell.getColumnIndex() == columnMapper.get("productID")) {
                    	productID = cell.getStringCellValue();
                    } else if(cell.getColumnIndex() == columnMapper.get("quantity")) {
                    	quantity = (int) cell.getNumericCellValue();
                    }
                }
                
                Supplier s = new Supplier(supplierID, productID, quantity);
    			
    			addInventoryFeed(s);
            }
            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	
	/**
	 * readData method reads csv and text file format type of data along with folder location and 
	 * file name it takes the delimiter which specifies how the data can be parsed. 
	 * 
	 * @param folderLocation	folder location path where the supplier file is stored 
	 * @param fileName	It is the excel format file name.
	 * @param delimiter	It specifies how the data needs to be read with given delimiter.	
	 * 
	 */
	public void readData(String folderLocation, String fileName, String delimiter) throws IOException {
		System.out.println(fileName);
		/**
		 * Creating a buffered reader to read the file
		 */
		BufferedReader bReader = new BufferedReader(new FileReader(folderLocation + fileName));

		String[] headerLine;
		
		headerLine = bReader.readLine().split(delimiter);
		
		HashMap<String, Integer> columnMapper = getHeaderMapping(Arrays.asList(headerLine));
		
		System.out.println(headerLine);
		
		String line;

		/**
		 * Looping the read block until all lines in the file are read.
		 */
		while ((line = bReader.readLine()) != null) {

			/**
			 * Splitting the content of tabbed separated line
			 */
			String values[] = line.split(delimiter);
			String productID = values[columnMapper.get("productID")];
			int quantity = Integer.parseInt(values[columnMapper.get("quantity")]);
			String supplierID = FilenameUtils.removeExtension(fileName);
			
			Supplier s = new Supplier(supplierID, productID, quantity);
			
			addInventoryFeed(s);
		}
		bReader.close();
	}

	
	/**
	 * getHeaderMapping method is a helper function which maps the productID and quantity columns in the
	 * given file. Here the assumption is that productID column is named 'product/ PRODUCT/ productid/ PRODUCTID'
	 * in all the file formats or the column metadata has to be given by the user to gain the knowledge about the 
	 * columns in the dataset. Similarly, for quantity the assumption is that it is named 'quantity/ QUANTITY/ inventory/ INVENTORY'
	 * in all the file formats or the column metadata has to be given by the user to gain the knowledge about the 
	 * columns in the dataset.
	 * 
	 * @param supplier	It is a Supplier object which contains the data to be stored in the database.
	 * @return mapper	returns a mapping object of column name and index of that column in the dataset.
	 * 
	 */
	public HashMap<String, Integer> getHeaderMapping(List<String> headerList) {
		HashMap<String, Integer> mapper = new HashMap<String, Integer>();
		for(String colName : headerList) {
			if(colName.toLowerCase().equals("product") || colName.toLowerCase().equals("productid")) {
				mapper.put("productID", headerList.indexOf(colName));
			} else if(colName.toLowerCase().equals("quantity") || colName.toLowerCase().equals("inventory")) {
				mapper.put("quantity", headerList.indexOf(colName));
			}
		}
		return mapper;
	}
	
	
	/**
	 * addInventoryFeed method reads supplier object and calls the persistence layer SupplierDAO
	 * to insert the record into database
	 * 
	 * @param supplier	It is a Supplier object which contains the data to be stored in the database.
	 * 
	 */
	public void addInventoryFeed(Supplier supplier) {
		supplierDAO.insertRecord(supplier);
	}
}
