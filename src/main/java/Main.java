import spark.Request;

import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

public class Main {

    // TODO make query params Constant


    final static int PORT = 8080;

    public static void main(String arg[]) {

        port(PORT);
        get("/contact", "application/json",(req, res) -> getAllContacts(req));
        post("/contact","application/json",(req, res) -> createContact(req));
        get("/contact/:name", "application/json", (req,res) -> getContact(req));
        put("/contact/:name", "application/json", (req,res) -> updateContact(req));
        delete("/contact/:name", "application/json", (req, res) -> deleteContact(req));

    }

    private static String deleteContact(Request req) {

        String name = req.params(":name");

        String contactInfo = "Deleting info for "+name+" !! ";

        boolean success = ContactService.getInstance().deleteContact(name);

        contactInfo += success?"Successful" : "Not Successful";

        return contactInfo;

    }

    private static Object updateContact(Request req) {


        Map<String, String> params = req.params();


        String contactInfo = "Updating info for "+params.get("name")+" !! ";

        boolean success = ContactService.getInstance().updateContact(params);

        contactInfo += success?"Successful" : "Not Successful";

        return contactInfo;

    }

    private static String getContact(Request req) {

        String name = req.params(":name");

        String contactInfo = "Displaying info for "+name;

        Contact contact = ContactService.getInstance().getContact(name);

        return contactInfo + contact.toString();


    }

    private static String createContact(Request req) {

        Map<String, String> params = req.params();

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

        try {
            Set<String> queryParams = req.queryParams();

            try {
                if (queryParams.contains("pageSize"))
                    pageSize = Integer.parseInt(req.queryParams("pageSize"));

            } catch (NumberFormatException nfe) {
                // TODO
            }

            try {
                if (queryParams.contains("page"))
                    pageNumber = Integer.parseInt(req.queryParams("page"));

            } catch (NumberFormatException nfe) {
                // TODO
            }

            try {
                if (queryParams.contains("query"))
                    queryStringQuery = req.queryParams("query");

            } catch (NumberFormatException nfe) {
                // TODO
            }


        }
        catch (Exception e) {
            // TODO
        }


        if (!queryStringQuery.isEmpty()) {

            // TODO

            return null;
        }
        else {


            StringBuilder output = new StringBuilder("");

            ContactService.getInstance().getAllContacts(pageSize, pageNumber)
                    .forEach(x -> {
                        output.append(x.getName());
                        output.append("\n\n\n");
                    });


            output.append(pageSize);
            output.append("\n\n\n");
            output.append(pageNumber);
            output.append("\n\n\n");
            output.append(queryStringQuery);
            output.append("\n\n\n");


            return output.toString();
        }
    }
}
