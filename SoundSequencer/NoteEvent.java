package assign11;

/**
 * This class creates a NoteEvent object that inherits methods from the
 * AudioEvent superclass. It contains getter methods, a String representation,
 * and implements Comparable.
 * 
 * @author Jayden Whalen
 * @version 2024-10-21
 */
public class NoteEvent extends AudioEvent {

	private int duration, pitch, channel;
	private SimpleSynthesizer synthesizer;

	/**
	 * This constructor creates a NoteEvent object by calling the superclass
	 * constructor and adding two new variables: duration and pitch.
	 * 
	 * @param time       - time at which the event occurs
	 * @param instrument - which instrument is being played
	 * @param channel    - explained in a later stage of the project
	 * @param duration   - how long it is being played
	 * @param pitch      - the highness/lowness of the tone
	 */
	public NoteEvent(int time, String instrument, int channel, int duration, int pitch, SimpleSynthesizer synthesizer) {
		super(time, instrument, channel);
		this.duration = duration;
		this.pitch = pitch;
		this.synthesizer = synthesizer;
		this.channel = channel;
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
	 * Getter method for the pitch variable.
	 * 
	 * @return pitch
	 */
	public int getPitch() {
		return pitch;
	}

	/**
	 * This method returns a String that represents the NoteEvent object and all of
	 * its variables.
	 * 
	 * @return String representation of the object
	 */
	public String toString() {
		return getName() + "[" + getChannel() + ", " + getTime() + ", " + duration + ", " + pitch + "]";
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
		if (other instanceof ChangeEvent)
			return 1;
		else if (other instanceof TrackEvent)
			return -1;
		else
			return 0;
	}

	/**
	 * Calls the synthesizer's noteOn method
	 */
	public void execute() {
		synthesizer.noteOn(channel, pitch);
	}

	/**
	 * Calls the synthesizer's noteOff method
	 */
	public void complete() {
		synthesizer.noteOff(channel, pitch);
	}

	/**
	 * Calls the synthesizer's noteOff method
	 */
	public void cancel() {
		synthesizer.noteOff(channel, pitch);
	}

}
