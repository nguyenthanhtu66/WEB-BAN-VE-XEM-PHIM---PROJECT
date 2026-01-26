package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.TicketDAO;
import vn.edu.hcmuaf.fit.demo1.model.Ticket;

import java.util.List;

public class TicketService {

    private final TicketDAO ticketDAO = new TicketDAO();

    public List<Ticket> getTicketsByUser(int userId) {
        return ticketDAO.findByUserId(userId);
    }

    public boolean cancelTicket(int ticketId, int userId) {
        // (Có thể mở rộng kiểm tra ticket có thuộc user hay không)
        return ticketDAO.cancelTicket(ticketId);
    }
    
}
