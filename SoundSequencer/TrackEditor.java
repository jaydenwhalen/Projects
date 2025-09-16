package assign11;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

/**
 * This class contains methods that work specifically with tracks, which
 * represent a sequence of audio events for a given instrument. It interprets
 * the vertical axis of the grid as pitch and the horizontal axis as time.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public class TrackEditor extends GridCanvas {
	private static final long serialVersionUID = 1L;

	private SimpleSynthesizer synthesizer;
	private SimpleSequencer sequencer;
	private int trackNumber, width, height, pitch;

	/**
	 * This constructor calls GridCanvas' constructor and initializes the
	 * SimpleSequencer. It also restrains the height value, assigns instance
	 * variables, and adds MouseListeners.
	 * 
	 * @param width       - of grid in pixels
	 * @param height      - of grid in pixels
	 * @param trackNumber - the ordinal number of the track
	 * @param synthesizer - SimpleSynthesizer
	 */
	public TrackEditor(int width, int height, int trackNumber, SimpleSynthesizer synthesizer) {
		super(width, height, 120, 30, 12, 4);
		setRestrictions(1, -1);
		this.width = width;
		this.height = height;
		this.trackNumber = trackNumber;
		this.synthesizer = synthesizer;
		sequencer = new SimpleSequencer(30);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Sets a new length for the sequence in tics and sets the number of columns to
	 * the given length. Stops the sequence if executing.
	 * 
	 * @param length - length in tics of the sequence
	 */
	public void setLength(int length) {
		sequencer.setLength(length);
		setColumns(length);
	}

	/**
	 * Gets the length for the sequence in tics.
	 * 
	 * @return length in tics
	 */
	public int getLength() {
		return sequencer.getLength();
	}

	/**
	 * Sets the volume of the track in the synthesizer to the given value.
	 * 
	 * @param volume - given volume value
	 */
	public void setVolume(int volume) {
		synthesizer.setVolume(trackNumber, volume);
	}

	/**
	 * Returns the track's volume using SimpleSynthesizer's getVolume method.
	 * 
	 * @return volume - volume of the track associated with trackNumber
	 */
	public int getVolume() {
		return synthesizer.getVolume(trackNumber);
	}

	/**
	 * Mutes or unmutes a given channel
	 * 
	 * @param mute - determines whether it mutes or unmutes the channel
	 */
	public void setMute(boolean mute) {
		synthesizer.setMute(trackNumber, mute);
	}

	/**
	 * Sets the current instrument on a given channel.The index will match an index
	 * in the list of instrument names provided by getInstrumentNames.
	 * 
	 * @param instrument
	 */
	public void setInstrument(int instrument) {
		synthesizer.setInstrument(trackNumber, instrument);
	}

	/**
	 * Gets a list of available instrument names from the midi system.If the midi
	 * system is not available, this returns a list with one element: "DEFAULT"
	 * 
	 * @return
	 */
	public Vector<String> getInstrumentNames() {
		return new Vector<String>(synthesizer.getInstrumentNames());
	}

	/**
	 * Returns the SimpleSequencer.
	 * 
	 * @return sequencer
	 */
	public SimpleSequencer getSequencer() {
		return sequencer;
	}

	/**
	 * Partially overrides the clear method from the GridCanvas class, stops and
	 * clears the sequencer.
	 */
	public void clear() {
		super.clear();
		sequencer.stop();
		sequencer.clear();
	}

	/**
	 * This method is used when loading a track from a file. It calls the clear
	 * method, adds every NoteEvent as a cell, and updates the sequencer.
	 * 
	 * @param newEvents - given array of AudioEvent objects
	 */
	public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
		clear();
		for (int i = 0; i < newEvents.size(); i++) {
			if (newEvents.get(i) instanceof NoteEvent)
				addCell(((NoteEvent) newEvents.get(i)).getPitch(), newEvents.get(i).getTime(), 1,
						((NoteEvent) newEvents.get(i)).getDuration());
		}
		sequencer.updateSequence(newEvents);
	}

	/**
	 * Partially overrides the GridCanvas paintComponent method and adds a time
	 * indicator to show the passage of time while a track plays.
	 * 
	 * @param g - Graphics object
	 */
	public void paintComponent(Graphics g) {
		height = getHeight();
		width = getWidth();
		super.paintComponent(g);
		g.setColor(Color.RED);
		g.fillRect((int) (sequencer.getElapsedTime() * width / sequencer.getLength()), 0, 3, height);
		repaint();
	}

	/**
	 * Sets the current pitch to the value of row. Starts playing that pitch through
	 * the synthesizer with the noteOn method.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
		pitch = row;
		synthesizer.noteOn(trackNumber, pitch);
	}

	/**
	 * If the pitch has changed, then the previous pitch is turned off and the new
	 * pitch is turned on. Pitch is updated.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
		if (row != pitch) {
			synthesizer.noteOff(trackNumber, pitch);
			pitch = row;
			synthesizer.noteOn(trackNumber, pitch);
		}

	}

	/**
	 * If the parameters describe a valid note then a new NoteEvent object is
	 * constructed. That NoteEvent gets added to the SimpleSequencer and the note is
	 * turned off.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
		if (colSpan > 0) {
			NoteEvent newNoteEvent = new NoteEvent(col, "piano", trackNumber, colSpan, row, synthesizer);
			sequencer.add(newNoteEvent);
			synthesizer.noteOff(trackNumber, pitch);
		}

	}

	/**
	 * Loops over the AudioEvents in the SimpleSequencer. If any event is a
	 * NoteEvent with pitch equal to row and time equal to col, the AudioEvent is
	 * removed from the sequencer.
	 * 
	 * @param row - index of cell removed
	 * @param col - index of cell removed
	 */
	@Override
	public void onCellRemoved(int row, int col) {
		for (AudioEvent audioEvent : sequencer) {
			if (audioEvent instanceof NoteEvent) {
				if (pitch == row && audioEvent.getTime() == col)
					sequencer.remove(audioEvent);
			}
		}

	}

}
