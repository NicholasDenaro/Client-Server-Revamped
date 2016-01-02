package denaro.nick.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public abstract class Server extends Thread
{
	public Server(String hostname, int port) throws IOException
	{
		server=new ServerSocket();
		
		if(hostname==null)
		{
			//hostname=InetAddress.getLocalHost().getHostAddress();
			System.out.print("hostname:port| ");
			hostname=getInput();
			port=new Integer(hostname.substring(hostname.indexOf(':')+1));
			hostname=hostname.substring(0,hostname.indexOf(':'));
		}
		server.setReuseAddress(true);
		
		String ip = Pattern.matches("[0-9]\\.[0-9]\\.[0-9]", hostname) ? hostname : InetAddress.getByName(hostname).getHostAddress();
		
		System.out.println("Attempting to bind to: "+ip+":"+port);
		server.bind(new InetSocketAddress(ip,port));
		System.out.println("server bound to: "+server.getInetAddress());
		
		this.clientPoolSize = 100;
		
		pools = new ArrayList<ClientPool>();
	}
	
	public Server(String hostname, int port, int clientPoolSize) throws IOException
	{
		this(hostname,port);
		
		this.clientPoolSize = clientPoolSize;
	}
	
	public static String getInput()
	{
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		String command;
		try
		{
			command=in.readLine();
			return(command);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return(null);
	}
	
	public abstract Client newClient(Socket socket) throws IOException;
	
	@Override
	public void run()
	{
		System.out.println("Server started.");
		running=true;
		while(running)
		{
			try
			{
				Socket socket=server.accept();
				
				if(pools.size() == 0)
				{
					ClientPool pool = new ClientPool(clientPoolSize);
					pools.add(pool);
					pool.start();
				}
				
				if(!isClientConnectedAlready(socket))
				{
					Client client = newClient(socket);
					socket.setSoTimeout(1);
					socket.setKeepAlive(true);
					socket.setReceiveBufferSize(256);
					socket.setTcpNoDelay(false);
					socket.setSoLinger(true, 1000*10);
					if(pools.get(pools.size() - 1).isFull())
					{
						ClientPool pool = new ClientPool(this.clientPoolSize);
						pool.start();
						pools.add(pool);
						//System.out.println("Added pool");
					}
					
					if(!pools.get(pools.size() - 1).addClient(socket,client))
					{
						System.out.println("failed to add...");
					}
					
				}
				else
				{
					
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
				running=false;
			}
		}
		System.out.println("Server shutting down.");
	}
	
	private boolean isClientConnectedAlready(Socket socket)
	{
		for(ClientPool pool:pools)
		{
			if(pool.hasSocket(socket))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void broadcastMessage(Message mes)
	{
		for(ClientPool pool:pools)
		{
			pool.broadcastMessage(mes);
		}
	}
	
	private int clientPoolSize;
	private ArrayList<ClientPool> pools;
	private ServerSocket server;
	private boolean running;
}
