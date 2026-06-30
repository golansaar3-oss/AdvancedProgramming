package graph;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * An immutable message object that can hold data as bytes, text, or numeric values.
 * Each message captures creation time and provides automatic conversion between formats.
 * Messages are used to pass data through topics in the publish-subscribe system.
 */
public class Message {
    /**
     * The raw UTF-8 byte representation of the message.
     */
    public final byte[] data;
    /**
     * The UTF-8 encoded message body.
     */
    public final String asText;
    /**
     * The text representation of the message.
     */
    public final double asDouble;
    /**
     * The numeric representation of the message when parseable.
     */
    public final Date date;
    /**
     * The creation timestamp of the message.
     */

    /**
     * Creates a message from text and derives the byte and numeric views.
     *
     * @param input the text content for the message
     */
    public Message(String input)
    {
        data = convertToBytes(input);
        asText = input;
        asDouble = convertToDouble(input);
        date = new Date();
    }
    /**
     * Creates a message from UTF-8 encoded bytes.
     *
     * @param data the UTF-8 encoded content
     */
    public Message(byte[] data)
    {
        this(new String(data,StandardCharsets.UTF_8));
    }
    /**
     * Creates a message from a numeric value.
     *
     * @param data the numeric content
     */
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
