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
public class Move {
    public int fromRow, fromCol, toRow, toCol;
    Move(int fromRow, int fromCol, int toRow, int toCol){
        this.fromRow=fromRow;
        this.fromCol=fromCol;
        this.toRow=toRow;
        this.toCol=toCol;
    }
    
    public String toString(){
        return (Integer.toString(fromRow) + Integer.toString(fromCol) + Integer.toString(toRow) + Integer.toString(toCol));
    }
}
