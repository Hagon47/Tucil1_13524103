import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GuiController implements Board.BoardListener {

    @FXML private MenuItem btnImg;      // Save as Image
    @FXML private MenuItem btnLoad;     // Load File
    @FXML private MenuItem btnSave;     // Save as Text
    @FXML private Button btnSolve;      // Solve 

    @FXML private Label context;        // Status
    @FXML private Label iteration;      // Iterations
    @FXML private Label timer;          // Time

    @FXML private CheckBox liveUpdate;  // Live Update
    @FXML private CheckBox showLetters; // Letters
    @FXML private GridPane queenGrid;   // Board

    private Board board;
    private char[][] startGrid;
    private String currentFileName;

    private final int TILE_SIZE = 50; 

    private final String[] tileColors = {
        "#FF6347", // A
        "#FFA500", // B 
        "#FFD700", // C 
        "#90EE90", // D 
        "#87CEEB", // E 
        "#DDA0DD", // F 
        "#FF69B4", // G 
        "#F0E68C", // H 
        "#00FFFF", // I 
        "#FFB6C1", // J 
        "#7FFFD4", // K 
        "#B0E0E6", // L 
        "#EE82EE", // M 
        "#FFDAB9", // N 
        "#E6E6FA", // O 
        "#00FA9A", // P 
        "#FFE4B5", // Q 
        "#B8860B", // R 
        "#FF7F50", // S 
        "#C0C0C0", // T 
        "#98FB98", // U 
        "#AFEEEE", // V 
        "#FFC0CB", // W 
        "#F5DEB3", // X 
        "#ADD8E6", // Y 
        "#D3D3D3"  // Z 
    };

    @FXML
    public void initialize() {
        btnSolve.setDisable(true);
        btnImg.setDisable(true);
        btnSave.setDisable(true);
        
        // Default text
        context.setText("Please Load File");
        iteration.setText("0");
        timer.setText("0 ms");
        
        // Menu Items
        btnLoad.setOnAction(e -> handleLoad());
        btnImg.setOnAction(e -> handleSaveImage());
        btnSave.setOnAction(e -> handleSaveText());

        // Solve
        btnSolve.setOnAction(e -> handleSolve());

        showLetters.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (board != null && board.getFinalGrid() != null) {
                updateView(board.getFinalGrid());
            } else if (startGrid != null) {
                char[][] empty = new char[startGrid.length][startGrid.length];
                updateView(empty);
            }
        });
    }

    // Event Handlers 
    private void handleLoad() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load file .txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            try {
                startGrid = FileHandler.readFiles(file);
                
                String fullName = file.getName();
                int pos = fullName.lastIndexOf(".");
                if (pos > 0) {
                    this.currentFileName = fullName.substring(0, pos);
                } else {
                    this.currentFileName = fullName;
                }

                btnSolve.setDisable(false);
                btnSave.setDisable(true); 
                btnImg.setDisable(true); 
                
                // Reset Label
                timer.setText("0 ms");
                iteration.setText("0");
                context.setText("Ready to Solve");

                char[][] emptyGrid = new char[startGrid.length][startGrid.length];
                updateView(emptyGrid);

            } catch (IOException e) {
                showAlert("Failed to load File: ", e.getMessage());
        
            } catch (Exception e) {
                showAlert("Error", "There is an error: " + e.getMessage());
            }
        }
    }

    private void handleSaveImage() {
        FileChooser fc = new FileChooser();
        String defaultName = "solution_" + (currentFileName != null ? currentFileName : "unknown") + ".png";
        fileLocation(fc, "Save Image (.png)", "PNG Image", "*.png", defaultName);

        File file = fc.showSaveDialog(null);

        if (file != null) {
            try {
                WritableImage image = queenGrid.snapshot(new SnapshotParameters(), null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert("Success", "Image successfully saved!");
            } catch (IOException ex) {
                showAlert("Error", "Failed to save image: " + ex.getMessage());
            }
        }
    }

    private void handleSaveText() {
        if (board == null || board.getFinalGrid() == null) {
            showAlert("Error", "No Solution to save!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        String defaultName = "solution_" + (currentFileName != null ? currentFileName : "unknown") + ".txt";
        fileLocation(fileChooser, "Save Solution (.txt)", "Text Files", "*.txt", defaultName);

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                char[][] grid = board.getFinalGrid();
                int N = grid.length;

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        writer.print(grid[i][j]);
                    }
                    writer.println();
                }

                showAlert("Success", "File successfully saved in:\n" + file.getAbsolutePath());
                
            } catch (Exception ex) {
                showAlert("Error", "Failed to save file: " + ex.getMessage());
            }
        }
    }

    private void handleSolve() {
        if (startGrid == null) {
            return;
        }

        btnSolve.setDisable(true);
        btnLoad.setDisable(true);
        context.setText("Solving...");

        board = new Board(startGrid);

        if (liveUpdate.isSelected()) {
            board.setListener(this);
            board.setDelay(1); 
        } else {
            board.setListener(null);
            board.setDelay(0);
        }

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            boolean success = board.solve(false);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            long totalIter = board.getIterations();

            Platform.runLater(() -> {
                timer.setText(totalTime + " ms");
                iteration.setText(String.valueOf(totalIter));
                
                btnLoad.setDisable(false);
                btnSolve.setDisable(false);

                if (success) {
                    context.setText("Solution Found!");
                    btnImg.setDisable(false); 
                    btnSave.setDisable(false);
                    updateView(board.getFinalGrid()); 
                } else {
                    context.setText("No Solution.");
                }
            });
        }).start();
    }

    // Visual
    @Override
    public void onStep(char[][] gridData) {
        long currentIter = board.getIterations();
        if (currentIter > 10 && currentIter % 10000 != 0) {
            return; 
        }

        char[][] copy = new char[gridData.length][gridData.length];
        for(int i=0; i<gridData.length; i++) copy[i] = gridData[i].clone();
        
        String iterCount = String.valueOf(currentIter);

        Platform.runLater(() -> {
            updateView(copy);
            iteration.setText(iterCount);
        });
    }

    private void updateView(char[][] gridToShow) {
        queenGrid.getChildren().clear();
        queenGrid.getColumnConstraints().clear();
        queenGrid.getRowConstraints().clear();

        int N = startGrid.length;

        for (int i = 0; i < N; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(TILE_SIZE);
            queenGrid.getColumnConstraints().add(colConst);

            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(TILE_SIZE);
            queenGrid.getRowConstraints().add(rowConst);
        }

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                char regionChar = startGrid[row][col];
                Color regionColor = getColor(regionChar);
                String hexColor = toHexString(regionColor);

                StackPane cell = new StackPane();
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.setStyle("-fx-background-color: " + hexColor + ";");

                double top = (row > 0 && startGrid[row-1][col] == regionChar) ? 0.0 : 2.0;
                double right = (col < N-1 && startGrid[row][col+1] == regionChar) ? 0.0 : 2.0;
                double bottom = (row < N-1 && startGrid[row+1][col] == regionChar) ? 0.0 : 2.0;
                double left = (col > 0 && startGrid[row][col-1] == regionChar) ? 0.0 : 2.0;
                
                cell.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left))));

                if (showLetters.isSelected()) {
                    Text letterText = new Text(String.valueOf(regionChar));
                    letterText.setFont(Font.font("Arial", FontWeight.BOLD, TILE_SIZE * 0.4));
                    letterText.setOpacity(0.3);
                    cell.getChildren().add(letterText);
                }

                if (gridToShow[row][col] == '#') {
                    Text queenText = new Text("♕");
                    queenText.setFont(Font.font("Serif", FontWeight.BOLD, TILE_SIZE * 0.6));
                    cell.getChildren().add(queenText);
                }
                queenGrid.add(cell, col, row);
            }
        }
    }

    // Helper
    private void fileLocation(FileChooser fc, String title, String extDesc, String extMap, String defaultName) {
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(extDesc, extMap));
        fc.setInitialFileName(defaultName);

        File testDir = new File("test");
    
        fc.setInitialDirectory(testDir);
    }

    private Color getColor(char c) {
        char upper = Character.toUpperCase(c);
            if (upper < 'A' || upper > 'Z') {
                return Color.WHITE; 
            }
            int index = upper - 'A';
            return Color.web(tileColors[index]);
    }

    private String toHexString(Color c) {
        return String.format("#%02X%02X%02X",
            (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}