package jp.unaguna.fmtbuilder;

import java.util.*;

public class TableDataFormatIterator<T> implements Iterator<String> {
    private int blockSize = Integer.MAX_VALUE;
    private final DataFormat baseDataFormat;
    private final ValueProviderAdapter<T> adapter;
    private final Iterator<T> dataIterator;
    private final List<T> dataBuffer = new LinkedList<>();
    private final TableFieldHolder widthProvider = new TableFieldHolder();

    public TableDataFormatIterator(
            final DataFormat baseDataFormat, final Iterator<T> dataIterator, final ValueProviderAdapter<T> adapter) {

        this.baseDataFormat = baseDataFormat;
        this.dataIterator = dataIterator;
        this.adapter = adapter;
    }

    public void setBlockSize(final int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    /** イテレータから次のブロックを取得 */
    private void loadNextBlock() {
        if (!dataBuffer.isEmpty()) {
            throw new IllegalStateException("cannot load next data block; buffer is not empty");
        }

        widthProvider.clear();

        while (dataIterator.hasNext() && dataBuffer.size() < blockSize) {
            final T nextData = dataIterator.next();
            synchronized (adapter) {
                adapter.setElement(nextData);
                for (final String variableName : baseDataFormat.getVariableNames()) {
                    final String value = adapter.get(variableName).toString();
                    widthProvider.updateWidth(variableName, value.length());
                }
            }
            dataBuffer.add(nextData);
        }
    }

    @Override
    public boolean hasNext() {
        return !dataBuffer.isEmpty() || dataIterator.hasNext();
    }

    @Override
    public String next() {
        final StringBuilder builder = new StringBuilder();
        this.nextFormat(builder);
        return builder.toString();
    }

    public void nextFormat(final StringBuilder builder) {
        if (dataBuffer.isEmpty()) {
            loadNextBlock();
        }

        // error if buffer is empty even if after loadNextBlock
        if (dataBuffer.isEmpty()) {
            throw new NoSuchElementException();
        }

        final T nextData = dataBuffer.remove(0);
        synchronized(adapter) {
            adapter.setElement(nextData);
            baseDataFormat.format(adapter, widthProvider, builder);
        }
    }

    private static class TableFieldHolder implements FieldWidthProvider {
        private final Map<String, Integer> width = new HashMap<>();

        public void clear() {
            width.clear();
        }

        public void updateWidth(String fieldName, int minimum) {
            if (width.getOrDefault(fieldName, 0) < minimum) {
                width.put(fieldName, minimum);
            }
        }

        @Override
        public Integer getWidth(String fieldName) {
            return width.get(fieldName);
        }
    }
}
