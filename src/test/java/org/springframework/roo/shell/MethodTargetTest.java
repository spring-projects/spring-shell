package org.springframework.roo.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Unit test of {@link MethodTarget}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class MethodTargetTest {

	// Constants
	private static final Object TARGET_1 = new CommandMarker() {};
	private static final Object TARGET_2 = new CommandMarker() {};
	private static final Method METHOD_1 = TARGET_1.getClass().getMethods()[0];	// unmockable
	private static final Method METHOD_2 = TARGET_2.getClass().getMethods()[1];	// unmockable
	
	@Test
	public void testInstanceEqualsItself() {
		final MethodTarget instance = new MethodTarget(METHOD_1, TARGET_1);
		assertEquals(instance, instance);
	}
	
	@Test
	public void testInstanceDoesNotEqualNull() {
		assertFalse(new MethodTarget(METHOD_1, TARGET_1).equals(null));
	}
	
	@Test
	public void testInstancesWithSameMethodAndTargetAreEqualAndHaveSameHashCode() {
		final MethodTarget instance1 = new MethodTarget(METHOD_1, TARGET_1, "the-buff", "the-key");
		final MethodTarget instance2 = new MethodTarget(METHOD_1, TARGET_1);
		assertEquals(instance1, instance2);
		assertEquals(instance1.hashCode(), instance2.hashCode());
	}

	@Test
	public void testInstancesWithDifferentMethodAreNotEqual() {
		assertFalse(new MethodTarget(METHOD_1, TARGET_1).equals(new MethodTarget(METHOD_2, TARGET_1)));
	}
	
	@Test
	public void testInstancesWithDifferentTargetAreNotEqual() {
		assertFalse(new MethodTarget(METHOD_1, TARGET_1).equals(new MethodTarget(METHOD_1, TARGET_2)));
	}
}
