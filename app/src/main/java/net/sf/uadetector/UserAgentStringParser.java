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

import javax.annotation.PreDestroy;

/**
 * Basic interface for user agent string parsers.
 * 
 * @author André Rouél
 */
public interface UserAgentStringParser {

	/**
	 * Returns the current version information of the used <em>UAS data</em>.
	 * 
	 * <p>
	 * The version will be set by the <em>UAS data provider</em> (http://user-agent-string.info/) and the version should
	 * look like this:<br>
	 * <br>
	 * format: <code>YYYYMMDD-counter</code> (<code>counter</code> is two digits long)<br>
	 * example: <code>20120931-02</code>
	 * 
	 * @return version of the current <em>UAS data</em>
	 */
	String getDataVersion();

	/**
	 * Detects informations about a network client based on a user agent string.<br>
	 * <br>
	 * Typically user agent string will be read by an instance of {@code HttpServletRequest}. With the method
	 * {@code getHeader("User-Agent")} you can get direct access to this string.
	 * 
	 * @param userAgent
	 *            user agent string
	 * @return the detected information of an user agent
	 */
	ReadableUserAgent parse(final String userAgent);

	/**
	 * In environments where the JVM will never shut down while reinstalling UADetector, it is necessary to manually
	 * shutdown running threads of <code>UserAgentStringParser</code>s with <b>updating functionality</b> like
	 * <code>UADetectorServiceFactory.getCachingAndUpdatingParser()</code> or
	 * <code>UADetectorServiceFactory.getOnlineUpdatingParser()</code>.
	 * <p>
	 * For example, if a web application with UADetector will be re-deployed within an <i>Apache Tomcat</i> you must
	 * shutdown your self-created or via <code>UADetectorServiceFactory</code> retrieved updating
	 * <code>UserAgentStringParser</code> otherwise more and more threads will be registered.
	 * <p>
	 * An implementation of <code>UserAgentStringParser</code> has <b>updating functionality</b> if it works with a
	 * {@link net.sf.uadetector.datastore.RefreshableDataStore}.
	 * <p>
	 * If you call shutdown on a non-updating <code>UserAgentStringParser</code> implementation nothing will happen.
	 * <p>
	 * A number of Dependency Injection containers support the annotation {@link PreDestroy} which is be useful for
	 * indicating methods that should be called when the container is shutting down. This annotation is available by
	 * default in Java SE 7 and can be made available through the external library <i>jsr250-api-1.0.jar</i> for earlier
	 * versions of Java.
	 * <p>
	 * We recommend to annotate an implementation of {@link #shutdown()} with {@link PreDestroy} to inform a container
	 * (for example <i>Spring Framework</i>) to trigger the shutdown method automatically by convention during the
	 * shutdown lifecycle. This saves developers to call explicitly the shutdown method.
	 * <p>
	 * To shutdown all managed {@code ExecutorService} by UADetector at once, you can call also
	 * {@link net.sf.uadetector.internal.util.ExecutorServices#shutdownAll()}.
	 */
	void shutdown();

}
