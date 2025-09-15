package jp.unaguna.fmtbuilder;

import java.util.Iterator;

public class TableDataFormatIteratorWithoutAdapter<T extends ValueProvider> extends TableDataFormatIterator<T> {
    public TableDataFormatIteratorWithoutAdapter(DataFormat baseDataFormat, Iterator<T> dataIterator) {
        super(baseDataFormat, dataIterator, new ValueProviderAdapter.AsIs<>());
    }
}
