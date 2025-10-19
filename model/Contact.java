package model;

public class Contact {
    private int id;
    private String registeredName;
    private String phone;
    private String displayName;
    private String email;    // New field
    private String bio;      // New field

    // Updated constructor
    public Contact(int id, String registeredName, String phone, String displayName, 
                  String email, String bio) {
        this.id = id;
        this.registeredName = registeredName;
        this.phone = phone;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // Returns the display name if set, otherwise the registered name
    public String getEffectiveName() {
        return (displayName != null && !displayName.isEmpty()) ? displayName : registeredName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return getEffectiveName() + " - " + phone;
    }
}