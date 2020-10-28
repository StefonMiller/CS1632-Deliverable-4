import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameOfLifePinningTest {
	/*
	 * READ ME: You may need to write pinning tests for methods from multiple
	 * classes, if you decide to refactor methods from multiple classes.
	 * 
	 * In general, a pinning test doesn't necessarily have to be a unit test; it can
	 * be an end-to-end test that spans multiple classes that you slap on quickly
	 * for the purposes of refactoring. The end-to-end pinning test is gradually
	 * refined into more high quality unit tests. Sometimes this is necessary
	 * because writing unit tests itself requires refactoring to make the code more
	 * testable (e.g. dependency injection), and you need a temporary end-to-end
	 * pinning test to protect the code base meanwhile.
	 * 
	 * For this deliverable, there is no reason you cannot write unit tests for
	 * pinning tests as the dependency injection(s) has already been done for you.
	 * You are required to localize each pinning unit test within the tested class
	 * as we did for Deliverable 2 (meaning it should not exercise any code from
	 * external classes). You will have to use Mockito mock objects to achieve this.
	 * 
	 * Also, you may have to use behavior verification instead of state verification
	 * to test some methods because the state change happens within a mocked
	 * external object. Remember that you can use behavior verification only on
	 * mocked objects (technically, you can use Mockito.verify on real objects too
	 * using something called a Spy, but you wouldn't need to go to that length for
	 * this deliverable).
	 */

	/* TODO: Declare all variables required for the test fixture. */
	MainPanel mp;
	Cell[][] cell_config;
	
	boolean[][] expected_vertical = {
			{false, false, false, false, false},
			{false, false, true, false, false},
			{false, false, true, false, false},
			{false, false, true, false, false},
			{false, false, false, false, false}
	};
	
	boolean[][] expected_horizontal = {
			{false, false, false, false, false},
			{false, false, false, false, false},
			{false, true, true, true, false},
			{false, false, false, false, false},
			{false, false, false, false, false}
	};

	@Before
	public void setUp() {
		/*
		 * TODO: initialize the text fixture. For the initial pattern, use the "blinker"
		 * pattern shown in:
		 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Examples_of_patterns
		 * The actual pattern GIF is at:
		 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#/media/File:Game_of_life_blinker.gif
		 * Start from the vertical bar on a 5X5 matrix as shown in the GIF.
		 */
		
		// Create game panel
		mp = new MainPanel(5);
		
		// Create new 2d Cell array and insert mocked Cells into it
		cell_config = new Cell[5][5];
		
		for(int i = 0; i < 5; i ++) {
			for(int j = 0; j < 5; j++) {
				cell_config[i][j] = Mockito.mock(Cell.class);
			}
		}
		
		// 1, 2 and 2, 2 and 3, 2
		// middle vert. bars will return alive
		Mockito.when(cell_config[1][2].getAlive()).thenReturn(true);
		Mockito.when(cell_config[2][2].getAlive()).thenReturn(true);
		Mockito.when(cell_config[3][2].getAlive()).thenReturn(true);
		
		// Overwrite _cells of the MainPanel with our mock
		mp.setCells(cell_config);
	}

	/* TODO: Write the three pinning unit tests for the three optimized methods */
	@Test
	public void calculateNextIterationTest() {
		// Precondition: above
		
		// Run test to calc next iteration
		mp.calculateNextIteration();
		
		// Postcondition: horiz. bar
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if(expected_horizontal[i][j]) {
					Mockito.verify(cell_config[i][j], Mockito.times(1)).setAlive(true);
				}
				
				else {
					Mockito.verify(cell_config[i][j], Mockito.never()).setAlive(true);
				}
			}
		}
	}
	
	@Test
	public void iterateCellTestAliveToAlive() {
		// Precondition: above
		
		// Run test to iterate a single cell in the very center (2, 2)
		// based on the given vertical bar pattern
		boolean test = mp.iterateCell(2, 2);
		
		// This cell has 2 neighbors, so it should still be alive
		assertTrue(test);
	}
	
	@Test
	public void iterateCellTestAliveToDead() {
		// Precondition: above
		
		// Run test to iterate a single cell at (1, 2)
		// based on the given vertical bar pattern
		boolean test = mp.iterateCell(1, 2);
		
		// This cell has 1 neighbor, so it should die
		assertFalse(test);
	}
	
	@Test
	public void iterateCellTestDeadToAlive() {
		// Precondition: above
		
		// Run test to iterate a single cell at (2, 3)
		// based on the given vertical bar pattern
		boolean test = mp.iterateCell(2, 3);
		
		// This cell has 2 neighbors, so it should resurrect
		assertTrue(test);
	}
	
	@Test
	public void iterateCellTestDeadToDead() {
		// Precondition: above

		// Run test to iterate a single cell at (0, 0)
		// based on the given vertical bar pattern
		boolean test = mp.iterateCell(0, 0);
		
		// This cell has 0 neighbors, so it should stay dead
		assertFalse(test);
	}
	
	@Test
	public void toStringCellTest() {
		// Precondition: some Cell that is alive
		Cell c = new Cell(true);
		
		// Convert the cell's state to a string
		String test = c.toString();
		String expected = "X";
		
		assertEquals(expected, test);
	}
	
	@Test
	public void toStringMainPanelTest() {
		// Precondition: above
		// and: stub cells toString() so they return the right thing
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if(expected_vertical[i][j]) {
					Mockito.when(cell_config[i][j].toString()).thenReturn("X");
				}
				
				else {
					Mockito.when(cell_config[i][j].toString()).thenReturn(".");
				}
			}
		}
		
		// Convert current game grid to string
		String test = mp.toString();
		String expected = ".....\n..X..\n..X..\n..X..\n.....\n";
		
		// Verify results
		assertEquals(expected, test);
	}
	
	@Test
	public void runContinuousTest() {
		
	}

}
