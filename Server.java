import java.io.File;
import java.net.Socket;

// this class handles all the Server side architecture
public class Server
{
	public static void main()
	{
		
	}
}

//to generate request and receive file
class generateRequest extends Thread
{
	private Socket sock;
	
	public generateRequest(Socket sock) 
	{
		this.sock = sock;
	}
	
	@Override
	public void run()
	{
		
	}
}

// this thread handles the acknowledgment from the Client
class handleAcknowledgment extends Thread
{
	private Socket sock;
	
	public handleAcknowledgment(Socket sock)
	{
		this.sock = sock;
	}
	
	@Override
	public void run()
	{
		
	}
}