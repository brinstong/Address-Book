import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ContactServiceTests {

    ContactService contactService;

    @Before
    public void createObject() {
        contactService = ContactService.getInstance();
    }


    @Test
    public void checkIfSameInstance() {

        assertEquals(contactService, ContactService.getInstance());
    }


    @Test
    public void testCreate() {

        Map<String, String> map = new HashMap<>();
        map.put("email","ema@il.com");

        contactService.createContact("name1",map);

        assertNotEquals(contactService.getAllContacts(5,0,null).toString(),"[]");
        assertEquals(contactService.getContact("name1").toString(),"{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"ema@il.com\"\n" +
                "}");
        assertTrue(contactService.getAllContacts(10,0,null).size()>0);
    }


    @Test
    public void testUpdate() {

        contactService.deleteContact("name1");

        Map<String, String> map = new HashMap<>();
        map.put("email","ema@il.com");

        contactService.createContact("name1",map);
        assertEquals(contactService.getContact("name1").toString(),"{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"ema@il.com\"\n" +
                "}");

        Map<String , String > map1= new HashMap<>();
        map1.put("contactNumber","1234567890");
        map1.put("name","name1");
        contactService.updateContact(map1);

        assertEquals(contactService.getContact("name1").toString(),"{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"ema@il.com\",\n" +
                "  \"contactNumber\": \"1234567890\"\n" +
                "}");

    }


    @Test(expected = NullPointerException.class)
    public void testDelete() {

        contactService.deleteContact("name1");

        Map<String, String> map = new HashMap<>();
        map.put("email","ema@il.com");

        contactService.createContact("name1",map);
        assertEquals(contactService.getContact("name1").toString(),"{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"ema@il.com\"\n" +
                "}");


        contactService.deleteContact("name1");

        assertNull(contactService.getContact("name1").toString());
    }


    @Test
    public void testGetContactAsJson() {

        contactService.deleteContact("name1");

        Map<String, String> map = new HashMap<>();
        map.put("email","ema@il.com");

        contactService.createContact("name1",map);

        assertEquals(ContactService.getContactAsJson(contactService.getContact("name1")),"{\n" +
                "  \"name\": \"name1\",\n" +
                "  \"email\": \"ema@il.com\"\n" +
                "}");

    }

}
