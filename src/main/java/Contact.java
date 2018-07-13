/**
 * This class acts like a model for storage and retrieval from the DataStore.
 * This could have completely been skipped but I have created this for Simplicity
 * of reading and making the code more understandable.
 */


public class Contact {

    // TODO email validation

    // name is a unique identifier
    private String name;
    private String email = "Not Specified";
    private String contactNumber = "Not Specified";
    private String address = "Not Specified";


    Contact(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name should not be empty");
        }

        this.name = name;
    }


    public void setAddress(String address) {

        if (address == null || address.isEmpty()) {
            address = "Not Specified";
        }

        this.address = address;
    }

    public void setEmail(String email) {

        if (email == null || email.isEmpty()) {
            email = "Not Specified";
        }

        this.email = email;
    }

    public boolean setContactNumber(String contactNumber) {

        if (contactNumber == null || !validateContactNumber(contactNumber)) {
            contactNumber = "Not Specified";
            return false;
        }

        this.contactNumber = contactNumber;
        return true;
    }

    public static boolean validateContactNumber(String contactNumber) {
        // return true if contact Number is valid

        if (contactNumber.length() != 10) {
            return false;
        }

        return contactNumber.chars().allMatch(Character::isDigit);

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
        return ContactService.getContactAsJson(this);
    }
}
