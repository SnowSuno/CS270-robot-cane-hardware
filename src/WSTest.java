import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WSTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(8080);
        try {
//            System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection…");
            System.out.println("Listening...");
            Socket client = server.accept();
            System.out.println("A client connected.");

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner s = new Scanner(in, "UTF-8");

            try {
                String data = s.useDelimiter("\\r\\n\\r\\n").next();
                Matcher get = Pattern.compile("^GET").matcher(data);

                if (get.find()) {
                    Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                    match.find();
                    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Sec-WebSocket-Accept: "
                            + base64Encode(sha1((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                            + "\r\n\r\n").getBytes("UTF-8");
                    out.write(response, 0, response.length);

                    byte[] decoded = new byte[6];
                    byte[] encoded = new byte[]{(byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135};
                    byte[] key = new byte[]{(byte) 167, (byte) 225, (byte) 225, (byte) 210};
                    for (int i = 0; i < encoded.length; i++) {
                        decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
                    }
                }
            } finally {
                s.close();
            }
        } finally {
            server.close();
        }
    }

    private static byte[] sha1(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        return sha1.digest(input);
    }

    private static String base64Encode(byte[] input) {
        final char[] BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        StringBuilder sb = new StringBuilder();
        int paddingCount = (3 - input.length % 3) % 3;

        for (int i = 0; i < input.length; i += 3) {
            int b = (input[i] & 0xFF) << 16 | (input[i + 1] & 0xFF) << 8 | (input[i + 2] & 0xFF);

            sb.append(BASE64_CHARS[(b >> 18) & 0x3F]);
            sb.append(BASE64_CHARS[(b >> 12) & 0x3F]);
            sb.append(BASE64_CHARS[(b >> 6) & 0x3F]);
            sb.append(BASE64_CHARS[b & 0x3F]);
        }

        for (int i = 0; i < paddingCount; i++) {
            sb.append('=');
        }

        return sb.toString();
    }

}
