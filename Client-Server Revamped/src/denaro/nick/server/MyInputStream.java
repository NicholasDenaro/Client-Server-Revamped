package denaro.nick.server;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class MyInputStream
{
	public MyInputStream(InputStream in)
	{
		this.in=in;
		buffer=ByteBuffer.allocate(0);
	}
	
	public MyInputStream(ByteBuffer buffer)
	{
		this.buffer=buffer;
		this.buffer.position(0);
		//this.buffer.getInt();
	}

	public void close() throws IOException
	{
		in.close();
	}
	
	/**
	 * Reads the bytes into a buffer
	 * @throws IOException 
	 */
	public void read() throws IOException
	{
		//read the size
		if(bodyBuffer == null)
		{
			byte[] byts = new byte[sizeBuffer.remaining()];
			int numRead = in.read(byts);
			if(numRead < sizeBuffer.remaining())
			{
				sizeBuffer.put(byts, 0, numRead);
				return;
			}
			
			sizeBuffer.put(byts);
			sizeBuffer.position(0);
			
			bodyBuffer = ByteBuffer.allocate(sizeBuffer.getInt());
			sizeBuffer.clear();
		}
		
		//read the body
		byte[] bytes = new byte[bodyBuffer.remaining()];
		int numRead = in.read(bytes);
		if(numRead < bodyBuffer.remaining())
		{
			bodyBuffer.put(bytes, 0, numRead);
			return;
		}
		
		//put the stuff into the local buffer
		ByteBuffer newbuffer = ByteBuffer.allocate(bodyBuffer.capacity() + buffer.remaining());
		newbuffer.put(buffer.array(),buffer.position(),buffer.remaining());
		newbuffer.put(bodyBuffer.array());
		buffer = newbuffer;
		buffer.position(0);
		bodyBuffer = null;
		
	}
	
	private ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
	private ByteBuffer bodyBuffer;
	
	/*private byte[] retrieveBytes(int size) throws IOException
	{
		byte[] bytes=new byte[size];
		int numBytesRead = in.read(bytes);
		//System.out.println("number of bytes read: " + numBytesRead);
		
		int offset = numBytesRead;
		while((size -= numBytesRead) > 0)
		{
			numBytesRead = in.read(bytes,offset,size);
			offset+=numBytesRead;
		}
		
		return bytes;
	}*/
	
	public boolean isEmpty()
	{
		return(buffer.position()==buffer.capacity());
	}
	
	public int remaining()
	{
		return(buffer.capacity()-buffer.position());
	}
	
	/**
	 * Reads a byte from the buffer
	 * @return - a byte
	 */
	public byte readByte()
	{
		return(buffer.get());
	}
	
	/**
	 * Reads bytes from the buffer
	 * @param size - the number of bytes to return
	 * @return - the bytes
	 */
	public byte[] readBytes(int size)
	{
		byte[] dst=new byte[size];
		buffer.get(dst);
		return(dst);
	}
	
	/**
	 * Reads a byte from the buffer 0=false
	 * @return - a boolean value
	 */
	public boolean readBoolean()
	{
		return(buffer.get()!=0);
	}
	
	/**
	 * Reads an int from the buffer
	 * @return - an int
	 */
	public int readInt()
	{
		return(buffer.getInt());
	}
	
	/**
	 * Reads a double from the buffer
	 * @return - a double
	 */
	public double readDouble()
	{
		return(buffer.getDouble());
	}
	
	/**
	 * Reads a char from the buffer
	 * @return - a char
	 */
	public char readChar()
	{
		return(buffer.getChar());
	}
	
	/**
	 * Reads a string from the buffer
	 * @return - a string
	 */
	public String readString()
	{
		String s="";
		int size=buffer.getInt();
		for(int i=0;i<size;i++)
			s+=buffer.getChar();
		if(size!=-1)
			return(s);
		else
			return(null);
	}

	/** The output stream to read from*/
	private InputStream in;
	
	/** The buffer to store the current bytes in*/
	private ByteBuffer buffer;
}
