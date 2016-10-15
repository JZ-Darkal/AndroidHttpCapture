package net.sf.uadetector.internal.data.domain;

/**
 * Defines domain objects that have a numeric identifier (ID).
 * 
 * @author André Rouél
 */
public interface Identifiable {

	/**
	 * Returns the identifier (ID) of an instance.
	 * 
	 * @return numeric identifier
	 */
	int getId();

}
