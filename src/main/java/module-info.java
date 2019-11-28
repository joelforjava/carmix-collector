/**
 * 
 */
module com.joelforjava.carmixcreator.gui {
	exports com.joelforjava;
	exports com.joelforjava.processor;
	exports com.joelforjava.request;
	exports com.joelforjava.service;
	exports com.joelforjava.model;

	// TODO - make this JAR Java 9 friendly?
	requires musicdb.domain.objects;
	requires commons.lang3;
	requires java.desktop;
	requires java.logging;
	requires jaudiotagger;
}