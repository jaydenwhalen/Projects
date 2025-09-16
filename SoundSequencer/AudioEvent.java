package assign11;

/**
 * This abstract class acts as a superclass for TrackEvent, NoteEvent, and
 * ChangeEvent. It contains three instance variables and implements the
 * Comparable interface.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public abstract class AudioEvent implements Comparable<AudioEvent> {

	private int time;
	private String name;
	private int channel;

	/**
	 * This constructor initializes the state of the event.
	 * 
	 * @param time    - time at which the event occurs
	 * @param name    - describing the event
	 * @param channel - explained in a later stage of the project
	 */
	public AudioEvent(int time, String name, int channel) {
		this.time = time;
		this.name = name;
		this.channel = channel;
	}

	/**
	 * Getter method for the time variable.
	 * 
	 * @return time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Getter method for the name variable.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter method for the channel variable.
	 * 
	 * @return channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * Abstract method that prints out a String representation of the object for
	 * now.
	 */
	public abstract void execute();

	/**
	 * Abstract method that does nothing for now.
	 */
	public abstract void complete();

	/**
	 * Abstract method that does nothing for now.
	 */
	public abstract void cancel();
}
