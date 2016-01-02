package denaro.nick.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import denaro.nick.server.Message;

public class Main
{
	public static final int NUM_CLIENTS = 1000;
	public static final int PAYLOADS = 1000;
	
	public static void main(String[] args)
	{
		try
		{
			if(args.length > 0 && args[0].equals("server"))
			{
				TestServer server = new TestServer("0.0.0.0",9400,100);
				//TestServer server = new TestServer("games.ndenaro.me",9400,10);
				server.start();
					
				while(TestServerClient.count == 0)
				{
					Thread.sleep(1);
					if(!TestServerClient.countStack.isEmpty())
					{
						TestServerClient.countStack.pop();
						TestServerClient.count++;
					}
				}
				
				long t = 0;
				long s = System.currentTimeMillis();
				long x = t;
				

				long start = System.nanoTime();
				
				while(TestServerClient.count < NUM_CLIENTS * PAYLOADS)
				{
					t+=System.currentTimeMillis() - s;
					s = System.currentTimeMillis();
					if(t > 1000)
					{
						System.out.println(TestServerClient.count);
						t-=1000;
					}
					if(!TestServerClient.countStack.isEmpty())
					{
						TestServerClient.countStack.pop();
						TestServerClient.count++;
					}
				}
				
				long time = System.nanoTime() - start;
				
				System.out.println("time: "+ time / 1000000000.0);
				
				server.broadcastMessage(new Message(0));
				Thread.sleep(5000);
				System.exit(0);
				return;
			}

			TestClient[] clients = new TestClient[NUM_CLIENTS];
			
			for(int i = 0 ; i < NUM_CLIENTS; i ++)
			{
				try
				{
					Socket socket = new Socket();
					socket.setPerformancePreferences(0, 1, 2);
					//socket.connect(new InetSocketAddress("192.168.1.10",9400));
					socket.connect(new InetSocketAddress("98.115.35.63",9400));
					socket.setSoLinger(true, 1000*10);
					socket.setTcpNoDelay(true);
					socket.setSendBufferSize(128);
					
					while(!socket.isConnected())
					{
						
					}
					//System.out.println("Connected: " + socket.isConnected());
					clients[i] =  new TestClient(socket);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Connected "+i+" sockets.");
					return;
				}
			}
			
			System.out.println("All sockets made.");
			
			System.out.println("Starting test...");
			
			Message mes = new Message(0);
			mes.addInt(-1);
			for(int i = 0; i < 512 / Integer.BYTES - 2; i++)
			{
				mes.addInt(-1);
			}
			
			long start = System.nanoTime();
			
			for(int j = 0 ; j < PAYLOADS; j ++)
			{
				for(int i = 0 ; i < NUM_CLIENTS; i ++)
				{
					clients[i].addMessage(mes);
					//clients[i].sendMessages();
				}
			}
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					try
					{
						while(true)
						{
							clients[0].run();
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						System.out.println("Error.");
						System.exit(0);
					}
				}
			},5000);
			
			while(true)
			{
				Thread.sleep(10);
				for(int i = 0 ; i < NUM_CLIENTS; i ++)
				{
					clients[i].sendMessages();
				}
			}
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
			System.out.println("error...");
		}
	}
}
