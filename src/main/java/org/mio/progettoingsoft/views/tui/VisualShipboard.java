package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.ShipBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class VisualShipboard {
    protected int rows ;
    protected int cols ;
    protected List<Optional<Component>> components;
    protected List<Optional<ShipCell>> shipCells;
    public VisualShipboard(List<Optional<Component>> components) {
        this.components = components;
        this.shipCells = new ArrayList<>();
        for (Optional<Component> component : components) {
            if (component.isPresent()) {
                ShipCell cell = new ShipCell(component.get());
                cell.computeCell();
                this.shipCells.add(Optional.of(cell));
            }
            else {
                this.shipCells.add(Optional.empty());
            }
        }
    }
    public void drawShipboard() {
        int cellHeight = 5;
        int cellWidth = 9;
        int offrow = 5;
        int offcol = cols == 5 ? 5 : 4;

        System.out.print("    ");
        for (int col = offcol; col < cols + offcol; col++) {
            String colStr = String.valueOf(col);
            int padLeft = (cellWidth - colStr.length()) / 2;
            int padRight = cellWidth - colStr.length() - padLeft;
            System.out.print(" ".repeat(padLeft) + colStr + " ".repeat(padRight));
        }
        System.out.println();

        for (int row = 0; row < rows; row++) {
            for (int line = 0; line < cellHeight; line++) {
                if (line == cellHeight / 2) {
                    int disp = row+offrow;
                    System.out.printf("%-3d ", disp);
                } else {
                    System.out.print("    ");
                }

                for (int col = 0; col < cols; col++) {
                    int index = row * cols + col;
                    if (index < shipCells.size() && shipCells.get(index).isPresent()) {
                        ColoredChar[][] matrix = shipCells.get(index).get().getMatrix();
                        for (int j = 0; j < cellWidth; j++) {
                            System.out.print(matrix[line][j]);
                        }
                    } else {
                        System.out.print(" ".repeat(cellWidth));
                    }
                }
                System.out.println();
            }
        }
    }

}

