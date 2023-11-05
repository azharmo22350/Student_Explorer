package lk.ijse.dep11.customermanagmentsystem;

import lk.ijse.dep11.customermanagmentsystem.to.Student;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/students")
@MultipartConfig(location = "/tmp")
public class StudentServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("connectionPool");

        try(Connection connection = pool.getConnection()){
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery("SELECT S.* , P.path FROM student AS S LEFT OUTER JOIN picture AS P ON S.id=P.student_id");

            List<Student> studentList =new ArrayList<>();

            while (resultSet.next()){
                String id = String.format("S%03d",resultSet.getInt("id"));
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String path = resultSet.getString("path");
                studentList.add(new Student(id, name, address, path));
            }

            req.setAttribute("studentlist",studentList);
            getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(req,resp);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String address = req.getParameter("address");
        Part picture = req.getPart("picture");

        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("connectionPool");

        try {
            System.out.println("get connection");
            Connection connection = pool.getConnection();
            connection.setAutoCommit(false);

            try {
                System.out.println("before writing query");
                PreparedStatement stmStudent = connection.prepareStatement("INSERT INTO student (name,address) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                stmStudent.setString(1, name);
                stmStudent.setString(2, address);
                stmStudent.executeUpdate();
                System.out.println("After exicute query");

                if (picture!= null) {
                    String uploadDirPath = getServletContext().getRealPath("/uploads");
                    UUID imageId = UUID.randomUUID();
                    String picturePath = uploadDirPath + imageId;

                    ResultSet generatedKeys = stmStudent.getGeneratedKeys();
                    generatedKeys.next();

                    PreparedStatement stmPicture = connection.prepareStatement("INSERT INTO picture (student_id,path) VALUES (?,?)");
                    stmPicture.setInt(1, generatedKeys.getInt(1));
                    stmPicture.setString(2, "uploads/" + imageId);
                    stmPicture.executeUpdate();
                    picture.write(picturePath);


                }
                connection.commit();
            }catch (Throwable t)
            {
                connection.rollback();
                t.printStackTrace();
            }finally {
                connection.setAutoCommit(true);
                connection.close();
            }

        } catch (SQLException e) {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeee");
            throw new RuntimeException(e);
        }
        resp.sendRedirect("/app");


    }
}
