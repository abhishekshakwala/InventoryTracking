package org.supplyhouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.supplyhouse.dao.SupplierDAO;
import org.supplyhouse.dao.SupplierDAOImpl;
import org.supplyhouse.service.SupplierService;
import org.supplyhouse.service.SupplierServiceImpl;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = { "org.supplyhouse" })
public class SupplyHouseConfiguration {
	@Bean
	public View jsonTemplate() {
		MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        return view;
	}
	
	@Bean
	public ViewResolver viewResolver() {
		return new BeanNameViewResolver();
	}
	
	@Bean
	public DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/");
		dataSource.setUsername("root");
		dataSource.setPassword("Mysuccess@26");
		return dataSource;
	}
	
	@Bean
	public SupplierService getSupplierService() {
		return new SupplierServiceImpl();
	}
	
	@Bean SupplierDAO getSupplierDAO() {
		return new SupplierDAOImpl(getDataSource());
	}
}
