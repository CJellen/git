public class Cell {

	public String location = "";
	public String string = "       ";
	public double value;
	public Date date;
	public Formula formula;
	public int type = 1;

	public Cell(String str, int variableType) {
		if (variableType == 1) {
			string = str.substring(1, str.length() - 1);
			type = 1;
		} else if (variableType == 2) {
			value = Double.parseDouble(str);
			type = 2;
		} else {
			date = new Date(str);
			type = 3;
		}
	}

	

	public String toString() {
		if (type == 1) {
			return string;
		} else if (type == 2) {
			return value + "";
		} else if (type == 3) {
			return date.toString();
		} else {
			return formula.toString();
		}
	}

	public String contains() {
		if (type == 1) {
			return string;
		} else if (type == 2) {
			return value + "";
		} else if (type == 3) {
			return date.toString();
		} else {
			return formula.inputFormula;
		}
	}
	
	public void setCell(String str, int variableType) {
		if (variableType == 1) {
			string = str.substring(1, str.length() - 1);
			type = 1;
		} else if (variableType == 2) {
			value = Double.parseDouble(str);
			type = 2;
		} else if (variableType == 3) {
			date = new Date(str);
			type = 3;
		} else {
			String[] formulaInfo = { location, str, contains() };
			formula = new Formula(formulaInfo, type);
		}
	}
}
