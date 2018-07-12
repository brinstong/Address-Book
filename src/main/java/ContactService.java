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
        contacts.add(new Contact("abc"));
        contacts.add(new Contact("def"));
        contacts.add(new Contact("ghi"));
        contacts.add(new Contact("jkl"));
        contacts.add(new Contact("mno"));


    }


    public List<Contact> getAllContacts(int pageSize, int page, String queryString) {

        // assuming start page is 1
        /*
        page_size = 10
        page  pages
        1     00 - 09
        2     10 - 19
        3     20 - 29
        */

        /*
        This always returns the last page if the pageNumber exceeds the total pages available.
         */

        int startIndex = Math.min(pageSize * (page -1), contacts.size() - (contacts.size() % pageSize));
        // end index is exclusive
        int endIndex = Math.min(pageSize * page, contacts.size());
        return getAllContacts(queryString).subList(startIndex,endIndex);

    }

    public List<Contact> getAllContacts(String queryStringQuery) {

        // TODO : fetch all from Elastic

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

        if (getContact(name) == null) {

            Contact contact = null;
            contact = new Contact(name);

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
                if (!contact.setContactNumber(params.get("contactNumber"))) {
                    return false;
                }
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