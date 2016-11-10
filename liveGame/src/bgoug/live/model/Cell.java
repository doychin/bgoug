package bgoug.live.model;

/**
 * Class representing single cell
 */
public class Cell {
    private boolean live;
    private boolean nextState;
    private int x;
    private int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public void setNextState(boolean nextState) {
        this.nextState = nextState;
    }

    /**
     * Assign new state to cell
     */
    public void apply() {
        setLive(nextState);
    }
}
