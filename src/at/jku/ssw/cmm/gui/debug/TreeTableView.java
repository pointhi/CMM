/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui.debug;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.InitTreeTableData;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeUtils;

public class TreeTableView{
	
	public TreeTableView( GUImain main, JPanel panel, String fileName ){
		
		this.main = main;
		this.panel = panel;
		this.forceUpdate = true;
		
		this.init(fileName);
	}
	
	private final GUImain main;
	
	// Main panel
	private JPanel panel;
	
	// Tree table for variables
	private TreeTable varTreeTable;
	private TreeTableDataModel varTreeTableModel;
	
	private boolean forceUpdate;
	
	/**
	 * Initializes the variable view table/tree table, etc
	 * 
	 * @param panel
	 */
	public void init( String fileName ) {
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		this.varTreeTableModel = new TreeTableDataModel(InitTreeTableData.createDataStructure(fileName));
		
		this.varTreeTable = new TreeTable(this.main, this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		this.panel.add(p, BorderLayout.CENTER);
		// Sub-panel end
	}

	/**
	 * Updates variable values and call stack
	 * 
	 * @param compiler
	 */
	public void update(final CMMwrapper compiler, final String fileName, boolean completeUpDate ) {
		
		System.out.println("updating #1234");
		
		if( completeUpDate || this.forceUpdate ){
			DebugShell.out(State.LOG, Area.READVAR, "complete variable structure update");
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.setTreeModel(InitTreeTableData.readSymbolTable(compiler, main, fileName));
				}
			});
			this.forceUpdate = false;
			this.varTreeTable.revalidate();
			this.varTreeTable.repaint();
		}
		else{
			DebugShell.out(State.LOG, Area.READVAR, "updating variable values");
			InitTreeTableData.updateTreeTable(this.varTreeTable.getTreeModel(), (DataNode)this.varTreeTable.getCellRenderer().getModel().getRoot(), compiler, main, fileName);
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.updateTreeModel();
				}
			});
			this.varTreeTable.revalidate();
			this.varTreeTable.repaint();
		}
	}

	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public void standby( String fileName ) {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel(InitTreeTableData.createDataStructure(fileName)));
		this.forceUpdate = true;
		
		DebugShell.out(State.LOG, Area.GUI, "treetable standby");
	}
	
	/**
	 * Highlights the variable with the given address in the variable tree table
	 * 
	 * @param adr The address of the variable which shall be highlighted
	 * @param changed TRUE if highlighting changed variables,
	 * 		FALSE if highlighting read variables
	 */
	public void highlightVariable( int adr, boolean changed ){
		TreeUtils.expandByAddress(varTreeTable, adr, changed);
		varTreeTable.repaint();
	}
	
	public void updateFontSize(){
		varTreeTable.setFont(varTreeTable.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
		varTreeTable.repaint();
	}
}
