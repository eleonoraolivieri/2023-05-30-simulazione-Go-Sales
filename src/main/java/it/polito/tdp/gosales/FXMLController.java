package it.polito.tdp.gosales;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Retailers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnalizzaComponente;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbNazione;

    @FXML
    private ComboBox<?> cmbProdotto;

    @FXML
    private ComboBox<Retailers> cmbRivenditore;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextField txtN;

    @FXML
    private TextField txtNProdotti;

    @FXML
    private TextField txtQ;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextArea txtVertici;

    @FXML
    void doAnalizzaComponente(ActionEvent event) {
    	
    	Retailers r = cmbRivenditore.getValue() ;
    	if(r==null) {
    		txtResult.appendText("Seleziona un Rivenditore");
    		return ;
    	}
    	
    	
    	
    	txtResult.appendText("Dimensione componente: "+ this.model.calcolaComponentiConnesse(r).size()+"\n");
    	txtResult.appendText("Peso totale: "+this.model.calcolaPeso(r)+"\n");

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	txtVertici.clear();
    	txtArchi.clear();
    	
    	int anno = 0;
    	int m = 0;
    	String nazione = cmbNazione.getValue();
    	if(nazione==null) {
    		txtResult.appendText("Seleziona una nazione\n");
    		return ;
    	}
    	try {
    		
    		m = Integer.parseInt( this.txtNProdotti.getText() );
    	}catch(NumberFormatException e) {
    		txtResult.setText("L'anno e m devono essere dei numeri");
    		return;
    	}

    	try {
    		anno = cmbAnno.getValue();
    		
    	}catch(NumberFormatException e) {
    		txtResult.setText("L'anno e m devono essere dei numeri");
    		return;
    	}

    	
    	
    	if (m<=0) {
    		txtResult.setText("Il salario deve essere un numero positivo.");
    		return;
    	}
    	
    	
    	this.model.creaGrafo(anno, nazione, m);
    	List<Retailers> vertici = this.model.getVertici();
    	List<Arco> archi = this.model.getArchi(anno, nazione, m);
    	
    	this.txtResult.setText("Grafo creato.\n");
    	this.txtResult.appendText("Ci sono " + this.model.nVertici() + " vertici\n");
    	this.txtResult.appendText("Ci sono " + this.model.nArchi() + " archi\n\n");
    	
    	for(Retailers r: vertici){
    		this.txtVertici.appendText("\n"+r);
    	}
    	
    	for(Arco a: archi){
    		this.txtArchi.appendText("\n"+a);
    	}
    	
    	cmbRivenditore.setDisable(false);
    	btnAnalizzaComponente.setDisable(false);
    	cmbRivenditore.getItems().addAll(vertici);

    }

    @FXML
    void doSimulazione(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btnAnalizzaComponente != null : "fx:id=\"btnAnalizzaComponente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbNazione != null : "fx:id=\"cmbNazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbRivenditore != null : "fx:id=\"cmbRivenditore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtNProdotti != null : "fx:id=\"txtNProdotti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtQ != null : "fx:id=\"txtQ\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtVertici != null : "fx:id=\"txtVertici\" was not injected: check your FXML file 'Scene.fxml'.";

        for (int anno = 2015; anno<=2018; anno++) {
        	cmbAnno.getItems().add(anno);
        }
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	List<String> nazioni = model.getNazioni();
    	cmbNazione.getItems().addAll(nazioni) ;
    }

}
