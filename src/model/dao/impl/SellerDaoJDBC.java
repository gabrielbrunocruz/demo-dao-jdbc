package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setObject(3, obj.getBirthDate());
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
			} else {
				throw new DbException("Error. Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public void Update(Seller obj) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?");

			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setObject(3, obj.getBirthDate());
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			ps.setInt(6, obj.getId());

			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {

			DB.closeStatement(ps);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("DELETE FROM seller " + "WHERE Id = ?");

			Seller seller = findByid(id);
			
			ps.setInt(1, id);
			
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected ==0) {
				throw new DbException("O id passado nao existe");

			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public Seller findByid(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "On seller.DepartmentId = department.Id " + "WHERE seller.id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instanciateSeller(rs, dep);
				return obj;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	private Seller instanciateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getObject("BirthDate", LocalDate.class));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller. *, department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name");

			rs = st.executeQuery();
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller obj = instanciateSeller(rs, dep);
				sellers.add(obj);
			}
			return sellers;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller. *, department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, department.getId());
			rs = st.executeQuery();
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));
				// se nao houver uma chave com esse valor dentro do map vai retornar null;
				// fazemos isso para nao retornar um departamento repetido, tendo um mesmo
				// departamento com os vendedores

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller obj = instanciateSeller(rs, dep);
				sellers.add(obj);
			}
			return sellers;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

}
