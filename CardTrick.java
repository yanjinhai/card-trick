import java.util.ArrayList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;

/**
 * TODO:
 * - Zoom in/out
 * - Icons
 *
 */
public class CardTrick extends Application {

	static final int LOWEST_CARD_VALUE = 2;
	static final int HIGHEST_CARD_VALUE = 14;
	static final int NUMBER_OF_VISIBLE_CARDS = 4;

	@Override  
    public void start(Stage stage) throws Exception {

		GridPane root = new GridPane();  
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setMaxWidth(Region.USE_PREF_SIZE);
		root.setAlignment(Pos.CENTER);
		root.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(100),
				new ColumnConstraints(100));
		root.setGridLinesVisible( true );

		//TilePane tilePane = new TilePane();  
		//tilePane.setHgap(10);
		//tilePane.setVgap(10);
		//tilePane.setPadding(new Insets(10, 10, 10, 10));
		//tilePane.setOrientation(Orientation.HORIZONTAL);
		//tilePane.setPrefColumns(3);
		//tilePane.setMaxWidth(Region.USE_PREF_SIZE);

		String[] suits = {"Clubs", "Hearts", "Spades", "Diamonds"};

		int[] cardValues = new int[NUMBER_OF_VISIBLE_CARDS];
		String[] cardSuits = new String[NUMBER_OF_VISIBLE_CARDS];
		for (int i = 0; i < cardValues.length; i++) {
			Label label = new Label ("Visible Card #" + (i + 1));
			MenuButton valueMenu = new MenuButton("Value");
			for (int j = LOWEST_CARD_VALUE; j <= HIGHEST_CARD_VALUE; j++) {
				MenuItem valueItem = new MenuItem(valueToRank(j));

				final int index = i;
				valueItem.setOnAction(e -> {
					String itemText = valueItem.getText();
					valueMenu.setText(itemText);
					cardValues[index] = rankToValue(itemText);
				});

				valueMenu.getItems().add(valueItem);
			}

			MenuButton suitMenu = new MenuButton("Suit");
			for (String s : suits) {
				MenuItem suitItem = new MenuItem(s);
				final int index = i;
				suitItem.setOnAction(e -> {
					String itemText = suitItem.getText();
					suitMenu.setText(itemText);
					cardSuits[index] = itemText;
				});
				suitMenu.getItems().add(suitItem);
			}

			root.addRow(i, label, valueMenu, suitMenu);
			//tilePane.getChildren().addAll(label, valueMenu, suitMenu);
		}

		Button guessButton = new Button("Guess Hidden Card");           
		Label answerText = new Label();
		guessButton.setOnAction(e -> {  
			// an array of integers representing indices in cardValues/cardSuits, sorted from
			// the highest card to lowest card
			int[] indiciesSorted = new int[cardValues.length - 1]; 

			if (compareCard(cardValues[0], cardValues[1], cardSuits[0], cardSuits[1], suits)) {
				indiciesSorted[0] = 0;
				indiciesSorted[1] = 1;
			} else {
				indiciesSorted[0] = 1;
				indiciesSorted[1] = 0;
			} 

			if (compareCard(cardValues[2], cardValues[indiciesSorted[0]],
						cardSuits[2], cardSuits[indiciesSorted[0]], suits)) {
				indiciesSorted[2] = indiciesSorted[1];
				indiciesSorted[1] = indiciesSorted[0];
				indiciesSorted[0] = 2;
			} else if (compareCard(cardValues[2], cardValues[indiciesSorted[1]],
						cardSuits[2], cardSuits[indiciesSorted[1]], suits)) {
				indiciesSorted[2] = indiciesSorted[1];
				indiciesSorted[1] = 2;
			} else {
				indiciesSorted[2] = 2;
			}


			int offset = (indiciesSorted[0] + 1) * 2;
			if (indiciesSorted[1] < indiciesSorted[2]) {
				offset--;
			}

			int answerValue = cardValues[cardValues.length - 1] + offset;

			// wrap around card ranks/values from King -> Ace -> 2
			if (answerValue > HIGHEST_CARD_VALUE) {
				answerValue -= HIGHEST_CARD_VALUE - LOWEST_CARD_VALUE + 1;
			}

			String answerSuit = cardSuits[cardSuits.length - 1];
			String answer = valueToRank(answerValue) + " of " + answerSuit;
			answerText.setText(answer);
		});

		root.addRow(cardValues.length, guessButton, answerText);
		//tilePane.getChildren().addAll(guessButton, answerText);

		Scene scene = new Scene(root, 600, 400);      
		//Scene scene = new Scene(new StackPane(tilePane), 600, 400);      
		stage.setScene(scene);  
		stage.setTitle("Card Trick");  
		stage.show();  
	}

	// returns true if the card represented by value1 and suit1 is higher than the card represented
	// by value2 and suit2
	private static boolean compareCard(int value1, int value2, String suit1, String suit2, 
			String[] suits) {
		if (value1 > value2) {
			return true;
		}

		if (value1 < value2) {
			return false;
		} 

		// The higher card is determined by which card's suit appears first in
		// suits[]
		for (String s : suits) {
			if (s.equals(suit1)) {
				return true;
			} 
			if (s.equals(suit2)) {
				return false;
			}
		}

		return false;
	}

	private static int rankToValue(String rank) /*throws Exception*/ {
		switch (rank) {
			case "Jack":
				return 11;
			case "Queen":
				return 12;
			case "King":
				return 13;
			case "Ace":
				return 14;
			default:
				int value = Integer.parseInt(rank);
				// Ranks of "11", "12", "13", and "14" are allowed
				// if (value < LOWEST_CARD_VALUE || value > HIGHEST_CARD_VALUE) {
				// 	throw new Exception("Error: invalid rank of " + rank + " passed as a parameter"
				// 			+ "to rankToValue(String rank). Please pass a rank as a number from 2" +
				// 			"to 10, \"Jack\", \"Queen\", \"King\", or \"Ace\".");
				// }
				return value;
		}
	}

	private static String valueToRank(int value) /*throws Exception*/ {
		//if (value < LOWEST_CARD_VALUE || value > HIGHEST_CARD_VALUE) {
		//	throw new Exception("Error: invalid value of " + value + " passed as a parameter to" +
		//			"valueToRank(int value). Please pass a value from " + LOWEST_CARD_VALUE + " to "
		//			+ HIGHEST_CARD_VALUE + ".");
		//}
		switch (value) {
			case 11:
				return "Jack";
			case 12:
				return "Queen";
			case 13:
				return "King";
			case 14:
				return "Ace";
			default:
				return Integer.toString(value);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
