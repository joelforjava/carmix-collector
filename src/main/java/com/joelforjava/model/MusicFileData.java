package com.joelforjava.model;

public class MusicFileData {

	private String artistName;
	private String uri;
	
	public MusicFileData(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	void setArtistName(String artistName) {
	    this.artistName = artistName;
    }

    public MusicFileData withArtistName(String artistName) {
	    setArtistName(artistName);
	    return this;
    }

    public String getArtistName() {
	    return this.artistName;
    }
}
