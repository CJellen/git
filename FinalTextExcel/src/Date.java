public class Date {

	public int month;
	public int day;
	public int year;

	public Date(String s) {
		
		String[] dateParts = s.split("/");
		month = Integer.parseInt(dateParts[0]);
		day = Integer.parseInt(dateParts[1]);
		year = Integer.parseInt(dateParts[2]);
	}

	public String toString() {
		
		return month + "/" + day + "/" + year;
	}
}
