package application;

import java.time.LocalDate;
import java.util.List;

import com.mysql.cj.protocol.a.LocalDateTimeValueEncoder;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {

		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("===TEST 1: seller findById ===");
		
		Seller seller = sellerDao.findByid(3);
		
		System.out.println(seller);
		
		System.out.println("\n===TEST 2: seller findByDepartment ===");
		Department department = new Department(2,null);
		List<Seller>list = sellerDao.findByDepartment(department);
		list.forEach(System.out::println);
		
		System.out.println("\n===TEST 3: seller findAll ===");
		list = sellerDao.findAll();
		list.forEach(System.out::println);
		
		System.out.println("\n===TEST 4: seller Insert ===");
		Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", LocalDate.now(),4000.0,department);
		sellerDao.insert(newSeller);
		System.out.println(newSeller.getId());
		
		System.out.println("\n===TEST 5: seller Update ===");
		
		seller = sellerDao.findByid(1);
		seller.setName("Martha Waine");
		sellerDao.Update(seller);
		System.out.println("Update completed");

	}

}
