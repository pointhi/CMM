package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.debug.ErrorTable;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.utils.LoadStatics;

/**
 * This class is responsible for the right panel of the main GUI. The right
 * panel contains a tabbed pane with two tabs:
 * <ul>
 * <li>Debugger panel: Contains control buttons for the debugger and the
 * variable tree table.</li>
 * <li>Quest/Profile info panel: Contains information about the user and his
 * current quest.</li>
 * </ul>
 * Also, the right panel contains basic control elements for the main GUI, eg.
 * the breakpoint button.
 * 
 * @author fabian
 *
 */
public class GUIrightPanel {

	/**
	 * This class is responsible for the right panel of the main GUI. The right
	 * panel contains a tabbed pane with two tabs:
	 * <ul>
	 * <li>Debugger panel: Contains control buttons for the debugger and the
	 * variable tree table.</li>
	 * <li>Quest/Profile info panel: Contains information about the user and his
	 * current quest.</li>
	 * </ul>
	 * Also, the right panel contains basic control elements for the main GUI,
	 * eg. the breakpoint button.
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIrightPanel() {

	}

	private JTabbedPane tabbedPane;

	/**
	 * A reference to the debug panel.
	 */
	private GUIdebugPanel debugPanel;

	private ErrorTable errorMap;

	/**
	 * A reference to the quest/profile info panel.
	 */
	private GUIquestPanel questPanel;

	private JPanel errorPanel;
	private JEditorPane errorDesc;

	public JTabbedPane init(GUImain main) {
		
		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Initialize Debug Panel
		JPanel jDebugPanel = new JPanel();
		jDebugPanel.setLayout(new BorderLayout());
		debugPanel = new GUIdebugPanel(jDebugPanel, main);
		tabbedPane.add(jDebugPanel, _("Debug"));

		// Initialize error panel
		this.errorMap = new ErrorTable(GUImain.LANGUAGE);
		this.errorPanel = new JPanel();
		this.errorPanel.setLayout(new BorderLayout());

		this.errorDesc = new JEditorPane();
		this.errorDesc.setEditable(false);
		this.errorDesc.setContentType("text/html");
		this.errorDesc.setDocument(LoadStatics.readStyleSheet("error"
				+ File.separator + "style.css"));

		JScrollPane editorScrollPane = new JScrollPane(this.errorDesc);
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		editorScrollPane.setPreferredSize(new Dimension(100, 300));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		this.errorPanel.add(editorScrollPane, BorderLayout.CENTER);

		// Initialize Quest Panel
		JPanel jQuestPanel = new JPanel();
		if (GUImain.ADVANCED_GUI) {
			jQuestPanel.setLayout(new BorderLayout());
			tabbedPane.add(jQuestPanel, _("Quest"));
			questPanel = new GUIquestPanel(jQuestPanel, main);
		}
		
		tabbedPane.setMinimumSize(new Dimension(250, 400));
		tabbedPane.setPreferredSize(new Dimension(260, 420));

		return tabbedPane;
	}

	/**
	 * @return A reference to the debug panel manager
	 */
	public GUIdebugPanel getDebugPanel() {
		return this.debugPanel;
	}

	/**
	 * @return A reference to the quest/profile info panel manager
	 */
	public GUIquestPanel getQuestPanel() {
		return this.questPanel;
	}

	public void showErrorPanel(String msg) {

		try {
			this.errorDesc.setPage(LoadStatics.getHTMLUrl(this.errorMap
					.getErrorHTML(msg)));
		} catch (IOException e) {
			DebugShell.out(State.ERROR, Area.ERROR, msg + " not found");
			e.printStackTrace();
		}

		if (this.tabbedPane.getTabCount() == (GUImain.ADVANCED_GUI ? 2 : 1))
			this.tabbedPane.add(errorPanel, _("Error"), 1);

		this.tabbedPane.setSelectedIndex(1);
	}

	public void hideErrorPanel() {
		if (this.tabbedPane.getTabCount() == (GUImain.ADVANCED_GUI ? 3 : 2))
			tabbedPane.remove(1);
	}
}
