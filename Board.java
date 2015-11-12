/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessengine1;
import java.util.ArrayList;
import java.lang.Math;
/**
 *
 * @author Connor Malin
 */
public class Board {
    private boolean leftCastleComp, rightCastleComp, kingCastleComp;
    private boolean leftCastleUser, rightCastleUser, kingCastleUser;
    private Tile[][] board;
    private final int PLY=4;
    Board(Tile [][] board, boolean leftUser, boolean kingUser, boolean rightUser, boolean leftComp, boolean kingComp, boolean rightComp){
        this.board=board;
        leftCastleUser=leftUser;
        kingCastleUser=kingUser;
        rightCastleUser=rightUser;
        leftCastleComp=leftComp;
        kingCastleComp=kingComp;
        rightCastleComp=rightComp;
    }
    
    // Function just for testing until Aidan is done
    public void drawBoard(){
        for(int row=0; row<=7; row++){
            System.out.print(row+" ¶");
            for(int col=0; col<=7; col++){
                System.out.print(board[row][col].getPiece()+" ");
            }
            System.out.println();
        }
        System.out.println("  +---------------");
        System.out.println("   0 1 2 3 4 5 6 7");
    }
    
    // Evaluate the current position taking into account a variety of things
    public double evaluate(){
        double score=0;
        for(int row=0; row<8; row++){ // Scan board
            for(int col=0; col<8; col++){
                if(board[row][col].getPiece() == 1){ // Pawns
                    score+=board[row][col].getOwner();
                }
                else if(board[row][col].getPiece() == 2){ // Rooks
                    score+=5*board[row][col].getOwner();
                }
                else if(board[row][col].getPiece() == 3){ // Knights
                    score+=3*board[row][col].getOwner();
                }
                else if(board[row][col].getPiece() == 4){ // Bishops
                    score+=3.25*board[row][col].getOwner();
                }
                else if(board[row][col].getPiece() == 5){ // Queens
                    score+=9*board[row][col].getOwner();
                }
                else if(board[row][col].getPiece() == 6){ // Stalemate and Checkmate
                    if(possibleMoves(board[row][col].getOwner()).isEmpty()){
                        if(!threatCount(row, col, board[row][col].getOwner()).isEmpty()){
                            return -board[row][col].getOwner()*10000;
                        }
                        else{
                            return 0;
                        }
                    }
                }
            }
        }
        for(int row=3; row<5; row++){ // Control of center
            for(int col=2; col<6; col++){
                score-=.1*threatCount(row, col, 1).size();
                score+=.1*threatCount(row, col, -1).size();
            }
        }
 
        return score;
    }
    
    // AI algorithm that uses Minimax and alpha-beta pruning
    public double minimax(int user, double alpha, double beta, int ply){
        if(ply>3){ // Base case
            return evaluate();
        }
        ArrayList<Move> possibleMoves = possibleMoves(user);
        if(possibleMoves.isEmpty()){ // If there is an end condition
            double evaluate=evaluate();
            if(evaluate>0){
                return evaluate-ply;
            }
            else if(evaluate<0){
                return evaluate+ply;
            }
            return evaluate;
        }
        Tile[][] grid = new Tile[8][8];
        int index=0;
        double value, candidate;
        boolean leftUser=leftCastleUser, kingUser=kingCastleUser, rightUser=rightCastleUser
              , leftComp=leftCastleComp, kingComp=kingCastleComp, rightComp=rightCastleComp;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                grid[i][j] = new Tile(board[i][j].getPiece(), board[i][j].getOwner());
            }
        }
        if(user==1){
            value=-10000.0;
            for(int i=0; i<possibleMoves.size(); i++){
                move(possibleMoves.get(i), user, true);
                value=Math.max(value, minimax(-user, alpha, beta, ply+1));
                alpha=Math.max(value, alpha);
                for(int row=0; row<8; row++){
                    for(int col=0; col<8; col++){
                        board[row][col].setPiece(grid[row][col].getPiece());
                        board[row][col].setOwner(grid[row][col].getOwner());
                    }
                }
                leftCastleUser=leftUser;
                kingCastleUser=kingUser;
                rightCastleUser=rightUser;
                leftCastleComp=leftComp;
                kingCastleComp=kingComp;
                rightCastleComp=rightComp;
                if(beta<=alpha){
                    return value;
                }
            }
        }
        else{
            value=10000.0;
            for(int i=0; i<possibleMoves.size(); i++){
                move(possibleMoves.get(i), user, true);
                candidate = minimax(-user, alpha, beta, ply+1);
                if(value>candidate){
                    index=i;
                    value=candidate;
                }
                beta=Math.min(value, beta);
                for(int row=0; row<8; row++){
                    for(int col=0; col<8; col++){
                        board[row][col].setPiece(grid[row][col].getPiece());
                        board[row][col].setOwner(grid[row][col].getOwner());
                    }
                }
                leftCastleUser=leftUser;
                kingCastleUser=kingUser;
                rightCastleUser=rightUser;
                leftCastleComp=leftComp;
                kingCastleComp=kingComp;
                rightCastleComp=rightComp;
                if(beta<=alpha){
                    if(ply==0){
                        move(possibleMoves.get(index), -1, true);
                        return value;
                    }
                    return value;
                }
            }
        }
        if(ply==0){
            move(possibleMoves.get(index), -1, true);
            return value;
        }
        return value;
    }
    
    
    // Creates a new board given a legal move
    public boolean move(Move move, int user, boolean minimax){
        ArrayList<Move> possibleMoves = possibleMoves(user);
        for(Move x : possibleMoves){ // Search through possible moves
            if(minimax || (x.fromRow==move.fromRow && x.fromCol==move.fromCol && x.toRow==move.toRow && x.toCol==move.toCol)){ // Check if it is a match
                if(board[x.fromRow][x.fromCol].getPiece() == 6 && ((x.fromRow==7 || x.fromRow==0) && x.fromCol==4)){ // Check if it is a castling
                    if(x.toCol==2){
                        board[x.fromRow][x.fromCol].setPiece(0);
                        board[x.fromRow][x.fromCol].setOwner(0);
                        board[x.toRow][x.toCol].setPiece(6);
                        board[x.toRow][x.toCol].setOwner(user);
                        board[x.fromRow][0].setPiece(0);
                        board[x.fromRow][0].setOwner(0);
                        board[x.toRow][3].setPiece(2);
                        board[x.toRow][3].setOwner(user);
                        if(user==1){
                            kingCastleUser=false; // Remove the castling right
                        }
                        else{
                            kingCastleComp=false; // Remove the castling right
                        }
                        return true;
                    }
                    if(x.toCol==6){
                        
                        board[x.fromRow][x.fromCol].setPiece(0);
                        board[x.fromRow][x.fromCol].setOwner(0);
                        board[x.toRow][x.toCol].setPiece(6);
                        board[x.toRow][x.toCol].setOwner(user);
                        board[x.fromRow][7].setPiece(0);
                        board[x.fromRow][7].setOwner(0);
                        board[x.toRow][5].setPiece(2);
                        board[x.toRow][5].setOwner(user);
                        if(user==1){
                            kingCastleUser=false; // Remove the castling right
                        }
                        else{
                            kingCastleComp=false; // Remove the castling right
                        }
                        return true;
                    }
                }
                int fromRow=move.fromRow, fromCol=move.fromCol, toRow=move.toRow, toCol=move.toCol;
                if(fromRow==7 && user==1){ // Remove castling rights
                    if(fromCol==0){
                        leftCastleUser=false;
                    }
                    else if(fromCol==4){
                        kingCastleUser=false;
                    }
                    else if(fromCol==7){
                        rightCastleUser=false;
                    }
                }
                else if(fromRow==0 && user==-1){ // Remove castling rights
                    if(fromCol==0){
                        leftCastleComp=false;
                    }
                    else if(fromCol==4){
                        kingCastleComp=false;
                    }
                    else if(fromCol==7){
                        rightCastleComp=false;
                    }
                }
                board[toRow][toCol].setPiece(board[fromRow][fromCol].getPiece());
                board[toRow][toCol].setOwner(user);
                board[move.fromRow][move.fromCol].setPiece(0);
                board[move.fromRow][move.fromCol].setOwner(0);
                return true;
            }
        }
        return false;
    }
    
    // Return what tiles are attacking the king
    public ArrayList<Point> threatCount(int row, int col, int user){
        ArrayList<Point> tiles = new ArrayList<>();

                    // Check if there is an opposing king adjacent
                    if(row>0){ // The row above
                        if(board[row-1][col].getPiece() == 6 && board[row-1][col].getOwner() == -user){ 
                            tiles.add(new Point(row-1, col));
                            //return tiles;
                        }
                        if(col>0 && board[row-1][col-1].getPiece() == 6 && board[row-1][col-1].getOwner() == -user){
                            tiles.add(new Point(row-1, col-1));
                            //return tiles;
                        }
                        if(col<7 && board[row-1][col+1].getPiece() == 6 && board[row-1][col+1].getOwner() == -user){
                            tiles.add(new Point(row-1, col+1));
                            //return tiles;
                        }
                    }
                    if(col>0 && board[row][col-1].getPiece() == 6 && board[row][col-1].getOwner() == -user){ // The same row
                        tiles.add(new Point(row, col-1));
                        //return tiles;
                    }
                    if(col<7 && board[row][col+1].getPiece() == 6 && board[row][col+1].getOwner() == -user){ // The same row
                        tiles.add(new Point(row, col+1));
                        //return tiles;
                    }
                    if(row<7){ // The row below
                        if(board[row+1][col].getPiece() == 6 && board[row+1][col].getOwner() == -user){
                            tiles.add(new Point(row+1, col));
                            //return tiles;
                        }
                        if(col>0 && board[row+1][col-1].getPiece() == 6 && board[row+1][col-1].getOwner() == -user){
                            tiles.add(new Point(row+1, col-1));
                            //return tiles;
                        }
                        if(col<7 && board[row+1][col+1].getPiece() == 6 && board[row+1][col+1].getOwner() == -user){
                            tiles.add(new Point(row+1, col+1));
                            //return tiles;
                        }
                    }
                    // Check for pawns
                    if(row-user>=0 && row-user<8){ // Board constraints
                        
                        if(col>0 && board[row-user][col-1].getPiece() == 1 && board[row-user][col-1].getOwner() == -user){
                            tiles.add(new Point(row-user, col-1));
                        }
                        if(col<7 && board[row-user][col+1].getPiece() == 1 && board[row-user][col+1].getOwner() == -user){
                            tiles.add(new Point(row-user, col+1));
                        }
                        
                    }
                    // Check for rooks or queens up the file
                    //if(!rookCheck)
                    for(int i=1; row-i>=0; i++){
                        if(board[row-i][col].getOwner() != 0){
                            if(board[row-i][col].getOwner() == -user && (board[row-i][col].getPiece() == 2 || board[row-i][col].getPiece() == 5)){
                                tiles.add(new Point(row-i,col));
                            }
                            break;
                        }
                    }
                    // Check for rooks or queens down the file
                    //else if(!rookCheck)
                    for(int i=1; row+i<8; i++){
                        if(board[row+i][col].getOwner() != 0){
                            if(board[row+i][col].getOwner() == -user && (board[row+i][col].getPiece() == 2 || board[row+i][col].getPiece() == 5)){
                                tiles.add(new Point(row+i,col));
                            }
                            break;
                        }
                    }
                    // Check for rooks or queens left on the row
                    //else if(!rookCheck)
                    for(int i=1; col-i>=0; i++){
                        if(board[row][col-i].getOwner() != 0){
                            if(board[row][col-i].getOwner() == -user && (board[row][col-i].getPiece() == 2 || board[row][col-i].getPiece() == 5)){
                                tiles.add(new Point(row,col-i));
                            }
                            break;
                        }
                    }
                    // Check for rooks or queens right on the row
                    //else if(!rookCheck)
                    for(int i=1; col+i<8; i++){
                        
                        if(board[row][col+i].getOwner() != 0){
                            if(board[row][col+i].getOwner() == -user && (board[row][col+i].getPiece() == 2 || board[row][col+i].getPiece() == 5)){
                                tiles.add(new Point(row,col+i));
                            }
                            break;
                        }
                    }
                    // Check for bishops and queens left and up
                    //if(!bishopCheck)
                    for(int i=1; row-i>=0 && col-i>=0; i++){
                        if(board[row-i][col-i].getOwner() != 0){
                            if(board[row-i][col-i].getOwner() == -user && (board[row-i][col-i].getPiece() == 4 || board[row-i][col-i].getPiece() == 5)){
                                tiles.add(new Point(row-i,col-i));
                            }
                            break;
                        }
                    }
                    // Check for bishops and queens left and down
                    //else if(!bishopCheck)
                    for(int i=1; row+i<8 && col-i>=0; i++){
                        if(board[row+i][col-i].getOwner() != 0){
                            if(board[row+i][col-i].getOwner() == -user && (board[row+i][col-i].getPiece() == 4 || board[row+i][col-i].getPiece() == 5)){
                                tiles.add(new Point(row+i,col-i));
                            }
                            break;
                        }
                    }
                    // Check for bishops and queens right and up
                    //else if(!bishopCheck)
                    for(int i=1; row-i>=0 && col+i<8; i++){
                        if(board[row-i][col+i].getOwner() != 0){
                            if(board[row-i][col+i].getOwner() == -user && (board[row-i][col+i].getPiece() == 4 || board[row-i][col+i].getPiece() == 5)){
                                tiles.add(new Point(row-i,col+i));
                            }
                            break;
                        }
                    }
                    // Check for bishops and queens right and down
                    //else if(!bishopCheck)
                    for(int i=1; row+i<8 && col+i<8; i++){
                        if(board[row+i][col+i].getOwner() != 0){
                            if(board[row+i][col+i].getOwner() == -user && (board[row+i][col+i].getPiece() == 4 || board[row+i][col+i].getPiece() == 5)){
                                tiles.add(new Point(row+i,col+i));
                            }
                            break;
                        }
                    }
                    // Check for knights
                    if(col>0){ // Check if the king has board to its immediate left
                        if(row>1 && board[row-2][col-1].getPiece() == 3 && board[row-2][col-1].getOwner() == -user){ // Check if there is room to move up
                            tiles.add(new Point(row-2, col-1));
                        }
                        if(row<6 && board[row+2][col-1].getPiece() ==3 && board[row+2][col-1].getOwner() == -user){ // Check if there is room to move down
                            tiles.add(new Point(row+2, col-1));
                        }                        
                        if(col>1){ // Check if there is room to move two spaces left
                            if(row>0 && board[row-1][col-2].getPiece() == 3 && board[row-1][col-2].getOwner() == -user){ // Check if there is room to move up
                                tiles.add(new Point(row-1, col-2));
                            }
                            if(row<7 && board[row+1][col-2].getPiece() == 3 && board[row+1][col-2].getOwner() == -user){ // Check if there is room to move down
                                tiles.add(new Point(row+1, col-2));
                            }
                        }
                    }
                    if(col<7){ // Check if the king has board to its immediate right
                        if(row>1 && board[row-2][col+1].getPiece() == 3 && board[row-2][col+1].getOwner() == -user){ // Check if there is room to move up
                            tiles.add(new Point(row-2, col+1));
                        }
                        if(row<6 && board[row+2][col+1].getPiece() == 3 && board[row+2][col+1].getOwner() == -user){ // Check if there is room to move down
                            tiles.add(new Point(row+2, col+1));
                        }                        
                        if(col<6){ // Check if there is room to move two spaces left
                            if(row>0 && board[row-1][col+2].getPiece() == 3 && board[row-1][col+2].getOwner() == -user){ // Check if there is room to move up
                                tiles.add(new Point(row-1, col+2));
                            }
                            if(row<7 && board[row+1][col+2].getPiece() == 3 && board[row+1][col+2].getOwner() == -user){ // Check if there is room to move down
                                tiles.add(new Point(row+1, col+2));
                            }
                        }
                    }
                    
                    return tiles;
    }
    
    // Checks if a move leaves the king in Check
    public boolean checkMove(int kingRow, int kingCol, Move move){
        boolean inCheck;
        int fromPiece = board[move.fromRow][move.fromCol].getPiece(), fromOwner = board[move.fromRow][move.fromCol].getOwner();
        int toPiece = board[move.toRow][move.toCol].getPiece(), toOwner = board[move.toRow][move.toCol].getOwner();
        board[move.fromRow][move.fromCol].setPiece(0);
        board[move.fromRow][move.fromCol].setOwner(0);
        board[move.toRow][move.toCol].setPiece(fromPiece);
        board[move.toRow][move.toCol].setOwner(fromOwner);
        inCheck = !threatCount(kingRow, kingCol, fromOwner).isEmpty();
        board[move.fromRow][move.fromCol].setPiece(fromPiece);
        board[move.fromRow][move.fromCol].setOwner(fromOwner);
        board[move.toRow][move.toCol].setPiece(toPiece);
        board[move.toRow][move.toCol].setOwner(toOwner);
        return inCheck;
    }
    
    // Create a list of legal moves
    public ArrayList<Move> possibleMoves(int user){
        ArrayList<Move> possibleMoves = new ArrayList();
        ArrayList<Point> threats = new ArrayList();
        int kingRow=0, kingCol=0;
        int piece=0, owner=0;
        // Restrictions involving king
        outerloop:
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                if(board[row][col].getPiece() == 6 && board[row][col].getOwner() == user){ // Find king
                    threats = threatCount(row, col, user);
                    kingRow=row;
                    kingCol=col;
                    if(threats.isEmpty()){
                        if(kingCastleUser==true && user==1){
                            if(leftCastleUser==true){
                                if(board[7][3].getOwner()==0 && board[7][2].getOwner()==0 && !checkMove(7,2, new Move(kingRow,kingCol,7,2)) && !checkMove(7,3, new Move(kingRow,kingCol,7,3))){
                                    possibleMoves.add(new Move(7,4,7,2));
                                }
                            }
                            if(rightCastleUser==true){
                                if(board[7][5].getOwner()==0 && board[7][6].getOwner()==0 && !checkMove(7,5, new Move(kingRow,kingCol,7,5)) && !checkMove(7,6, new Move(kingRow,kingCol,7,6))){
                                    possibleMoves.add(new Move(7,4,7,6));
                                }
                            }
                        }
                        else if(kingCastleComp=true && user ==-1){
                            if(leftCastleComp==true){
                                if(board[0][3].getOwner()==0 && board[0][2].getOwner()==0 && !checkMove(0,2, new Move(kingRow,kingCol,0,2)) && !checkMove(0,3, new Move(kingRow,kingCol,0,3))){
                                    possibleMoves.add(new Move(0,4,0,2));
                                }
                            }
                            if(rightCastleComp==true){
                                if(board[0][5].getOwner()==0 && board[0][6].getOwner()==0 && !checkMove(0,5, new Move(kingRow,kingCol,0,5)) && !checkMove(0,6, new Move(kingRow,kingCol,0,6))){
                                    possibleMoves.add(new Move(0,4,0,6));
                                }
                            }
                        }
                    }
                    if(row>0){ // Check if the king can move up
                        if(col>0){
                            if(board[row-1][col-1].getOwner() != user){
                                if(!checkMove(kingRow-1, kingCol-1, new Move(kingRow, kingCol, kingRow-1, kingCol-1))){
                                    possibleMoves.add(new Move(kingRow, kingCol, kingRow-1, kingCol-1));
                                }
                            }
                        }
                        if(board[row-1][col].getOwner() != user){
                                if(!checkMove(kingRow-1, kingCol, new Move(kingRow, kingCol, kingRow-1, kingCol))){
                                    possibleMoves.add(new Move(kingRow, kingCol, kingRow-1, kingCol));
                                }
                        }
                        if(col<7){
                            if(board[row-1][col+1].getOwner() != user){
                                if(!checkMove(kingRow-1, kingCol+1, new Move(kingRow, kingCol, kingRow-1, kingCol+1))){
                                    possibleMoves.add(new Move(kingRow, kingCol, kingRow-1, kingCol+1));
                                }
                            }
                        }
                    }
                    if(col<7){
                        if(board[row][col+1].getOwner() != user){
                            if(!checkMove(kingRow, kingCol+1, new Move(kingRow, kingCol, kingRow, kingCol+1))){
                                possibleMoves.add(new Move(kingRow, kingCol, kingRow, kingCol+1));
                            }
                        }
                    }
                    if(col>0){
                        if(board[row][col-1].getOwner() != user){
                            if(!checkMove(kingRow, kingCol-1, new Move(kingRow, kingCol, kingRow, kingCol-1))){
                                possibleMoves.add(new Move(kingRow, kingCol, kingRow, kingCol-1));
                            }
                        }
                    }
                    if(row<7){ // Check if the king can move up
                        if(col>0){
                            if(board[row+1][col-1].getOwner() != user){
                                if(!checkMove(kingRow+1, kingCol-1, new Move(kingRow, kingCol, kingRow+1, kingCol-1))){
                                    possibleMoves.add(new Move(kingRow, kingCol, kingRow+1, kingCol-1));
                                }
                            }
                        }
                        if(board[row+1][col].getOwner() != user){
                            if(!checkMove(kingRow+1, kingCol, new Move(kingRow, kingCol, kingRow+1, kingCol))){
                                possibleMoves.add(new Move(kingRow, kingCol, kingRow+1, kingCol));
                            }
                        }
                        
                    
                        if(col<7){
                            if(board[row+1][col+1].getOwner() != user){
                                if(!checkMove(kingRow+1, kingCol+1, new Move(kingRow, kingCol, kingRow+1, kingCol+1))){
                                    possibleMoves.add(new Move(kingRow, kingCol, kingRow+1, kingCol+1));
                                }
                            }
                        }
                    }
                    break outerloop;
                }
            }
        }
        if(threats.size()>1){
            return possibleMoves;
        }
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                // Find pawn moves (sans en passant)
                if(row-user>=0 && row-user<8 && board[row][col].getPiece() == 1 && board[row][col].getOwner() == user){  // Search for pawns 
                   if(board[row-user][col].getOwner() == 0){ // Check if piece is in front of pawn
                        if(!checkMove(kingRow, kingCol, new Move(row, col, row-user, col))){
                            possibleMoves.add(new Move(row, col, row-user, col));
                        }
                        if((row==1 && user==-1) || (row==6 && user==1)){ // Check if pawn is able to move two squares
                            if(board[row-2*user][col].getOwner() == 0 && !checkMove(kingRow, kingCol, new Move(row, col, row-2*user, col))){ //Check if second square is open
                                possibleMoves.add(new Move(row, col, row-2*user, col));
                            }
                            
                        }
                    }
                   if(col>0 && board[row-user][col-1].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-user, col-1))){ // Check if pawn can capture forward diagonally
                       possibleMoves.add(new Move(row, col, row-user, col-1));
                   }
                   if(col<7 && board[row-user][col+1].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-user, col+1))){ // Check if pawn can capture forward diagonally
                       possibleMoves.add(new Move(row, col, row-user, col+1));
                   }
                }
                
                // Find knight moves
                else if(board[row][col].getPiece() == 3 && board[row][col].getOwner() == user){ // Search for knights
                    if(col>0){ // Check if the knight has board to its immediate left
                        if(row>1 && board[row-2][col-1].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row-2, col-1))){ // Check if there is room to move up
                            possibleMoves.add(new Move(row, col, row-2, col-1));
                        }
                        if(row<6 && board[row+2][col-1].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row+2, col-1))){ // Check if there is room to move down
                            possibleMoves.add(new Move(row, col, row+2, col-1));
                        }                        
                        if(col>1){ // Check if there is room to move two spaces left
                            if(row>0 && board[row-1][col-2].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row-1, col-2))){ // Check if there is room to move up
                                possibleMoves.add(new Move(row, col, row-1, col-2));
                            }
                            if(row<7 && board[row+1][col-2].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row+1, col-2))){ // Check if there is room to move down
                                possibleMoves.add(new Move(row, col, row+1, col-2));
                            }
                        }
                    }
                    if(col<7){ // Check if the knight has board to its immediate right
                        if(row>1 && board[row-2][col+1].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row-2, col+1))){ // Check if there is room to move up
                            possibleMoves.add(new Move(row, col, row-2, col+1));
                        }
                        if(row<6 && board[row+2][col+1].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row+2, col+1))){ // Check if there is room to move down
                            possibleMoves.add(new Move(row, col, row+2, col+1));
                        }                        
                        if(col<6){ // Check if there is room to move two spaces left
                            if(row>0 && board[row-1][col+2].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row-1, col+2))){ // Check if there is room to move up
                                possibleMoves.add(new Move(row, col, row-1, col+2));
                            }
                            if(row<7 && board[row+1][col+2].getOwner() != user && !checkMove(kingRow, kingCol, new Move(row, col, row+1, col+2))){ // Check if there is room to move down
                                possibleMoves.add(new Move(row, col, row+1, col+2));
                            }
                        }
                    }
                }
                
                // Find rook moves
                else if(board[row][col].getPiece() == 2 && board[row][col].getOwner() == user){ // Search for rooks
                    for(int i=1; row-i>=0; i++){ // Search up the column from the piece's spot
                        if(board[row-i][col].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col)))
                                possibleMoves.add(new Move(row, col, row-i, col));
                        }
                        else{ // If it isn't...
                            if(board[row-i][col].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row-i, col));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; row+i<8; i++){ // Search down the column from the piece's spot
                        if(board[row+i][col].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col)))
                                possibleMoves.add(new Move(row, col, row+i, col));
                        }
                        else{ // If it isn't...
                            if(board[row+i][col].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row+i, col));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; col-i>=0; i++){ // Search the row left from a piece's spot
                        if(board[row][col-i].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row, col-i))){
                                possibleMoves.add(new Move(row, col, row, col-i));
                            }
                        }
                        else{ // If it isn't...
                            if(board[row][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row, col-i))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row, col-i));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; col+i<8; i++){ // Search the row right from a piece's spot
                        if(board[row][col+i].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row, col+i))){    
                                possibleMoves.add(new Move(row, col, row, col+i));
                            }
                        }
                        else{ // If it isn't...
                            if(board[row][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row, col+i))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row, col+i));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                }
                
                // Bishop moves
                else if(board[row][col].getPiece() == 4 && board[row][col].getOwner() == user){ // Search for bishops
                    for(int i=1; row-i>=0 && col-i>=0; i++){ // Search up and to the left
                        if(board[row-i][col-i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col-i))){
                                possibleMoves.add(new Move(row, col, row-i, col-i));
                            }
                        }
                        else{ // If it isn't
                            if(board[row-i][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col-i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row-i, col-i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row-i>=0 && col+i<8; i++){ // Search up and to the right
                        if(board[row-i][col+i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col+i))){
                                possibleMoves.add(new Move(row, col, row-i, col+i));
                            }
                        }
                        else{ // If it isn't
                            if(board[row-i][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col+i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row-i, col+i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row+i<8 && col-i>=0; i++){ // Search down and to the left
                        if(board[row+i][col-i].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col-i))){
                                possibleMoves.add(new Move(row, col, row+i, col-i));
                            }
                        }
                        else{ // If it isn't
                            if(board[row+i][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col-i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row+i, col-i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row+i<8 && col+i<8; i++){ // Search down and to the right
                        if(board[row+i][col+i].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col+i))){
                                possibleMoves.add(new Move(row, col, row+i, col+i));
                            }
                        }
                        else{ // If it isn't
                            if(board[row+i][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col+i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row+i, col+i));
                            }
                            break; // Don't search more
                        }
                    }
                }
                // Queen moves
                else if(board[row][col].getPiece() == 5 && board[row][col].getOwner() == user){ // Search for queens
                    for(int i=1; row-i>=0; i++){ // Search up the column from the piece's spot
                        if(board[row-i][col].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col)))
                                possibleMoves.add(new Move(row, col, row-i, col));
                        }
                        else{ // If it isn't...
                            if(board[row-i][col].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row-i, col));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; row+i<8; i++){ // Search down the column from the piece's spot
                        if(board[row+i][col].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col)))
                                possibleMoves.add(new Move(row, col, row+i, col));
                        }
                        else{ // If it isn't...
                            if(board[row+i][col].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row+i, col));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; col-i>=0; i++){ // Search the row left from a piece's spot
                        if(board[row][col-i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row, col-i)))
                                possibleMoves.add(new Move(row, col, row, col-i));
                        }
                        else{ // If it isn't...
                            if(board[row][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row, col-i))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row, col-i));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; col+i<8; i++){ // Search the row right from a piece's spot
                        if(board[row][col+i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row, col+i)))
                                possibleMoves.add(new Move(row, col, row, col+i));
                        }
                        else{ // If it isn't...
                            if(board[row][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row, col+i))){ // If it is an opponent's you can capture, so add it
                                possibleMoves.add(new Move(row, col, row, col+i));
                            }
                            break; // Don't search more along this direction
                        }
                    }
                    for(int i=1; row-i>=0 && col-i>=0; i++){ // Search up and to the left
                        if(board[row-i][col-i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col-i)))
                                possibleMoves.add(new Move(row, col, row-i, col-i));
                        }
                        else{ // If it isn't
                            if(board[row-i][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col-i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row-i, col-i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row-i>=0 && col+i<8; i++){ // Search up and to the right
                        if(board[row-i][col+i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row-i, col+i)))
                                possibleMoves.add(new Move(row, col, row-i, col+i));
                        }
                        else{ // If it isn't
                            if(board[row-i][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row-i, col+i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row-i, col+i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row+i<8 && col-i>=0; i++){ // Search downn and to the left
                        if(board[row+i][col-i].getOwner() == 0 ){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col-i)))
                                possibleMoves.add(new Move(row, col, row+i, col-i));
                        }
                        else{ // If it isn't
                            if(board[row+i][col-i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col-i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row+i, col-i));
                            }
                            break; // Don't search more
                        }
                    }
                    for(int i=1; row+i<8 && col+i<8; i++){ // Search down and to the right
                        if(board[row+i][col+i].getOwner() == 0){ // Check if it is empty
                            if(!checkMove(kingRow, kingCol, new Move(row, col, row+i, col+i)))
                                possibleMoves.add(new Move(row, col, row+i, col+i));
                        }
                        else{ // If it isn't
                            if(board[row+i][col+i].getOwner() == -user && !checkMove(kingRow, kingCol, new Move(row, col, row+i, col+i))){ // If it is an opponent's capture it
                                possibleMoves.add(new Move(row, col, row+i, col+i));
                            }
                            break; // Don't search more
                        }
                    }
                }
                // King moves
                
            }
        }
        
        return possibleMoves;
    }
}
