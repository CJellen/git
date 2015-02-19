import java.io.*;
import java.util.*;

public class Commands {

	public static void findCommand(String inputCommand) {

		if (inputCommand.toUpperCase().indexOf("CLEAR") != -1) {

			clearCell(inputCommand.toUpperCase());

		} else if (inputCommand.toUpperCase().indexOf("SORT") != -1) {

			sort(inputCommand.toUpperCase());
			
		} else if (inputCommand.toUpperCase().indexOf("ALLRANDOM") != -1) {

			allrandom();

		} else if (inputCommand.toUpperCase().indexOf("IMPORT") != -1) {

			int startidx = inputCommand.toUpperCase().indexOf("IMPORT") + 7;
			String location = inputCommand.substring(startidx).trim();
			importFile(location);

		} else if (inputCommand.toUpperCase().indexOf("EXPORT") != -1) {

			int startidx = inputCommand.indexOf("EXPORT") + 7;
			String location = inputCommand.substring(startidx).trim();
			exportFile(location);
			
		} else if (inputCommand.toUpperCase().indexOf("HELP") != -1) {
			helpList();

		} else if (inputCommand.indexOf("=") != -1) {

			setCellCommand(inputCommand, true);

		} else if (inputCommand.toUpperCase().indexOf("CELL ") != -1) {

			inputCommand = inputCommand.substring(inputCommand.toUpperCase().indexOf("CELL ") + 4).trim();
			showCell(inputCommand);

		} else if (inputCommand.toUpperCase().indexOf("NEW DIMENSIONS") != -1) {

			inputCommand = inputCommand.substring(
					inputCommand.toUpperCase().indexOf("NEW DIMENSIONS") + 14).trim();
			createNewArray(inputCommand);

		} else {

			Main.returnError = "Unable to determine command";
		}
	}

	public static boolean checkRange(String upperBound, String lowerBound) {

		int Length = Data.Sheet.length;
		int Width = Data.Sheet[0].length;

		try {

			int yTop1 = upperBound.charAt(0) - 64;
			int xTop1 = Integer.parseInt(upperBound.substring(1));

			if (yTop1 > Length || xTop1 > Width) {
				return false;
			}

			int yBot1 = lowerBound.charAt(0) - 64;
			int xBot1 = Integer.parseInt(lowerBound.substring(1));

			if (yBot1 > Length || xBot1 > Width) {
				return false;
			}

			if (xTop1 > xBot1
					|| yTop1 > yBot1) {
				return false;
			}

			return true;

		} catch (NumberFormatException inValidRange) {

			return false;
		}

	}
	
	public static void allrandom() {

		for (int i = 0; i < PrintSheet.rows; i++) {

			for (int j = 0; j < PrintSheet.columns; j++) {

				Random r = new Random();
				double randomValue = -9999 + (9999 - -9999) * r.nextDouble();
				
				Data.Sheet[i][j].setCell(randomValue+"", 2);
;
			}
		}
	}
	
	public static void clearCell(String com) {

		if (com.indexOf(' ') == -1) {

			for (int i = 0; i < PrintSheet.rows; i++) {

				for (int j = 0; j < PrintSheet.columns; j++) {

					Data.Sheet[i][j] = new Cell("       ", 1);
				}
			}

		} else {

			try {

				int addressIndex = com.indexOf("CLEAR") + 5;
				String address = com.substring(addressIndex).trim();

				int xvals = address.charAt(0) - 65;
				int yvals = address.charAt(1) - 49;

				if (!checkRange("A1", address)) {

					StringIndexOutOfBoundsException e = new StringIndexOutOfBoundsException();
					throw e;
				}

				int tableColumn = Data.Sheet.length;
				int tableRow = Data.Sheet[0].length;

				boolean refered = true;

				for (int i = 0; i < tableColumn; i++) {
					for (int k = 0; k < tableRow; k++) {

						if (Data.Sheet[i][k].type == 4) {

							if (!Data.Sheet[i][k].formula.check(address)) {

								refered = false;
							}
						}
					}
				}

				if (!refered) {

					Data.Sheet[yvals][xvals] = new Cell("0.0", 2);
					Main.returnError = "Cell Defaulted to 0 because \nit is used in a formula";
					return;
				}

				Data.Sheet[yvals][xvals] = new Cell("       ", 1);

			} catch (StringIndexOutOfBoundsException e) {

				Main.returnError = "Unable to determine the inputted command";
			}
		}

	}

	public static void sort(String com) {

		try {

			String range = com.substring(com.indexOf("SORT") + 5).trim();

			String upperLeft = range.substring(0, range.indexOf('-'));
			String lowerRight = range.substring(range.indexOf('-') + 1);

			if (!checkRange(upperLeft, lowerRight)) {
				Main.returnError = "Range is invalid";
				return;
			}

			int rangeWidth = range.charAt(range.indexOf('-') + 1)
					- range.charAt(0) + 1;
			int rangeHeight = Integer.parseInt(range.substring(range
					.indexOf('-') + 2))
					- Integer.parseInt(range.substring(1, range.indexOf('-')))
					+ 1;

			double[] tempValues = new double[rangeWidth * rangeHeight];

			int startingHorizontalDisplacement = range.charAt(0) - 65;
			int startingVerticalDisplacement = Integer.parseInt(range
					.substring(1, range.indexOf('-'))) - 1;

			int tempidx = 0;
			for (int i = 0; i < rangeHeight; i++) {
				for (int k = 0; k < rangeWidth; k++) {
					tempValues[tempidx] = Data.Sheet[startingVerticalDisplacement
							+ i][startingHorizontalDisplacement + k].value;
					tempidx++;
				}
			}

			int[] metaData = { rangeWidth, rangeHeight,
					startingHorizontalDisplacement,
					startingVerticalDisplacement };

			if (areAllDoubles(metaData)) {
				char order = com.charAt(com.indexOf("SORT") + 4);

				if (order == 'A') {

					arrangeAscending(tempValues);

				} else if (order == 'D') {

					arrangeDescending(tempValues);

				} else {

					Exception e = new Exception();

					throw e;
				}

				tempidx = 0;
				for (int i = 0; i < rangeHeight; i++) {
					for (int k = 0; k < rangeWidth; k++) {

						Data.Sheet[startingVerticalDisplacement + i][startingHorizontalDisplacement
								+ k].value = tempValues[tempidx];
						tempidx++;
					}
				}

			} else {

				System.out.println("Not all values in range were doubles");
			}
		} catch (Exception e) {

			Main.returnError = "Error with sort command";
		}
	}

	public static boolean areAllDoubles(int[] inputData) {
		boolean isTrue = true;

		for (int i = 0; i < inputData[1]; i++) {
			for (int j = 0; j < inputData[0]; j++) {

				int displayContent = Data.Sheet[inputData[3] + i][inputData[2]
						+ j].type;

				if (displayContent != 2) {

					isTrue = false;
				}
			}
		}
		return isTrue;
	}

	public static void arrangeAscending(double[] tempValues) {
		
		for (int i = 1; i < tempValues.length; i++) {
			int j = i;

			while (tempValues[j] < tempValues[j - 1]) {

				double tempDouble1 = tempValues[j];
				double tempDouble2 = tempValues[j - 1];

				tempValues[j] = tempDouble2;
				tempValues[j - 1] = tempDouble1;

				if (j >= 2)
					j--;
			}
		}
	}

	public static void arrangeDescending(double[] tempValues) {
		for (int i = 1; i < tempValues.length; i++) {

			int j = i;

			while (tempValues[j] > tempValues[j - 1]) {

				double tempDouble1 = tempValues[j];
				double tempDouble2 = tempValues[j - 1];

				tempValues[j] = tempDouble2;
				tempValues[j - 1] = tempDouble1;

				if (j >= 2)
					j--;
			}
		}
	}

	public static void exportFile(String location) {
		File f = new File(location);
		PrintStream p = null;
		try {
			p = new PrintStream(f);
			p.println(PrintSheet.rows + "X" + PrintSheet.columns);

			for (int i = 0; i < PrintSheet.rows; i++) {
				for (int k = 0; k < PrintSheet.columns; k++) {

					p.print(Data.Sheet[i][k].location + ":");

					if (Data.Sheet[i][k].type == 4) {

						p.print(Data.Sheet[i][k].formula.inputFormula);

					} else {

						p.print(Data.Sheet[i][k].toString());

					}
					p.println("," + Data.Sheet[i][k].type);
				}
			}
			p.close();
		} catch (Exception e) {

			Main.returnError = "invalid location";
		}

	}

	public static void importFile(String location) {

		File f = new File(location);
		Scanner s;
		Scanner m;

		try {

			s = new Scanner(f);
			boolean isValid = checkImportedFile(s);

			m = new Scanner(f);

			if (isValid) {

				String dimensions = m.nextLine();
				Main.SheetCreation(dimensions);

				while (m.hasNextLine()) {
					String lineConversion = m.nextLine();

					int column = lineConversion.charAt(0) - 65;
					int row = Integer.parseInt(lineConversion.substring(1,
							lineConversion.indexOf(':'))) - 1;

					String data = lineConversion.substring(
							lineConversion.indexOf(':') + 1,
							lineConversion.indexOf(','));

					int dataType = Integer.parseInt(lineConversion.substring(
							lineConversion.lastIndexOf(',') + 1).trim());
					Data.Sheet[row][column].setCell(data, dataType);

				}
			} else {

				Main.returnError = "failed to import";
			}

		} catch (FileNotFoundException e) {

			Main.returnError = "no file";

		}
	}

	public static boolean checkImportedFile(Scanner s) {
		boolean isValid = true;
		int range[] = new int[2];

		try {

			String dimensions = s.nextLine();

			int row = Integer.parseInt(dimensions.substring(0,
					dimensions.indexOf('X')));
			int column = Integer.parseInt(dimensions.substring(dimensions
					.indexOf('X') + 1));

			int numTest = row * column;
			if (numTest <= 0) {
				Exception exception = new Exception();
				throw exception;
			}
			range[0] = row;
			range[1] = column;

		} catch (Exception e) {

			isValid = false;
		}

		try {

			while (s.hasNextLine()) {

				String lineToCheck = s.nextLine();
				int dataType = Integer.parseInt(lineToCheck.substring(
						lineToCheck.lastIndexOf(',') + 1).trim());
				int column = lineToCheck.charAt(0) - 65;
				int row = Integer.parseInt(lineToCheck.substring(1,
						lineToCheck.indexOf(':'))) - 1;

				if (lineToCheck.length() < 5) {
					isValid = false;

				} else if (lineToCheck.indexOf(':') != 2
						&& lineToCheck.indexOf(':') != 3) {

					isValid = false;

				} else if (dataType < 1 || dataType > 4) {

					isValid = false;

				} else if (!testLocation(row, column, range)) {

					isValid = false;

				}
			}

		} catch (Exception e) {

			isValid = false;
		}

		return isValid;

	}
	
	public static void createNewArray(String s) {

		int row = 0;
		int column = 0;

		try {

			column = Integer.parseInt(s.substring(0, s.indexOf('X')));
			row = Integer.parseInt(s.substring(s.indexOf('X') + 1));

		} catch (Exception e) {
			Main.returnError = "Could not determine dimensions of new table!";
			return;

		}

		Data.SheetCopy = new Cell[row][column];

		for (int i = 0; i <= row - 1; i++) {
			for (int k = 0; k <= column - 1; k++) {

				Data.SheetCopy[i][k] = new Cell("       ", 1);
				String xLocation = (char) (k + 'A') + "";

				int yLocation = (i + 1);

				Data.SheetCopy[i][k].location = xLocation + yLocation;

			}
		}

		int tempRow;

		if (row > Data.Sheet.length) {

			tempRow = Data.Sheet.length;

		} else {

			tempRow = row;
		}

		int tempColumn;

		if (column > Data.Sheet[0].length) {

			tempColumn = Data.Sheet[0].length;

		} else {

			tempColumn = column;
		}

		for (int i = 0; i <= tempRow - 1; i++) {
			for (int j = 0; j <= tempColumn - 1; j++) {

				char letter = (char) (j + 65);
				char num = (char) (i + 49);
				String holder = letter + "" + num + " = ";

				if (Data.Sheet[i][j].type == 1) {

					holder += "\"" + Data.Sheet[i][j].string + "\"";

				} else {

					holder += Data.Sheet[i][j].toString();
				}

				setCellCommand(holder, false);
			}
		}

		Data.Sheet = Data.SheetCopy;
		PrintSheet.rows = row;
		PrintSheet.columns = column;
	}

	public static boolean testLocation(int row, int column, int[] range) {

		boolean isTrue = true;

		if (row > range[0] || column > range[1]) {
			isTrue = false;
		}
		return isTrue;

	}

	public static void helpList(){
		System.out.println("Below are the supported commands: ");
		System.out.println("- <cell location> (prints the value at that location)");
		System.out.println("- <cell location> = value (assingns values to a cell location)");
		System.out.println("- clear <cell location> (clears a cell)");
		System.out.println("- clearall (clears all cells)");
		System.out.println("- sorta <cell location> - <cell location> (sorts a range, ascending)");
		System.out.println("- clearall (clears all cells)");
		System.out.println("- sortd <cell location> - <cell location> (sorts a range, descending)");
		System.out.println("- resize nXn (this resizes the sheet, while maintaining the values)");
		System.out.println("- allrandom (all values in the table are now random doubles)");
		System.out.println("- help (displays all commands that are supported)");


	}

	public static void setCellCommand(String com, boolean central) {

		try {

			int row = com.charAt(0) - 65;
			int column = com.charAt(1) - 49;

			String data = com.substring(com.indexOf("=") + 1).trim();
			int displayType = inputDataType(data);

			if (displayType != 0) {

				if (central) {

					Data.Sheet[column][row].setCell(data, displayType);

				} else {

					Data.SheetCopy[column][row].setCell(data, displayType);

				}
			} else {

				throw new Exception();
			}
		} catch (Exception invalidCommand) {

			Main.returnError = "The set cell input format was wrong, ";
		}
	}

	public static void showCell(String Address) {

		try {

			int column = Address.charAt(0) - 65;
			int row = Address.charAt(1) - 49;

			System.out.println("Cell " + Address + ": "
					+ Data.Sheet[row][column].contains());

		} catch (ArrayIndexOutOfBoundsException e) {

			Main.returnError = "Can't determine which cell(s) to show";
		}
	}

	public static int inputDataType(String data) {

		if (isString(data)) {

			return 1;

		} else if (isDouble(data)) {

			return 2;

		} else if (isDate(data)) {

			return 3;

		} else if (isFormula(data)) {

			return 4;

		} else {

			return 0;

		}
	}

	public static boolean isString(String s) {

		if (s.length() < 2) {

			return false;

		}

		if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {

			return true;

		} else {

			return false;

		}
	}

	public static boolean isDouble(String s) {

		try {

			Double.parseDouble(s);

		} catch (NumberFormatException e) {

			return false;
		}

		return true;
	}

	public static boolean isDate(String s) {

		if (s.indexOf('/') == -1) {
			return false;
		}
		String[] parts = s.split("/");

		if (parts.length != 3) {
			return false;
		}

		for (int i = 0; i <= 2; i++) {

			String part = parts[i];
			int partLength = part.length();

			if (partLength > 2) {
				return false;
			}

			for (int k = 0; k <= partLength - 1; k++) {
				if (part.charAt(k) < '0' || part.charAt(k) > '9') {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isFormula(String s) {

		if (s.length() < 2) {
			return false;
		}

		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {

			return true;

		} else {

			return false;
		}
	}

	
}