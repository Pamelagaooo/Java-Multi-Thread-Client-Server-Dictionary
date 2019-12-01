/*********************************************************************************
 *Student ID: 686274
 *Student Name: Ziping Gao
 *Last Modified: 04/09/2019
 *Description: The ServerMultiThread class is the multithread server to set up
 *TCP server connection and listening for incoming requests from multiple clients.
 *Then, client requests will be handled by 'worker pool architechture' threads.
 *When a request comes in, create a thread and check all currently processing 
 *threads. If a current thread is handling the same action as this client's request,
 *then wait. Otherwise, create a thread to handle this client request.
 *********************************************************************************/

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServerMultiThread {
    private Socket client = null;
    private InetAddress IPAddress;
    private int Port;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ServerMultiThread(Socket socket) {
        this.client = socket;
        this.Port = this.client.getLocalPort();
        this.IPAddress = this.client.getInetAddress();        
    }

    public void setupIO() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            this.writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveData() {
        try {
        	DataInputStream input = new DataInputStream(this.client.getInputStream());        	
            return input.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public void sendData(String data) {
        try {
        	DataOutputStream output = new DataOutputStream(this.client.getOutputStream());
        	output.writeUTF(data);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DictionaryAction action = new DictionaryAction();
        ObjectMapper mapper = new ObjectMapper();
        ServerSocket socket = null;
        Socket client = null;
        try 
        {
        	socket = new ServerSocket(1666);
        	
        	while(true) 
        	{
        		client = socket.accept();
                ServerMultiThread task = new ServerMultiThread(client);              
                
                //read data sent from clients
                String data = task.receiveData();
                System.out.println(data);
                // change the data format sent from client to processed data format
                RequestMessage request = mapper.readValue(data, RequestMessage.class);
                               
                // create a thread to process the request
                Runnable requestRunning = () ->
                {
                    while (true) {
                        // get all processing threads
                        Set<Thread> threads = Thread.getAllStackTraces().keySet();

                        // check whether current threads are processing the same action
                        Boolean isExist = false;
                        for (Thread t : threads) {
                            if (t.getName().equals(request.getAction().name())) {
                                isExist = true;
                                break;
                            }
                        }

                        if (isExist) {
                            // if there is a current thread processing the same action, just wait 
                            continue;
                        } else {
                            // if no current thread is processing the action, create a thread to process the action.  
                            Runnable actionRunning = () ->
                            {                            	
                                try {
                                    request.getAction().dictionaryAction = action;
                                    ResponseMessage response = request.getAction().execute(data);                                    
                                    task.sendData(mapper.writeValueAsString(response));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            };

                            Thread actionThread = new Thread(actionRunning, request.getAction().name());
                            actionThread.start();
                            break;
                        }
                    }
                };

                Thread requestThread = new Thread(requestRunning);
                requestThread.start();                
        	}
        	
        }
        catch(IOException e) 
        {
        	e.printStackTrace();
        }
        finally
        {
        	if (socket != null)
            try 
        	{
                socket.close();
            } 
        	catch (IOException e) 
        	{
                e.printStackTrace();
            }
        }
    }
}
