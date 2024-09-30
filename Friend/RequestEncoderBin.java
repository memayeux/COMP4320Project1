import java.io.*;  // for ByteArrayOutputStream and DataOutputStream

public class RequestEncoderBin implements RequestEncoder, RequestBinConst {

  private String encoding;  // Character encoding

  public RequestEncoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public RequestEncoderBin(String encoding) {
    this.encoding = encoding;
  }

  public byte[] encode(Request request) throws Exception {

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buf);
    out.writeInt(request.tml);
    out.writeShort(request.opCode);
    out.writeInt(request.op1);
    out.writeInt(request.op2);
    out.writeInt(request.requestID);
    out.writeInt(request.opNameLength);

    /*
    out.writeLong(friend.ID);
    // Will deal with the lasname at the end
    out.writeShort(friend.streetNumber);
    out.writeInt(friend.zipCode);
    byte flags = 0;
    if (friend.single)
	flags = SINGLE_FLAG;
    if (friend.rich)
	flags |= RICH_FLAG;
    if (friend.female)
	flags |= FEMALE_FLAG;
    out.writeByte(flags);
    */

    byte[] encodedOpName = request.opName.getBytes(encoding);
    if (encodedOpName.length > MAX_OPNAME_LENGTH)
      throw new IOException("Opcode name exceeds encoded length limit");
    out.writeByte(encodedOpName.length); // provides length of opcode name
    out.write(encodedOpName);
    out.flush();
    return buf.toByteArray();
  }
}
