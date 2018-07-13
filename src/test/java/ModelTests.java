import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelTests {

    Contact contact;

    @Before
    public void createObject() {

        contact = new Contact("name1");
    }

    @Test
    public void testName() {
        assertEquals(contact.getName(), "name1");
    }

    @Test
    public void testModifyObject() {
        contact.setEmail("one@one.one");
        contact.setAddress("address one one");
        contact.setContactNumber("1234567890");

        assertEquals(contact.getEmail(), "one@one.one");
        assertEquals(contact.getAddress(), "address one one");
        assertEquals(contact.getContactNumber(), "1234567890");


        assertEquals(contact.toString(), "{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"one@one.one\",\n" +
                "  \"contactNumber\": \"1234567890\",\n" +
                "  \"address\": \"address one one\"\n" +
                "}");
    }


    @Test
    public void testContactNumberValidator() {

        assertTrue(Contact.validateContactNumber("1234567890"));
        assertFalse(Contact.validateContactNumber("123456789"));
        assertFalse(Contact.validateContactNumber("ab12345678"));
        assertFalse(Contact.validateContactNumber("123 456 7890"));
        assertFalse(Contact.validateContactNumber("123a456789"));
    }

}
