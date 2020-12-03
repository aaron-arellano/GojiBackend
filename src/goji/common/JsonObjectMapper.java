package goji.common;

import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Maps Object to a JSON string using Jackson library
 * 
 * @author Aaron
 * @version 2020.12.02
 */
public class JsonObjectMapper {

	public static <T> String objectToJsonString(T type) {
		Logger logger = GojiLogManagement.createLogger(JsonObjectMapper.class.getName());
		String result = "";
		
		try {
			result = new ObjectMapper().writeValueAsString(type);
		} 
		catch (JsonProcessingException jpe) {
			logger.severe("Failed to map object to JSON " + jpe.toString());
			throw new InternalServerErrorException();
		}
		
		return result;
	}
	
}
