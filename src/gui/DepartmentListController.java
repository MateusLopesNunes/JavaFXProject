package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener /* esta classe trata os eventos lançados para ele */ {

	private DepartmentService despartmentservice;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event /* referencia ao controle que executou o evento */) {
		Utils utils = new Utils();
		Stage parentStage = utils.currentStage(event); // pega a referencia para o stage atual
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		// padrão do javaFx para vincular as colunas da tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// codigo para faz a tela ficar responsiva
		Stage stage = (Stage) Main.getMainScene().getWindow(); // o Stage recebe a minha cena modo janela
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // macete para deixar o tableView
																				// responsivo
	}

	public void updateTableView() {
		if (despartmentservice == null) { // entra no if se a anta do programador não instanciar o serviço
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = despartmentservice.findAll(); // essa lista recebe a lista do departmentService atrsvez
																// do polimorfismo
		obsList = FXCollections.observableArrayList(list); // obsList recebe minha lista
		tableViewDepartment.setItems(obsList); // tableView recebe o obsList atravez deste metodo
		initEditButtons();
		initRemoveButtons();
	}

	public void setDepartmentservice(DepartmentService despartmentservice) {
		this.despartmentservice = despartmentservice;
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // abre a tela desejada
			Pane pane = loader.load(); // carrega a tela

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.updateFormData();
			controller.SubscribeDataChangeListener(this); // adciona esta classe como parametro do metodo

			// como é uma caixa de dialogo, eu vou ter que estanciar outro stage, ou seja
			// uma tela na frente da outra
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data"); // seta o titulo
			dialogStage.setScene(new Scene(pane)); // seta a cena
			dialogStage.setResizable(false); // deixa a janela não regulavel
			dialogStage.initOwner(parentStage); // informa qual é o stage pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // deixa a tela modal
			dialogStage.showAndWait(); // executa a janela com as configurações
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView(); // chama o metodo atualizando a tabela
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) {
		Optional<ButtonType> confirmation = Alerts.showConfirmation("Confirmation", "Are you sure to delete ?");
		
		if (despartmentservice == null) {
			throw new IllegalStateException("despartmentservice was null");
		}
		
		if (confirmation.get() == ButtonType.OK) {
			try {
				despartmentservice.delete(obj);
				updateTableView();
			}
			catch(DbException e) {
				Alerts.showAlert("Error", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}