/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package gui;

import DAL.AccountDAO;
import DAL.DAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.Account;

/**
 *
 * @author phanh
 */
@WebServlet(name = "ForgotPassword", urlPatterns = {"/forgotPassword"})
public class ForgotPassword extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ForgotPassword</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPassword at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy địa chỉ email từ biến form
        String userEmail = request.getParameter("email");

        DAO dao = new DAO();
        String userName = dao.getUserNameByGmail(userEmail);

        if (userName != "") {
            // Tạo mã xác thực duy nhất
            String uuid = UUID.randomUUID().toString();

            // Lưu mã xác thực trong cơ sở dữ liệu của bạn để sử dụng sau này
            // Trong ví dụ này, chúng ta sẽ in mã xác thực ra màn hình để kiểm tra xem nó hoạt động như thế nào
//        System.out.println("Verification code: " + uuid);
            // Thiết lập thông tin email
            final String username = "phanhieu000lc@gmail.com";
            final String password = "kenbyojmgcjglacz";
            String host = "smtp.gmail.com";
            int port = 587;
            String from = userEmail;
            String subject = "Email Forgot Password";
            String content = "Nhan Vao Link Ben Duoi Cap Nhat Lai Mat Khau Cua Ban:\nhttp://localhost:9999/SWP391-G2/resetPass?userName=" + userName;

            // Thiết lập các thuộc tính email
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);

            // Tạo phiên gửi email và thiết lập thông tin người gửi
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                // Tạo đối tượng MimeMessage và thiết lập các thuộc tính email
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
                message.setSubject(subject);
                message.setText(content);

                // Gửi email
                Transport.send(message);
                
                Account a = new AccountDAO().getAccountByUserName(userName);

                
                dao.insertCodeMail("Forgot Password", uuid, a.getAccountID());

                // Chuyển hướng đến trang xác nhận email
            } catch (MessagingException e) {
                throw new RuntimeException("SendMail Controller -> doGet(): " + e);
            }
            request.setAttribute("errorForgot", "PLS Check Your Mail To ResetPass !");
            request.getRequestDispatcher("gui/common/home.jsp").forward(request, response);
        } else {
            request.setAttribute("errorForgot", "Not Found Your Gmail. PLS Try Again !");
            request.getRequestDispatcher("gui/common/home.jsp").forward(request, response);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
