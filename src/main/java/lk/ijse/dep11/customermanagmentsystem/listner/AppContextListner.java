package lk.ijse.dep11.customermanagmentsystem.listner;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.sql.SQLException;

@WebListener
public class AppContextListner implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("inside contex initialize");
        String appPath = sce.getServletContext().getRealPath("/");
        new File(appPath,"uploads").mkdir();

        BasicDataSource pool = new BasicDataSource();
        pool.setUsername("root");
        pool.setPassword("azhar");
        pool.setDriverClassName("com.mysql.cj.jdbc.Driver");
        pool.setUrl("jdbc:mysql://localhost:3306/dep11_ssr2");
        pool.setInitialSize(5);
        pool.setMaxTotal(15);

        sce.getServletContext().setAttribute("connectionPool",pool);
        System.out.println("ending contex initialize");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ((BasicDataSource) sce.getServletContext().getAttribute("connectionPool")).close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
