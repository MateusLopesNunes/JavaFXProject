package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	private void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> { //recebe um controler e suas funções ao carregar uma nova tela
			controller.setSellerservice(new SellerService());
			controller.updateTableView();
			//tudo isso é feito para que ocorra uma injeção de dependencia no meu service
		});	}
	
	@FXML
	private void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> { //recebe um controler e suas funções ao carregar uma nova tela
			controller.setDepartmentservice(new DepartmentService());
			controller.updateTableView();
			//tudo isso é feito para que ocorra uma injeção de dependencia no meu service
		});
	}
	
	@FXML
	private void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {}); //carrega a tela da minha preferencia passando seu caminho
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}
	
	private synchronized <T> void  loadView(String absoluteName, Consumer<T> initializingAction) { //metodo para carregar as minhas telas
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //codigo para carreegar a tela
			VBox newVBox = loader.load(); //adciona a tela no VBox
			
			Scene mainScene = Main.getMainScene(); //é a referencia da minha tela principal
			
			//agr o conteudo do meu VBox da tela about tem que ser adcionado no meu VBOx da tela Principal view
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //adciona no mainVBOX o conteudo do root da tela (ScroolPane), e do conteúdo adciona os filhos do VBox da tela, tela about 
			
			//agr do VBox deve ser apagado, com a exceção do menuBar que deve ser mantido, esses conteudo apagados devem ser substituidos pelo VBox do About ou de qualquer outra tela, about é so um exemplo
			Node mainMenu = mainVBox.getChildren().get(0); //adciona no objeto menu o menu da tela principal;
			mainVBox.getChildren().clear(); //limpa os filhos do VBox da tela principal
			mainVBox.getChildren().add(mainMenu); //adciona o menu do Vbox da tela principal
			mainVBox.getChildren().addAll(newVBox.getChildren()); //adciona os filhos da tela About(ou qualquer janela aberta)
			
			T controller = loader.getController(); //adciona uma referencia a um controler passado como parametro da expressão lambida
			initializingAction.accept(controller); //valida meu consumer
			
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}
	
	/*private synchronized void  loadView2(String absoluteName) { //metodo para carregar a minha tela about
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //codigo para carreegar a tela
			VBox newVBox = loader.load(); //adciona a tela no VBox
			
			Scene mainScene = Main.getMainScene(); //é a referencia da minha tela principal
			
			//agr o conteudo do meu VBox da tela about tem que ser adcionado no meu VBOx da tela Principal view
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //adciona no mainVBOX o conteudo do root da tela (ScroolPane), e do conteúdo adciona os filhos do VBox da tela, tela about 
			
			//agr do VBox deve ser apagado, com a exceção do menuBar que deve ser mantido, esses conteudo apagados devem ser substituidos pelo VBox do About ou de qualquer outra tela, about é so um exemplo
			Node mainMenu = mainVBox.getChildren().get(0); //adciona no objeto menu o menu da tela principal;
			mainVBox.getChildren().clear(); //limpa os filhos do VBox da tela principal
			mainVBox.getChildren().add(mainMenu); //adciona o menu do Vbox da tela principal
			mainVBox.getChildren().addAll(newVBox.getChildren()); //adciona os filhos da tela About(ou qualquer janela aberta)
			
			DepartmentListController controller = loader.getController(); //adciona uma referencia ao controller
			controller.setDespartmentservice(new DepartmentService());
			controller.updateTableView();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}*/
	
}
