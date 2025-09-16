package assign11;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;

/**
 * A GUI panel for handling tracks.
 * 
 * This class contains a TrackEditor that allows the user to create a track by
 * drawing notes and a control panel that contains a mute toggle button, a
 * volume slider, and a track length JSpinner.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public class TrackPanel extends SketchingPanel implements ActionListener, ChangeListener {
	private static final String MUTE_BUTTON = "src/assign09/volOn.png";
	private static final String UNMUTE_BUTTON = "src/assign09/mute.png";

	private static final long serialVersionUID = 1L;

	private TrackEditor editor;
	private JPanel controlPanel;
	private JToggleButton muteButton;
	private JSpinner lengthSpinner;
	private JLabel trackLengthDescription, volumeDescription, panelName;
	private int trackNumber, instrumentNumber;
	private JSlider volumeSlider;
	private JComboBox<String> instruments;
	private ImageIcon newMuteIcon, newUnmuteIcon;

	/**
	 * The constructor creates a panel that houses a control panel and a
	 * TrackEditor. It keeps track of the number of tracks, assigns icons to
	 * buttons, and calls the buttons' respective listeners. It also creates useful
	 * labels and JToolTips to describe the components.
	 * 
	 * @param width       - given width of the panel
	 * @param height      - given height of the panel
	 * @param trackNumber - the ordinal number of the track
	 */
	public TrackPanel(int width, int height, int trackNumber, SimpleSynthesizer synthesizer) {
		this.trackNumber = trackNumber;
		editor = new TrackEditor(width, height, this.trackNumber, synthesizer);
		editor.setLayout(new BorderLayout());
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(115, height));
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		ImageIcon unmuteIcon = new ImageIcon(UNMUTE_BUTTON);
		Image unmuteImage = unmuteIcon.getImage();
		Image newUnmuteImage = unmuteImage.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
		newUnmuteIcon = new ImageIcon(newUnmuteImage);
		ImageIcon muteIcon = new ImageIcon(MUTE_BUTTON);
		Image muteImage = muteIcon.getImage();
		Image newMuteImage = muteImage.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
		newMuteIcon = new ImageIcon(newMuteImage);
		muteButton = new JToggleButton(newMuteIcon);
		muteButton.setPreferredSize(new Dimension(65, 65));
		muteButton.setToolTipText("Mutes/Unmutes the track");
		muteButton.addActionListener(this);
		lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 4, 8, 16, 32, 64, 128, 256, 512 }));
		lengthSpinner.setPreferredSize(new Dimension(45, 45));
		lengthSpinner.setValue(32);
		lengthSpinner.addChangeListener(this);
		trackLengthDescription = new JLabel("track length");
		trackLengthDescription.setFont(new Font(Font.SERIF, Font.CENTER_BASELINE, width / 60));
		volumeSlider = new JSlider(SwingConstants.VERTICAL, 0, 127, editor.getVolume());
		volumeSlider.setPaintTicks(true);
		volumeSlider.setMajorTickSpacing(20);
		volumeSlider.setPaintLabels(true);
		volumeSlider.addChangeListener(this);
		volumeDescription = new JLabel("volume");
		volumeDescription.setFont(new Font(Font.SERIF, Font.CENTER_BASELINE, 13));
		instruments = new JComboBox<String>(editor.getInstrumentNames());
		instruments.addActionListener(this);
		panelName = new JLabel("TRACK PANEL " + trackNumber);
		panelName.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
		controlPanel.add(panelName);
		controlPanel.add(instruments);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		controlPanel.add(trackLengthDescription);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 150)));
		controlPanel.add(lengthSpinner);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		controlPanel.add(volumeDescription);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		controlPanel.add(volumeSlider);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		controlPanel.add(muteButton);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.WEST);
		this.add(editor, BorderLayout.CENTER);
	}

	/**
	 * Gets the sequencer for this TrackEditor.
	 */
	@Override
	public SimpleSequencer getSequencer() {
		return editor.getSequencer();
	}

	/**
	 * Sets the number of beats for the TrackEditor.
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
	 * Clears and sets the events associated with this TrackEditor to the given
	 * list.
	 */
	@Override
	public void setEvents(BetterDynamicArray<AudioEvent> events) {
		editor.setEvents(events);
	}

	/**
	 * Clears all events from the TrackEditor.
	 */
	@Override
	public void clear() {
		editor.clear();
	}

	/**
	 * Calls TrackEditor's getVolume method, returning that result.
	 * 
	 * @return value of volume
	 */
	public int getVolume() {
		return editor.getVolume();
	}

	/**
	 * Passes the volume to TrackEditor's setVolume and sets the value of the volume
	 * slider.
	 * 
	 * @param volume - new volume value
	 */
	public void setVolume(int volume) {
		editor.setVolume(volume);
	}

	/**
	 * Returns the instrument number instance variable.
	 * 
	 * @return instrumentNumber
	 */
	public int getInstrument() {
		return instrumentNumber;
	}

	/**
	 * Sets the instance variable, sets the JComboBox using its setSelectedIndex
	 * method, and passes it to TrackEditor's setInstrument method.
	 * 
	 * @param instrument - new instrument number
	 */
	public void setInstrument(int instrument) {
		instrumentNumber = instrument;
		instruments.setSelectedIndex(instrumentNumber);
		editor.setInstrument(instrumentNumber);
	}

	/**
	 * This method is called when either the track length spinner or volume slider
	 * is changed. If the spinner is changed, the beats per minute of the track is
	 * set to the spinner value. If the volume slider is moved, the volume gets set
	 * the the slider's value.
	 * 
	 * @param event - a ChangeEvent 
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == lengthSpinner)
			setLength((int) lengthSpinner.getValue());
		if (event.getSource() == volumeSlider)
			setVolume(volumeSlider.getValue());
	}

	/**
	 * This method is called when either the mute toggle button is pressed or if the
	 * instrument is changed. If the mute button is pressed, it sets the volume to
	 * zero. If the button is clicked again, the volume returns to its original
	 * value. If the instrument ComboBox is used, the TrackEditor sets the active
	 * instrument to the selected instrument.
	 * 
	 * @param event - an ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == muteButton) {
			if (muteButton.isSelected()) {
				muteButton.setIcon(newUnmuteIcon);
				editor.setVolume(0);
			} else {
				muteButton.setIcon(newMuteIcon);
				editor.setVolume(volumeSlider.getValue());
			}
		}

		if (event.getSource() == instruments) {
			editor.setInstrument(instruments.getSelectedIndex());
			editor.requestFocus();
		}

	}
}