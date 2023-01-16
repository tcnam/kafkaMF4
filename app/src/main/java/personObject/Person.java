package personObject;
import com.github.javafaker.Faker;

public class Person {
    private String name;
    private String phone;
    private String address;

    public Person(){
        Faker faker=new Faker();
        this.name=faker.name().fullName();
        this.phone=faker.phoneNumber().cellPhone();
        this.address=faker.address().fullAddress();
    }

    public String getName(){
        return this.name;
    }

    public String getPhone(){
        return this.phone;
    }

    public String getAddress(){
        return this.address;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public void setAddress(String address){
        this.address=address;
    }
}
