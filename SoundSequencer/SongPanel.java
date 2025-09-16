package assign11;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A GUI panel for handling songs.
 * 
 * This version consists of a SongEditor that houses a large grid where a track
 * sequence can be drawn and a control panel that contains a play/stop toggle
 * button, a loop toggle button, and a song length JSpinner.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public class SongPanel extends SketchingPanel implements ActionListener, ChangeListener {
	private static final String PLAY_BUTTON = "src/assign09/play.png";
	private static final String STOP_BUTTON = "src/assign09/stop.png";
	private static final String LOOP_BUTTON = "src/assign09/loop.png";

	private static final long serialVersionUID = 1L;

	private SongEditor editor;
	private JPanel controlPanel;
	private JToggleButton playButton, loopButton;
	private JSpinner lengthSpinner;
	private JLabel songLengthDescription, panelName;
	private ImageIcon newPlayIcon, newStopIcon;

	/**
	 * The constructor creates a panel that houses a control panel and a SongEditor.
	 * It assigns icons to buttons and calls their respective listeners. It also
	 * creates useful labels and JToolTips to describe the components.
	 * 
	 * @param width  - given width of the panel
	 * @param height - given height of the panel
	 */
	public SongPanel(int width, int height) {
		editor = new SongEditor(width, height);
		editor.setLayout(new BorderLayout());
		
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(800, 80));
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		
		ImageIcon stopIcon = new ImageIcon(STOP_BUTTON);
		Image stopImage = stopIcon.getImage();
		Image newStopImage = stopImage.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
		newStopIcon = new ImageIcon(newStopImage);
		
		ImageIcon playIcon = new ImageIcon(PLAY_BUTTON);
		Image playImage = playIcon.getImage();
		Image newPlayImage = playImage.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
		newPlayIcon = new ImageIcon(newPlayImage);
		
		playButton = new JToggleButton(newPlayIcon);
		playButton.setPreferredSize(new Dimension(65, 65));
		playButton.setToolTipText("Plays the song");
		playButton.addActionListener(this);
		
		ImageIcon loopIcon = new ImageIcon(LOOP_BUTTON);
		Image loopImage = loopIcon.getImage();
		Image newLoopImage = loopImage.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
		ImageIcon newLoopIcon = new ImageIcon(newLoopImage);
		
		loopButton = new JToggleButton(newLoopIcon);
		loopButton.setPreferredSize(new Dimension(65, 65));
		loopButton.setToolTipText("Toggles a loop feature");
		loopButton.addActionListener(this);
		
		lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 4, 8, 16, 32, 64, 128, 256, 512 }));
		lengthSpinner.setPreferredSize(new Dimension(45, 45));
		lengthSpinner.setToolTipText("Number of beats in the song");
		lengthSpinner.addChangeListener(this);
		
		songLengthDescription = new JLabel("# of beats");
		panelName = new JLabel("SONG PANEL");
		panelName.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		
		controlPanel.add(panelName);
		controlPanel.add(Box.createRigidArea(new Dimension(50, 0)));
		controlPanel.add(playButton);
		controlPanel.add(Box.createRigidArea(new Dimension(40, 0)));
		controlPanel.add(loopButton);
		controlPanel.add(Box.createRigidArea(new Dimension(40, 0)));
		controlPanel.add(lengthSpinner);
		controlPanel.add(songLengthDescription);
		
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.NORTH);
		this.add(editor, BorderLayout.CENTER);
	}

	/**
	 * Gets the sequencer of the SongEditor
	 */
	@Override
	public SimpleSequencer getSequencer() {
		return editor.getSequencer();
	}

	/**
	 * Sets the number of beats for the SongEditor and sets the value of the song
	 * length spinner.
	 * 
	 * @param length
	 */
	@Override
	public void setLength(int length) {
		editor.setLength(length);
		try {
		    lengthSpinner.setValue(length);
		}
		catch (IllegalArgumentException e) {
		    SpinnerListModel model = (SpinnerListModel)(lengthSpinner.getModel());
		    @SuppressWarnings("unchecked")
		    ArrayList<Integer> values = new ArrayList<Integer>((List<Integer>)model.getList());
		    values.add(length);
		    Collections.sort(values);
		    model.setList(values);
		    lengthSpinner.setValue(length);
		}
	}

	/**
	 * Clears and sets the events associated with this SongEditor to the given list.
	 * 
	 * @param event - BetterDynamicArray of AudioEvents
	 */
	@Override
	public void setEvents(BetterDynamicArray<AudioEvent> events) {
		editor.setEvents(events);
	}

	/**
	 * Clears all events from the SongEditor.
	 */
	@Override
	public void clear() {
		editor.clear();
	}

	/**
	 * This method is called when the song length spinner is altered. The number of
	 * beats in the song is set to the spinner's value.
	 * 
	 * @param event - a ChangeEvent
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == lengthSpinner)
			setLength((int) lengthSpinner.getValue());
	}

	/**
	 * This method is called when the play/stop or loop toggle button is clicked.
	 * The play button either plays or stops the song when clicked and sets the icon
	 * to the appropriate symbol (also sets JToolTip text). The loop button toggles
	 * the looping feature.
	 * 
	 * @param event - an ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == playButton) {
			if (playButton.isSelected()) {
				playButton.setIcon(newStopIcon);
				playButton.setToolTipText("Stops the song");
				play();
			} else {
				playButton.setIcon(newPlayIcon);
				playButton.setToolTipText("Plays the song");
				stop();
			}

		}
		if (event.getSource() == loopButton)
			if (loopButton.isSelected())
				setLoop(true);
			else
				setLoop(false);
	}

	/**
	 * Passes the list of TrackPanels along to the SongEditor by calling its
	 * setTrackList method.
	 * 
	 * @param trackList - BetterDynamicArray list of tracks
	 */
	public void setTrackList(BetterDynamicArray<TrackPanel> trackList) {
		editor.setTrackList(trackList);
	}

}
