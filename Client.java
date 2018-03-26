import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
	
	// this method sends the error message to the server
	public static void sendErrorMsg(Socket soc, String msg) throws Exception
	{
		String response = "Error " + msg + "\n";
		soc.getOutputStream().write(response.getBytes("UTF-8"));
		soc.close();
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
		try
		{
			// Read the request from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			String request = reader.readLine();
			
			// request is of the format "blank_file_name integer"
			String requestFormat = "[a-zA-Z0-9.]+ \\d+";
			if(!request.matches(requestFormat))
			{
				String msg = "Invalid request format.";
				System.out.println(msg);
				Client.sendErrorMsg(soc, msg);
				return ;
			}
			
			// get the blank file name and integer n
			String blankFileName = request.split(" ")[0];
			int n = Integer.parseInt(request.split(" ")[1]);
			
			// array to be shared by this and the next thread
			int[] sequence = new int[n];
			
			// generating fibbonacci sequence
			Thread sequenceThread = new GenerateSequence(n, sequence, soc);
			sequenceThread.start();
			sequenceThread.join();
			
			// file to which the sequence has to be written
			File blankFile = new File(blankFileName);
			if(blankFile.exists())
			{
				String msg = "This file temporarily exists in the system. Try again sometime later.";
				System.out.println(msg);
				Client.sendErrorMsg(soc, msg);
				return ;
			}
			
			blankFile.createNewFile();
			
			// writing to the blank file
			Thread writeThread = new WriteSequence(blankFile, sequence);
			writeThread.start();
			writeThread.join();
			
			// sending the file to the server
			Thread sendThread = new SendFile(blankFile, soc);
			sendThread.start();
			sendThread.join();
			
			// thread for acknowledgement
			Thread ackThread = new HandleAcknowledgement(soc);
			ackThread.start();
		}
		catch (Exception e) 
		{
			System.out.println("Something unknown occured in ProcessRequest");
			return ;
		}
	}
}

// this thread generates the fibbonacci sequence of length n
class GenerateSequence extends Thread
{
	private int n;
	private int[] sequence;
	private Socket soc;
	
	// constructor
	public GenerateSequence(int n, int[] sequence, Socket soc)
	{
		this.n = n;
		this.sequence = sequence;
		this.soc = soc;
	}
	
	@Override
	public void run()
	{
		try
		{
			// if n <= 0 send error msg
			if(n <= 0)
			{
				String msg = "n must be a positive integer";
				System.out.println(msg);
				Client.sendErrorMsg(soc, msg);
				return ;
			}
			
			sequence[0] = 0;
			if(n == 1)
				return ;
			
			sequence[1] = 1;
			
			// generating fibbonacci sequence
			for(int i = 2;i < n;i++)
				sequence[i] = sequence[i-1] + sequence[i-2];
			
			return ;
		}
		catch(Exception e)
		{
			System.out.println("Something unknown occured in GenerateSequence");
			return ;
		}
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
		try 
		{
			// writing the fibbnacci sequence to the file
			PrintStream ps = new PrintStream(blankFile);
			for(int i = 0;i < sequence.length;i++)
				ps.println(sequence[i]);
			ps.close();
		}
		catch(Exception e)
		{
			System.out.println("Something unknown occured in WriteSequence.");
			return ;
		}
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
		try
		{
			// sending success message indicating arrival of the file containing the fibbonacci sequence
			String response = "Ok. Sending file...\n";
			soc.getOutputStream().write(response.getBytes("UTF-8"));
			
			// getting file input stream
			FileInputStream fis = new FileInputStream(toSend);
			
			// sending file to the server
			byte[] sendData = new byte[1024];
			int lengthRead;
			while((lengthRead = fis.read(sendData)) != -1)
				soc.getOutputStream().write(sendData, 0, lengthRead);
			
			fis.close();
		}
		catch(Exception e)
		{
			System.out.println("Something unknown occured in SendFile");
			return ;
		}
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
		try
		{
			// reading the ack
			BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			String response = reader.readLine();
			if(response.equals("SUCCESS"))
				System.out.println("Request successfully served. Closing the connection...");
			else
				System.out.println("Positive ack not recieved. Closing the connection...");
			
			soc.close();
		}
		catch(Exception e)
		{
			System.out.println("Something unknown occured in HandleAcknowledgement");
			return ;
		}
	}
}