import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * This class acts as the medium of contact for ElasticSearch. This class implements the
 * ElasticSearch Java API for interaction.
 *
 * This class also utilizes Google GSON library for converting the objects of Contact class
 * to and from JSON. This allows us to store everything in standard Data Structures utilizing
 * the Contact class like List<Contact> and Map<>
 *
 *
 */

public class ContactService{

    private static final int PORT_ELASTIC_TRANSPORT = 9300;
    private static final String HOST_ELASTIC = "localhost";

    final static Logger logger = Logger.getLogger(ContactService.class);

    static final String indexName = "addressbook";
    static final String typeName = "contact";
    static final String nameKey = "name";
    static final String emailKey = "email";
    static final String contactNumberKey = "contactNumber";
    static final String addressKey = "address";





    private TransportClient client;


    private static ContactService ourInstance = new ContactService();

    public static ContactService getInstance() {
        return ourInstance;
    }


    final static GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    final static Gson gson = builder.create();

    private ContactService() {


        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_ELASTIC), PORT_ELASTIC_TRANSPORT));
            logger.info("Successfully initialized Client for interacting with ElasticSearch on "+HOST_ELASTIC+":"+PORT_ELASTIC_TRANSPORT);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("Error while connecting to ElasticSearch. Please check host and port configuration");
        }


    }


    public List<Contact> getAllContacts(int pageSize, int page, String queryString) {

        List<Contact> contacts = new ArrayList<>();

        SearchResponse response;

        if (queryString != null) {

            logger.info("Using user provided Query for search");

            response = client.prepareSearch(indexName)
                    .setTypes(typeName)
                    .setQuery(new QueryStringQueryBuilder(queryString))
                    .setFrom(page * pageSize).setSize(pageSize).setExplain(true)
                    .get();
        }
        else {

            logger.info("Using default match all Query for search");

            response = client.prepareSearch(indexName)
                    .setTypes(typeName)
                .setQuery(QueryBuilders.matchAllQuery())                 // Query
                    .setFrom(page * pageSize).setSize(pageSize).setExplain(true)
                    .get();
        }

        SearchHit[] results = response.getHits().getHits();


        try {

            for (SearchHit hit : results) {

                String currentRecord = hit.getSourceAsString();

                Contact contact = gson.fromJson(currentRecord,Contact.class);
                contacts.add(contact);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Returning "+contacts.size()+" records from getAllContacts");

        return contacts;

    }


    public Contact getContact(String name) {


        GetResponse getResponse = client.prepareGet(indexName, typeName,name).get();

        System.out.println(getResponse.getSource());

        Contact contact = gson.fromJson(getResponse.getSourceAsString(),Contact.class);

        if (contact == null) {
            logger.info("Could not find record for "+name);
        }
        else {
            logger.info("Found Record for "+name+". Returning Object from getContact");
        }

        return contact;

    }

    public boolean createContact(String name, Map<String, String> params) {

        if (getContact(name) == null) {

            try {

                XContentBuilder json = jsonBuilder();
                json.startObject();


                json.field(nameKey, name);


                if (params.containsKey(emailKey)) {
                    json.field(emailKey,params.get(emailKey));
                }

                if (params.containsKey(contactNumberKey)) {
                    json.field(contactNumberKey,params.get(contactNumberKey));
                }

                if (params.containsKey(addressKey)) {
                    json.field(addressKey,params.get(addressKey));
                }

                json.endObject();

                IndexResponse response = client.prepareIndex(indexName, typeName, name).setSource(json).get();

                logger.info("Contact Creation Successful : " + response);

                return true;


            }
            catch (IOException ex) {
                logger.info("Contact Creation Not Successful");

                return false;
            }

        }
        logger.info("Contact Creation Not Successful");
        return false;

    }

    public boolean updateContact(Map<String, String> params) {

        String name = params.get(nameKey);

        XContentBuilder json = null;

        try {
            json = jsonBuilder();
            json.startObject();

            if (params.containsKey(emailKey)) {
                json.field(emailKey,params.get(emailKey));
                logger.info("In Update.  Reading email from user");

            }

            if (params.containsKey(contactNumberKey)) {

                if (!Contact.validateContactNumber(params.get(contactNumberKey))) {
                    logger.info("In Update.  contactNumber validation failed");

                    return false;
                }
                logger.info("In Update.  Reading contactNumber from user");

                json.field(contactNumberKey, params.get(contactNumberKey));
            }

            if (params.containsKey(addressKey)) {
                String address = params.get(addressKey);

                if (address == null || address.isEmpty()) {
                    logger.info("In Update.  email validation failed");

                    return false;
                }
                logger.info("In Update.  Reading address from user");

                json.field(addressKey, address);

            }

            json.endObject();



            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(indexName)
                    .type(typeName)
                    .id(name)
                    .doc(json);


            UpdateResponse updateResponse = client.update(updateRequest).get();

            System.out.println("Update Response : "+updateResponse.status());

            if (updateResponse.status() == RestStatus.OK) {
                logger.info("Contact Updating Successful");

                return true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        logger.info("Contact Updating Not Successful");

        return false;

    }

    public boolean deleteContact(String name) {


        DeleteResponse deleteResponse = client.prepareDelete(indexName,typeName,name).get();
        System.out.println("Delete Response : "+deleteResponse.getResult().toString());


        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            logger.info("Contact Deletion Successful");
            return true;
        }
        logger.info("Contact Deletion Not Successful");

        return false;

    }





    public static String getContactAsJson(Contact contact) {

        return gson.toJson(contact);
    }




}