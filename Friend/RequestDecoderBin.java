import java.io.*;  // for ByteArrayInputStream
import java.net.*; // for DatagramPacket

public class RequestDecoderBin implements RequestDecoder, RequestBinConst {

  private String encoding;  // Character encoding

  public RequestDecoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public RequestDecoderBin(String encoding) {
    this.encoding = encoding;
  }

  public Request decode(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    /*
    long  ID            = src.readLong();
    short streetnumber  = src.readShort();
    int   zipcode       = src.readInt();
    byte  flags         = src.readByte();
    */
    int tml = src.readInt();
    short opCode = src.readShort();
    int op1 = src.readInt();
    int op2 = src.readInt();
    int requestID = src.readInt();
    
    // Deal with the operation name
    int opNameLength = src.read(); // Returns an unsigned byte as an int
    if (opNameLength == -1) throw new EOFException();
    byte[] stringBuf = new byte[opNameLength];
    src.readFully(stringBuf);
    String opName = new String(stringBuf, encoding);

    /*
    return new Request(ID,lastname, streetnumber, zipcode,
    ((flags & SINGLE_FLAG) == SINGLE_FLAG),
		((flags & RICH_FLAG) == RICH_FLAG),
		((flags & FEMALE_FLAG) == FEMALE_FLAG));
    */
  
    // return new request
    return new Request(tml, opCode, op1, op2, requestID, opNameLength, opName);
  }

  public Request decode(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decode(payload);
  }
}
