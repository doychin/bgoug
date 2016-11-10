package bgoug.live.model;

@FunctionalInterface
public interface CellSupplier<T extends Cell> {
    T createCell(int x, int y);
}
