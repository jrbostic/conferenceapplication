package main;

import javax.swing.SwingUtilities;

import gui.Login;

/**
 * Starts up program.
 * 
 * @author Jesse Bostic
 *
 */
public class Main {
	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
		    public void run() {
		        new Login();
		    }
		});
	}
}
