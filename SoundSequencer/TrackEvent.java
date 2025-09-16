package assign11;

/**
 * This class creates a TrackEvent object that inherits methods from the
 * AudioEvent superclass. It contains getter methods, a String representation,
 * and implements Comparable.
 * 
 * @author Jayden Whalen
 * @version 2024-10-21
 */
public class TrackEvent extends AudioEvent {

	private int duration;
	SimpleSequencer sequencer;

	/**
	 * This constructor creates a TrackEvent object by calling the superclass
	 * constructor and adding two new variables: duration and sequence.
	 * 
	 * @param time      - time at which the event occurs
	 * @param trackName - the name of the track
	 * @param channel   - explained in a later stage of the project
	 * @param duration  - how long it is being played
	 * @param sequence  - array of AudioEvent objects
	 */
	public TrackEvent(int time, String trackName, int channel, int duration, SimpleSequencer sequencer) {
		super(time, trackName, channel);
		this.duration = duration;
		this.sequencer = sequencer;
	}

	/**
	 * Getter method for the duration variable.
	 * 
	 * @return duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Getter method for the sequence variable.
	 * 
	 * @return sequence
	 */
	public SimpleSequencer getSequence() {
		return sequencer;
	}

	/**
	 * This method returns a String that represents the TrackEvent object and its
	 * array of sequence objects represented by their own toString methods. The
	 * event strings are each on separate lines.
	 * 
	 * @return String representation of the object and its array
	 */
	public String toString() {
		String list = "";
		for (int i = 0; i < sequencer.getEventCount(); i++)
			list += "\n- " + sequencer.toString();
		return getName() + "[" + getChannel() + ", " + getTime() + ", " + duration + ", " + sequencer.getEventCount() + "]"
				+ list;
	}

	/**
	 * Compares the time of two AudioEvent objects. If the first object has a time
	 * less than the other object called, then a negative number is returned. If the
	 * first object's time is greater, then a positive number is returned. For
	 * events with equal time, they are ordered so that ChangeEvents happen first,
	 * NoteEvents are in the middle, and TrackEvents happen last. Note: this class
	 * has a natural ordering that is inconsistent with equals.
	 * 
	 * @return 1, 0, or -1
	 */
	public int compareTo(AudioEvent other) {
		if (this.getTime() < other.getTime())
			return -1;
		if (this.getTime() > other.getTime())
			return 1;
		if (other instanceof ChangeEvent || other instanceof NoteEvent)
			return 1;
		else
			return 0;
	}

	/**
	 * Starts the sequencer.
	 */
	public void execute() {
		sequencer.start();
	}

	/**
	 * Does nothing for now.
	 */
	public void complete() {

	}

	/**
	 * Stops the sequencer.
	 */
	public void cancel() {
		sequencer.stop();
	}
}