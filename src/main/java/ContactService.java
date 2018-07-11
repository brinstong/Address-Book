import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactService {

    private static ContactService ourInstance = new ContactService();

    public static ContactService getInstance() {
        return ourInstance;
    }

    private List<Contact> contacts;


    private ContactService() {

        // initializing with default values;

        contacts = new ArrayList<>();
        try {

            contacts.add(new Contact("abc"));
            contacts.add(new Contact("def"));
            contacts.add(new Contact("ghi"));
            contacts.add(new Contact("jkl"));
            contacts.add(new Contact("mno"));

        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
        }


    }


    public List<Contact> getAllContacts(int pageSize, int page) {

        // TODO : utilize parameters

        return contacts;

    }

    public List<Contact> getAllContacts(String queryStringQuery) {

        // TODO

        return contacts;

    }



    public Contact getContact(String name) {

        for (Contact c : contacts) {
            if (c.getName().equals(name)) {
                return c;
            }
        }

        return null;
    }

    public boolean createContact(String name, Map<String, String> params) {
        // TODO : check what all parameters are required to create a Contact


        Contact contact = null;
        try {
            contact = new Contact(name);
        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
        }

        if (contact != null) {

            if (params.containsKey("email")) {
                contact.setEmail(params.get("email"));
            }

            if (params.containsKey("contactNumber")) {
                contact.setEmail(params.get("contactNumber"));
            }

            if (params.containsKey("address")) {
                contact.setAddress(params.get("address"));
            }


            contacts.add(contact);

            return true;
        }

        return false;
    }

    public boolean updateContact(Map<String, String> params) {

        String name = params.get("name");

        Contact contact = getContact(name);

        if (contact != null) {

            if (params.containsKey("email")) {
                contact.setEmail(params.get("email"));
            }

            if (params.containsKey("contactNumber")) {
                contact.setEmail(params.get("contactNumber"));
            }

            if (params.containsKey("address")) {
                contact.setAddress(params.get("address"));
            }

            return true;
        }

        return false;

    }

    public boolean deleteContact(String name) {

        Contact contact = getContact(name);

        if (contact != null) {
            contacts.remove(contact);
            return true;
        }

        return false;

    }




}