package model;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String profilePic;
    private String bio;
    
    public User(int id, String name, String email, String phone, 
               String password, String profilePic, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profilePic = profilePic;
        this.bio = bio;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    @Override
    public String toString() {
        return name;
    }
}