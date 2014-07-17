/*
 * Currently not used due to exceptions being generated.  
 * Provides a basic combobox functionality to table column.
 * Almost working; could be useful in future.
 */

package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import roles.User;

/**
 * Provides combobox functionality to a table column.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class TComboBox extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
	
	private final JTable myTable;
	private final Action myAction;
	private final JComboBox<String> myRenderCB;
	private final JComboBox<String> myEditCB;
	List<List<User>> myReviewerLists;
	private Object myCellEditorValue;
	private boolean isEditButton;
	
	public TComboBox(final JTable theTable, final Action theAction, final int theColumn, List<List<User>> theReviewerLists) {
		myTable = theTable;
		myAction = theAction;
		myReviewerLists = theReviewerLists;
		
		myRenderCB = new JComboBox<String>();
		myEditCB = new JComboBox<String>();
		myEditCB.addActionListener(this);
		
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
		
		myEditCB.setModel(new JComboBox<String>(new String[] {"pig", "cat", "dog"}).getModel());//theNameList.toArray(theArray)).getModel());

		this.myCellEditorValue = value;
		return myEditCB;
		
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, Object value,
													final boolean isSelected, final boolean hasFocus, 
													final int row, final int column) {
		
		do {
				
			if (isSelected)
			{
				myRenderCB.setForeground(table.getSelectionForeground());
		 		myRenderCB.setBackground(table.getSelectionBackground());
			}
			else
			{
				myRenderCB.setForeground(table.getForeground());
				myRenderCB.setBackground(Color.WHITE);
			}
			
			myRenderCB.setModel(new JComboBox<String>(new String[]{(String) value}).getModel());
			
		} while (myRenderCB == null);
		

		return myRenderCB;

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (myTable.isEditing() &&  myTable.getCellEditor() == this) {
			isEditButton = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(final MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		
		isEditButton = false;
		
		if (isEditButton &&  myTable.isEditing()) {
		    myTable.getCellEditor().stopCellEditing();
		}
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final int row = myTable.convertRowIndexToModel( myTable.getEditingRow() );
		fireEditingStopped();
		final ActionEvent event = new ActionEvent(myTable, ActionEvent.ACTION_PERFORMED, "" + row);
		myAction.actionPerformed(event);
	}

	

}
