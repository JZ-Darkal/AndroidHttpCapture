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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;

/**
 * This enum represents the more commonly used operating system families. It will never be complete, but can assist in
 * identifying the version of an operating system.
 * 
 * @author André Rouél
 */
public enum OperatingSystemFamily {

	/**
	 * AIX (Advanced Interactive eXecutive) is a Unix operating system from IBM.
	 */
	AIX("AIX", Pattern.compile("AIX")),

	/**
	 * AROS is a free operating system aiming at being compatible with AmigaOS at the API level.
	 */
	AROS("AROS", Pattern.compile("AROS")),

	/**
	 * AmigaOS is the native operating system for the Commodore Amiga, consisting of the components of Workbench,
	 * AmigaDOS with the command line interpreter CLI (later renamed to shell) and for many Amiga models in the ROM
	 * included kernel <i>kickstart</i>.
	 */
	AMIGA_OS("Amiga OS", Pattern.compile("Amiga OS")),

	/**
	 * Android is both an operating system and a software platform for mobile devices like smart phones, mobile phones,
	 * netbooks and tablets, which is developed by the Open Handset Alliance.
	 */
	ANDROID("Android", Pattern.compile("Android")),

	/**
	 * The Berkeley Software Distribution (BSD) is a version of the Unix operating system, which was created at the
	 * University of California at Berkeley in 1977.
	 */
	BSD("BSD", Pattern.compile("BSD")),

	/**
	 * Bada is a service-oriented operating system that is developed by Samsung Electronics and is designed for use in
	 * smartphones.
	 */
	BADA("Bada", Pattern.compile("Bada")),

	/**
	 * Be Operating System (BeOS) was an operating system of the company <i>Be Incorporated</i> and was called in later
	 * versions Be. Due to its multimedia capabilities it is also commonly called "Media OS".
	 */
	BEOS("BeOS", Pattern.compile("BeOS")),

	/**
	 * Chrome OS is an operating system based on the Linux kernel and designed by Google to work with web applications and installed applications. 
	 */
	CHROME_OS("Chrome OS", Pattern.compile("Chome OS")),

	/**
	 * Danger OS is a smartphone operating system. It is used on Sidekick devices, which are sold in Germany by
	 * T-Mobile.
	 */
	DANGEROS("DangerOS", Pattern.compile("DangerOS")),

	/**
	 * Firefox OS is an open source operating system for smartphones and tablet computers being developed by Mozilla.
	 */
	FIREFOX_OS("Firefox OS", Pattern.compile("Firefox OS")),

	/**
	 * HP-UX (Hewlett Packard UniX) is a commercial Unix operating system from Hewlett-Packard and is based on UNIX
	 * System V.
	 */
	HPUX("HP-UX", Pattern.compile("HP-UX")),

	/**
	 * Haiku (formerly OpenBeOS) is an open-source project with the aim, to reprogram and expand that in 2001 abandoned
	 * operating system BeOS.
	 */
	HAIKU("Haiku OS", Pattern.compile("Haiku OS")),

	/**
	 * IRIX is a commercial Unix operating system of the company Silicon Graphics (SGI).
	 */
	IRIX("IRIX", Pattern.compile("IRIX")),

	/**
	 * Inferno is a distributed computer operating system that comes from Bell Laboratories.
	 */
	INFERNO_OS("Inferno OS", Pattern.compile("Inferno OS")),

	/**
	 * The Java Virtual Machine (abbreviated Java VM or JVM) is the part of the Java Runtime Environment (JRE) for Java
	 * programs, which is responsible for the execution of Java bytecode.<br>
	 * <br>
	 * This value is not an operating system family.
	 */
	JVM("JVM", Pattern.compile("JVM")),

	/**
	 * Linux or GNU/Linux are usually called free, unix-like multi-user operating systems running based on the Linux
	 * kernel and other GNU software.
	 */
	LINUX("Linux", Pattern.compile("Linux")),

	/**
	 * Mac OS is the name of the classic operating system (1984-2001) by Apple for Macintosh computers.
	 */
	MAC_OS("Mac OS", Pattern.compile("Mac OS")),

	/**
	 * Maemo is a linux based software platform for smartphones and Internet tablets.
	 */
	MAEMO("Maemo", Pattern.compile("Maemo")),

	/**
	 * MeeGo was a Linux kernel-based free mobile operating system project resulting from the fusion of Intel's Moblin
	 * and Nokia's Maemo operating systems.
	 */
	MEEGO("MeeGo", Pattern.compile("MeeGo")),

	/**
	 * Minix is a free unixoides operating system that was developed by Andrew S. Tanenbaum at the Free University of
	 * Amsterdam as a teaching tool.
	 */
	MINIX("MINIX", Pattern.compile("MINIX")),

	/**
	 * OpenVMS (Open Virtual Memory System), previously known as VAX-11/VMS, VAX/VMS or (informally) VMS, is a computer
	 * server operating system that runs on VAX, Alpha and Itanium-based families of computers.
	 */
	OPENVMS("OpenVMS", Pattern.compile("OpenVMS")),

	/**
	 * OS X, formerly Mac OS X, is a Unix-based operating systems developed by Apple. It is a proprietary distribution
	 * of the free Darwin operating system from Apple.
	 */
	OS_X("OS X", Pattern.compile("(Mac OS X|OS X)")),

	/**
	 * MorphOS is an Amiga-compatible computer operating system. It is a mixed proprietary and open source OS produced
	 * for the Pegasos PowerPC processor based computer.
	 */
	MORPHOS("MorphOS", Pattern.compile("MorphOS")),

	/**
	 * This value indicates the operating systems from Nintendo, which they developed for their devices.<br>
	 * <br>
	 * This value is not an operating system family.
	 */
	NINTENDO("Nintendo", Pattern.compile("Nintendo")),

	/**
	 * OS/2 is a multitasking operating system for PCs. It was originally developed by IBM and Microsoft together with
	 * the aim to replace DOS.
	 */
	OS_2("OS/2", Pattern.compile("OS/2")),

	/**
	 * Palm OS was the operating system for organizer of the Palm series (known as PDAs) and smartphones.
	 */
	PALM_OS("Palm OS", Pattern.compile("Palm OS")),

	/**
	 * The PlayStation Vita system software is the official, updatable firmware and operating system for the PlayStation
	 * Vita.
	 */
	PLAYSTATION_VITA("LiveArea", Pattern.compile("LiveArea")),

	/**
	 * QNX is a POSIX-compatible proprietary Unix-like real-time operating system that focused primarily at the embedded
	 * market.
	 */
	QNX("QNX", Pattern.compile("QNX")),

	/**
	 * RISC OS is a computer operating system originally designed by Acorn Computers Ltd. It was specifically designed
	 * to run on the ARM chipset, which Acorn had designed concurrently for use in its new line of Archimedes personal
	 * computers.
	 */
	RISC_OS("RISC OS", Pattern.compile("RISC OS|RISK OS")),

	/**
	 * Binary Runtime Environment for Wireless (Brew MP, Brew, or BREW) is an application development platform created
	 * by Qualcomm.
	 */
	BREW("Brew", Pattern.compile("Brew")),

	/**
	 * BlackBerry OS (up to the fifth edition known as the <i>BlackBerry Device Software</i>, also known as <i>Research
	 * In Motion OS</i>) is a proprietary mobile operating system developed by BlackBerry Ltd for its BlackBerry line of
	 * smartphone handheld devices.
	 */
	BLACKBERRY_OS("BlackBerry OS", Pattern.compile("(RIM OS|BlackBerry OS)")),

	/**
	 * Sailfish is a Linux-based mobile operating system developed by Jolla in cooperation with the Mer project and
	 * supported by the Sailfish Alliance.
	 */
	SAILFISH_OS("Sailfish", Pattern.compile("Sailfish")),

	/**
	 * Solaris is the name of an operating system distribution based on SunOS and is a Unix operating system. Since the
	 * takeover of Sun Microsystems in 2010 Solaris is part of Oracle.
	 */
	SOLARIS("Solaris", Pattern.compile("Solaris")),

	/**
	 * Syllable is a slim and fast desktop Unix-like operating system for x86 processors.
	 */
	SYLLABLE("Syllable", Pattern.compile("Syllable")),

	/**
	 * The Symbian platform, simply called Symbian, is an operating system for smartphones and PDAs. The Symbian
	 * platform is the successor to Symbian OS
	 */
	SYMBIAN("Symbian OS", Pattern.compile("Symbian OS")),

	/**
	 * Tizen is a free operating system based on Linux respectively Debian and was launched by the Linux Foundation and
	 * LiMo Foundation.
	 */
	TIZEN("Tizen", Pattern.compile("Tizen")),

	/**
	 * The Wii Operating System is based on Nintendo’s proprietary software and runs on the Wii video game console.
	 */
	WII_OS("Wii OS", Pattern.compile("Nintendo Wii|Wii OS")),

	/**
	 * Microsoft Windows is a trademark for operating systems of the Microsoft Corporation. Microsoft Windows was
	 * originally a graphical extension of the operating system MS-DOS.
	 */
	WINDOWS("Windows", Pattern.compile("Windows")),

	/**
	 * XrossMediaBar (XMB) is the name of the graphical user interface, which are used on PlayStation 3, PlayStation
	 * Portable, Sony Blu-Ray players and Sony Bravia TVs. Also some special versions of the PlayStation 2, PSX, already
	 * using the XMB.
	 */
	XROSSMEDIABAR("XrossMediaBar (XMB)", Pattern.compile("XrossMediaBar (XMB)")),

	/**
	 * iOS (until June 2010 iPhone OS) is the standard operating system of Apple products like iPhone, iPod touch, iPad,
	 * and the second generation of Apple TV. iOS is based on Mac OS X.
	 */
	IOS("iOS", Pattern.compile("iOS|iPhone OS")),

	/**
	 * webOS is a smartphone and tablet operating system from Hewlett-Packard (formerly HP Palm). It represents the
	 * follower of Palm OS.
	 */
	WEBOS("webOS", Pattern.compile("webOS")),

	/**
	 * Unknown operating system family<br>
	 * <br>
	 * This value will be returned if the operating system family cannot be determined.
	 */
	UNKNOWN("", Pattern.compile("^$"));

	/**
	 * This method try to find by the given family name a matching enum value. The family name must match against an
	 * operating system entry in UAS data file.
	 * 
	 * @param family
	 *            name of an operating system family
	 * @return the matching enum value or {@code OperatingSystemFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	public static OperatingSystemFamily evaluate(@Nonnull final String family) {
		Check.notNull(family, "family");

		OperatingSystemFamily result = UNKNOWN;

		// search by name
		result = evaluateByName(family);

		// search by pattern
		if (result == UNKNOWN) {
			result = evaluateByPattern(family);
		}

		return result;
	}

	/**
	 * This method try to find by the given family name a matching enum value. The family name will be evaluated against
	 * the stored name of an operating system entry.
	 * 
	 * @param family
	 *            name of an operating system family
	 * @return the matching enum value or {@code OperatingSystemFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	protected static OperatingSystemFamily evaluateByName(@Nonnull final String family) {
		Check.notNull(family, "family");

		OperatingSystemFamily result = UNKNOWN;
		for (final OperatingSystemFamily value : values()) {
			if (value.getName().equals(family)) {
				result = value;
				break;
			}
		}

		return result;
	}

	/**
	 * This method try to find by the given family name a matching enum value. The family name will be evaluated against
	 * the stored regular expression of an operating system entry.
	 * 
	 * @param family
	 *            name of an operating system family
	 * @return the matching enum value or {@code OperatingSystemFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	protected static OperatingSystemFamily evaluateByPattern(@Nonnull final String family) {
		Check.notNull(family, "family");

		OperatingSystemFamily result = UNKNOWN;
		for (final OperatingSystemFamily value : values()) {
			final Matcher m = value.getPattern().matcher(family);
			if (m.matches()) {
				result = value;
				break;
			}
		}

		return result;
	}

	/**
	 * The internal family name in the UAS database.
	 */
	@Nonnull
	private final String name;

	/**
	 * The regular expression which a family name must be match.
	 */
	@Nonnull
	private final Pattern pattern;

	private OperatingSystemFamily(@Nonnull final String name, @Nonnull final Pattern pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	/**
	 * Gets the internal family name in the UAS database.
	 * 
	 * @return the internal family name
	 */
	@Nonnull
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the regular expression which a family name must be match with.
	 * 
	 * @return regular expression
	 */
	@Nonnull
	public Pattern getPattern() {
		return pattern;
	}

}
