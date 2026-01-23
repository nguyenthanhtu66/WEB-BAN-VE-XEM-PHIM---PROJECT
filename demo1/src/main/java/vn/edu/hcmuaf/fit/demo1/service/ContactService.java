package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.ContactDAO;
import vn.edu.hcmuaf.fit.demo1.model.Contact;

public class ContactService {

    private final ContactDAO contactDAO = new ContactDAO();

    // public void save(Contact contact) {
    //     contactDAO.insert(contact);
    // }
      public void save(Contact contact) {
        // TEST TẠM – CHƯA LƯU DB
        System.out.println("===== TEST CONTACT =====");
        System.out.println("Họ tên: " + contact.getHoTen());
        System.out.println("SĐT: " + contact.getSoDienThoai());
        System.out.println("Email: " + contact.getEmail());
        System.out.println("Dịch vụ: " + contact.getDichVu());
        System.out.println("Chi tiết: " + contact.getChiTiet());
        System.out.println("========================");
    }
}
