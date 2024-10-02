// for DatagramSocket and DatagramPacket
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ServerUDP {
    public static void main(String[] args) throws Exception {
        Boolean cont = true;

        // Verifying args
        if (args.length != 1) {
            throw new IllegalArgumentException("Parameter: <portnumber>");
        }
        int port = Integer.parseInt(args[0]);   // Receiving Port

        DatagramSocket sock = new DatagramSocket(port);  // UDP socket for receiving
    
    do {
        // Response ID:
        byte tempResponseID = 1;

        // Receiving packet
        DatagramPacket packet = new DatagramPacket(new byte[1024],1024);
        sock.receive(packet);
    
        // Decode binary-encoded request
        RequestDecoder decoder = new RequestDecoderBin();
        Request receivedRequest = decoder.decode(packet);

        // Signal that request is received
        System.out.println("Received Binary-Encoded Request");
        System.out.println(receivedRequest);

        // Server calculations to be sent back to client (result):
        int result;
        switch (receivedRequest.opCode) {
            case 0:
                // Division case '/'
                result = receivedRequest.op1 / receivedRequest.op2;
                break;
            case 1:
                // Multiplication case '*'
                result = receivedRequest.op1 * receivedRequest.op2;
                break;
            case 2:
                // Bitwise AND case '&'
                result = receivedRequest.op1 & receivedRequest.op2;
                break;
            case 3:
                // Bitwise OR case '|'
                result = receivedRequest.op1 | receivedRequest.op2;
                break;
            case 4:
                // Addition case '+'
                result = receivedRequest.op1 + receivedRequest.op2;
                break;
            case 5:
                // Subtraction case '-'
                result = receivedRequest.op1 - receivedRequest.op2;
                break;
            default:
                throw new IllegalArgumentException("Unknown operand");
        }

        // Determine error code:
        byte tempErrorCode;
        if (receivedRequest.tml == (short) (9 + receivedRequest.opNameLength)) {
            tempErrorCode = 0;
        } else {
            tempErrorCode = 127;
        }

        // Creates new response object to be sent
        Response response = new Response((short)8, tempResponseID, result, tempErrorCode);

        // Encodes response object
        ResponseEncoder encoder = new ResponseEncoderBin();
        byte[] codedResponse = encoder.encode(response);

        // Gets client address and port
        InetAddress clientAddr = packet.getAddress();
        int clientPort = packet.getPort();

        // Creates Datagram packet with the response in bytes, then sends
        DatagramPacket sendPacket = new DatagramPacket(codedResponse, codedResponse.length,
            clientAddr, clientPort);
        sock.send(sendPacket);

        // Verifies keeping server open
        Scanner s = new Scanner(System.in);
        System.out.println("Keep server open? (y/n)");
        String yesNo = s.nextLine();
        if (yesNo != "y") {
            cont = false;
        }
        tempResponseID++;
        s.close();
        
    } while (cont == true);
    sock.close();
    }
}
