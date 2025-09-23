package jp.unaguna.fmtbuilder.buffered;

import jp.unaguna.fmtbuilder.DataFormat;
import jp.unaguna.fmtbuilder.FieldWidthProvider;
import jp.unaguna.fmtbuilder.ValueProviderAdapter;

import java.util.*;

/**
 * The iterator of formatted data line.
 *
 * <p>
 * This iterator buffers the data.
 * Then, it implements features requiring buffering, such as value padding.
 * </p>
 *
 * <p>
 * This object returns a formatted string one line at a time as an iterator.
 * Instead of {@link #next()} that returns a string,
 * you can also use {@link #nextFormat(StringBuilder)}, which appends the formatted string to the StringBuilder.
 * </p>
 *
 * @param <T> Data equivalent to one line
 */
public class BufferedDataFormatIterator<T> implements Iterator<String> {
    private int blockSize = Integer.MAX_VALUE;
    private final DataFormat baseDataFormat;
    private final ValueProviderAdapter<T> adapter;
    private final Iterator<T> dataIterator;
    private final LinkedList<T> dataBuffer = new LinkedList<>();
    private WidthProviderProvider widthProviderProvider = null;
    private final List<BufferHandler<T>> bufferHandlers = new ArrayList<>();
    private final List<BufferObserver<T>> bufferObservers = new ArrayList<>();

    public BufferedDataFormatIterator(
            final DataFormat baseDataFormat, final Iterator<T> dataIterator, final ValueProviderAdapter<T> adapter) {

        this.baseDataFormat = baseDataFormat;
        this.dataIterator = dataIterator;
        this.adapter = adapter;
    }

    public BufferedDataFormatIterator<T> applyBufferHandler(final BufferHandler<T> bufferHandler) {
        this.bufferHandlers.add(bufferHandler);
        return this;
    }

    public BufferedDataFormatIterator<T> applyBufferObserver(final BufferObserver<T> bufferObserver) {
        this.bufferObservers.add(bufferObserver);
        return this;
    }

    public BufferedDataFormatIterator<T> useDefaultWidthProviderProvider() {
        final DefaultWidthProviderProvider<T> wpp = new DefaultWidthProviderProvider<>(baseDataFormat);
        widthProviderProvider = wpp;
        applyBufferObserver(wpp);

        return this;
    }

    public void setBlockSize(final int blockSize) {
        if (blockSize <= 0) {
            throw new IllegalArgumentException("blockSize must be positive integer");
        }

        this.blockSize = blockSize;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    private void loadNextBlock() {
        if (!dataBuffer.isEmpty()) {
            throw new IllegalStateException("cannot load next data block; buffer is not empty");
        }

        while (dataIterator.hasNext() && dataBuffer.size() < blockSize) {
            final T nextData = dataIterator.next();
            dataBuffer.add(nextData);
        }

        synchronized (adapter) {
            for (final BufferHandler<T> bufferHandler : bufferHandlers) {
                bufferHandler.handle(dataBuffer, adapter);
            }
            for (final BufferObserver<T> bufferObserver : bufferObservers) {
                bufferObserver.observe(Collections.unmodifiableList(dataBuffer), adapter);
            }
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
        final FieldWidthProvider widthProvider = widthProviderProvider != null
                ? widthProviderProvider.widthProvider()
                : null;
        synchronized(adapter) {
            adapter.setElement(nextData);
            baseDataFormat.format(adapter, widthProvider, builder);
        }
    }

    private static class DefaultWidthProviderProvider<T> implements WidthProviderProvider, BufferObserver<T> {
        private final DefaultWidthProvider widthProvider = new DefaultWidthProvider();
        private final DataFormat baseDataFormat;

        DefaultWidthProviderProvider(final DataFormat baseDataFormat) {
            this.baseDataFormat = baseDataFormat;
        }

        @Override
        public FieldWidthProvider widthProvider() {
            return widthProvider;
        }

        @Override
        public void observe(final List<T> buffer, final ValueProviderAdapter<T> adapter) {
            widthProvider.clear();

            for (final T element : buffer) {
                adapter.setElement(element);
                for (final String variableName : baseDataFormat.getVariableNames()) {
                    final String value = adapter.get(variableName).toString();
                    widthProvider.updateWidth(variableName, value.length());
                }
            }
        }
    }

    private static class DefaultWidthProvider implements FieldWidthProvider {
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
