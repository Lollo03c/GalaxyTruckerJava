package org.mio.progettoingsoft.views.tui;

public class CircuitCell {
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    private int row = 3;
    private int col = 5;
    private ColoredChar[][] cell = new ColoredChar[row][col];
    public CircuitCell() {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                cell[i][j] = new ColoredChar(' ');
        this.drawBorders();
    }
    public CircuitCell(String color, char c) {
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                cell[i][j] = new ColoredChar(' ',color);
            }
        }
        switch(c){
            case 't' : cell[1][2].setChar('▷');
            break;
            case 'l' : cell[1][2].setChar('△');
            break;
            case 'r' : cell[1][2].setChar('▽');
            break;
            case 'b' : cell[1][2].setChar('◁');
            break;
        }
        this.drawBorders();
    }
    private void drawBorders(){
        cell[0][0].setChar('╔');
        cell[0][4].setChar('╗');
        cell[2][0].setChar('╚');
        cell[2][4].setChar('╝');
        cell[1][0].setChar('║');
        cell[1][4].setChar('║');
        cell[0][1].setChar('═');
        cell[2][1].setChar('═');
        cell[0][2].setChar('═');
        cell[0][3].setChar('═');
        cell[2][2].setChar('═');
        cell[2][3].setChar('═');
    }
    public CircuitCell modifyFlag(char c){
        cell[2][2].setChar(c);
        return this;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ColoredChar[] row : cell) {
            for (ColoredChar cc : row) {
                if (cc != null) {
                    sb.append(cc);
                } else {
                    sb.append(" ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    public String getRow(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < col; j++) {
            sb.append(cell[i][j]);
        }
        return sb.toString();
    }


    public static void main(String[] args){
        CircuitCell cc = new CircuitCell(BLUE,'t');
        System.out.println(cc);
    }
}
