package chessengine1;

import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 *
 * @author Aidan Noel
 */
public class ChessGUI extends Application {
    private double score;
    private ImageView quit;
    private ImageView restart;
    private Tile[][] board = new Tile[8][8];
    private Board b;
    //Buttons
    Button quitBtn = new Button();
    Button restartBtn = new Button();
    //Root for the board       
    GridPane root = new GridPane();
    //Sets up the top menu bar
    BorderPane root2 = new BorderPane();
    VBox topContainer = new VBox(); //Creates a container to hold all menu objects
    MenuBar mainMenu = new MenuBar();  //Creates a main menu to hold the sub-menus
    ToolBar toolBar = new ToolBar();            
    //Sets up sub-menus for mainMenu
    //Scoring submenu
    Menu menu1 = new Menu("Scoring");
    MenuItem currScore = new MenuItem("Current Score");
    //Pieces submenu
    Menu menu2 = new Menu("Pieces");
    CheckMenuItem regular = new CheckMenuItem("Regular");
    CheckMenuItem special = new CheckMenuItem("Special");
    //Other submenu
    Menu menu3 = new Menu("Other");
    MenuItem about = new MenuItem("About");
    //Box to hold everything
    VBox grandRoot = new VBox();
    //Promotion dialogue options
    Alert alert = new Alert(AlertType.CONFIRMATION);
    ButtonType buttonQueen = new ButtonType("Queen");
    ButtonType buttonRook = new ButtonType("Rook");
    ButtonType buttonBishop = new ButtonType("Bishop");
    ButtonType buttonKnight = new ButtonType("Knight");
    ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    
    @Override
    public void start(Stage primaryStage) {
        
        // Initialize black's back rank
        board[0][0] = new Tile(2,-1);
        board[0][1] = new Tile(3,-1);
        board[0][2] = new Tile(4,-1);
        board[0][3] = new Tile(5,-1);
        board[0][4] = new Tile(6,-1);
        board[0][5] = new Tile(4,-1);
        board[0][6] = new Tile(3,-1);
        board[0][7] = new Tile(2,-1);
        
        // Initialize the pawns and the empty squares
        for(int col=0; col<8; col++){
            board[1][col] = new Tile(1,-1);
            board[2][col] = new Tile(0,0);
            board[3][col] = new Tile(0,0);
            board[4][col] = new Tile(0,0);
            board[5][col] = new Tile(0,0);
            board[6][col] = new Tile(1,1);
        }
        // Initialize white's back rank
        board[7][0] = new Tile(2,1);
        board[7][1] = new Tile(3,1);
        board[7][2] = new Tile(4,1);
        board[7][3] = new Tile(5,1);
        board[7][4] = new Tile(6,1);
        board[7][5] = new Tile(4,1);
        board[7][6] = new Tile(3,1);
        board[7][7] = new Tile(2,1);
        //board[4][6] = new Tile(6,1);
        //board[4][4] = new Tile(5,-1);
        
        b = new Board(board, true, true, true, true, true, true);
        primaryStage.initStyle(StageStyle.UNDECORATED);   

        //Set grid constraints
        for (int i = 0; i < 8; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(100, 100, 100, Priority.ALWAYS, HPos.CENTER, true));
            root.getRowConstraints().add(new RowConstraints(100, 100, 100, Priority.ALWAYS, VPos.CENTER, true));
        }
        
        //width of top bar
        root2.setPrefWidth(800);
        //adds Scoring dropdown
        menu1.getItems().addAll(currScore);
        setScoring(currScore);
        //adds Pieces dropdown
        menu2.getItems().addAll(regular, special);
        regular.setSelected(true);
        updateSet(regular, special, primaryStage);
        //adds Other dropdown
        menu3.getItems().add(about);
        setAbout(about);
        //Pulls it all together
        mainMenu.getMenus().addAll(menu1, menu2, menu3);

        //Toolbar icons/buttons
        //Quit Button
        quit = new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/redx.gif")));
        quit.setFitHeight(18);
        quit.setFitWidth(18);
        quitBtn.setGraphic(quit);
        setQuit(quitBtn);
        //Restart button
        restart = new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/restart.png")));
        restart.setFitHeight(18);
        restart.setFitWidth(18);
        restartBtn.setGraphic(restart);
        setRestart(primaryStage);
        
        toolBar.getItems().addAll(quitBtn, restartBtn);
        
        //Adds everything to topContainer
        topContainer.getChildren().add(mainMenu);
        topContainer.getChildren().add(toolBar);
        root2.setTop(topContainer);
        
        //Promotion alert
        alert.setTitle("Promotion Confirmation");
        alert.setContentText("");
        alert.getButtonTypes().setAll(buttonQueen, buttonRook, buttonBishop, buttonKnight, buttonCancel);
        //Set up the grandRoot to contain all other roots and displays
        grandRoot.getChildren().addAll(root2, root);
        //primaryStage.setScene(new Scene(grandRoot, 760, 827));
        primaryStage.setScene(new Scene(grandRoot, 800, 867));
        //Build the stage
        update(primaryStage);
        clickMove(primaryStage);
    }
       
    int temp;
    int tempX;
    int tempY;
    boolean firstClick = true;
    boolean secondClick = false;
    private void clickMove(Stage primaryStage){
        root.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public final void handle(MouseEvent t){
                //Gets position of mouse on click and converts into whole number location [0,7]
                double x = t.getY();
                double y = t.getX();
                final double pieceX = (x - (x % 100)) + 50;
                final double pieceY = (y - (y % 100)) + 50;
                double unrX = (pieceX/100) - 0.5;
                double unrY = (pieceY/100) - 0.5;
                int gridX = (int)unrX;
                int gridY = (int)unrY;
                System.out.println(x + ","+ y + ","+ pieceX + ","+ pieceY + ","+ unrY + ","+ unrY + ","+ gridX + ","+ gridY);
                if(firstClick && board[gridX][gridY].getOwner() == 1){
                    temp = board[gridX][gridY].getPiece();
                    tempX = gridX;
                    tempY = gridY;
                    firstClick = false;
                    secondClick = true;
                    System.out.println("FIRST CLICK");
                    System.out.println(" ["+gridX+","+gridY+"]");
                }
                else if (secondClick){
                    //Second click and placement
                    System.out.println("SECOND CLICK");
                    System.out.println(" ["+gridX+","+gridY+"]");                    
                    if(b.move(new Move(tempX, tempY, gridX, gridY), 1, false)){
                        update(primaryStage);
                        //Alert with options for promotion
                        alert.setHeaderText("Choose Piece to Promote To At  ["+gridX+","+gridY+"]");   
                        if (temp == 1 && gridX == 0){
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == buttonQueen){
                                temp = 5;
                            } else if (result.get() == buttonRook) {
                                temp = 2;
                            } else if (result.get() == buttonBishop) {
                                temp = 4;
                            } else if (result.get() == buttonKnight) {
                                temp = 3;
                            } else {
                                gridX = tempX;
                                gridY = tempY;
                            }
                            board[gridX][gridY].setPiece(temp);
                        }
                        score=b.minimax(-1, -10000.0, 10000.0, 0);
                        update(primaryStage);
                        System.gc();
                    }
                    temp = 0;
                    firstClick = true;
                    secondClick = false;
                } 
            } 
        });
    }
    
    private void update(Stage primaryStage){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col ++) {
               StackPane square = new StackPane();
                String color ;
                if ((row + col) % 2 == 0) {
                    color = "moccasin";
                } else {
                    color = "saddlebrown";
                }
                square.setStyle("-fx-background-color: "+color+";");
                root.add(square, col, row);
                if(regular.isSelected()){
                    if(board[row][col].getOwner() == -1){
                        if(board[row][col].getPiece() == 1){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Pawn.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 2){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Rook.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 3){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Dark Knight.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 4){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Bishop.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 5){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Queen alt.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 6){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black King.png"))), col, row);
                        }
                    }
                    else if(board[row][col].getOwner() == 1){
                        if(board[row][col].getPiece() == 1){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Pawn.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 2){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Rook.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 3){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Knight.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 4){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Bishop.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 5){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Queen alt.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 6){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White King.png"))), col, row);
                        }
                    }
                } else if(special.isSelected()){
                    if(board[row][col].getOwner() == -1){
                        if(board[row][col].getPiece() == 1){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Pawn S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 2){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Rook S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 3){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Dark Knight S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 4){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Bishop S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 5){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black Queen S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 6){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/Black King S.png"))), col, row);
                        }
                    }
                    else if(board[row][col].getOwner() == 1){
                        if(board[row][col].getPiece() == 1){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Pawn S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 2){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Rook S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 3){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Knight S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 4){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Bishop S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 5){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White Queen S.png"))), col, row);
                        }
                        else if(board[row][col].getPiece() == 6){
                            root.add(new ImageView(new Image(ChessGUI.class.getResourceAsStream("images/White King S.png"))), col, row);
                        }
                    }
                }
            } 
        }
        System.out.println("updating here");
        primaryStage.show();
    }
    
    private void setScoring(MenuItem currScore) {
        currScore.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                
                String scoreStr = String.valueOf(score);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Scoring");
                alert.setHeaderText("Current Score \n (Positive is user, Negative is computer)");
                alert.setContentText(scoreStr);
                alert.showAndWait();
            }
        });
    }
    
    private void setAbout(MenuItem about) {
        about.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("About the Creators");
                alert.setContentText("Connor Malin and Aidan Noel's Senior Project");
                alert.showAndWait();
            }
        });
    }

    private void setQuit(Button quitBtn) {
        quitBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                System.exit(0);
            }
        });
    }
    
    private void setRestart(Stage primaryStage) {
        restartBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                //Reset the board
                // Initialize black's back rank
                System.gc();
                
                board[0][0].setPiece(2);
                board[0][0].setOwner(-1);
                board[0][1].setPiece(3);
                board[0][1].setOwner(-1);
                board[0][2].setPiece(4);
                board[0][2].setOwner(-1);
                board[0][3].setPiece(5);
                board[0][3].setOwner(-1);
                board[0][4].setPiece(6);
                board[0][4].setOwner(-1);
                board[0][5].setPiece(4);
                board[0][5].setOwner(-1);
                board[0][6].setPiece(3);
                board[0][6].setOwner(-1);
                board[0][7].setPiece(2);
                board[0][7].setOwner(-1);

                // Initialize the pawns and the empty squares
                for(int col=0; col<8; col++){
                    board[1][col].setPiece(1);
                    board[1][col].setOwner(-1);
                    board[2][col].setPiece(0);
                    board[2][col].setOwner(0);
                    board[3][col].setPiece(0);
                    board[3][col].setOwner(0);
                    board[4][col].setPiece(0);
                    board[4][col].setOwner(0);
                    board[5][col].setPiece(0);
                    board[5][col].setOwner(0);
                    board[6][col].setPiece(1);
                    board[6][col].setOwner(1);
                }
                // Initialize white's back rank
                board[7][0].setPiece(2);
                board[7][0].setOwner(1);
                board[7][1].setPiece(3);
                board[7][1].setOwner(1);
                board[7][2].setPiece(4);
                board[7][2].setOwner(1);
                board[7][3].setPiece(5);
                board[7][3].setOwner(1);
                board[7][4].setPiece(6);
                board[7][4].setOwner(1);
                board[7][5].setPiece(4);
                board[7][5].setOwner(1);
                board[7][6].setPiece(3);
                board[7][6].setOwner(1);
                board[7][7].setPiece(2);
                board[7][7].setOwner(1);
                b = new Board(board, true, true, true, true, true, true);
                update(primaryStage);
            }
        });
    }
    
    private void updateSet(CheckMenuItem item, CheckMenuItem item2, Stage primaryStage) {
        item.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                if(item.isSelected()){
                    item2.setSelected(false);
                } else if(item2.isSelected()){
                    item2.setSelected(false);
                }
                update(primaryStage);
            }
        });
        item2.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                if(item.isSelected()){
                    item.setSelected(false);
                } else if(item2.isSelected()){
                    item.setSelected(false);
                }
                update(primaryStage);
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
//TO DO ************************************************************************
//color scheme(DONE), queen(DONE), menu bar(DONE), pop-up window(DONE)
//Init board(DONE), connect w/ Connor's(DONE), clickable moves(DONE)
/*Tile[][] board = new Tile[8][8];
        Scanner input = new Scanner(System.in);
        // Initialize black's back rank
        board[0][0] = new Tile(2,-1);
        board[0][1] = new Tile(3,-1);
        board[0][2] = new Tile(4,-1);
        board[0][3] = new Tile(5,-1);
        board[0][4] = new Tile(6,-1);
        board[0][5] = new Tile(4,-1);
        board[0][6] = new Tile(3,-1);
        board[0][7] = new Tile(2,-1);
        
        // Initialize the pawns and the empty squares
        for(int col=0; col<8; col++){
            board[1][col] = new Tile(1,-1);
            board[2][col] = new Tile(0,0);
            board[3][col] = new Tile(0,0);
            board[4][col] = new Tile(0,0);
            board[5][col] = new Tile(0,0);
            board[6][col] = new Tile(1,1);
        }
        // Initialize white's back rank
        board[7][0] = new Tile(2,1);
        board[7][1] = new Tile(3,1);
        board[7][2] = new Tile(4,1);
        board[7][3] = new Tile(5,1);
        board[7][4] = new Tile(6,1);
        board[7][5] = new Tile(4,1);
        board[7][6] = new Tile(3,1);
        board[7][7] = new Tile(2,1);
        
        board[4][6] = new Tile(6,1);
        board[4][4] = new Tile(5,-1);
        
        Board b = new Board(board);*/
