package goji.common.test;

import org.junit.Test;
import java.util.UUID;
import goji.common.utils.ObjectUtils;

/** Tests ObjectUtils methods
 * 
 * @author Aaron
 * @version 2020.12.03
 */
public class ObjectUtilsTest {

	@Test
	public void validateObjectNotNullTest() {
		String notNullObject = "I am not null";
		
		ObjectUtils.validateObjectNotNull(notNullObject);
	}
	
	@Test
	public void validUuidTest() {
		UUID uuid = UUID.randomUUID();
		
		ObjectUtils.validUuid(uuid.toString());
	}
	
}
