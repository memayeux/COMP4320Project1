/*
 * Client Side code
 */
// for DatagramSocket, DatagramPacket, and InetAddress
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;   // for encoding
import java.util.Scanner;   // for user input

public class ClientUDP {

  public static void main(String args[]) throws Exception {
    Boolean cont = true;    // Used in the do-while loop to continue

    // Verifying args
    if (args.length != 2) {
      throw new IllegalArgumentException("Parameters: <servername> <portnumber>");
    }
    InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
    int destPort = Integer.parseInt(args[1]);               // Destination port

    DatagramSocket sock = new DatagramSocket(); // UDP socket for sending

    do {
      // Initializing
      Scanner s = new Scanner(System.in);
      byte tempRequestID = 0;

      // Asking the user for the opcode
      System.out.println("Opcode: div=0, mul=1, and=2, or=3, add=4, sub=5");
      System.out.println("What is the operation you want to perform?");
      byte tempOpCode = s.nextByte();
      while (!(tempOpCode>=0) || !(tempOpCode<6)) {
        System.out.println("Not a valid opcode, try again.");
        tempOpCode = s.nextByte();
      }

      // Asking the user for the operands
      System.out.println("What is the first operand?");
      short tempOp1 = s.nextShort();
      System.out.println("What is the second operand?");
      short tempOp2 = s.nextShort();

      // Determines operation name
      String tempOpName;
      switch (tempOpCode) {
        case 0:
          tempOpName = "div";
          break;
        case 1:
          tempOpName = "mul";
          break;
        case 2:
          tempOpName = "and";
          break;
        case 3:
          tempOpName = "or";
          break;
        case 4:
          tempOpName = "add";
          break;
        default:
          tempOpName = "sub";
          break;
      }

      // TML: Adds size of opName to static size of the rest of Request
      short tempTml = (short) (9 + tempOpName.getBytes(StandardCharsets.UTF_16).length);

      // Creates new request object to be sent
      // Notes: (byte) tempOpName.getBytes().length gets the length
      // of a string in bytes; no need to convert later.
      Request request = new Request(tempTml, tempOpCode, tempOp1, tempOp2, tempRequestID,
        (byte) tempOpName.getBytes(StandardCharsets.UTF_16).length, tempOpName);
      
      // Encodes request object
      RequestEncoder encoder = new RequestEncoderBin();
      byte[] codedRequest = encoder.encode(request);
      
      // Creates DatagramPacket with the request in bytes, then sends
      DatagramPacket message = new DatagramPacket(codedRequest, codedRequest.length,
				destAddr, destPort);
      sock.send(message);

      // Asks the user for continue/quit
      System.out.println("Continue sending? (y/n)");
      String yesNo = s.nextLine();
      if (yesNo != "y") {
        cont = false;   // This signals the end of the do-while loop
      }
      tempRequestID++;
      s.close();

    } while(cont == true);
    sock.close();
  }
}
