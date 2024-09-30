public interface RequestBinConst {
    public static final String DEFAULT_ENCODING = "UTF-16";
    public static final int SINGLE_FLAG = 1 << 0; // Least significant bit
    public static final int RICH_FLAG   = 1 << 1; // weight 2^1
    public static final int FEMALE_FLAG = 1 << 2; // weight 2^2
    public static final int MAX_OPNAME_LENGTH = 255; // Max length op name
    public static final int MAX_WIRE_LENGTH  = 1024; // Max length on the "wire"
}
