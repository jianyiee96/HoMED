package web.filter;

import entity.Employee;
import java.io.IOException;
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
                } else if (checkAccessRight(requestServletPath, currentEmployee.getRole()) == 2) {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/access-right-error.xhtml");
                } else if (checkAccessRight(requestServletPath, currentEmployee.getRole()) == 0) {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/page-not-found.xhtml");
                } else {
                    chain.doFilter(request, response);
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
            "/access-right-error.xhtml", // 1
            "/page-not-found.xhtml", // 2
            "/medical-centre-management.xhtml", // 3
            "/profile.xhtml", // 4
            "/employee-management.xhtml", // 5
            "/serviceman-management.xhtml", // 6
            "/form-util.xhtml", // 7
            "/consultation-util.xhtml", // 8
            "/medical-centre-staff-management.xhtml", // 9
            "/scheduler-management.xhtml", // 10
            "/booking-management.xhtml" //11
        };

        // Pages that all logged in users can enter
        if (path.equals(pathArr[0])
                || path.equals(pathArr[1])
                || path.equals(pathArr[2])
                || path.equals(pathArr[4])) {
            return 1;
        }

        if (accessRight == EmployeeRoleEnum.SUPER_USER) {

            if (path.equals(pathArr[3])
                    || path.equals(pathArr[5])
                    || path.equals(pathArr[6])
                    || path.equals(pathArr[7])
                    || path.equals(pathArr[8])
                    || path.equals(pathArr[9])
                    || path.equals(pathArr[10])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.MEDICAL_OFFICER) {
        } else if (accessRight == EmployeeRoleEnum.CLERK) {
            if (path.equals(pathArr[10])
                    || path.equals(pathArr[11])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.MB_ADMIN) {
        }

        for (String currentPath : pathArr) {
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
