package assign11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class houses a collection of static methods to handle writing and
 * reading song files with a specific format.
 * 
 * @author Jayden Whalen
 * @version 2024-11-20
 */
public class SongFiles {

	/**
	 * Writes a song file using the given parameters. Uses FileWriter and
	 * StringBuilder to effectively stack values to the correct format.
	 * 
	 * @param file   - File to be written in
	 * @param tempo  - tempo of the song
	 * @param tracks - BetterDynamicArray of TrackPanel objects
	 * @param song   - the SongPanel
	 */
	public static void writeFile(File file, int tempo, BetterDynamicArray<TrackPanel> tracks, SongPanel song) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			StringBuilder fileBuilder = new StringBuilder();
			fileBuilder.append(tempo);
			fileBuilder.append("\n");
			fileBuilder.append(tracks.size());
			fileBuilder.append("\n");

			// Track blocks
			for (int i = 0; i < tracks.size(); i++) {
				fileBuilder.append("track" + tracks.get(i) + "\n");
				fileBuilder.append(i);
				fileBuilder.append("\n");
				fileBuilder.append(tracks.get(i).getInstrument());
				fileBuilder.append("\n");
				fileBuilder.append(tracks.get(i).getVolume());
				fileBuilder.append("\n");
				fileBuilder.append(tracks.get(i).getLength());
				fileBuilder.append("\n");
				fileBuilder.append(tracks.get(i).getSequencer().getEventCount());
				fileBuilder.append("\n");
				for (AudioEvent event : tracks.get(i).getSequencer()) {
					fileBuilder.append(audioEventText(event));
				}
			}

			// Song block
			fileBuilder.append("song" + "\n");
			fileBuilder.append(song.getLength());
			fileBuilder.append("\n");
			fileBuilder.append(song.getSequencer().getEventCount());
			fileBuilder.append("\n");
			for (AudioEvent event : song.getSequencer()) {
				fileBuilder.append(audioEventText(event));
			}
			fileWriter.append(fileBuilder);
			fileWriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * This methods reads a song file according to the format and appropriately
	 * transfers the information to the Sound Sketcher. It returns the tempo value.
	 * 
	 * @param file        - song file to be read
	 * @param synthesizer - SimpleSynthesizer
	 * @param tracks      - BetterDynamicArray of TrackPanel objects
	 * @param song        - SongPanel
	 * @param width       - given width from SoundSketcherFrame
	 * @param height      - given height from SoundSketcherFrame
	 * @return tempo - tempo of the file, first line
	 */
	public static int readFile(File file, SimpleSynthesizer synthesizer, BetterDynamicArray<TrackPanel> tracks,
			SongPanel song, int width, int height) {
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage() + "The file was not found.");
			return 0;
		}
		try {
			int tempo = fileScanner.nextInt();
			int trackSize = fileScanner.nextInt();
			tracks.clear();
			song.clear();
			fileScanner.nextLine();

			// Reading track blocks
			for (int i = 0; i < trackSize; i++) {
				fileScanner.nextLine();
				tracks.add(new TrackPanel(width, height, fileScanner.nextInt(), synthesizer));
				tracks.get(i).setInstrument(fileScanner.nextInt());
				tracks.get(i).setVolume(fileScanner.nextInt());
				tracks.get(i).setLength(fileScanner.nextInt());
				tracks.get(i).setTempo(tempo);
				int sequencerLength = fileScanner.nextInt();
				fileScanner.nextLine();
				BetterDynamicArray<AudioEvent> events = new BetterDynamicArray<AudioEvent>();
				for (int j = 0; j < sequencerLength; j++) {
					String type = fileScanner.nextLine();
					if (type.equals("change")) {
						String name = fileScanner.nextLine();
						int time = fileScanner.nextInt();
						int channel = fileScanner.nextInt();
						int value = fileScanner.nextInt();
						fileScanner.nextLine();
						fileScanner.nextLine();
						AudioEvent event = new ChangeEvent(time, name, channel, value, synthesizer);
						events.add(event);
					} else {
						String name = fileScanner.nextLine();
						int time = fileScanner.nextInt();
						int channel = fileScanner.nextInt();
						int pitch = fileScanner.nextInt();
						int duration = fileScanner.nextInt();
						fileScanner.nextLine();
						AudioEvent event = new NoteEvent(time, name, channel, duration, pitch, synthesizer);
						events.add(event);
					}

				}
				tracks.get(i).setEvents(events);
			}

			// Reading song block
			fileScanner.nextLine();
			song.setTempo(tempo);
			song.setLength(fileScanner.nextInt());
			int sequencerLength = fileScanner.nextInt();
			SimpleSequencer sequencer = new SimpleSequencer(sequencerLength);
			fileScanner.nextLine();
			BetterDynamicArray<AudioEvent> events = new BetterDynamicArray<AudioEvent>();
			for (int i = 0; i < sequencerLength; i++) {
				fileScanner.nextLine();
				String name = fileScanner.nextLine();
				int time = fileScanner.nextInt();
				int channel = fileScanner.nextInt();
				fileScanner.nextInt();
				int duration = fileScanner.nextInt();
				fileScanner.nextLine();
				AudioEvent event = new TrackEvent(time, name, channel, duration, sequencer);
				events.add(event);
			}
			song.setEvents(events);
			song.setTrackList(tracks);
			fileScanner.close();
			return tempo;
		} catch (InputMismatchException | IllegalStateException e) {
			System.out.println(e.getMessage() + "The file is invalid, make sure the format is correct.");
			return 0;
		}
	}

	/**
	 * Private helper method for creating text for AudioEvent blocks to put in the
	 * file. Determines the instance of the AudioEvent and appends the appropriate
	 * information.
	 * 
	 * @param event - AudioEvent
	 * @return text - String of multiple lines with specified values
	 */
	private static String audioEventText(AudioEvent event) {
		String text = "";
		if (event instanceof NoteEvent) {
			text += "note" + "\n";
			text += event.getName() + "\n";
			text += String.valueOf(event.getTime()) + "\n";
			text += String.valueOf(event.getChannel()) + "\n";
			text += String.valueOf(((NoteEvent) event).getPitch()) + "\n";
			text += String.valueOf(((NoteEvent) event).getDuration()) + "\n";
			return text;
		} else if (event instanceof TrackEvent) {
			text += "track" + "\n";
			text += event.getName() + "\n";
			text += String.valueOf(event.getTime()) + "\n";
			text += String.valueOf(event.getChannel()) + "\n";
			text += "0" + "\n";
			text += String.valueOf(((TrackEvent) event).getDuration()) + "\n";
			return text;
		} else {
			text += "change" + "\n";
			text += event.getName() + "\n";
			text += String.valueOf(event.getTime()) + "\n";
			text += String.valueOf(event.getChannel()) + "\n";
			text += String.valueOf(((ChangeEvent) event).getValue()) + "\n";
			text += "0" + "\n";
			return text;
		}
	}
}
