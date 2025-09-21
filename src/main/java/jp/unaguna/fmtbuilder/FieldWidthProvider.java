package jp.unaguna.fmtbuilder;

public interface FieldWidthProvider {
    Integer getWidth(String fieldName);

    FieldWidthProvider empty = fieldName -> null;
}
