package denaro.nick.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPool extends Thread
{
	public ClientPool(int size)
	{
		this.size = size;
		
		clients = new ConcurrentHashMap<Socket, Client>();
		
		running = false;
	}
	
	public boolean isFull()
	{
		return clients.size() == size;
	}
	
	public boolean hasSocket(Socket socket)
	{
		return clients.containsKey(socket);
	}
	
	public boolean addClient(Socket socket, Client client)
	{
		if(isFull())
		{
			return false;
		}
		else
		{
			clients.put(socket, client);
			return true;
		}
	}
	
	@Override
	public void run()
	{
		running = true;
		while(running)
		{
			clients.forEach((Socket s, Client c)->{
				c.run();
			});
		}
	}
	
	protected void broadcastMessage(Message mes)
	{
		clients.forEach((Socket s, Client c)->{
			c.addMessage(mes);
			c.sendMessages();
		});
	}
	
	private boolean running;
	private int size;
	private ConcurrentHashMap<Socket, Client> clients;
}
