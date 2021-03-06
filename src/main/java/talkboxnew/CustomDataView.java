package talkboxnew;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.util.List;

public final class CustomDataView extends Application {
	private TalkBoxData ts;
	private Stage owner;

	public static final String[] nodesId = {"ADD_TEXTFIELD"};

	public CustomDataView(TalkBoxData tbd, Stage s) {
		this.ts = tbd;
		this.owner = s;
	}

	@Override
	public synchronized void start(Stage stage) throws Exception {
		stage.initStyle(StageStyle.UTILITY);
		stage.initOwner(owner);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		stage.setTitle("Custom Button Lists");
		final TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		final Tab verbs = makeTab("Verbs", 0);
		final Tab subjects = makeTab("Subjects", 1);
		final Tab objects = makeTab("Objects", 2);

		tabPane.getTabs().addAll(verbs, subjects, objects);

		final Scene scene = new Scene(tabPane, 300, 500);
		stage.setScene(scene);
		stage.initModality(Modality.NONE);
		stage.show();
	}

	private Tab makeTab(String name, int index) {
		final Tab tab = new Tab(name);

		final List<String> rawData = ts.customWords.get(index);
		final ObservableList<String> data = FXCollections.observableArrayList(rawData);
		Bindings.bindContent(ts.customWords.get(index), data);

		data.addListener((ListChangeListener<String>) c -> {
			ConfigStage.save.disableProperty().unbind();
			ConfigStage.save.setDisable(false);
		});

		final TableView<String> table = getTable(name, index, data);

		final VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER_RIGHT);

		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().add(table);

		tab.setContent(vbox);

		final HBox hbox = makeAddField(name.substring(0, name.length() - 1), table, index, data);
		vbox.getChildren().add(hbox);

		return tab;
	}

	private TableView<String> getTable(String name, int index, ObservableList<String> data) {
		final TableView<String> table = new TableView<>();
		table.setEditable(true);

		final TableColumn<String, String> col = new TableColumn<>(name);

		col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		col.setCellFactory(TextFieldTableCell.forTableColumn());
		col.setOnEditCommit(event -> event.getTableView()
				.getItems()
				.set(event.getTablePosition().getRow(), event.getNewValue())
		);

		table.setOnKeyPressed(event -> {
			final int cell = table.getSelectionModel().getSelectedIndex();

			if (event.getCode().equals(KeyCode.BACK_SPACE) || event.getCode().equals(KeyCode.DELETE))
				col.getTableView().getItems().remove(cell);
		});

		table.setItems(data);
		table.getColumns().add(col);

		return table;
	}

	private HBox makeAddField(String name, TableView<String> table, int index, ObservableList<String> data) {
		final HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);

		final TextField addString = new TextField();
		addString.setId(nodesId[0]);
		addString.setPromptText(name);
		addString.setMaxWidth(table.getPrefWidth());

		addString.setOnAction(event -> {
			data.add(addString.getText());
			addString.clear();
		});

		final Label label = new Label("Add " + name + ": ");

		hBox.getChildren().addAll(label, addString);
		return hBox;
	}

}
