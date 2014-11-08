package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.RightPanelBreakpointListener;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.popup.PopupInterface;

/**
 * This class is responsible for the right panel of the main GUI. The right panel contains
 * a tabbed pane with two tabs:
 * <ul>
 * 		<li>Debugger panel: Contains control buttons for the debugger and the variable tree table.</li>
 * 		<li>Quest/Profile info panel: Contains information about the user and his current quest.</li>
 * </ul>
 * Also, the right panel contains basic control elements for the main GUI, eg. the breakpoint button.
 * 
 * @author fabian
 *
 */
public class GUIrightPanel {
	
	/**
	 * This class is responsible for the right panel of the main GUI. The right panel contains
	 * a tabbed pane with two tabs:
	 * <ul>
	 * 		<li>Debugger panel: Contains control buttons for the debugger and the variable tree table.</li>
	 * 		<li>Quest/Profile info panel: Contains information about the user and his current quest.</li>
	 * </ul>
	 * Also, the right panel contains basic control elements for the main GUI, eg. the breakpoint button.
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIrightPanel(JComponent cp, GUImainMod mod, PopupInterface popup) {
		
		this.mod = mod;
		
		//Main right panel
		this.jRightContainer = new JPanel();
		this.jRightContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.jRightContainer.setLayout(new BorderLayout());
		
		this.jRightContainer.add(this.initCommonPanel(),
				BorderLayout.PAGE_START);
		
		//Tabbed Pane
		JTabbedPane tabbedPane = new JTabbedPane();
		
			//Debug Panel
			JPanel jDebugPanel = new JPanel();
			jDebugPanel.setLayout(new BorderLayout());
			tabbedPane.add(jDebugPanel, _("Debug"));
			debugPanel = new GUIdebugPanel( jDebugPanel, mod, popup );
	
			//Quest Panel
			JPanel jQuestPanel = new JPanel();
			if( GUImain.ADVANCED_GUI ){
				jQuestPanel.setLayout(new BorderLayout());
				tabbedPane.add(jQuestPanel, _("Quest"));
				questPanel = new GUIquestPanel( jQuestPanel, mod );
			}
		
		this.jRightContainer.add(tabbedPane);
		cp.add(this.jRightContainer, BorderLayout.CENTER);
	}
	
	/**
	 * Modifier of main GUI data. This interface interacts with the main GUI.
	 */
	private final GUImainMod mod;
	
	/**
	 * Main container for the right panel. All interface changes happen inside this JPanel
	 */
	private final JPanel jRightContainer;
	
	/**
	 * A reference to the debug panel.
	 */
	private final GUIdebugPanel debugPanel;
	
	/**
	 * A reference to the quest/profile info panel.
	 */
	private GUIquestPanel questPanel;
	
	/* --- top panel objects --- */
	/**
	 * The breakpoint button.
	 */
	private JButton jButtonBreakPoint;
	
	/**
	 * Initializes the panel with the common control elements, eg. breakpoint button.
	 * 
	 * @return The initialized panel
	 */
	private JPanel initCommonPanel() {
		JPanel jTopPanel = new JPanel();

		this.jButtonBreakPoint = new JButton("\u2326");
		RightPanelBreakpointListener listener = new RightPanelBreakpointListener(this.mod, this.jButtonBreakPoint);
		this.jButtonBreakPoint.addMouseListener(listener);
		this.jButtonBreakPoint.setToolTipText("<html>" + _("toggle breakpoint") + "</b><br><ul><li>" + _("adds breakpoint to the current<br>line in the source code") + "</li><li>" + _("removes the breakpoint from the current<br>line if there is already one") + "</ul><html>" );
		jTopPanel.add(this.jButtonBreakPoint);

		return jTopPanel;
	}
	
	/**
	 * @return A reference to the debug panel manager
	 */
	public GUIdebugPanel getDebugPanel(){
		return this.debugPanel;
	}
	
	/**
	 * @return A reference to the quest/profile info panel manager
	 */
	public GUIquestPanel getQuestPanel(){
		return this.questPanel;
	}
	
	/**
	 * Locks the common control elements, eg. breakpoint button.
	 * Used while cmm program in interpreted.
	 */
	public void lockInput(){
		this.jButtonBreakPoint.setEnabled(false);
	}
	
	/**
	 * unlocks the common control elements, eg. breakpoint button.
	 * Used when cmm program interpreting is finished.
	 */
	public void unlockInput(){
		this.jButtonBreakPoint.setEnabled(true);
	}
}
