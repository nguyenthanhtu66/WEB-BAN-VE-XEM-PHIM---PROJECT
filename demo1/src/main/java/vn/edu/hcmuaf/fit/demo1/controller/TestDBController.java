package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.util.TicketDBContext;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/test-db")
public class TestDBController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain; charset=UTF-8");

        try (Connection con = TicketDBContext.getConnection()) {
            resp.getWriter().println("✅ KẾT NỐI SQL SERVER THÀNH CÔNG");
            resp.getWriter().println("Connection = " + con);
        } catch (Exception e) {
            resp.getWriter().println("❌ KẾT NỐI SQL SERVER THẤT BẠI");
            e.printStackTrace(resp.getWriter());
        }
    }
}
