/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessengine1;

/**
 *
 * @author Connor Malin
 */
public class Tile {
    private int piece, owner;
    Tile(int piece, int owner){
        this.piece=piece; // 0 for empty square; 1 for pawn; 2 for rook; 3 for knight; 4 for bishop; 5 for queen; 6 for king
        this.owner=owner; // -1 for computer; 0 for empty square; 1 for user
    }
    
    // mutator method
    public void setPiece(int piece){
        this.piece=piece;
    }
     
    // mutator method
    public void setOwner(int owner){
        this.owner=owner;
    }
    
    // accesor method
    public int getPiece(){
        return piece;
    }
    
    // accesor method
    public int getOwner(){
        return owner;
    }
}
 
