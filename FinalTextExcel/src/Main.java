import java.util.Scanner;

public class Main {

	public static String returnError = "";

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);

		Dimensions();

		Data.SheetCopy = Data.Sheet.clone();

		PrintSheet.printData();

		String command = null;

		do {

			printErrorMessage();

			System.out.print("> ");

			command = scan.nextLine().trim();
			System.out.println();

			if (!command.equals("quit")) {

				InputHandling(command);
			}

		} while (!command.toLowerCase().equals("quit"));
		
		System.out.println("");
		scan.close();

	}

	public static void Dimensions() {

		Scanner getDimensions = new Scanner(System.in);

		try {
			System.out.println("Enter the dimensions of the spreadsheet nXn");

			String dimensions = getDimensions.nextLine();
			dimensions.trim();

			SheetCreation(dimensions);
		} catch (Exception e) {
			
			System.out.println("Not formatted correctly");
			Main.Dimensions();
		}
	}

	public static void InputHandling(String userInput) {

		Commands.findCommand(userInput);
		PrintSheet.printData();
	}

	public static void SheetCreation(String dimensions) {

		PrintSheet.rows = Integer.parseInt(dimensions.substring(0,
				dimensions.indexOf('X')));
		PrintSheet.columns = Integer.parseInt(dimensions.substring(dimensions
				.indexOf('X') + 1));

		Data.Sheet = new Cell[PrintSheet.rows][PrintSheet.columns];

		for (int i = 0; i <= PrintSheet.rows - 1; i++) {
			for (int j = 0; j <= PrintSheet.columns - 1; j++) {
				
				Data.Sheet[i][j] = new Cell("       ", 1);
				String xLocation = (char) (j + 'A') + "";
				int yLocation = (i + 1);
				Data.Sheet[i][j].location = xLocation + yLocation;
			}
		}
	}

	public static void printErrorMessage() {
		
		if (!returnError.equals("")) {
			System.out.print("the error is that ");
			System.out.print(returnError);
		}
		returnError = "";
	}
}
