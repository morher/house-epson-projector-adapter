package net.morher.house.epson.api;

public class ResponseData {
    private final String value;
    private final int offset;

    public ResponseData(String value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    public ResponseData prefixedValue(String prefix) {
        if (!toString().startsWith(prefix)) {
            throw new IllegalStateException("Invalid response to command: " + value);
        }
        return new ResponseData(value, offset + prefix.length());
    }

    @Override
    public String toString() {
        return value.substring(offset);
    }

    public Integer asInteger() {
        return Integer.valueOf(toString());
    }
}
