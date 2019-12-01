/*********************************************************************************
 *Student ID: 686274
 *Student Name: Ziping Gao
 *Last Modified: 04/09/2019
 *Description: The DictionaryClient class is to send requests to server and receive
 *processed response back.
 *********************************************************************************/

import java.io.*;
import java.net.Socket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DictionaryClient
{
	private static String ip = "localhost";
	private static int port = 1666;
	private static Socket clientSocket;
	
	public static String send(RequestMessage requestMsg)
	
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			clientSocket = new Socket(ip, port);
		
			System.out.println("Connection established");
			
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			
			String requestMessage = mapper.writeValueAsString(requestMsg);
		
			String responseMessage = null;
						
			output.writeUTF(requestMessage);
			output.flush();
			System.out.println("Message sent");
			
			responseMessage = input.readUTF();		
			
			
			return responseMessage;
		}
		catch (JsonProcessingException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(clientSocket != null)
			{
				try 
				{	
					clientSocket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}				
		}
		return "Error";
	}
	
	
	public static void main (String[] args) 
	{
		new DictionaryGui();
		
	}
}
