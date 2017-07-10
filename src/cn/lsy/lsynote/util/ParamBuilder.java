package cn.lsy.lsynote.util;

public class ParamBuilder {
	
	private StringBuffer stringBuffer = new StringBuffer();
	
	public void add(String name, String value) {
		if (stringBuffer.length() != 0) {
			stringBuffer.append("&");
		}
		
		stringBuffer.append(name);
		stringBuffer.append("=");
		stringBuffer.append(value);
	}
	
	public String build() {
		return stringBuffer.toString();
	}


}
