package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.ContactDAO;
import vn.edu.hcmuaf.fit.demo1.model.Contact;

public class ContactService {

    private final ContactDAO contactDAO = new ContactDAO();

    public void save(Contact contact) {
        contactDAO.insert(contact);
    }
}
