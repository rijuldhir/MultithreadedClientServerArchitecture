import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Server
{

	public static final int Port = 2737;

	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Input -1 to STOP SERVER");
		while(true)
		{
			System.out.println("Enter Integer n and FileName");
			int n;
			String fileName;
			try
			{
				n = in.nextInt();
				if(n==-1)
					break;
				fileName = in.next();
			}
			catch(Exception e)
			{
				System.out.println("Invalid input format.");
				in.nextLine();
				continue;
			}

			File file = new File(fileName);
			if(file.exists())
			{
				System.out.println("This file exists in the system. Choose a different Name.");
				continue;
			}
			file.createNewFile();


			Socket cs = null;
	        try
			{
	            cs = new Socket("localhost",Port);
	        }
			catch (Exception ex)
			{
	            System.out.println("The connection to client could not be established.");
				Thread.sleep(3000);
	            continue;
        	}

			Thread new_thread = new generateRequest(cs,fileName,n);
			new_thread.start();
	        try
			{
	             new_thread.join();
	        }
			catch (InterruptedException ex)
			{
	             System.out.println("Interrupt occured while waiting for the thread");
			}
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
		this.n = n;
		this.fileName = file;
	}

	@Override
	public void run()
	{
		String request = fileName+" "+n+"\n";
        try
		{
			sock.getOutputStream().write(request.getBytes("UTF-8"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String response = reader.readLine();
			System.out.println(response);
			int count  = 1;
        	if(response.startsWith("Ok"))
            {
                File file = new File(fileName);
                FileOutputStream fis = new FileOutputStream(file);
				int size = Integer.parseInt(reader.readLine());
				System.out.println(size);

				DataInputStream bis = new DataInputStream(sock.getInputStream());
                while(count <= size)
				{
					byte data = bis.readByte();
					fis.write(data);
                    count++;
                }
                fis.close();
            }
            else
            {
                System.out.println(response);
                return ;
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            return ;
        }

		System.out.println("File recieved.");
        handleAcknowledgment ack = new handleAcknowledgment(sock);
        ack.start();
        try
		{
            ack.join();
			Thread.sleep(3000);
        }
		catch (InterruptedException ex)
		{
            System.out.println(ex);
        }
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
		String response = "SUCCESS\n";
            try {
                sock.getOutputStream().write(response.getBytes("UTF-8"));
            } catch (IOException ex) {
                System.out.println(ex);
            }
	}
}
