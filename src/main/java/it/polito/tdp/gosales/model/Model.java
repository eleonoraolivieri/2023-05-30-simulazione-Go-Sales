package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;




public class Model {
	
	private List<String> nazioni ;
	private SimpleWeightedGraph<Retailers, DefaultWeightedEdge> grafo;
	private  GOsalesDAO dao;
	private Map<Integer,Retailers> idMap;
	private List<Retailers> retailers;
	private List<Retailers> allVertices;
	private List<Arco> archi;

	public Model() {
		
		dao = new  GOsalesDAO();
		idMap = new HashMap<Integer,Retailers>();
		dao.getAllRetailers(idMap);
		
	}

	public List<String> getNazioni() {
		if(this.nazioni==null) {
			GOsalesDAO dao = new GOsalesDAO() ;
			this.nazioni = dao.getAllNazioni() ;
		}
		
		return this.nazioni ;
	}
	
	public void creaGrafo(int anno, String nazione, int m) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		retailers = this.dao.getVertici(nazione,this.idMap);
		//aggiungo i vertici
		
		Graphs.addAllVertices(this.grafo, retailers);
		
		//aggiungo gli archi
		for(Arco a : dao.getArchi(anno,nazione,m,idMap)) {
			if(this.grafo.containsVertex(a.getV1()) && 
					this.grafo.containsVertex(a.getV2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(a.getV1(), a.getV2());
				if(e == null) {
					Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
				
					}
			}

	}
		
		System.out.println("VERTICI: " +this.grafo.vertexSet().size());
		System.out.println("ARCHI: " +this.grafo.edgeSet().size());
		
		
			
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Retailers> getVertici () {
		List<Retailers> allVertices = new ArrayList<>(this.grafo.vertexSet());
		
		return allVertices;
		}
	
	public List<Arco> getArchi(int anno, String nazione, int m) {
		if(this.archi==null) {
			GOsalesDAO dao = new GOsalesDAO() ;
			this.archi = dao.getArchi(anno,nazione,m,idMap);
		}
		return this.archi;
	}
	
	public Set<Retailers> calcolaComponentiConnesse(Retailers r){
		
		ConnectivityInspector<Retailers, DefaultWeightedEdge> inspect = new ConnectivityInspector<Retailers, DefaultWeightedEdge>(this.grafo);
		return inspect.connectedSetOf(r);
	}
	
	public int calcolaPeso(Retailers r){
		ConnectivityInspector<Retailers, DefaultWeightedEdge> inspect = new ConnectivityInspector<Retailers, DefaultWeightedEdge>(this.grafo);
		
		Set<Retailers> connessioni = inspect.connectedSetOf(r);
		
		int totalWeight = 0;
		for (DefaultWeightedEdge edge : this.grafo.edgeSet()) {
		    Retailers source = this.grafo.getEdgeSource(edge);
		    Retailers target = this.grafo.getEdgeTarget(edge);
		    
		    if (connessioni.contains(source) && connessioni.contains(target)) {
		        totalWeight += this.grafo.getEdgeWeight(edge);
		    }
		}
		
		return totalWeight;
		
		
	}
	

}
