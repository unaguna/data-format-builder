package jp.unaguna.fmtbuilder.buffered;

import jp.unaguna.fmtbuilder.ValueProviderAdapter;

import java.util.LinkedList;

public interface BufferEditor<T> {
    void edit(LinkedList<T> buffer, ValueProviderAdapter<T> adapter);
}
