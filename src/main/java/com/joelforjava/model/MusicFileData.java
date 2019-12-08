package com.joelforjava.model;

import com.joelforjava.music.model.Album;
import com.joelforjava.music.model.Artist;
import com.joelforjava.music.model.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;

public class MusicFileData {

	private String artistName;
	private String uri;
	private Song song;

	public MusicFileData(String uri) {
		this.uri = uri;
		try {
			this.song = new Song(uri);
			// TODO - leave for now. Eventually defer to Song object and remove other properties.
			this.artistName = this.song.getArtist().getName();
		} catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
			e.printStackTrace(); // TODO - change to LOG
			throw new IllegalStateException(e);
		}
	}

	public String getUri() {
		return uri;
	}

	/**
	 * Set the artistName property.
	 * @param artistName - the desired artistName value.
	 * @deprecated
	 */
	@Deprecated
	void setArtistName(String artistName) {
	    this.artistName = artistName;
    }

	/**
	 * Set the artistName property and return the instance.
	 * @param artistName - the desired artistName value.
	 * @deprecated
	 */
    @Deprecated
    public MusicFileData withArtistName(String artistName) {
	    setArtistName(artistName);
	    return this;
    }

    public String getArtistName() {
	    return this.artistName;
    }

    public String getAlbumArtistName() {
    	final String value;
    	Artist albumArtist = this.song.getAlbum().getAlbumArtist();
    	if (albumArtist != null) {
    		value = albumArtist.getName();
		} else {
    		value = this.artistName;
		}
    	return value;
	}

	public String getAlbumName() {
    	final String value;
		Album album = this.song.getAlbum();
		if (album != null) {
			value = album.getTitle();
		} else {
			value = "Unknown Album";
		}
		return value;
	}
}
