import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

/**
 * This class is the entry point to the application.
 * <p>
 * This class interacts with the Spark framework and sets the mapping for REST calls.
 * <p>
 * This class also interacts with the ContactService class in order to fetch data from
 * ElastoicSearch DataStore.
 */

public class Main {


    final static int PORT_SPARK = 8080;
    final static String NAME_QUERY_IDENTIFIER = ":name";
    final static String CONTACT_ENDPOINT_IDENTIFIER = "contact";
    final static Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String arg[]) {


        // set port number to run spark on
        port(PORT_SPARK);

        // set mappings for rest calls
        get("/" + CONTACT_ENDPOINT_IDENTIFIER, "application/json", (req, res) -> getAllContacts(req));
        post("/" + CONTACT_ENDPOINT_IDENTIFIER, "application/json", (req, res) -> createContact(req));
        get("/" + CONTACT_ENDPOINT_IDENTIFIER + "/" + NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> getContact(req));
        put("/" + CONTACT_ENDPOINT_IDENTIFIER + "/" + NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> updateContact(req));
        delete("/" + CONTACT_ENDPOINT_IDENTIFIER + "/" + NAME_QUERY_IDENTIFIER, "application/json", (req, res) -> deleteContact(req));

    }

    // function to delete a contact
    private static String deleteContact(Request req) {

        String name = req.params(NAME_QUERY_IDENTIFIER);

        String contactInfo = "Deleting info for " + name + " was ";

        boolean success = ContactService.getInstance().deleteContact(name);

        contactInfo += success ? "Successful" : "Not Successful";

        logger.info(contactInfo);

        return contactInfo;

    }

    // function to update info of existing contact
    private static String updateContact(Request req) {


        Map<String, String> params = new HashMap<>();

        for (String key : req.queryParams()) {
            params.put(key, req.queryParams(key));
        }

        params.put("name", req.params("name"));


        String contactInfo = "Updating info for " + req.params("name") + " was ";

        boolean success = ContactService.getInstance().updateContact(params);

        contactInfo += success ? "Successful" : "Not Successful";

        logger.info(contactInfo);

        return contactInfo;

    }

    // function to get info of an existing contact
    private static Contact getContact(Request req) {

        String name = req.params(NAME_QUERY_IDENTIFIER);

        Contact contact = ContactService.getInstance().getContact(name);


        logger.info("Returning from getContact for " + name);

        return contact;


    }

    // function to create new contact
    private static String createContact(Request req) {


        Map<String, String> params = new HashMap<>();

        for (String key : req.queryParams()) {
            params.put(key, req.queryParams(key));
        }


        String name = params.get("name");

        boolean success = ContactService.getInstance().createContact(name, params);

        String contactInfo = "Creating info for " + name + " was ";

        contactInfo += success ? "Successful" : "Not Successful";

        logger.info(contactInfo);


        return contactInfo;
    }

    // function to get a list of all contacts. Prints as a JsonArray
    private static List<Contact> getAllContacts(Request req) {

        // default values
        int pageSize = 10;
        int pageNumber = 0;
        String queryStringQuery = null;

        Set<String> queryParams = req.queryParams();

        try {
            if (queryParams.contains("pageSize")) {
                pageSize = Integer.parseInt(req.queryParams("pageSize"));
                logger.info("Setting user defined pageSize : " + pageSize);
            } else {
                logger.warn("Using default pageSize : " + pageSize);
            }

        } catch (NumberFormatException nfe) {
//            throw new IllegalArgumentException("Invalid Page Size");
            logger.warn("PageSize not correct. Using default");
        }

        try {
            if (queryParams.contains("page")) {
                pageNumber = Integer.parseInt(req.queryParams("page"));
                logger.info("Setting user defined pageNumber : " + pageNumber);

            } else {
                logger.warn("Using default pageNumber : " + pageNumber);

            }
        } catch (NumberFormatException nfe) {
//            throw new IllegalArgumentException("Invalid Page Value");
            logger.warn("PageNumber not correct. Using default");

        }


        if (queryParams.contains("query"))
            queryStringQuery = req.queryParams("query");
        else
            logger.warn("Query not provided. Using default of match all");


        return ContactService.getInstance().getAllContacts(pageSize, pageNumber, queryStringQuery);

    }
}
