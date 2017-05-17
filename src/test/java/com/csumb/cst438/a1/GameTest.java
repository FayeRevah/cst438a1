package com.csumb.cst438.a1;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of Game class
 * @author david wisneski
 * @veraion 1.0
 */
public class GameTest {
    
    public GameTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getState method, of class Game.
     * at start of game, state should be 1.
     * a correct guess will not change the state
     * an incorrect guess will increase state by 1
     */
    @org.junit.Test
    public void testGetState() {
        System.out.println("getState");
        Game instance = new Game();
        int expResult = 1;
        int result = instance.getState();
        assertEquals(expResult, result);
        instance.playGame('c');
        result = instance.getState();
        assertEquals(expResult, result);
        instance.playGame('d');
        result = instance.getState();
        assertEquals(expResult+1, result);
    }

    /**
     * Test of getWord method, of class Game.
     */
    @org.junit.Test
    public void testGetWord() {
        System.out.println("getWord");
        Game instance = new Game();
        String expResult = instance.getWord();
        String result = instance.getWord();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayWord method, of class Game.
     */
    @org.junit.Test
    public void testGetDisplayWord() {
        System.out.println("getDisplayWord");
        Game instance = new Game();
        instance.startNewGame();
        
        String word = instance.getWord();
        String expResult = "";
        
        for(int i = 0; i < word.length(); i++){
            expResult += "_";
            if(!(i == word.length() - 1))
                expResult += " ";
        }
        
        //System.out.println("ExpResult: " + expResult + ":" + word + "\n");
        //guess first character, play it, and update displayWord
        String result = instance.getDisplayWord();
        assertEquals(expResult, result);
        instance.playGame(word.charAt(0));
        result = instance.getDisplayWord();
        
        //String should guess the first character and fill it in
        char firstChar = word.charAt(0);
        String afterOneGuess = firstChar + expResult.substring(1);
        //System.out.println("After one guess: " + afterOneGuess);
        assertEquals(afterOneGuess, result);
    }

    /**
     * Test of startNewGame method, of class Game.
     */
    @org.junit.Test
    public void testStartNewGame() {
        System.out.println("startNewGame");
        Game instance = new Game();
        instance.startNewGame();
        instance.playGame('c');
        instance.playGame('d');
        instance.startNewGame();
        int result = instance.getState();
        assertEquals(1,result);
    }

    /**
     * Test of playGame method, of class Game.
     *   correct guess should return 0 , or 1 when game is won
     *   incorrect guess should return 2, or 3 when game is lost
     */
    @org.junit.Test
    public void testPlayGame() {
        System.out.println("playGame");
        Game instance = new Game();
        instance.startNewGame();
        
        int expResult = 0;
        String word = instance.getWord();
        char guess;
        int result;
       
        //testing correct results of all characters other than the last
        //result = 0 (good guess, continue game)
        for(int i = 0; i < word.length() - 1; i++)
        {
            guess = word.charAt(i);
            result = instance.playGame(guess);
            //System.out.println("GUESS: " + guess + "\n");
            assertEquals(0, result);
        }
        
        //testing final character, win game
        //result = 1 (good guess, win game)
        char lastChar = word.charAt(word.length() - 1);
        result = instance.playGame(lastChar);
        assertEquals(1, result);
        
    }
    
}
