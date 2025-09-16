package assign11;

/**
 * This class creates a ChangeEvent object that inherits methods from the
 * AudioEvent superclass. It contains getter methods, a String representation,
 * and implements Comparable.
 * 
 * @author Jayden Whalen
 * @version 2024-10-21
 */
public class ChangeEvent extends AudioEvent {

	private int value;
	private SimpleSynthesizer synthesizer;

	/**
	 * This constructor creates a ChangeEvent object by calling the superclass
	 * constructor and adding a new value variable.
	 * 
	 * @param time    - time at which the event occurs
	 * @param type    - type of change
	 * @param channel - explained in a later stage of the project
	 * @param value   - new value for the property being changed
	 */
	public ChangeEvent(int time, String type, int channel, int value, SimpleSynthesizer synthesizer) {
		super(time, type, channel);
		this.value = value;
		this.synthesizer = synthesizer;
	}

	/**
	 * Getter method for the value variable.
	 * 
	 * @return value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * This method returns a String that represents the ChangeEvent object and all
	 * of its variables.
	 * 
	 * @return String representation of the object
	 */
	public String toString() {
		return getName() + "[" + getChannel() + ", " + getTime() + ", " + value + "]";
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
		if (other instanceof TrackEvent || other instanceof NoteEvent)
			return -1;
		else
			return 0;
	}

	/**
	 * Does nothing for now.
	 */
	public void execute() {
	}

	/**
	 * Does nothing for now.
	 */
	public void complete() {

	}

	/**
	 * Does nothing for now.
	 */
	public void cancel() {

	}

}
