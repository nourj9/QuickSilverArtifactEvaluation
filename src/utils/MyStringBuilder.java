package utils;

public class MyStringBuilder {
	private StringBuilder sb = new StringBuilder();

	public void app(String s) {
		sb.append(s);
	}

	public void appn(Object s) {
		sb.append(s).append("\n");
	}
	public void appn() {
		sb.append("\n");
	}

	public void appt(Object s) {
		sb.append(s).append("\t");
	}

	public void apps(Object s) {
		sb.append(s).append(" ");
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	public void line() {
		sb.append("==============================================\n");
	}
}
