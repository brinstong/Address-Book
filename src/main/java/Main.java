import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

public class Main {



    final static int PORT = 8080;
    final static String NAME_QUERY_IDENTIFIER = ":name";
    final static String CONTACT_ENDPOINT_IDENTIFIER = "contact";

    public static void main(String arg[]) {

        port(PORT);
        get("/"+CONTACT_ENDPOINT_IDENTIFIER, "application/json",(req, res) -> getAllContacts(req));
        post("/"+CONTACT_ENDPOINT_IDENTIFIER,"application/json",(req, res) -> createContact(req));
        get("/"+CONTACT_ENDPOINT_IDENTIFIER+"/"+ NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> getContact(req));
        put("/"+CONTACT_ENDPOINT_IDENTIFIER+"/"+ NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> updateContact(req));
        delete("/"+CONTACT_ENDPOINT_IDENTIFIER+"/"+ NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> deleteContact(req));

    }

    private static String deleteContact(Request req) {

        String name = req.params(NAME_QUERY_IDENTIFIER);

        String contactInfo = "Deleting info for "+name+" was ";

        boolean success = ContactService.getInstance().deleteContact(name);

        contactInfo += success?"Successful" : "Not Successful";

        return contactInfo;

    }

    private static Object updateContact(Request req) {


        Map<String, String> params = new HashMap<>();

        for (String key : req.queryParams()) {
            params.put(key, req.queryParams(key));
        }

        params.put("name",req.params("name"));


        String contactInfo = "Updating info for "+req.params("name")+" was ";

        boolean success = ContactService.getInstance().updateContact(params);

        contactInfo += success?"Successful" : "Not Successful";

        return contactInfo;

    }

    private static String getContact(Request req) {

        String name = req.params(NAME_QUERY_IDENTIFIER);

        String contactInfo = "Displaying info for "+name;

        Contact contact = ContactService.getInstance().getContact(name);

        return contactInfo + contact.toString();


    }

    private static String createContact(Request req) {

//        System.out.println("In Create " +req.params("name"));

//        Map<String, String> params = req.params();


        Map<String, String> params = new HashMap<>();

        for (String key : req.queryParams()) {
            params.put(key, req.queryParams(key));
        }


        String name = params.get("name");

        boolean success = ContactService.getInstance().createContact(name, params);

        String contactInfo = "Creating info for "+name;

        contactInfo += success?"Successful" : "Not Successful";

        return contactInfo;
    }

    private static String getAllContacts(Request req) {

        int pageSize = -1;
        int pageNumber = -1;
        String queryStringQuery = "";

        Set<String> queryParams = req.queryParams();

        try {
//            if (queryParams.contains("pageSize"))
                pageSize = Integer.parseInt(req.queryParams("pageSize"));

        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Page Size");
        }

        try {
//            if (queryParams.contains("page"))
                pageNumber = Integer.parseInt(req.queryParams("page"));

        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Page Value");
        }

        try {
//            if (queryParams.contains("query"))
                queryStringQuery = req.queryParams("query");

        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Query");
        }


        List<Contact> contacts;

        /*

        if (!queryStringQuery.isEmpty()) {

            // call the function in elastic class via Service

            contacts = ContactService.getInstance().getAllContacts(queryStringQuery)

        }
        else {



            contacts = ContactService.getInstance().getAllContacts(pageSize, pageNumber);

        }
*/

        contacts = ContactService.getInstance().getAllContacts(pageSize, pageNumber, queryStringQuery);


        StringBuilder output = new StringBuilder("");
        contacts.forEach(x -> {
            output.append(x.toString());
            output.append("\n\n\n");
        });


//        output.append(pageSize);
//        output.append("\n\n\n");
//        output.append(pageNumber);
//        output.append("\n\n\n");
//        output.append(queryStringQuery);
//        output.append("\n\n\n");


        return output.toString();

    }
}
