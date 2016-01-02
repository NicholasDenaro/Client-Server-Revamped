package denaro.nick.test;

import java.io.IOException;
import java.net.Socket;

import denaro.nick.server.Client;
import denaro.nick.server.MyInputStream;

public class TestClient extends Client
{
	public static int count = 0;
	
	public int id;
	
	public TestClient(Socket socket) throws IOException
	{
		super(socket);
		id = count++;
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
			System.exit(0);
		}
	}

}
