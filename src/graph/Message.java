package graph;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * An immutable message object that can hold data as bytes, text, or numeric values.
 * Each message captures creation time and provides automatic conversion between formats.
 * Messages are used to pass data through topics in the publish-subscribe system.
 */
public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String input)
    {
        data = convertToBytes(input);
        asText = input;
        asDouble = convertToDouble(input);
        date = new Date();
    }
    public Message(byte[] data)
    {
        this(new String(data,StandardCharsets.UTF_8));
    }
    public Message(double data)
    {
      this(String.valueOf(data));
    }

    // Convertions
    private byte[] convertToBytes(String data)
    {
        return data.getBytes(StandardCharsets.UTF_8);
    }
    private double convertToDouble(String data)
    {
        try {
            return Double.parseDouble(data);
        } catch (Exception e) {
            return Double.NaN;
        }
        
    }
    
}
