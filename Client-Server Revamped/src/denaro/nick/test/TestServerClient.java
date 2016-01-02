package denaro.nick.test;

import java.io.IOException;
import java.net.Socket;
import java.util.Stack;

import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;

public class TestServerClient extends Client
{
	static int count = 0;
	public TestServerClient(Socket socket) throws IOException
	{
		super(socket);
		socket.setKeepAlive(true);
		socket.setSoTimeout(1);
		socket.setSendBufferSize(128);
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;
	}

	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		if(messageid == 0)
		{
			in.readInt();
			for(int i = 0; i < 512 / Integer.BYTES - 2; i++)
			{
				in.readInt();
			}
			count();
			//System.out.println("hello world from client" + id);
		}
		else
		{
			System.out.println("oops...");
		}
	}
	
	private static void count()
	{
		countStack.push(null);
	}
	
	public static Stack<Object> countStack = new Stack<Object>();
	
	private static final Object countLock = new Object();

	/*public static void counter()
	{
		synchronized(countLock)
		{
			count++;
		}
	}*/
}
