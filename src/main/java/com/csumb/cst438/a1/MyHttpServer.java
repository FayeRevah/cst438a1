package com.csumb.cst438.a1;  
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import java.io.*;

public class MyHttpServer {

	static final String RESOURCE_DIR = "src/main/resources/";
	static final int PORT = 8080;
        static Random generator = new Random();

	public static void main(String[] args) {

		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
			System.out.print("Server started. ");
			// Display current directory
			System.out.print("pwd=" + System.getProperty("user.dir"));
			server.createContext("/", new MyHandler());
			server.setExecutor(null); // creates a default executor
			server.start();
			System.out.println();
			System.out.println("MyHttp Server running.");
		} catch (Exception e) {
			System.out.println("My HttpServer Exception: " + e.getMessage());
		}
	}

	public static class MyHandler implements HttpHandler {
            
                private Game game = new Game();
                private String cookie="0";     // used to keep track of current game 

                /**
                 * handle an HTTP request
                 * @param t
                 * @throws IOException 
                 */
		public void handle(HttpExchange t) throws IOException {
			String uri = t.getRequestURI().toString();
			System.out.println("URI=" + uri);
			if (uri.endsWith(".gif") || uri.endsWith(".ico")) {
				// http get request for an image file
				sendFile(t, uri.substring(1));
			} else {
				// come here to play the game
                                String response = "";
                                // get cookie value from http request
                                String requestCookie = t.getRequestHeaders().getFirst("Cookie");
                                System.out.println("Cookie=" + requestCookie);
                                // if there is no cookie, or it is "0" or differfent from the current value, then start a new game
                                if (requestCookie==null || requestCookie.equals("0") || cookie.equals("0") || !requestCookie.equals(cookie)) {
                                    game.startNewGame();
                                    cookie=generateCookie();    
                                    response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                            + "<form action=\"/\" method=\"get\"> "
                                            + "Guess a character <input type=\"text\" name=\"guess\"><br>"
                                            + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                } else {
                                    // continue with current game
                                    int result;
                                    int index = uri.indexOf('=');
                                    String requestString = uri.substring(index + 1).toLowerCase();
                                    if(requestString.length() > 1 || requestString.length() == 0)//if user guesses >1 character or no character
                                        result = 4;
                                    else //user guesses only 1 character
                                    {
                                        char ch = requestString.charAt(0);  // letter that user has guessed
                                        result = game.playGame(ch);
                                    }
                                    
                                    switch(result) {
                                        case 0: // good guess, continue game
                                            response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                            + "<form action=\"/\" method=\"get\"> "
                                            + "Guess a character <input type=\"text\" name=\"guess\"><br>"
                                            + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                            break;
                                        case 1: // good guess, win game
                                             response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + "</h2>"
                                            + "<h2>Congratulations you win!</h2>" + "</body></html>";
                                             cookie="0";
                                             break;
                                        case 2: // bad guess, continue game
                                            response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                            + "<form action=\"/\" method=\"get\"> "
                                            + "Guess a character <input type=\"text\" name=\"guess\"><br>"
                                            + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                            break;
                                        case 3: // bad guess, lost game
                                             response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h7.gif" + "\">" + "<h2>You lost!  The word is " + game.getWord() + "</h2>"
                                            + "</body></html>";
                                             cookie="0";
                                             break;
                                        case 4:
                                            response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                            + "<form action=\"/\" method=\"get\"> "
                                            + "Guess a character <input type=\"text\" name=\"guess\"><br>"
                                            + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>" + "<h3>You must enter 1 alphabetic character</h3>";
                                            break;
                                    }
                                    
                                }
                                t.getResponseHeaders().set("Content-Type", "text/html");
                                if (cookie!=null) 
                                     t.getResponseHeaders().set("Set-Cookie", cookie);
                                else 
                                     t.getResponseHeaders().set("Set-Cookie", "0");
                                System.out.println("New cookie:" + cookie);
                                t.sendResponseHeaders(200, response.length());
                                System.out.println("response=" + response);
                                OutputStream os = t.getResponseBody();
                                os.write(response.getBytes());
                                os.close();
                         }
                }

                /*
                 * send a gif file
                 */
                private static void sendFile(HttpExchange t, String filename) {

                        try {
                                File file = new File(RESOURCE_DIR + filename);
                                byte[] fileData = new byte[(int) file.length()];
                                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                                dis.readFully(fileData);
                                dis.close();

                                t.getResponseHeaders().set("Content-Type", "image/gif");
                                t.sendResponseHeaders(200, fileData.length);
                                OutputStream os = t.getResponseBody();
                                os.write(fileData);
                                os.close();

                        } catch (Exception e) {
                                System.out.println("Error in sendFile:" + filename + " " + e.getMessage());
                        }

                }
        
                /*
                * generate a random cookie which is a random long integer
                */
                 private String generateCookie() {
                    return Long.toString(generator.nextLong());
                 }
                 
        }  // end of static class MyHandler
        
}
