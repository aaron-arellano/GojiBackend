package goji.common.utils;

import java.util.logging.Logger;
import javax.ws.rs.BadRequestException;
import goji.common.GojiLogManagement;

/** Utils class for object static methods
 * 
 * @author Aaron
 * @version 2020.12.02
 */
public class ObjectUtils {
	private static final Logger LOGGER =
	        GojiLogManagement.createLogger(ObjectUtils.class.getName());

	private ObjectUtils() {}
	
	public static <T> void validateObjectNotNull(T type) {
		if (type == null) {
			LOGGER.severe("User did not add request content for create or update.");
			throw new BadRequestException("Create or Update REST calls must contain JSON content, this may not be null");
		}
	}
	
	public static void validUuid(String uuid) {
		String regex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
		
		if(!uuid.matches(regex)) {
			LOGGER.severe("User task id not valid " + uuid);
			throw new BadRequestException("Provided task id is not valid " + uuid);
		}
	}
}
