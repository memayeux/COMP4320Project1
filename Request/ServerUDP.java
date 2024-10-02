import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ServerUDP {
    public static void main(String[] args) throws Exception {
        boolean cont = true;

        // Verifying args
        if (args.length != 1) {
            throw new IllegalArgumentException("Parameter: <portnumber>");
        }
        int port = Integer.parseInt(args[0]);   // Receiving Port

        DatagramSocket sock = new DatagramSocket(port);  // UDP socket

        Scanner s = new Scanner(System.in);  // Scanner for server termination prompt

        do {
            // Response ID:
            byte tempResponseID = 1;

            // Receiving packet
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            sock.receive(packet);

            // Decode binary-encoded request
            RequestDecoder decoder = new RequestDecoderBin();
            Request receivedRequest = decoder.decode(packet);

            // Signal that request is received
            System.out.println("Received Request:");
            System.out.println(receivedRequest);

            // Server calculations (result):
            int result;
            switch (receivedRequest.opCode) {
                case 0: result = receivedRequest.op1 / receivedRequest.op2; break;
                case 1: result = receivedRequest.op1 * receivedRequest.op2; break;
                case 2: result = receivedRequest.op1 & receivedRequest.op2; break;
                case 3: result = receivedRequest.op1 | receivedRequest.op2; break;
                case 4: result = receivedRequest.op1 + receivedRequest.op2; break;
                case 5: result = receivedRequest.op1 - receivedRequest.op2; break;
                default: throw new IllegalArgumentException("Unknown operand");
            }

            // Determine error code
            byte tempErrorCode = (receivedRequest.tml == (short) (9 + receivedRequest.opNameLength)) ? (byte) 0 : 127;

            // Create response
            Response response = new Response((short) 8, tempResponseID, result, tempErrorCode);

            // Encode and send response
            ResponseEncoder encoder = new ResponseEncoderBin();
            byte[] codedResponse = encoder.encode(response);

            // Send response to client
            InetAddress clientAddr = packet.getAddress();
            int clientPort = packet.getPort();
            DatagramPacket sendPacket = new DatagramPacket(codedResponse, codedResponse.length, clientAddr, clientPort);
            sock.send(sendPacket);

            // Verifies keeping server open
            System.out.println("Keep server open? (y/n)");
            String yesNo = s.nextLine();
            if (!yesNo.equals("y")) {
                cont = false;
            }

            tempResponseID++;

        } while (cont);
        sock.close();
        s.close();
    }
}
