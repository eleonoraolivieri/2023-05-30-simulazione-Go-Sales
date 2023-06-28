package it.polito.tdp.gosales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;


public class GOsalesDAO {
	
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @param idMap 
	 * @return
	 */

	public void getAllRetailers(Map<Integer, Retailers> idMap){
		String query = "SELECT * from go_retailers";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("Retailer_code"))) {
				Retailers r = new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country"));
				
				idMap.put(rs.getInt("Retailer_code"), r);
				
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<String> getAllNazioni() {
		String sql = "SELECT DISTINCT Country FROM go_retailers ORDER BY Country ASC" ;
		List<String> result = new ArrayList<>() ;
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getString("Country")) ;
			}
			conn.close();
			return result ;
		} catch (SQLException e) {
			throw new RuntimeException("Errore nel DB", e) ;
		}
	}


	public List<Retailers> getVertici(String nazione, Map<Integer, Retailers> idMap) {
		String sql = "SELECT Retailer_code as id "
				+ "FROM go_retailers "
				+ "WHERE Country = ? "
				+ "ORDER BY Retailer_name ASC ";
		List<Retailers> result = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, nazione);
		
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(idMap.get(rs.getInt("id")));
			}
			
			rs.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	

	public List<Arco> getArchi(int anno,String nazione, int m,Map<Integer,Retailers> idMap){
		String sql = "WITH selezionati AS( "
				+ "SELECT Retailer_code "
				+ "FROM go_retailers "
				+ "WHERE Country = ?) "
				+ "SELECT r1.Retailer_code as id1, r2.Retailer_code as id2, COUNT(DISTINCT g1.product_number) as PESO "
				+ "FROM selezionati r1, selezionati r2, "
				+ "go_daily_sales g1, go_daily_sales g2 "
				+ "WHERE r1.Retailer_code< r2.Retailer_code "
				+ "AND r1.Retailer_code = g1.Retailer_code "
				+ "AND r2.Retailer_code = g2.Retailer_code "
				+ "AND g1.product_number = g2.product_number "
				+ "AND YEAR(g1.Date) = ? "
				+ "AND YEAR(g1.Date) = YEAR(g2.Date) "
				+ "GROUP BY r1.Retailer_code, r2.Retailer_code "
				+ "HAVING COUNT(DISTINCT g1.product_number) >=? "
				+ "ORDER BY peso ASC";
		
		
		
		
		List<Arco> result = new LinkedList<Arco>();
	
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, nazione);
			st.setInt(2, anno);
			st.setInt(3, m);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				Retailers sorgente = idMap.get(rs.getInt("id1"));
				Retailers destinazione = idMap.get(rs.getInt("id2"));
				if(sorgente != null && destinazione != null) {
					result.add(new Arco(sorgente, 
							destinazione,rs.getInt("peso")));
				} else {
					System.out.println("Errore in getAdiacenze");
				}
			}
			rs.close();
			st.close();
			conn.close();
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		
	}
	
	
}
}
