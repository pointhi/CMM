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
 
package at.jku.ssw.cmm.gui.file;

import static at.jku.ssw.cmm.gettext.Language._;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.debug.ErrorMessage;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;

/**
 * This is a manager class for opening a dialog window for choosing a file in order to save the source
 * code of the RSyntaxTextArea in the main GUI. The save dialog is called
 *  - when the user clicks "file -> save as" or presses a hot key
 *  - when the user tries to save and there is no working directory given in the configuration data
 *  - When the user closes the window and has never saved the current C-- file
 *  
 * @author fabian
 *
 */
public class SaveDialog {
	
	/**
	 * This constructor should be called when the main GUI is initialized.
	 * 
	 * @param jFrame The main GUI window frame
	 * @param jSourcePane The text area with the source code
	 * @param settings The main GUI configuration object
	 */
	public SaveDialog( JFrame jFrame, RSyntaxTextArea jSourcePane, JTextPane jInputPane, GUImainSettings settings ){
		this.jFrame = jFrame;
		this.jSourcePane = jSourcePane;
		this.jInputPane = jInputPane;
		this.settings = settings;
	}
	
	/**
	 * The main GUI window frame
	 */
	private final JFrame jFrame;
	
	/**
	 * The text area with the source code
	 */
	private final RSyntaxTextArea jSourcePane;
	
	/**
	 * The text panel with the input data
	 */
	private final JTextPane jInputPane;
	
	/**
	 * The main GUI configuration object
	 */
	private final GUImainSettings settings;
	
	
	/**
	 * Calls the "save as" dialog. The chosen file directory is automatically saved to the
	 * main GUI configuration object and the source code is automatically saved.
	 */
	public boolean doSaveAs(){
		
		boolean repeat = true;
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(
				"CMM " + _("file"), "cmm"));
		
		while( repeat ) {
		
			int option = chooser.showSaveDialog(jFrame);
			
			if (option == JFileChooser.APPROVE_OPTION) {
				
				if( FileManagerCode.completeFileName(chooser.getSelectedFile()).exists() ) {
					int n = JOptionPane.showConfirmDialog(
							jFrame,
						    _("Would you like to replace the existing file?"),
						    _("This file does already exist"),
						    JOptionPane.YES_NO_OPTION);
					if( n == 1 )
						continue;
				}
				
					String filePath = null;
					try {
						filePath = FileManagerCode.saveSourceCode(chooser.getSelectedFile(), jSourcePane.getText(), jInputPane.getText());
					} catch (IOException e) {
						new ErrorMessage().showErrorMessage(this.jFrame, "#2001", this.settings.getLanguage());
					}
					settings.setCMMFilePath(filePath);
					
					saveInProfile();
					return true;
				
			}
			if (option == JFileChooser.CANCEL_OPTION)
				return false;
		}
		
		return false;
	}
	
	/**
	 * Saves the Path of the File in the current Quest and changes the quest to inprogress, 
	 * if the quest isn't finished!		System.out.println("save as ... ");
	 */
	private void saveInProfile(){
		
		//Saving the Profile, and adding the Quests which are inprogress
		Profile profile = settings.getProfile();
		if(profile != null && profile.getCurrentQuest() != null && settings.getCMMFilePath() != null){
			if (!settings.getCMMFilePath().equals(_("Unnamed"))){
				try {
					//Adding LastCmmFile
					profile.getCurrentQuest().setCmmFilePath(settings.getCMMFilePath());
					
					//Updating Profile
					if(!profile.getCurrentQuest().getState().equals(Quest.STATE_FINISHED))
						profile.changeQuestStateToInprogress( profile.getCurrentQuest());
					else
						profile.updateProfileQuestPath( profile.getCurrentQuest());
					
					
				} catch (XMLWriteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Saves the current file directly without calling a save dialog.
	 * Save dialog is called though, if there is no working directory for the file defined.
	 */
	public void directSave(){
		if( settings.getCMMFilePath() != null ){
			try {
				FileManagerCode.saveSourceCode(new File(settings.getCMMFilePath()), jSourcePane.getText(), jInputPane.getText());
			} catch (IOException e) {
				new ErrorMessage().showErrorMessage(this.jFrame, "#2001", this.settings.getLanguage());
			}
			saveInProfile();
			return;
		}
		else
			this.doSaveAs();
	}
	
	public boolean safeCheck(String title) {
		
		// Warning if current file is not saved -> opens a warning dialog
		if (settings.getCMMFilePath() != null && settings.getCMMFilePath().equals(_("Unnamed"))) {

			// Custom button text
			Object[] options = { _("Yes"), _("No") };

			// Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog(jFrame,
					_("Do you want to save the new file?"),
					title, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, // do not use a custom
														// Icon
					options, // the titles of buttons
					options[0]); // default button title

			if (n == JOptionPane.YES_OPTION) {
				// Open a save dialog to save current source code
				return doSaveAs();
			} else if (n == JOptionPane.NO_OPTION)
				return true;
			else
				return false;
		}

		// Warning if last changes are not saved -> opens a warning dialog
		else if (jFrame.getTitle().endsWith("*")) {

			// Custom button text
			Object[] options = { _("Save now"), _("Close without saving") };

			// Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog(jFrame,
					_("The current file has not yet been saved!"),
					title, JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, // do not use a custom
														// Icon
					options, // the titles of buttons
					options[0]); // default button title

			if (n == JOptionPane.YES_OPTION) {
				// Save the last changes to current file path
				directSave();
				return true;
			} else if (n == JOptionPane.NO_OPTION)
				return true;
			else
				return false;
		}
		return true;
	}
}
