package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	private DepartmentService service;
	
	private Department entity;
	
	private List<DataChangeListener> dataChangeListener = new ArrayList<>(); //lista da interface
	/*esta classe é responsavel para lancar os eventos para outra classe que implemente ess interface*/
	
	@FXML
	private TextField textId;
	
	@FXML
	private TextField textName;
	
	@FXML
	private Label labelError;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	private void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
	 		entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener(); //quando este botão for clicado, a classe que implemeta a intteface irá executor o metodo implementado, que atualiza a tabela
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
 	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(textId.getText()));
		
		if (textName.getText() == null || textName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empity");
		}
		
 		obj.setName(textName.getText());
 		
 		if (exception.getErrors().size() > 0) {
 			throw exception;
 		}
		return obj;
	}

	@FXML
	private void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 30);
	}
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void SubscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener); //adciona uma classe que implemmeta a interface
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
	}	
	
	public void notifyDataChangeListener() { 
		dataChangeListener.forEach((DataChangeListener x) -> x.onDataChanged());
		//A implemetação da interface deve executar o metodo implementado;
	}
	
	public void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelError.setText(errors.get("name"));
		}
	}
}
