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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ContactService{


    private static final int PORT_ELASTIC_TRANSPORT = 9300;
    private static final String HOST_ELASTIC = "localhost";


    private TransportClient client;


    private static ContactService ourInstance = new ContactService();

    public static ContactService getInstance() {
        return ourInstance;
    }



    private ContactService() {

        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_ELASTIC), PORT_ELASTIC_TRANSPORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }


    public List<String> getAllContacts(int pageSize, int page, String queryString) {


        SearchResponse response;

        if (queryString != null) {
            response = client.prepareSearch("addressbook")
                    .setTypes("contact")
                    .setQuery(new QueryStringQueryBuilder(queryString))
                    .setFrom(page * pageSize).setSize(pageSize).setExplain(true)
                    .get();
        }
        else {
            response = client.prepareSearch("addressbook")
                    .setTypes("contact")
                .setQuery(QueryBuilders.matchAllQuery())                 // Query
                    .setFrom(page * pageSize).setSize(pageSize).setExplain(true)
                    .get();
        }

        SearchHit[] results = response.getHits().getHits();


        try {

            List<String> listOfContacts = new ArrayList<>();

            for (SearchHit hit : results) {

                String currentRecord = hit.getSourceAsString();
                System.out.println("search result contains " + currentRecord);

                listOfContacts.add(currentRecord);
            }

            return listOfContacts;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;


    }


    public Map<String, Object> getContact(String name) {


        GetResponse getResponse = client.prepareGet("addressbook", "contact",name).get();

        System.out.println(getResponse.getSource());


        return getResponse.getSource();

    }

    public boolean createContact(String name, Map<String, String> params) {

        if (getContact(name) == null) {

            try {

                XContentBuilder json = jsonBuilder();
                json.startObject();



                json.field("name", name);


                if (params.containsKey("email")) {
                    json.field("email",params.get("email"));
                }

                if (params.containsKey("contactNumber")) {
                    json.field("contactNumber",params.get("contactNumber"));
                }

                if (params.containsKey("address")) {
                    json.field("address",params.get("address"));
                }

                json.endObject();

                IndexResponse response = client.prepareIndex("addressbook", "contact", name).setSource(json).get();

                System.out.println("Successful : " + response);

                return true;


            }
            catch (IOException ex) {
                return false;
            }

        }

        return false;

    }

    public boolean updateContact(Map<String, String> params) {

        String name = params.get("name");

        XContentBuilder json = null;

        try {
            json = jsonBuilder();
            json.startObject();

            if (params.containsKey("email")) {
                json.field("email",params.get("email"));
            }

            if (params.containsKey("contactNumber")) {

                if (!validateContactNumber(params.get("contactNumber"))) {
                    return false;
                }
                json.field("contactNumber", params.get("contactNumber"));
            }

            if (params.containsKey("address")) {
                String address = params.get("address");

                if (address == null || address.isEmpty()) {
                    return false;
                }
                json.field("address", address);

            }

            json.endObject();



            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index("addressbook")
                    .type("contact")
                    .id(name)
                    .doc(json);


            UpdateResponse updateResponse = client.update(updateRequest).get();

            System.out.println("Update Response : "+updateResponse.status());

            if (updateResponse.status() == RestStatus.OK) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        return false;

    }

    public boolean deleteContact(String name) {


        DeleteResponse deleteResponse = client.prepareDelete("addressbook","contact",name).get();
        System.out.println("Delete Response : "+deleteResponse.getResult().toString());


        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            return true;
        }

        return false;

    }


    private static boolean validateContactNumber(String contactNumber) {
        // return true if contact Number is valid

        if (contactNumber.length() != 10) {
            return false;
        }

        return contactNumber.chars().allMatch(Character::isDigit);

    }




}