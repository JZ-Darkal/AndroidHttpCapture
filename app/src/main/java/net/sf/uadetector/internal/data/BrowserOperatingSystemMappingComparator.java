package net.sf.uadetector.internal.data;

import javax.annotation.concurrent.ThreadSafe;

import net.sf.uadetector.internal.data.domain.BrowserOperatingSystemMapping;
import net.sf.uadetector.internal.util.CompareNullSafe;

@ThreadSafe
public final class BrowserOperatingSystemMappingComparator extends CompareNullSafe<BrowserOperatingSystemMapping> {

	private static final long serialVersionUID = 3227329029706985170L;

	public static final BrowserOperatingSystemMappingComparator INSTANCE = new BrowserOperatingSystemMappingComparator();

	/**
	 * <strong>Attention:</strong> This class is a stateless singleton and not intended to create more than one object
	 * from it.
	 */
	private BrowserOperatingSystemMappingComparator() {
		// This class is not intended to create own objects from it.
	}

	@Override
	public int compareType(final BrowserOperatingSystemMapping o1, final BrowserOperatingSystemMapping o2) {
		return o1.getBrowserId() < o2.getBrowserId() ? -1 : o1.getBrowserId() == o2.getBrowserId() ? 0 : 1;
	}

}
