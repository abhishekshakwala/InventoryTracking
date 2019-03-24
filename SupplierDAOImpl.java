package org.supplyhouse.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.supplyhouse.model.Supplier;

public class SupplierDAOImpl implements SupplierDAO {
	
	private JdbcTemplate jdbcTemplate;

	public SupplierDAOImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void insertRecord(Supplier supplier) {
		// TODO Auto-generated method stub
		String sql = "SELECT COUNT(*) AS total FROM SupplyHouse.SUPPLIER_PRODUCT WHERE SUPPLIER_ID=? AND PRODUCT_ID=?";
		
		int count = jdbcTemplate.queryForObject(sql, new Object[] { supplier.getSupplierID(), supplier.getProductID() }, Integer.class);
		
		if(count > 0) {
			String deleteRecord = "DELETE FROM SupplyHouse.SUPPLIER_PRODUCT WHERE SUPPLIER_ID=? AND PRODUCT_ID=?";
			
			jdbcTemplate.update(deleteRecord, supplier.getSupplierID(), supplier.getProductID());
		}
		
		String insertQuery = "INSERT INTO SupplyHouse.SUPPLIER_PRODUCT (SUPPLIER_ID, PRODUCT_ID, QUANTITY) VALUES (?, ?, ?)";
		
		jdbcTemplate.update(insertQuery,
				supplier.getSupplierID(),
				supplier.getProductID(),
				supplier.getQuantity());
	}
}
