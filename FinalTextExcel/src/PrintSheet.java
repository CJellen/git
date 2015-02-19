public class PrintSheet {

	public static int rows;
	public static int columns;

	public static void print(String s) {
		System.out.print(s);
	}

	public static void println(String s) {
		System.out.println(s);
	}

	public static void printData() {

		String LinetoPrint = "__";
		char topcharacter = 'A';
		for (int i = 1; i < columns + 1; i++) {
			if (topcharacter == 'N') {
				System.out
						.print("                                                          ");
			} else {
				if (i == 1) {
					LinetoPrint += "_|_____" + topcharacter + "____";
					topcharacter++;
				} else if (i == columns) {
					LinetoPrint += "|_____" + topcharacter + "____|";
					topcharacter++;
				} else {
					LinetoPrint += "|_____" + topcharacter + "____";
					topcharacter++;
				}
			}
		}
		LinetoPrint += "\n";
		for (int i = 1; i < rows + 1; i++) {
			LinetoPrint += (i < 10 ? "  " : " ") + i;
			for (int j = 1; j < columns + 1; j++) {
				if (j == rows) {
					LinetoPrint += PrintFormLast(i - 1, j - 1);

				} else {
					LinetoPrint += PrintForm(i - 1, j - 1);

				}
			}
			LinetoPrint += "\n";
		}
		System.out.println(LinetoPrint);
	}

	public static String PrintForm(int rowNum, int columnNum) {
		String cellString = Data.Sheet[rowNum][columnNum].toString();
		return String
				.format("| %8.8s ", (cellString == null ? "" : cellString));

	}

	public static String PrintFormLast(int rowNum, int columnNum) {
		String cellString = Data.Sheet[rowNum][columnNum].toString();
		return String.format("| %8.8s |",
				(cellString == null ? "" : cellString));

	}

}
