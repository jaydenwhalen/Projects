package assign11;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class implements a GUI for creating musical tracks and songs.
 * 
 * This version contains tabs to manage a SongPanel and manage (or add)
 * TrackPanels. It also has toggle buttons for playing/stopping or looping the
 * tracks, a tempo slider, and useful descriptions.
 * 
 * @author Jayden Whalen
 * @version 2024-12-2
 */
public class SoundSketcherFrame extends JFrame implements ActionListener, ChangeListener {
	private static final String PLAY_BUTTON = "src/assign09/play.png";
	private static final String STOP_BUTTON = "src/assign09/stop.png";
	private static final String LOOP_BUTTON = "src/assign09/loop.png";

	private static final long serialVersionUID = 1L;

	private SongPanel songPanel;
	private BetterDynamicArray<TrackPanel> trackPanels;
	private final int maxTracks = 16;
	private boolean addingTrack;
	private JPanel controlPanel;
	private JToggleButton playButton, loopButton;
	private JSlider tempoSlider;
	private JLabel tempoDescription;
	private JTabbedPane tracksPane;
	private int width, height;
	private ImageIcon newStopIcon, newPlayIcon;
	private SimpleSynthesizer synthesizer;
	private JMenuItem save;
	private JMenuItem load;

	/**
	 * The constructor consists of a panel that houses a control panel, a SongPanel,
	 * and one TrackPanel. It assigns icons to buttons and calls their respective
	 * listeners. It also sets the title and basic panel functions.
	 */
	public SoundSketcherFrame() {
		width = 800;
		height = 800;

		synthesizer = new SimpleSynthesizer();
		songPanel = new SongPanel(width, height);
		TrackPanel trackPanel = new TrackPanel(800, 800, 0, synthesizer);
		trackPanels = new BetterDynamicArray<TrackPanel>();
		trackPanels.add(trackPanel);
		songPanel.setTrackList(trackPanels);
		tracksPane = new JTabbedPane();
		tracksPane.add("Song", songPanel);
		tracksPane.add("Track 0", trackPanels.get(0));
		tracksPane.addTab("Add Track", new JPanel());
		tracksPane.setSelectedIndex(1);
		addingTrack = false;
		tracksPane.addChangeListener(this);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Save/Load");
		save = new JMenuItem("Save");
		save.addActionListener(this);
		menu.add(save);
		load = new JMenuItem("Load");
		load.addActionListener(this);
		menu.add(load);
		menuBar.add(menu);
		setJMenuBar(menuBar);

		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(width, height / 10));
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		ImageIcon stopIcon = new ImageIcon(STOP_BUTTON);
		Image stopImage = stopIcon.getImage();
		Image newStopImage = stopImage.getScaledInstance(width / 12, height / 12, java.awt.Image.SCALE_SMOOTH);
		newStopIcon = new ImageIcon(newStopImage);

		ImageIcon playIcon = new ImageIcon(PLAY_BUTTON);
		Image playImage = playIcon.getImage();
		Image newPlayImage = playImage.getScaledInstance(width / 12, height / 12, java.awt.Image.SCALE_SMOOTH);
		newPlayIcon = new ImageIcon(newPlayImage);

		playButton = new JToggleButton(newPlayIcon);
		playButton.setPreferredSize(new Dimension(width / 12, height / 12));
		playButton.setToolTipText("Plays every Track Panel, not the Song Panel");
		playButton.addActionListener(this);

		ImageIcon loopIcon = new ImageIcon(LOOP_BUTTON);
		Image loopImage = loopIcon.getImage();
		Image newLoopImage = loopImage.getScaledInstance(width / 12, height / 12, java.awt.Image.SCALE_SMOOTH);
		ImageIcon newLoopIcon = new ImageIcon(newLoopImage);

		loopButton = new JToggleButton(newLoopIcon);
		loopButton.setPreferredSize(new Dimension(width / 12, height / 12));
		loopButton.setToolTipText("Toggles a loop feature on every Track Panel but not the Song Panel");
		loopButton.addActionListener(this);

		tempoSlider = new JSlider(SwingConstants.HORIZONTAL, 60, 140, 60);
		tempoSlider.setPaintTicks(true);
		tempoSlider.setMajorTickSpacing(20);
		tempoSlider.setPaintLabels(true);
		tempoSlider.addChangeListener(this);
		setTempoSlider(60);
		trackPanels.get(0).setTempo(tempoSlider.getValue());
		songPanel.setTempo(tempoSlider.getValue());

		tempoDescription = new JLabel("BPM");

		controlPanel.add(tempoDescription);
		controlPanel.add(tempoSlider);
		controlPanel.add(Box.createRigidArea(new Dimension(width / 16, 0)));
		controlPanel.add(playButton);
		controlPanel.add(Box.createRigidArea(new Dimension(width / 16, 0)));
		controlPanel.add(loopButton);

		JPanel outerLayer = new JPanel(new BorderLayout());
		outerLayer.add(controlPanel, BorderLayout.SOUTH);
		outerLayer.add(tracksPane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(width, height));
		setTitle("Sound Sketcher");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(outerLayer);
		pack();
	}

	/**
	 * Adds a new TrackPanel to the next tab if possible. This is only possible if
	 * the number of tracks is less than the maximum. If it is not possible, this
	 * has no effect.
	 */
	public void addTrack() {
		addingTrack = true;
		if (trackPanels.size() < maxTracks) {
			TrackPanel newTrack = new TrackPanel(width, height, trackPanels.size(), synthesizer);
			newTrack.setTempo(tempoSlider.getValue());
			newTrack.setLoop(loopButton.isSelected());
			trackPanels.add(newTrack);
			tracksPane.insertTab("Track " + (trackPanels.size() - 1), null, trackPanels.get(trackPanels.size() - 1),
					null, tracksPane.getTabCount() - 1);
		}
		tracksPane.setSelectedIndex(tracksPane.getTabCount() - 2);
		addingTrack = false;
	}

	/**
	 * This method is called when either the new tab button is clicked or if the
	 * tempo slider is moved. If the new tab button is pressed, a new TrackPanel is
	 * made as long as the number of tracks is less than the maximum. If the tempo
	 * slider is moved, then the tempo is adjusted accordingly.
	 * 
	 * @param event - a ChangeEvent
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if ((event.getSource() == tracksPane) && (tracksPane.getSelectedIndex() == tracksPane.getTabCount() - 1)
				&& !addingTrack)
			addTrack();
		if (event.getSource() == tempoSlider) {
			for (int i = 0; i < trackPanels.size(); i++)
				trackPanels.get(i).setTempo(tempoSlider.getValue());
			songPanel.setTempo(tempoSlider.getValue());
		}

	}

	/**
	 * This method is called when either the play/stop toggle button or loop toggle
	 * button is clicked. Both toggle buttons act on every TrackPanel but not the
	 * SongPanel. It is also called when either the save or load menu items are
	 * used, handling exceptions and doing the appropriate action if the file is
	 * valid. It sets the file extension to .song,
	 * 
	 * @param event - an ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == save) {
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File("epicSong.song"));
			chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));
			chooser.setDialogTitle("Select the location for the new file.");
			if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "Save file cancelled.");
				return;
			}
			SongFiles.writeFile(chooser.getSelectedFile(), tempoSlider.getValue(), trackPanels, songPanel);
		} else if (event.getSource() == load) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				setTempoSlider(SongFiles.readFile(chooser.getSelectedFile(), synthesizer, trackPanels, songPanel, width,
						height));
			}
			updateTabs();
		}
		if (event.getSource() == playButton) {
			if (playButton.isSelected()) {
				playButton.setIcon(newStopIcon);
				playButton.setToolTipText("Stops every Track Panel");
				for (int i = 0; i < trackPanels.size(); i++)
					trackPanels.get(i).play();
			} else {
				playButton.setIcon(newPlayIcon);
				playButton.setToolTipText("Plays every Track Panel, not the Song Panel");
				for (int i = 0; i < trackPanels.size(); i++)
					trackPanels.get(i).stop();
			}

		}
		if (event.getSource() == loopButton) {
			if (loopButton.isSelected()) {
				for (int i = 0; i < trackPanels.size(); i++)
					trackPanels.get(i).setLoop(true);
			} else {
				for (int i = 0; i < trackPanels.size(); i++)
					trackPanels.get(i).setLoop(false);
			}
		}

	}

	/**
	 * Updates the tabbed pane after loading a file.
	 */
	private void updateTabs() {
		addingTrack = true;
		// remove all old tracks
		while (tracksPane.getTabCount() > 2)
			tracksPane.remove(1);
		// add all new tracks
		int trackNumber = 0;
		while (trackNumber < trackPanels.size()) {
			tracksPane.insertTab("Track " + trackNumber, null, trackPanels.get(trackNumber), null, trackNumber + 1);
			trackNumber++;
		}
		tracksPane.setSelectedIndex(1);
		addingTrack = false;
	}

	/**
	 * Helper method for setting the tempo by altering bounds if necessary.
	 * 
	 * @param newTempo - value to set the slider to
	 */
	private void setTempoSlider(int newTempo) {
		if (newTempo < tempoSlider.getMinimum())
			tempoSlider.setMinimum(newTempo);
		if (newTempo > tempoSlider.getMaximum())
			tempoSlider.setMaximum(newTempo);
		tempoSlider.setValue(newTempo);
	}
}
