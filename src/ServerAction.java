/*******************************************************************************
 *Student ID: 686274
 *Student Name: Ziping Gao
 *Last Modified: 04/09/2019
 *Description: The ServerAction class is to process client requests by defining
 *get() method, add()method and delete() method. All methods use objectmapper
 *to convert String type requests to Json format, process, and return a response
 *message of corresponding type.
 ******************************************************************************/

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;

public enum ServerAction 
{
	Get
	{
	@Override
	 	public ResponseMessage<DictionaryModel> execute(String request)
		{
			
			ResponseMessage<DictionaryModel> responseJson = new ResponseMessage();
			try
			{
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(request, JsonNode.class);
			RequestMessage<String> requestJson = mapper.convertValue(node, new TypeReference<RequestMessage<String>>(){});
			DictionaryModel result = dictionaryAction.getDictionary(requestJson.getData());	
			
			DictionaryModel failedResult = new DictionaryModel();
			failedResult.setVocabulary(requestJson.getData());
			ArrayList errorMsg = new ArrayList();
			errorMsg.add("The word does not exist");
			failedResult.setMeaning(errorMsg);
			
			responseJson.setData(result != null ? result : failedResult);
			responseJson.setSuccess(result != null);
			}
			catch(IOException e)
			{
				DictionaryModel failedResult = new DictionaryModel();
				failedResult.setVocabulary("Error");
				ArrayList errorMsg = new ArrayList();
				errorMsg.add("System Error");
				failedResult.setMeaning(errorMsg);
				responseJson.setData(failedResult);
				responseJson.setSuccess(false);
			}
			finally
			{
				return responseJson;
			}
			
		}
	},
	 Add{
			@Override
        	public ResponseMessage<String> execute(String request)
        	{
				ResponseMessage<String> responseJson = new ResponseMessage<String>();
				
				try {
					ObjectMapper mapper = new ObjectMapper();
	        		JsonNode node = mapper.readValue(request, JsonNode.class);
	        		RequestMessage<DictionaryModel> requestJson = mapper.convertValue(node, new TypeReference<RequestMessage<DictionaryModel>>(){});
	        		Boolean result = dictionaryAction.addDictionary(requestJson.getData());
	        		
	        		responseJson.setSuccess(result);
	        		responseJson.setData(result ? "Success" : "This word has existed.");	        		
				}
				catch(IOException e) {
					responseJson.setSuccess(false);
					responseJson.setData("System Error.");					
				}
				finally {
					return responseJson;
				}
        	}
    },
    Delete{
        	@Override
        	public ResponseMessage<String> execute(String request)
        	{
        		ResponseMessage<String> responseJson = new ResponseMessage();
        		try
        		{
        		ObjectMapper mapper = new ObjectMapper();
        		JsonNode node = mapper.readValue(request, JsonNode.class);
        		RequestMessage<String> requestJson = mapper.convertValue(node, new TypeReference<RequestMessage<String>>(){});
        		Boolean result = dictionaryAction.deleteDictionary(requestJson.getData());
        		responseJson.setData(result ? "Success" : "This word does not exist.");

    
        		responseJson.setSuccess(result);
        		}
        		catch(IOException e)
        		{
        			responseJson.setSuccess(false);
        			responseJson.setData("System Error.");	
        		}
        		finally
        		{
        			return responseJson;
        		}
        	}
    };
	
	public DictionaryAction dictionaryAction;
	
	public abstract <T> T execute(String data);
}
