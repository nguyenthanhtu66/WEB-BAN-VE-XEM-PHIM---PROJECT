package vn.edu.hcmuaf.fit.demo1.service;
import vn.edu.hcmuaf.fit.demo1.dao.TicketWarehouseDao;

import vn.edu.hcmuaf.fit.demo1.model.Ticket;

import java.util.List;

public class TicketService {

    private final TicketWarehouseDao ticketDAO = new TicketWarehouseDao();

    public List<Ticket> getTicketsByUser(int userId) {
        return ticketDAO.findByUserId(userId);
    }

    public boolean cancelTicket(int ticketId, int userId) {
        return ticketDAO.cancelTicket(ticketId, userId);
    }

    public boolean deleteTicket(int ticketId, int userId) {
        return ticketDAO.deleteCancelledTicket(ticketId, userId);
    }

    public int getTotalTicketPrice(int userId) {
        int total = ticketDAO.getTotalPriceByUser(userId);
        return Math.max(total, 0);
    }

    public int countActiveTickets(int userId) {
        int total = ticketDAO.countActiveTicketsByUser(userId);
        return Math.max(total, 0);
    }

}
