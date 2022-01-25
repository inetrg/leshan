package org.eclipse.leshan.core.attributes;

import static org.junit.Assert.*;

import org.junit.Test;

public class AttributeTest {

    @Test
    public void should_pick_correct_model() {
        LwM2mAttribute<String> verAttribute = new LwM2mAttribute<String>(LwM2mAttributeModel.OBJECT_VERSION_ATTR,
                "1.0");
        assertEquals("ver", verAttribute.getName());
        assertEquals("1.0", verAttribute.getValue());
        assertTrue(verAttribute.canBeAssignedTo(AssignationLevel.OBJECT));
        assertFalse(verAttribute.isWritable());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void should_throw_on_invalid_value_type() {
        new LwM2mAttribute(LwM2mAttributeModel.OBJECT_VERSION_ATTR, 123);
    }
}
