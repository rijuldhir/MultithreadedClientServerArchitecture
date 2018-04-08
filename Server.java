import java.io.File;
import java.net.Socket;

// this class handles all the Server side architecture
public class Server
{
	public static final int Port = 2737;
	public static void main()
	{
		Scanner in = new Scanner(System.in);
		while(true){
		Socket cs = new Socket("localhost",Port);
		//DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		System.out.println("Enter Integer n and FileName");
		int n = in.nextInt();
		if(n==-1)
			break;
		String fileName = in.next();
		Thread new_thread = new generateRequest(cs,fileName,n)
		new_thread.start();
		new_thread.join();
		}
	}
}

//to generate request and receive file
class generateRequest extends Thread
{
	private Socket sock;
	private String fileName;
	private int n;
	public generateRequest(Socket sock,String file,int n) 
	{
		this.sock = sock;
		this.n=n;
		this.fileName=file;
	}
	
	@Override
	public void run()
	{
		//PrintWriter outToClient = new PrintWriter(sock.getOutputStream(),true);
		
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