package main;

import domain.Cake;
import domain.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.OrderService;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

public class CakeShopGUIController {
    private OrderService serv;
    @FXML private ListView<Cake> cakeListView = new ListView<>();
    @FXML private ListView<Order> orderListView = new ListView<>();
    @FXML private ListView<Order> finishedListView = new ListView<>();
    @FXML private ListView<Order> unfinishedListView = new ListView<>();
    @FXML private Button ListCakesButton = new Button();
    @FXML private Button FinishedOrdersButton = new Button();
    @FXML private Button UninishedOrdersButton = new Button();
    @FXML private TextField OrderIDTextField = new TextField();
    @FXML private TextField CakeTypeTextField = new TextField();
    @FXML private TextField CakePriceTextField = new TextField();
    @FXML private TextField ClientNameTextField = new TextField();
    @FXML private CheckBox FinishedCheckBox = new CheckBox();

    @FXML private Button AddOrderButton = new Button();
    @FXML private Button CancelOrderButton = new Button();
    @FXML private Button ModifyStatusButton = new Button();
    @FXML private Button ModifyCakeButton = new Button();

    @FXML private Button CustomerOrdersButton = new Button();
    @FXML private Button SameCakeTypeButton = new Button();
    @FXML private Button SamePriceButton = new Button();

    @FXML
    public void initialize() {
        loadCakes(serv);
        loadOrders(serv);
        Platform.runLater(OrderIDTextField::requestFocus);
        ListCakesButton.setOnAction(this::handleShowCakesButtonClick);
        FinishedOrdersButton.setOnAction(this::handleShowFinishedButtonClick);
        UninishedOrdersButton.setOnAction(this::handleShowUnfinishedButtonClick);
        setIntTextFieldConstraints(OrderIDTextField);
        setRealTextFieldConstraints(CakePriceTextField);
        AddOrderButton.setOnAction(this::handleAddButtonClick);
        CancelOrderButton.setOnAction(this::handleRemoveButtonClick);
        ModifyStatusButton.setOnAction(this::handleChangeStatusButtonClick);
        ModifyCakeButton.setOnAction(this::handleModifyCakeButtonClick);
        CustomerOrdersButton.setOnAction(this::handleCustomerOrdersButtonClick);
        SameCakeTypeButton.setOnAction(this::handleSameCakeTypeButtonClick);
        SamePriceButton.setOnAction(this::handleSamePriceButtonClick);
    }
    public CakeShopGUIController(OrderService s) {
        this.serv = s;
    }
    public CakeShopGUIController() {}

    private void setIntTextFieldConstraints(TextField textField) {
        Pattern intPattern = Pattern.compile("[0-9]*");
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String inputText = change.getText();
            if (intPattern.matcher(inputText).matches()) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<String>(integerFilter));
    }
    private void setRealTextFieldConstraints(TextField textField) {
        Pattern realPattern = Pattern.compile("\\d*\\.?\\d*");
        UnaryOperator<TextFormatter.Change> realFilter = change -> {
            String inputText = change.getControlNewText();
            if (realPattern.matcher(inputText).matches()) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<String>(realFilter));
    }

    public void loadCakes(OrderService serv){
        ObservableList<Cake> itemList = FXCollections.observableArrayList(serv.getCakes());
        cakeListView.setItems(itemList);
    }
    public void loadOrders(OrderService serv){
        ObservableList<Order> itemList = FXCollections.observableArrayList(serv.getAll());
        orderListView.setItems(itemList);
    }

    @FXML
    private void handleAddButtonClick(ActionEvent event) {
        if (OrderIDTextField.getText().isBlank() || CakeTypeTextField.getText().isBlank() || CakePriceTextField.getText().isBlank() || ClientNameTextField.getText().isBlank()){
            //if any of the text fields are empty / only white spaces, show a warning popup window
            alertPopUp("All fields must be filled to complete this action");
        } else {
            int id = Integer.parseInt(OrderIDTextField.getText());
            String type = CakeTypeTextField.getText().trim();
            double price = Double.parseDouble(CakePriceTextField.getText());
            String name = ClientNameTextField.getText().trim();
            boolean status = FinishedCheckBox.isSelected(); //selected = true; not selected = false
            if(serv.findId(id)) {
                alertPopUp("Order with specified id already exits");
            }
            else
            {
                Cake c = new Cake();
                int found=0;
                for(Cake e : serv.getCakes())
                    if(e.getFlavour().equals(type) && e.getPrice()==price)
                    {
                        c=new Cake(e); //if the cake is already in the cake repo, it will be used for the order
                        found=1;
                    }
                if(found==0)//if it doesn't exist, it's automatically given an id and added to the repo
                {
                    int cid=serv.getCakes().size()+1;
                    c=new Cake(type,price,cid);
                }
                serv.addOrder(c,name,status,id);
                loadOrders(serv);
                loadCakes(serv);

                OrderIDTextField.clear();
                CakeTypeTextField.clear();
                CakePriceTextField.clear();
                ClientNameTextField.clear();
                FinishedCheckBox.setSelected(false);
                goodPopUp("Order added successfully.");
            }
        }
    }

    @FXML
    private void handleRemoveButtonClick(ActionEvent event) {
        Order selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if(selectedOrder==null)
            alertPopUp("Select an item from the list to complete this action.");
        else {
            serv.cancelOrder(selectedOrder.getId());
            loadOrders(serv);
            goodPopUp("Order cancelled successfully.");
        }
    }

    @FXML
    private void handleChangeStatusButtonClick(ActionEvent event) {
        Order selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if(selectedOrder==null)
            alertPopUp("Select an item from the list to complete this action.");
        else {
            if(selectedOrder.getStatus())
                serv.markAsUnfinished(selectedOrder.getId());
            else
                serv.markAsFinished(selectedOrder.getId());
            loadOrders(serv);
        }
    }

    @FXML
    private void handleModifyCakeButtonClick(ActionEvent event){
        Order selectedOrder = orderListView.getSelectionModel().getSelectedItem();
        if(selectedOrder==null)
            alertPopUp("Select an item from the list to complete this action.");
        else {
            Dialog<ButtonType> diagBox = new Dialog<>();
            diagBox.setTitle("Change cake for order no."+selectedOrder.getId());
            diagBox.setHeaderText(null);

            ButtonType okButton = new ButtonType("OK",OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel",CANCEL_CLOSE);
            diagBox.getDialogPane().getButtonTypes().addAll(okButton,cancelButton);

            GridPane dGrid = new GridPane(10,10);
            dGrid.add(new Label("Type:"),0,0);
            dGrid.add(new Label("Price:"),0,1);

            TextField typeField= new TextField();
            typeField.setText(selectedOrder.getCake().getFlavour());

            TextField priceField= new TextField();
            setRealTextFieldConstraints(priceField);
            priceField.setText(String.valueOf(selectedOrder.getCake().getPrice()));

            dGrid.add(typeField,1,0);
            dGrid.add(priceField,1,1);

            diagBox.getDialogPane().setContent(dGrid);
            Platform.runLater(typeField::requestFocus);

            diagBox.setResultConverter(buttonType ->{
                if(buttonType==okButton){
                    return new ButtonType("Ok",OK_DONE);
                }
                return null;
            });//get info when clicked OK, closes diagBox if another button is pressed
            java.util.Optional<ButtonType> result = diagBox.showAndWait();

            result.ifPresent(buttonType -> {
                if(buttonType.getButtonData()== OK_DONE){
                    String type = typeField.getText().trim();
                    double price = Double.parseDouble(priceField.getText());
                    serv.changeOrderType(selectedOrder.getId(),type,price);
                    loadOrders(serv);
                    loadCakes(serv);
                    goodPopUp("Order changed successfully.");
                }
            });
        }
    }

    @FXML
    private void handleShowCakesButtonClick(ActionEvent event) {
        Stage cakePopup = new Stage();
        cakePopup.initModality(Modality.APPLICATION_MODAL);
        cakePopup.setTitle("Cakes List");

        VBox cakePopupVBox = new VBox();
        cakePopupVBox.getChildren().addAll(new ListView<>(cakeListView.getItems()));

        cakePopup.setScene(new Scene(cakePopupVBox, 300, 200));
        cakePopup.showAndWait();
    }

    @FXML
    private void handleShowFinishedButtonClick(ActionEvent event) {
        Stage finishedPopup = new Stage();
        finishedPopup.initModality(Modality.APPLICATION_MODAL);
        finishedPopup.setTitle("Finished Orders List");

        VBox fPopupVBox = new VBox();

        ObservableList<Order> itemList = FXCollections.observableArrayList(serv.allFinishedOrders());
        finishedListView.setItems(itemList);
        fPopupVBox.getChildren().addAll(new ListView<>(finishedListView.getItems()));

        finishedPopup.setScene(new Scene(fPopupVBox, 300, 200));
        finishedPopup.showAndWait();
    }

    @FXML
    private void handleShowUnfinishedButtonClick(ActionEvent event) {
        Stage unfinishedPopup = new Stage();
        unfinishedPopup.initModality(Modality.APPLICATION_MODAL);
        unfinishedPopup.setTitle("Finished Orders List");

        VBox uPopupVBox = new VBox();

        ObservableList<Order> itemList = FXCollections.observableArrayList(serv.allUnfinishedOrders());
        finishedListView.setItems(itemList);
        uPopupVBox.getChildren().addAll(new ListView<>(finishedListView.getItems()));

        unfinishedPopup.setScene(new Scene(uPopupVBox, 300, 200));
        unfinishedPopup.showAndWait();
    }

    @FXML
    private void handleCustomerOrdersButtonClick(ActionEvent event){
        Dialog<ButtonType> diagBox = new Dialog<>();
        diagBox.setHeaderText("Input name of client:");
        diagBox.setTitle("See client's order");
        //diagBox.setHeaderText(null);

        ButtonType okButton = new ButtonType("OK",OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel",CANCEL_CLOSE);
        diagBox.getDialogPane().getButtonTypes().addAll(okButton,cancelButton);

        GridPane dGrid = new GridPane(1,1);

        TextField nameField= new TextField();
        nameField.setPromptText("Client name");
        dGrid.add(nameField,0,0);

        diagBox.getDialogPane().setContent(dGrid);

        diagBox.setResultConverter(buttonType ->{
            if(buttonType==okButton){
                return new ButtonType("Ok",OK_DONE);
            }
            return null;
        });
        java.util.Optional<ButtonType> result = diagBox.showAndWait();

        result.ifPresent(buttonType -> {
            if(buttonType.getButtonData()==ButtonBar.ButtonData.OK_DONE) {
                if (nameField.getText().isBlank())
                    alertPopUp("Input a name to complete this action");
                else {
                    String name = nameField.getText().trim();
                    if(serv.allOrdersSameCustomer(name).isEmpty())
                        alertPopUp("No orders for customer '"+name+"'");
                    else {
                        ObservableList<Order> itemList = FXCollections.observableArrayList(serv.allOrdersSameCustomer(name));
                        ListView<Order> customerOrdersListView = new ListView<>(itemList);

                        Stage popupStage = new Stage();
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setTitle(name+"'s orders:");

                        VBox popupVBox = new VBox();
                        popupVBox.getChildren().addAll(new ListView<>(customerOrdersListView.getItems()));

                        popupStage.setScene(new Scene(popupVBox, 300, 200));
                        popupStage.showAndWait();
                    }
                }
            }
        });
    }
    @FXML
    private void handleSameCakeTypeButtonClick(ActionEvent event){
        Dialog<ButtonType> diagBox = new Dialog<>();
        diagBox.setHeaderText("Input cake type:");
        diagBox.setTitle("See orders");

        ButtonType okButton = new ButtonType("OK",OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel",CANCEL_CLOSE);
        diagBox.getDialogPane().getButtonTypes().addAll(okButton,cancelButton);

        GridPane dGrid = new GridPane(1,1);

        TextField typeField = new TextField();
        typeField.setPromptText("Cake type");
        dGrid.add(typeField,0,0);

        diagBox.getDialogPane().setContent(dGrid);

        diagBox.setResultConverter(buttonType ->{
            if(buttonType==okButton){
                return new ButtonType("Ok",OK_DONE);
            }
            return null;
        });
        java.util.Optional<ButtonType> result = diagBox.showAndWait();

        result.ifPresent(buttonType -> {
            if(buttonType.getButtonData()==ButtonBar.ButtonData.OK_DONE) {
                if (typeField.getText().isBlank())
                    alertPopUp("Input a type to complete this action");
                else {
                    String type = typeField.getText().trim();
                    if(serv.allOrdersSameFlavour(type).isEmpty())
                        alertPopUp("No orders of '"+type+"' cake");
                    else {
                        ObservableList<Order> itemList = FXCollections.observableArrayList(serv.allOrdersSameFlavour(type));
                        ListView<Order> cakeOrdersListView = new ListView<>(itemList);

                        Stage popupStage = new Stage();
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setTitle(type+" cake orders:");

                        VBox popupVBox = new VBox();
                        popupVBox.getChildren().addAll(new ListView<>(cakeOrdersListView.getItems()));

                        popupStage.setScene(new Scene(popupVBox, 300, 200));
                        popupStage.showAndWait();
                    }
                }
            }
        });
    }

    @FXML
    private void handleSamePriceButtonClick(ActionEvent event){
        Dialog<ButtonType> diagBox = new Dialog<>();
        diagBox.setHeaderText("Input price:");
        diagBox.setTitle("See cakes");

        ButtonType okButton = new ButtonType("OK",OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel",CANCEL_CLOSE);
        diagBox.getDialogPane().getButtonTypes().addAll(okButton,cancelButton);

        GridPane dGrid = new GridPane(1,1);

        TextField priceField = new TextField();
        setRealTextFieldConstraints(priceField);
        priceField.setPromptText("0.00");
        dGrid.add(priceField,0,0);

        diagBox.getDialogPane().setContent(dGrid);

        diagBox.setResultConverter(buttonType ->{
            if(buttonType==okButton){
                return new ButtonType("Ok",OK_DONE);
            }
            return null;
        });
        java.util.Optional<ButtonType> result = diagBox.showAndWait();

        result.ifPresent(buttonType -> {
            if(buttonType.getButtonData()==ButtonBar.ButtonData.OK_DONE) {
                if (priceField.getText().isBlank())
                    alertPopUp("Input a price to complete this action");
                else {
                    double price = Double.parseDouble(priceField.getText());
                    if(serv.allCakesSamePrice(price).isEmpty())
                        alertPopUp("No cakes that cost $"+price);
                    else {
                        ObservableList<Cake> itemList = FXCollections.observableArrayList(serv.allCakesSamePrice(price));
                        ListView<Cake> cakeOrdersListView = new ListView<>(itemList);

                        Stage popupStage = new Stage();
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setTitle("$"+price+" cake orders:");

                        VBox popupVBox = new VBox();
                        popupVBox.getChildren().addAll(new ListView<>(cakeOrdersListView.getItems()));

                        popupStage.setScene(new Scene(popupVBox, 300, 200));
                        popupStage.showAndWait();
                    }
                }
            }
        });
    }

    private void alertPopUp(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Alert");
        a.setContentText(msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void goodPopUp(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Information");
        a.setContentText(msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}