/*******************************************************************************
 * Copyright 2012 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Defines a version number of an operating system or user agent.<br>
 * <br>
 * Generally, a version number represents unique states of a software. Version numbers are different versions of a
 * single software in order to distinguish different versions of development cycles.<br>
 * <br>
 * A classic version number is often composed of:
 * <ul>
 * <li><strong>Major release</strong><br>
 * indexes mostly very significant change in the program - for example when the program was completely rewritten or
 * libraries can be maintained at no interface compatibility.</li>
 * <li><strong>Minor release</strong><br>
 * usually referred to a functional extension of a program.</li>
 * <li><strong>Patch level</strong><br>
 * contains mostly bug fixes.</li>
 * </ul>
 * A version number may also contain additions, for example, to document a development stage of a software.<br>
 * <br>
 * The implementation of this interface may be mutable or immutable. This interface only gives access to retrieve data,
 * never to change it.
 * 
 * @author André Rouél
 */
public interface ReadableVersionNumber extends Comparable<ReadableVersionNumber> {

	/**
	 * Gets the bugfix category of the version number.
	 * 
	 * @return bugfix segment
	 */
	@Nonnull
	String getBugfix();

	/**
	 * Gets the additions or extension of the version number.
	 * 
	 * @return extension of the version number
	 */
	@Nonnull
	String getExtension();

	/**
	 * Get all groups (or categories) of this version number. The first element in the list is the major category,
	 * followed by the minor and bugfix segment of the version number.<br>
	 * <br>
	 * The returned list of the version number segments should be immutable.
	 * 
	 * @return a list of segments of the version number
	 */
	@Nonnull
	List<String> getGroups();

	/**
	 * Gets the major category of the version number.
	 * 
	 * @return major segment
	 */
	@Nonnull
	String getMajor();

	/**
	 * Gets the minor category of the version number.
	 * 
	 * @return minor segment
	 */
	@Nonnull
	String getMinor();

	/**
	 * Gets this version number as string.
	 * 
	 * @return numeric groups as dot separated version string
	 */
	@Nonnull
	String toVersionString();

}
