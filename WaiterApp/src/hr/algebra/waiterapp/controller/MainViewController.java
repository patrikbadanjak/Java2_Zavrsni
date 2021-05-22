package hr.algebra.waiterapp.controller;

import hr.algebra.dal.model.Item;
import hr.algebra.dal.model.MenuItem;
import hr.algebra.dal.model.Order;
import hr.algebra.dal.sql.SqlRepository;
import hr.algebra.waiterapp.component.MenuItemComponent;
import hr.algebra.waiterapp.component.PendingOrderComponent;
import hr.algebra.waiterapp.documentation.DocumentationGenerator;
import hr.algebra.waiterapp.rmi.OrderClient;
import hr.algebra.waiterapp.thread.ClientThread;
import hr.algebra.waiterapp.utils.DialogUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MainViewController implements Initializable {

    //<editor-fold desc="FXML variables">
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button btnReset, btnCreateOrder, btnUndoRedo;

    @FXML
    private ComboBox<Integer> cbNumberOfTables, cbNumberOfPeople;

    @FXML
    private VBox vboxOrderMenu;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private VBox vbPendingOrders, vbCompletedOrders;
    //</editor-fold>

    private final String SERIALIZED_DATA_FILE_NAME = "data.ser";
    private final String ORDER_FILE = "order.xml";

    public static final String CLIENT_NAME = "Client";

    private ObservableList<MenuItemComponent> menuItemComponents;

    private OrderClient orderClient;

    private ClientThread clientThread;

    private static boolean isOrderingInProgress = false;

    private boolean isOrderUndone = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            orderClient = new OrderClient(this);
            initObservables();
            initComponents();
            initClientThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initClientThread() {
        clientThread = new ClientThread(this);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    private void setOrderReady(int tableID) {
        vbPendingOrders
                .getChildren()
                .stream()
                .map(PendingOrderComponent.class::cast)
                .filter(poc -> poc.getTableNumber() == tableID)
                .forEach(poc -> poc.setOrderStatus(true));
    }

    private void initObservables() {
        menuItemComponents = FXCollections.emptyObservableList();
    }

    private void initComponents() throws Exception {
        initBindings();
        fillComboboxes();
        fillMenu();
    }

    private void initBindings() {
        vboxOrderMenu.prefWidthProperty().bind(scrollPaneContainer.widthProperty());
        vboxOrderMenu.prefHeightProperty().bind(scrollPaneContainer.heightProperty());
    }

    private void fillMenu() throws Exception {
        List<MenuItemComponent> tempList = new ArrayList<>();

        new SqlRepository().getItems().forEach(item -> {
            MenuItemComponent mic = new MenuItemComponent(item);

            if (tempList.size() % 2 == 0)
                mic.setStyle("-fx-background-color: #ccc");

            tempList.add(mic);
        });

        menuItemComponents = FXCollections.observableList(tempList);
        vboxOrderMenu.getChildren().addAll(menuItemComponents);
    }

    private void fillMenuWithSerializedData(List<Item> serializedData) {
        List<MenuItemComponent> tempList = new ArrayList<>();

        serializedData.forEach(item -> {
            MenuItemComponent mic = new MenuItemComponent(item);

            if (tempList.size() % 2 == 0)
                mic.setStyle("-fx-background-color: #ccc");

            tempList.add(mic);
        });

        menuItemComponents = FXCollections.observableList(tempList);

        vboxOrderMenu.getChildren().clear();
        vboxOrderMenu.getChildren().addAll(menuItemComponents);
    }

    private void fillComboboxes() {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 1; i <= 25; i++)
            integerList.add(i);

        ObservableList<Integer> observableList = FXCollections.observableList(integerList);
        cbNumberOfTables.setItems(observableList);
        cbNumberOfPeople.setItems(observableList);
    }

    @FXML
    private synchronized void createOrder() {
        if (cbNumberOfPeople.getValue() == 0 || cbNumberOfTables.getValue() == 0) {
            return;
        }

        while (isOrderingInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        toggleUndoRedoButton();
        isOrderingInProgress = true;

        List<MenuItem> orderedItems =
                menuItemComponents.stream()
                    .filter(mic -> mic.getItemAmount() > 0)
                    .map(mic -> new MenuItem(mic.getItem(), mic.getItemAmount()) )
                    .collect(Collectors.toList());

        Order order = new Order(
                cbNumberOfTables.getValue(),
                cbNumberOfPeople.getValue().shortValue(),
                orderedItems
        );

        writeOrderToXML(order);

        waitForUndo(this);

        isOrderingInProgress = false;
        notifyAll();
    }

    private void waitForUndo(MainViewController controller) {
        Task<Void> waiter = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(3000);
                    if (!isOrderUndone) {
                        Platform.runLater(() -> {
                            Optional<Order> order = readOrderFromXML(ORDER_FILE);
                            vbPendingOrders.getChildren().add(new PendingOrderComponent(controller, order.get()));
                            orderClient.sendOrder(order.get());
                            resetOrder();
                            toggleUndoRedoButton();
                        });
                    } else {
                        Platform.runLater(() -> {
                            DialogUtils.showAlert(Alert.AlertType.CONFIRMATION, "Success!", "Order undone");
                            resetOrder();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        new Thread(waiter).start();
    }

    @FXML
    private void resetOrder() {
        menuItemComponents.forEach(MenuItemComponent::resetItemAmount);
        cbNumberOfPeople.getSelectionModel().clearSelection();
        cbNumberOfTables.getSelectionModel().clearSelection();
    }

    @FXML
    private void createDocumentation() {
        DocumentationGenerator.generateDocumentation();
    }

    @FXML
    private void saveData() {
        List<Item> itemsForSerialization = menuItemComponents.stream()
                .map(MenuItemComponent::getItem)
                .collect(Collectors.toList());

        try (FileOutputStream serializationStream = new FileOutputStream(SERIALIZED_DATA_FILE_NAME);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializationStream)) {

            objectOutputStream.writeObject(itemsForSerialization);
            DialogUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Data saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadData() {
        if (!Files.exists(Paths.get(SERIALIZED_DATA_FILE_NAME))) {
            DialogUtils.showAlert(Alert.AlertType.ERROR, "Error", "There is no saved data to load!");
            return;
        }

        try(FileInputStream serializationStream = new FileInputStream(SERIALIZED_DATA_FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(serializationStream)) {

            List<Item> serializedItems = (List<Item>) objectInputStream.readObject();

            fillMenuWithSerializedData(serializedItems);

            DialogUtils.showAlert(Alert.AlertType.INFORMATION, "Success!", "Data loaded successfully!");
        } catch (Exception e) {
            DialogUtils.showAlert(Alert.AlertType.ERROR, "Error", "Unable to load data");
            e.printStackTrace();
        }
    }

    @FXML
    private void undoRedoAction() {
        isOrderUndone = !isOrderUndone;
        toggleUndoRedoButton();
    }

    private void toggleUndoRedoButton() {
        btnUndoRedo.setDisable(!btnUndoRedo.isDisabled());
    }

    public void receiveOrder(Order order) {
        setOrderReady(order.getTableID());
    }

    public void moveOrderToCompleted(Order order) {
        List<Node> completedOrders = vbPendingOrders
                .getChildren()
                .stream()
                .map(PendingOrderComponent.class::cast)
                .filter(component -> component.getTableNumber() == order.getTableID())
                .collect(Collectors.toList());

        vbPendingOrders.getChildren().removeAll(completedOrders);

        vbCompletedOrders.getChildren().addAll(completedOrders);
    }

    private void writeOrderToXML(Order order) {
        try {
            Document xmlDocument = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = xmlDocument.createElement("Order");

            Element tableIDElement = xmlDocument.createElement("tableID");
            tableIDElement.setTextContent(String.valueOf( order.getTableID() ));
            root.appendChild(tableIDElement);
            
            Element numOfPeopleElement = xmlDocument.createElement("numberOfPeople");
            numOfPeopleElement.setTextContent(String.valueOf( order.getNumOfPeople() ));
            root.appendChild(numOfPeopleElement);
            
            Element orderStatusElement = xmlDocument.createElement("orderStatus");
            orderStatusElement.setTextContent(order.getOrderStatus());
            root.appendChild(orderStatusElement);
            
            Element orderedItemsElement = xmlDocument.createElement("orderedItems");
            order.getOrderedItems().forEach(item -> {
                Element itemElement = xmlDocument.createElement("item");
                itemElement.setAttribute("idItem", String.valueOf( item.getItem().getIdItem() ));
                itemElement.setAttribute("amount", item.getItemAmount().toString());
                orderedItemsElement.appendChild(itemElement);
            });
            root.appendChild(orderedItemsElement);

            Element orderDateElement = xmlDocument.createElement("orderDate");
            orderDateElement.setTextContent(order.getTimeOfOrder().toString());
            root.appendChild(orderDateElement);

            xmlDocument.appendChild(root);
            xmlDocument.normalize();
            Source xmlSource = new DOMSource(xmlDocument);

            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(xmlSource, new StreamResult(new File(ORDER_FILE)));

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            DialogUtils.showAlert(Alert.AlertType.ERROR, "Error writing XML", e.getMessage());
        }
    }

    private Optional<Order> readOrderFromXML(String filename) {
        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new File(filename));
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();

            NodeList tableIDTag = rootElement.getElementsByTagName("tableID");
            int tableID = Integer.parseInt(tableIDTag.item(0).getTextContent());

            NodeList numOfPplTag = rootElement.getElementsByTagName("numberOfPeople");
            short numberOfPeople = Short.parseShort(numOfPplTag.item(0).getTextContent());

            NodeList orderStatusNode = rootElement.getElementsByTagName("orderStatus");
            String orderStatus = orderStatusNode.item(0).getTextContent();

            NodeList orderedItemsNode = rootElement.getElementsByTagName("orderedItems");
            NodeList orderedItemsList = orderedItemsNode.item(0).getChildNodes();

            List<MenuItem> orderedMenuItems = new ArrayList<>();
            for(int i = 0; i < orderedItemsList.getLength(); i++) {
                Item item = new SqlRepository().getItem(
                    Integer.parseInt(orderedItemsList.item(i).getAttributes().getNamedItem("idItem").getNodeValue())
                );
                Integer itemAmount = Integer.parseInt(
                        orderedItemsList.item(i).getAttributes().getNamedItem("amount").getNodeValue()
                );
                orderedMenuItems.add(new MenuItem(item, itemAmount));
            }

            NodeList orderDateNode = rootElement.getElementsByTagName("orderDate");
            LocalDateTime orderDate = LocalDateTime.parse(orderDateNode.item(0).getTextContent());

            return Optional.of(
                new Order(
                    tableID,
                    numberOfPeople,
                    orderStatus,
                    orderedMenuItems,
                    orderDate
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
