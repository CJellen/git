import java.util.ArrayList;

public class Formula {

	// public fields
	public String inputFormula = "none";

	public ArrayList<String> arrlist = new ArrayList<String>(100);

	public int inputCellType;

	public String inputCellData = "";

	public String inputCellLocation = "";

	public Formula(String[] formulaElements, int displayType) {

		inputCellLocation = formulaElements[0];
		inputFormula = formulaElements[1];
		inputCellData = formulaElements[2];
		inputCellType = displayType;

		int column = inputCellLocation.charAt(0) - 65;
		int row = inputCellLocation.charAt(1) - 49;

		Data.Sheet[row][column].type = 4;

	}

	public String toString() {
		String noParenthesesFormula = inputFormula.substring(1,
				inputFormula.length() - 1);
		arrlist = new ArrayList<String>();

		String[] parts;
		try {
			parts = noParenthesesFormula.split(" ");
		} catch (StringIndexOutOfBoundsException noInput) {
			parts = new String[1];
			parts[0] = "";
		}

		convertCellAddress(parts);

		if (!validateContents(parts)) {
			terminateFormula(parts);
		}

		mult_div(parts);

		add_sub(parts);

		if (parts[0].length() == 0) {
			int column = inputCellLocation.charAt(0) - 65;
			int row = inputCellLocation.charAt(1) - 49;

			Data.Sheet[row][column].setCell(inputCellData, inputCellType);
			return Data.Sheet[row][column].toString();

		} else {
			return parts[0];
		}

	}

	public boolean checkReferences() {
		return check(inputCellLocation);
	}

	public boolean check(String address) {
		boolean isValid = true;
		for (int i = 0; i < arrlist.size(); i++) {
			if (arrlist.get(i).equals(address)) {
				return false;
			}
		}

		for (int i = 0; i < arrlist.size(); i++) {
			String reference = arrlist.get(i);
			int column = reference.charAt(0) - 65;
			int row = reference.charAt(1) - 49;

			int cellDisplayType = Data.Sheet[row][column].type;
			if (cellDisplayType == 4) {
				isValid = Data.Sheet[row][column].formula.check(address);
			}
		}

		return isValid;
	}

	public static boolean validateContents(String[] parts) {
		boolean isValid = true;

		for (int i = 0; i < parts.length; i += 2) {
			try {
				Double.parseDouble(parts[i]);
			} catch (NumberFormatException isNotNum) {
				isValid = false;
			}
		}
		return isValid;
	}

	private void convertCellAddress(String[] parts) {
		for (int i = 0; i < parts.length; i++) {

			char firstChar;
			if (parts[i].length() == 0) {
				firstChar = '!';
			} else {
				firstChar = parts[i].charAt(0);
			}

			if (parts[i].toUpperCase().indexOf("SUM") == -1
					&& parts[i].toUpperCase().indexOf("AVG") == -1) {

				if ((firstChar >= 65 && firstChar <= 90)
						&& parts[i].length() == 2) {
					arrlist.add(parts[i]);

					int column = firstChar - 65;
					int row = parts[i].charAt(1) - 49;

					try {
						if (checkReferences()) {
							String tempStringHolder = Data.Sheet[row][column]
									.toString();
							parts[i] = tempStringHolder;

							Double.parseDouble(parts[i]);
						} else {
							Exception e = new Exception();
							throw e;
						}
					} catch (Exception e) {
						terminateFormula(parts);
					}
				}
			} else {
				if (parts[i].toUpperCase().indexOf("SUM") != -1) {
					String tempStringHolder = sum(parts[i]);

					if (tempStringHolder.length() != 0) {
						parts[i] = tempStringHolder.substring(0,
								tempStringHolder.indexOf(" "));
					} else {
						terminateFormula(parts);
					}
				} else {
					parts[i] = avg(parts[i]);
				}
			}
		}
		if (!checkReferences()) {
			terminateFormula(parts);
		}
	}

	private void mult_div(String[] parts) {
		for (int i = 1; i < parts.length; i++) {
			if (parts[i].equals("*") || parts[i].equals("/")) {

				if (parts[i].equals("*")) {
					try {
						Double tempValue = Double.parseDouble(parts[i - 1])
								* Double.parseDouble(parts[i + 1]);
						parts[i - 1] = "" + tempValue;

						for (int k = i; k < parts.length - 2; k++) {
							parts[k] = parts[k + 2];
						}

						parts[parts.length - 1] = "";
						parts[parts.length - 2] = "";

						i = 0;
					} catch (NumberFormatException division) {
						terminateFormula(parts);
					}
				} else {
					try {
						Double tempValue = Double.parseDouble(parts[i - 1])
								/ Double.parseDouble(parts[i + 1]);
						parts[i - 1] = "" + tempValue;

						for (int k = i; k < parts.length - 2; k++) {
							parts[k] = parts[k + 2];
						}

						parts[parts.length - 1] = "";
						parts[parts.length - 2] = "";

						i = 0;
					} catch (NumberFormatException division) {
						terminateFormula(parts);
					}

				}

			}

		}
	}

	private void add_sub(String[] parts) {

		for (int i = 1; i < parts.length; i++) {
			if (parts[i].equals("+")
					|| (parts[i].equals("-") && parts[i].length() == 1)) {
				if (parts[i].equals("+")) {
					try {
						Double tempValue = Double.parseDouble(parts[i - 1])
								+ Double.parseDouble(parts[i + 1]);
						parts[i - 1] = "" + tempValue;

						for (int k = i; k < parts.length - 2; k++) {
							parts[k] = parts[k + 2];
						}

						parts[parts.length - 1] = "";
						parts[parts.length - 2] = "";

						i = 0;
					} catch (NumberFormatException plus) {
						terminateFormula(parts);
					}

				} else {
					try {
						Double tempValue = Double.parseDouble(parts[i - 1])
								- Double.parseDouble(parts[i + 1]);
						parts[i - 1] = "" + tempValue;

						for (int k = i; k < parts.length - 2; k++) {
							parts[k] = parts[k + 2];
						}

						parts[parts.length - 1] = "";
						parts[parts.length - 2] = "";

						i = 0;
					} catch (NumberFormatException minus) {
						terminateFormula(parts);
					}
				}

			}

		}
	}

	private String sum(String sum) {

		String range = "";
		try {
			range = sum.substring(sum.indexOf("(") + 1, sum.lastIndexOf(")"));

			String upperLeft = range.substring(0, range.indexOf('-'));
			String lowerRight = range.substring(range.indexOf('-') + 1);

			if (!Commands.checkRange(upperLeft, lowerRight)) {
				return "";
			}
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}

		int boxWidth = range.charAt(range.indexOf('-') + 1) - range.charAt(0)
				+ 1;
		int boxHeight = Integer
				.parseInt(range.substring(range.indexOf('-') + 2))
				- Integer.parseInt(range.substring(1, range.indexOf('-'))) + 1;

		int startingHorizontalDisplacement = range.charAt(0) - 65;
		int startingVerticalDisplacement = Integer.parseInt(range.substring(1,
				range.indexOf('-'))) - 1;

		double total = 0;
		int numOfCells = 0;
		for (int i = 0; i < boxHeight; i++) {
			for (int k = 0; k < boxWidth; k++) {
				int row = startingVerticalDisplacement + i;
				int column = startingHorizontalDisplacement + k;
				total += Data.Sheet[row][column].value;
				numOfCells++;

				String Address = (char) (column + 65) + "" + (char) (row + 49);
				arrlist.add(Address);
			}
		}
		if (!checkReferences()) {
			return "";
		}
		return total + " " + numOfCells;
	}

	private String avg(String avg) {
		String toBeSplit = sum(avg);
		if (toBeSplit.length() != 0) {
			String[] sumData = toBeSplit.split(" ");

			return (Double.parseDouble(sumData[0]) / Double
					.parseDouble(sumData[1])) + "";
		} else {
			return "";
		}

	}

	public void terminateFormula(String[] parts) {
		parts[0] = "";

		Main.returnError = "Formula couldn't be evaluated";
	}
}
