package application.wrapper;

 

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import application.model.UsuarioImpl;
 

/**
 * Helper class to wrap a list of Users. This is used for saving the
 * list of Users to XML.
 *
 */
@XmlRootElement(name = "Users")
public class PatientListWrapper {

    private List<UsuarioImpl> Users;

    @XmlElement(name = "User")
    public List<UsuarioImpl> getUsers() {
        return Users;
    }

    public void setUsers(List<UsuarioImpl> users) {
        this.Users = users;
    }

    public void setUser(UsuarioImpl user){
    	if(this.Users == null ){

    	}else{
    	this.Users.add(user);
    	}
   }

}