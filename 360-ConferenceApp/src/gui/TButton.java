package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Provides implementation for a column of buttons in a JTable.
 * 
 * Modeled after http://tips4java.wordpress.com/2009/07/12/table-button-column/ by Rob Camick
 * 
 * @author Rob Camick (?)
 * @editor Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class TButton extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
	
	/**
	 * the table being added to
	 */
	private final JTable myTable;
	
	/**
	 * the action be attached to button
	 */
	private final Action myAction;
	
	/**
	 * the rendering of normal cell state
	 */
	private final JButton myRenderButton;
	
	/**
	 * the rendering of interacting cell state
	 */
	private final JButton myEditButton;
	
	/**
	 * ?
	 */
	private Object myCellEditorValue;
	
	/**
	 * boolean reporting if cell is being interacted with
	 */
	private boolean isEditButton;
	
	/**
	 * Sets up the basic associations
	 * 
	 * @param theTable table to modify
	 * @param theAction action to add
	 * @param theColumn column to add actions and rendering to
	 */
	public TButton(final JTable theTable, final Action theAction, final int theColumn) {
		myTable = theTable;
		myAction = theAction;
		
		myRenderButton = new JButton();
		myEditButton = new JButton();
		myEditButton.setFocusPainted(false);
		myEditButton.addActionListener(this);
		
		final TableColumnModel columnModel = theTable.getColumnModel();
		columnModel.getColumn(theColumn).setCellRenderer( this );
		columnModel.getColumn(theColumn).setCellEditor( this );
		theTable.addMouseListener( this );
	}

	@Override
	public Object getCellEditorValue() {
		return myCellEditorValue;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, 
													final boolean isSelected, final int row, 
													final int column) {
		
		myEditButton.setText( value.toString() );

		this.myCellEditorValue = value;
		return myEditButton;
		
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value,
													final boolean isSelected, final boolean hasFocus, 
													final int row, final int column) {
		if (isSelected)
		{
			myRenderButton.setForeground(table.getSelectionForeground());
	 		myRenderButton.setBackground(table.getSelectionBackground());
		}
		else
		{
			myRenderButton.setForeground(table.getForeground());
			myRenderButton.setBackground(UIManager.getColor("Button.background"));
		}

		myRenderButton.setText( value.toString() );

		return myRenderButton;

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		if (myTable.isEditing() &&  myTable.getCellEditor() == this) {
				isEditButton = true;
		}

	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		if (isEditButton &&  myTable.isEditing()) {
		    myTable.getCellEditor().stopCellEditing();
		}

		isEditButton = false;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final int row = myTable.convertRowIndexToModel( myTable.getEditingRow() );
		fireEditingStopped();
		final ActionEvent event = new ActionEvent(myTable, ActionEvent.ACTION_PERFORMED, "" + row);
		myAction.actionPerformed(event);
	}

	

}
