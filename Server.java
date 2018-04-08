import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;


public class Server {

	public static final int Port = 2737;
	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		while(true){
		Socket cs = null;
                    try {
                        cs = new Socket("localhost",Port);
                    } catch (IOException ex) {
                        System.out.println(ex);
                        continue;
                    }
		System.out.println("Enter Integer n and FileName");
		int n = in.nextInt();
		if(n==-1)
			break;
		String fileName = in.next();
		/*File file = new File(fileName);
		if(file.exists())
		{
			String msg = "This file exists in the system. Choose a different Name.";
			System.out.println(msg);
			continue;
		}
		file.createNewFile();*/	
		Thread new_thread = new generateRequest(cs,fileName,n);
		new_thread.start();
        try {
                new_thread.join();
            } catch (InterruptedException ex) {
                System.out.println(ex);
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
		this.n=n;
		this.fileName=file;
	}
	
	@Override
	public void run()
	{
		String request = fileName+" "+n+"\n";
        try{
		sock.getOutputStream().write(request.getBytes("UTF-8"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		String response = reader.readLine();
        //reader.close();
        BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
            if(response.startsWith("Ok"))
            {
                File file = new File(fileName);
                FileOutputStream fis = new FileOutputStream(file);
                int data;
                data = bis.read();
                while(data != -1){
                    fis.write(data);
                    data = bis.read();   
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
        handleAcknowledgment ack = new handleAcknowledgment(sock);
        ack.start();
        try {
            ack.join();
        } catch (InterruptedException ex) {
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
		String response = "SUCCESS";
            try {
                sock.getOutputStream().write(response.getBytes("UTF-8"));
            } catch (IOException ex) {
                System.out.println(ex);
            }
	}
}
