package jp.unaguna.fmtbuilder;

interface DataFormatPart {
    void format(StringBuilder stringBuilder, ValueProvider valueProvider, Integer width);
    String variableName();
}
