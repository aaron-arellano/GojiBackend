package common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "age", "isAdult"})
public class JsonSerializeExample {
	@JsonProperty()
	private String name;
	@JsonProperty()
	private int age;
	@JsonProperty()
	private boolean isAdult;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public boolean getIsAdult() {
		return isAdult;
	}
	public void setIsAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}
	
}
