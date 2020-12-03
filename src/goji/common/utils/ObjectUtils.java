package goji.common.utils;

import javax.ws.rs.BadRequestException;

/** Utils class for object static methods
 * 
 * @author Aaron
 * @version 2020.12.02
 */
public class ObjectUtils {

	private ObjectUtils() {}
	
	public static <T> void validateObjectNotNull(T type) {
		if (type == null) {
			throw new BadRequestException("Create or Update REST calls must contain JSON content, this may not be null");
		}
	}
}
