package vn.edu.hcmuaf.fit.demo1.service;

import java.util.List;

import vn.edu.hcmuaf.fit.demo1.dao.ContactDAO;
import vn.edu.hcmuaf.fit.demo1.model.Contact;

public class ContactService {

    private final ContactDAO contactDAO = new ContactDAO();

    public void save(Contact contact) {
        contactDAO.insert(contact);
    }

    public List<Contact> getAll() {
        return contactDAO.getAll();
    }

    public void markDone(int id) {
        contactDAO.markDone(id);
    }
}