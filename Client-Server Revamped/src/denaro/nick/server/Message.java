package denaro.nick.server;
import java.nio.ByteBuffer;


public class Message
{
	public Message()
	{
		size=0;
		buffer=ByteBuffer.allocate(size);
	}
	
	public Message(byte id)
	{
		size=1;
		buffer=ByteBuffer.allocate(size);
		buffer.put(id);
	}
	
	public Message(int id)
	{
		this((byte)id);
	}
	
	public Message(Message mes)
	{
		this.size = mes.size;
		buffer=ByteBuffer.allocate(size);
		buffer.put(mes.buffer);
	}
	
	/**
	 * Accesses the byte array of the buffer
	 * @return - a byte array that is represented by the buffer
	 */
	public byte[] bytes()
	{
		if(!buffer.hasArray())
			System.out.println("ERROR: buffer should have array!");
		return(buffer.array());
	}
	
	/**
	 * Accesses the size of the message
	 * @return - the size of the message
	 */
	public int size()
	{
		return(size);
	}
	
	/**
	 * Increases the buffer size
	 * @param increase - the ammount to increase the size
	 */
	private void increaseBuffer(int increase)
	{
		size+=increase;
		byte[] bytes=buffer.array();
		buffer=ByteBuffer.allocate(size);
		buffer.put(bytes);
	}
	
	/**
	 * Adds a byte to the buffer, increasing the size appropriately
	 * @param b - the byte to add to the buffer
	 * @return - this message
	 */
	public Message addByte(byte b)
	{
		increaseBuffer(1);
		buffer.put(b);
		return(this);
	}
	
	/**
	 * Adds bytes to the buffer, increasing the size appropriately
	 * @param bytes - the bytes to add the to buffer
	 * @return - this message
	 */
	public Message addBytes(byte[] bytes)
	{
		increaseBuffer(bytes.length);
		buffer.put(bytes);
		return(this);
	}
	
	/**
	 * Adds a boolean to the buffer using 1 byte of space, increasing the size appropriately
	 * @param bool - the boolean to add to the buffer
	 * @return - this message
	 */
	public Message addBoolean(boolean bool)
	{
		increaseBuffer(1);
		buffer.put((byte)(bool?1:0));
		return(this);
	}
	
	/**
	 * Adds an int to the buffer, increasing the size appropriately
	 * @param i - the int to add to the buffer
	 * @return - this message
	 */
	public Message addInt(int i)
	{
		increaseBuffer(4);
		buffer.putInt(i);
		return(this);
	}
	
	/**
	 * Adds a double to the buffer, increasing the size appropriately
	 * @param d - the double to add to the buffer
	 * @return - this message
	 */
	public Message addDouble(double d)
	{
		increaseBuffer(8);
		buffer.putDouble(d);
		return(this);
	}
	
	/**
	 * Adds a char to the buffer, increasing the size appropriately
	 * @param c - the char to add to the buffer
	 * @return - this message
	 */
	public Message addChar(char c)
	{
		increaseBuffer(2);
		buffer.putChar(c);
		return(this);
	}
	
	/**
	 * Adds a String to the buffer, increasing the size appropriately
	 * @param s - the string to add to the buffer
	 * @return - this message
	 */
	public Message addString(String s)
	{
		if(s!=null)
		{
			increaseBuffer(4+s.length()*2);
			buffer.putInt(s.length());
			for(int i=0;i<s.length();i++)
				buffer.putChar(s.charAt(i));
		}
		else
		{
			increaseBuffer(4);
			buffer.putInt(-1);
		}
		return(this);
	}
	
	/**
	 * Adds a Message to the buffer, increasing the size appropriately
	 * @param message - the message to add to the buffer
	 * @return - this message
	 */
	public Message addMessage(Message message)
	{
		increaseBuffer(message.size);
		buffer.put(message.bytes());
		return(this);
	}
	
	/** The buffer*/
	private ByteBuffer buffer;
	
	/**	The size of the message*/
	private int size;
}
