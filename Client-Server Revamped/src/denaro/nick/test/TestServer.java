package denaro.nick.test;

import java.io.IOException;
import java.net.Socket;

import denaro.nick.server.Client;
import denaro.nick.server.Server;

public class TestServer extends Server
{

	public TestServer(String hostname, int port, int clientPoolSize) throws IOException
	{
		super(hostname, port, clientPoolSize);
	}

	@Override
	public Client newClient(Socket socket) throws IOException
	{
		return new TestServerClient(socket);
	}

}
