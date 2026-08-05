package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	
	void insert(Department obj);
	void Update(Department obj);
	void deleteById(Integer id);
	Department findByid(Integer id);
	List<Department> findAll();

}
