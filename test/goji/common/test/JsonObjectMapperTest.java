package goji.common.test;

import static org.junit.Assert.*;
import org.junit.Test;
import common.JsonSerializeExample;
import goji.common.JsonObjectMapper;

public class JsonObjectMapperTest {

	@Test
	public void objectToJsonStringTest() {
		JsonSerializeExample exampleObj = new JsonSerializeExample();
		exampleObj.setName("Cam");
		exampleObj.setAge(22);
		exampleObj.setIsAdult(true);
		
		String expected = "{\"name\":\"Cam\",\"age\":22,\"isAdult\":true}";
		String actual = JsonObjectMapper.objectToJsonString(exampleObj);
				
		assertEquals(expected, actual);
	}
	
}
