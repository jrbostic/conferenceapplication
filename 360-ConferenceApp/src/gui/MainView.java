package gui;

import java.util.Observable;

import roles.User;

/**
 * Class for instantiating a concrete selector.
 * (basically implements observable... ultimately somewhat superfluous, 
 * 		but could implement additional non-essentials... originally meant
 * 		to contain role panel logic.)
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class MainView extends Selecter {
	
	/**
	 * Creates new mainview selecter.
	 * 
	 * @param theUser the current user
	 */
	public MainView(final User theUser) {
		super(theUser);
		super.myUser.addDBObserver(this);
	}
	
	/**
	 * Recreates role selecter's role combobox and role panel.
	 */
	@Override
	public void update(final Observable o, final Object arg) {
		super.createRoleView();
		super.populateRoles();
	}	
	
}
