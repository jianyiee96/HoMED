package web.filter;

import entity.Employee;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.enumeration.EmployeeRoleEnum;

@WebFilter(filterName = "SecurityFilter", urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

    private FilterConfig filterConfig;

    private static final String CONTEXT_ROOT = "/HoMED-war";

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpSession httpSession = httpServletRequest.getSession(true);
        String requestServletPath = httpServletRequest.getServletPath();

        Boolean isLogin = httpSession.getAttribute("currentEmployee") != null;
        if (!excludeLoginCheck(requestServletPath)) {
            if (isLogin == true) {
                Employee currentEmployee = (Employee) httpSession.getAttribute("currentEmployee");

                if (requestServletPath.equals("/login.xhtml")) {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/homepage.xhtml");
                } else if (checkAccessRight(requestServletPath, currentEmployee.getRole()) != 2) {
                    chain.doFilter(request, response);
                } else {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/accessRightError.xhtml");
                }
            } else {
                if (requestServletPath.equals("/login.xhtml")) {
                    chain.doFilter(request, response);
                } else {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/login.xhtml");
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private Integer checkAccessRight(String path, EmployeeRoleEnum accessRight) {
        // 0 - Page doesn't exist
        // 1 - Authorised
        // 2 - Unauthorized

        String[] pathArr = new String[]{
            "/homepage.xhtml", // 0
            "/accessRightError.xhtml", // 1
            "/medicalCentreManagement.xhtml", // 2
            "/form-util.xhtml" //3
        };

        // Pages that all logged in users can enter
        if (path.equals(pathArr[0])
                || path.equals(pathArr[1])) {
            return 1;
        }

        if (accessRight == EmployeeRoleEnum.ADMIN) {
            if (path.equals(pathArr[2]) 
                    || path.equals(pathArr[3])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.MEDICAL_OFFICER) {

        } else if (accessRight == EmployeeRoleEnum.CLERK) {

        }
        
        for (String currentPath: pathArr) {
            if (path.equals(currentPath)) {
                return 2;
            }
        }
        
        // FOR DEVELOPMENT ==> SET TO TRUE
        return 0;
    }

    private Boolean excludeLoginCheck(String path) {
        if (path.startsWith("/javax.faces.resource")
                || path.startsWith("/resources/")) {
            return true;
        } else {
            return false;
        }
    }

    public void destroy() {
    }

}
