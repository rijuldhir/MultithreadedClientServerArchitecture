import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

// this class handles all the client side architecture
public class Client
{
	// this client always listens to this port for requests
	public static final int listeningPort = 2737;
	
	// 1)catch the server requests
	// 2)create a thread to handle the request
	public static void main() throws Exception
	{
		// create a socket to accept requests at port specified
		ServerSocket ss = new ServerSocket(listeningPort);
		
		while(true)
		{
			Socket cs = ss.accept();
			Thread new_thread = new ProcessRequest(cs);
			new_thread.start();
		}
	}
}

// this thread is for processing a particular request
class ProcessRequest extends Thread
{
	private Socket soc;
	
	// constructor
	public ProcessRequest(Socket soc) 
	{
		this.soc = soc;
	}
	
	@Override
	public void run()
	{
		
	}
}

// this thread generates the fibbonacci sequence of length n
class GenerateSequence extends Thread
{
	private int n;
	
	// constructor
	public GenerateSequence(int n)
	{
		this.n = n;
	}
	
	@Override
	public void run()
	{
		
	}
}

//this thread writes the generated fibbonacci sequence of length n to the recieved file
class WriteSequence extends Thread
{
	private File blankFile;
	private int[] sequence;
	
	// constructor
	public WriteSequence(File blankFile, int[] sequence)
	{
		this.blankFile = blankFile;
		this.sequence = sequence;
	}
	
	@Override
	public void run()
	{
		
	}
}

// this thread sends the updated file back to the server
class SendFile extends Thread
{
	private File toSend;
	private Socket soc;
	
	// constructor
	public SendFile(File toSend, Socket soc)
	{
		this.toSend = toSend;
		this.soc = soc;
	}
	
	@Override
	public void run()
	{
		
	}
}

// this thread handles the acknowledgement from the server
class HandleAcknowledgement extends Thread
{
	private Socket soc;
	
	public HandleAcknowledgement(Socket soc)
	{
		this.soc = soc;
	}
	
	@Override
	public void run()
	{
		
	}
}