import javax.naming.InsufficientResourcesException;

public class Contact {

    // name is a unique identifier
    private String name;
    private String email;
    private String contactNumber;
    private String address;


    Contact(String name) throws InsufficientResourcesException {

        if (name == null || name.isEmpty()) {
            throw new InsufficientResourcesException("Name should not be empty");
            // TODO How else to handle
        }

        this.name = name;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
