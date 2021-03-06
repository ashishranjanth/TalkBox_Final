package talkboxnew;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import talkboxnew.Buttons.AudioButton;
import talkboxnew.Commands.History;
import talkboxnew.Commands.ImportCommand;

import javax.swing.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static talkboxnew.Utils.tryFactory;

public class ConfigStage extends Stage {
	/**
	 * The path to the {@code *.zip} directory containing the {@code Audio} and {@code Image} subdirectories, the {@code *.tbc} configuration file, and the {@code *.log} logging file.
	 */
	public static Path masterPath;
	private final TalkBoxData ts;
	public static ObservableList<Entry> data;
	public static MenuItem save;

	public static final Logger log = Logger.getLogger(ConfigStage.class.getName());

	public static final String WAV = ".*\\.wav$";
	public static final int WIDTH_FACTOR = 120;

	// nodes for testing
	public static final String[] nodesId = {"BUTTONS_FLOWPANE"};

	public ConfigStage(Path masterPath) throws Exception {
		ConfigStage.masterPath = masterPath;
		updateRecents(masterPath.toFile());
		this.initOwner(null);
		this.initModality(Modality.NONE);

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		log.info("Starting Configuration App");

		this.setHeight(400);
		this.getIcons().add(new Image(Utils.getResource("icon2.png").toString()));

		final BorderPane pane = new BorderPane();
		pane.topProperty().bindBidirectional(new SimpleObjectProperty<>(makeMenuBar()));

		final VBox box = new VBox();
		pane.centerProperty().bindBidirectional(new SimpleObjectProperty<>(box));
		pane.prefHeightProperty().bind(this.heightProperty());

		this.ts = readConfig();

		data = FXCollections.observableList(ts.database);
		data.addListener((ListChangeListener<Entry>) c -> {
			save.disableProperty().unbind();
			save.disableProperty().setValue(false);
		});

		box.getChildren().add(getGridPane());
		this.setWidth(ts.numberOfAudioButtons * WIDTH_FACTOR);

		this.setOnCloseRequest(this::warnBeforeExit);
		this.setTitle("TalkBox Configurator — " + masterPath.getFileName().toString());
		this.setScene(new Scene(pane));
	}

	private void warnBeforeExit(WindowEvent event) {
		if (save.isDisable()) return;

		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(this);

		alert.setTitle("Confirm Exit");
		alert.setHeaderText("Save File?");
		alert.setContentText("Please choose an option.");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

		final Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			log.info("Saving and exiting...");
			event.consume();

			tryFactory.attemptTo(this::saveConfig);
			Platform.exit();
		} else if (result.isPresent() && result.get() == ButtonType.NO) {
			log.info("Not saving and exiting...");
			Platform.exit();
		} else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
			log.info("Alert cancelled");
			event.consume();
		}
	}

	private Pagination getGridPane() {
		final Pagination pagination = new Pagination(ts.getNumberOfAudioSets());

		pagination.prefHeightProperty().bind(this.heightProperty());
		pagination.setPadding(new Insets(0, 0, 30, 0));
		pagination.setPageFactory(this::configButtons);

		return pagination;
	}

	private FlowPane configButtons(int page) {
		final FlowPane flowPane = new FlowPane();
		flowPane.setId(nodesId[0]);

		flowPane.setPadding(new Insets(30, 20, 30, 20));
		flowPane.setVgap(10);
		flowPane.setHgap(10);
		flowPane.setAlignment(Pos.CENTER);

		for (int i = 0; i < ts.numberOfAudioButtons; i++) {
			flowPane.getChildren().add(new AudioButton(page * ts.numberOfAudioButtons + i));
		}

		flowPane.minWidthProperty().bindBidirectional(this.minWidthProperty());

		return flowPane;
	}

	private MenuBar makeMenuBar() {
		final MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(this.widthProperty().subtract(150));

		final Menu menuFile = new Menu("File");
		final Menu menuEdit = new Menu("Edit");
		final Menu menuView = new Menu("View");

		final MenuItem sim = new MenuItemBuilder("Open in Simulator...")
				.withAction(e -> {
					tryFactory.attemptTo(this::saveConfig);
					new SimulatorStage(masterPath, this).showAndWait();
				})
				.withShortcutKey(KeyCode.O)
				.build();

		final MenuItem undo = new MenuItemBuilder("Undo")
				.disableWhen(History.getInstance().getIsEmptyProperty())
				.withAction(e -> tryFactory.attemptTo(() -> History.getInstance().undo()))
				.withShortcutKey(KeyCode.Z)
				.build();

		ConfigStage.save = new MenuItemBuilder("Save")
				.disableWhen(new SimpleBooleanProperty(true))
				.withAction(e -> tryFactory.attemptTo(this::saveConfig))
				.withShortcutKey(KeyCode.S)
				.build();

		final MenuItem custom = new MenuItemBuilder("Custom Phrase List")
				.withAction(e -> tryFactory.attemptTo(() -> new CustomDataView(ts, this).start(new Stage())))
				.withShortcutKey(KeyCode.C)
				.build();

		final MenuItem importM = new MenuItemBuilder("Import Audio Files")
				.withAction(e -> importFiles())
				.withShortcutKey(KeyCode.I)
				.build();

		menuFile.getItems().addAll(save, importM);
		menuEdit.getItems().addAll(undo, custom);
		menuView.getItems().addAll(sim);

		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

		return menuBar;
	}

	private void importFiles() {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		chooser.setTitle("Select a Directory");

		final File dir = chooser.showDialog(this);
		log.info("FileChooser invoked. File(s) being selected.");

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				dir.toPath(),
				entry -> entry.toString().matches(WAV))
		) {
			History.getInstance().execute(new ImportCommand(stream));
		} catch (IOException ex) {
			Utils.release(ex);
		}
	}

	static TalkBoxData readConfig() throws Exception {
		final FileInputStream fis = new FileInputStream(Utils.getTBC());

		final ObjectInputStream oin = new ObjectInputStream(fis);
		final TalkBoxData tsImport = (TalkBoxData) oin.readObject();

		fis.close();
		oin.close();

		return tsImport;
	}

	private void saveConfig() throws Exception {
		try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Utils.getTBC()))) {
			ts.database = new ArrayList<>(data);
			oos.writeObject(ts);
		}

		save.disableProperty().unbind();
		save.setDisable(true);
	}

	private static void updateRecents(File s) throws Exception {
		ArrayList<File> list;
		try {
			final ObjectInputStream oin = new ObjectInputStream(new FileInputStream(Utils.getRecentsPath().toString()));
			System.out.println(oin.available());
			list = (ArrayList<File>) oin.readObject();
			oin.close();
		} catch (Exception e) {
			list = new ArrayList<>();
		}

		list.remove(s);
		list.add(s);

		final FileOutputStream fos = new FileOutputStream(Utils.getRecentsPath().toString());
		final ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(list);
		oos.close();
	}


}
