package assign11;

import java.awt.Color;
import java.awt.Graphics;

/**
 * This class contains methods that work specifically with songs, which
 * represent a sequence of tracks. It interprets the vertical axis of the grid
 * as track number and the horizontal axis as time.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public class SongEditor extends GridCanvas {
	private static final long serialVersionUID = 1L;

	private SimpleSequencer sequencer;
	private BetterDynamicArray<TrackPanel> trackPanels;
	private int width, height, currentTrack;

	/**
	 * This constructor calls the GridCanvas super constructor and initializes the
	 * width, height, track number, and synthesizer from the parameters. It also
	 * creates a new instance of SimpleSequencer with a chosen initial length and
	 * implements MouseListeners.
	 * 
	 * @param width  - of grid in pixels
	 * @param height - of grid in pixels
	 */
	public SongEditor(int width, int height) {
		super(width, height, 1, 100, 12, 12);
		this.width = width;
		this.height = height;
		sequencer = new SimpleSequencer(100);
		setColumns(sequencer.getLength());
		trackPanels = new BetterDynamicArray<TrackPanel>();
		setRows(trackPanels.size());
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
	 * Gets the SimpleSequencer.
	 * 
	 * @return sequencer - SimpleSequencer
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
	 * method, adds every TrackEvent as a cell, and updates the sequencer.
	 * 
	 * @param newEvents - given array of AudioEvent objects
	 */
	public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
		clear();
		for (int i = 0; i < newEvents.size(); i++) {
			if (newEvents.get(i) instanceof TrackEvent){
				addCell(((TrackEvent) newEvents.get(i)).getChannel(), newEvents.get(i).getTime(), 1,
						((TrackEvent) newEvents.get(i)).getDuration());
			}
		}
		sequencer.updateSequence(newEvents);
	}

	/**
	 * Reassign the instance variable to a given trackList.
	 * 
	 * @param trackList - dynamic array of TrackPanels
	 */
	public void setTrackList(BetterDynamicArray<TrackPanel> trackList) {
		trackPanels = trackList;
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
		setRows(trackPanels.size());
		super.paintComponent(g);
		g.setColor(Color.RED);
		g.fillRect((int) (sequencer.getElapsedTime() * width / sequencer.getLength()), 0, 3, height);
		repaint();
	}

	/**
	 * Sets the current track number to the value of "row". Cell size restrictions
	 * are reset to match the length of this track.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
		currentTrack = row;
		setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
	}

	/**
	 * The restrictions are updated if the track number has changed using a call to
	 * setRestrictions for the new currentTrack.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
		if (row != currentTrack) {
			currentTrack = row;
			setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
		}
	}

	/**
	 * Constructs a new TrackEvent object and adds it to the SimpleSequencer.
	 * 
	 * @param row     - vertical position index of cell
	 * @param col     - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	@Override
	public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
		TrackEvent trackEvent = new TrackEvent(col, "track", currentTrack, colSpan,
				trackPanels.get(currentTrack).getSequencer());
		sequencer.add(trackEvent);
	}

	/**
	 * Loops over the AudioEvents in the SimpleSequencer. If any event is a
	 * TrackEvent with channel equal to row and time equal to col, the AudioEvent is
	 * removed from the SimpleSequencer.
	 * 
	 * @param row - index of cell removed
	 * @param col - index of cell removed
	 */
	@Override
	public void onCellRemoved(int row, int col) {
		for (AudioEvent audioEvent : sequencer) {
			if (audioEvent instanceof TrackEvent) {
				if (audioEvent.getChannel() == row && audioEvent.getTime() == col)
					sequencer.remove(audioEvent);
			}
		}

	}

}
