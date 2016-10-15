package net.sf.uadetector;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;

/**
 * Defines a category of devices.<br>
 * <p>
 * A device category is a group that is often determined by considering various aspects such as form factor,
 * functionality and its application. Common to all devices within a group is that they have at least one specific user
 * agent.
 * <p>
 * The implementation of this interface may be mutable or immutable. This interface only gives access to retrieve data,
 * never to change it.
 * 
 * @author André Rouél
 */
public interface ReadableDeviceCategory {

	/**
	 * Gets the enum value of a category.
	 * <p>
	 * It provides all known device categories at the time of implementing this and makes it easier and type-safe to
	 * query for a specific one rather the comparing the name.
	 * 
	 * @return enum value of a category
	 */
	@Nonnull
	Category getCategory();

	/**
	 * Gets the icon of the category.
	 * 
	 * @return icon of the category
	 */
	@Nonnull
	String getIcon();

	/**
	 * Returns the URL to get more informations behind a category.
	 * 
	 * @return information URL
	 */
	@Nonnull
	String getInfoUrl();

	/**
	 * Gets the category name.
	 * 
	 * @return name of the category
	 */
	@Nonnull
	String getName();

	/**
	 * Contains all at the time of implementation known device categories, so that a caller can easily and type-safe
	 * test against them.
	 */
	enum Category {

		/**
		 * A game console is an interactive computer that produces a video display signal which can be used with a
		 * display device (a television, monitor, etc.) to display a video game. The term "game console" is used to
		 * distinguish a machine designed for people to buy and use primarily for playing video games on a TV in
		 * contrast to arcade machines, handheld game consoles, or home computers.
		 */
		GAME_CONSOLE("Game console"),

		/**
		 * A device that doesn't match the other categories
		 */
		OTHER("Other"),

		/**
		 * A personal digital assistant (PDA), also known as a palmtop computer, or personal data assistant, is a mobile
		 * device that functions as a personal information manager. PDAs are largely considered obsolete with the
		 * widespread adoption of smartphones.
		 */
		PDA("PDA"),

		/**
		 * A personal computer (PC) is a general-purpose computer, whose size, capabilities, and original sale price
		 * makes it useful for individuals, and which is intended to be operated directly by an end-user with no
		 * intervening computer operator.
		 */
		PERSONAL_COMPUTER("Personal computer"),

		/**
		 * A smart TV, sometimes referred to as connected TV or hybrid TV
		 */
		SMART_TV("Smart TV"),

		/**
		 * A smartphone is a mobile phone built on a mobile operating system, with more advanced computing capability
		 * and connectivity than a feature phone
		 */
		SMARTPHONE("Smartphone"),

		/**
		 * A tablet computer, or simply tablet, is a mobile computer with display, circuitry and battery in a single
		 * unit. Tablets are often equipped with sensors, including cameras, microphone, accelerometer and touchscreen,
		 * with finger or stylus gestures replacing computer mouse and keyboard.
		 */
		TABLET("Tablet"),

		/**
		 * An unknown device category
		 */
		UNKNOWN(""),

		/**
		 * Wearable computers, also known as body-borne computers are miniature electronic devices that are worn by the
		 * bearer under, with or on top of clothing.
		 */
		WEARABLE_COMPUTER("Wearable computer");

		/**
		 * Tries to find by the given category name a matching enum value. The category name must match against an
		 * device entry in the <i>UAS data</i>.
		 * 
		 * @param categoryName
		 *            name of an device category
		 * @return the matching enum value or {@link #UNKNOWN}
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 */
		public static Category evaluate(@Nonnull final String categoryName) {
			Check.notNull(categoryName, "categoryName");

			Category result = UNKNOWN;
			for (final Category value : values()) {
				if (value.getName().equals(categoryName)) {
					result = value;
					break;
				}
			}
			return result;
		}

		/**
		 * Name of the device category
		 */
		@Nonnull
		private final String name;

		private Category(@Nonnull final String name) {
			this.name = name;
		}

		/**
		 * Returns the name of the device category.
		 * 
		 * @return name of the category
		 */
		@Nonnull
		public String getName() {
			return name;
		}

	}

}
