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
            "/booking-slot-management.xhtml", // 10
            "/medical-board-slot-management.xhtml", // 11
            "/booking-management.xhtml", // 12
            "/medical-board-management.xhtml", // 13
            "/queue-management.xhtml", // 14
            "/past-consultations.xhtml", // 15
            "/serviceman-consultation-records.xhtml", // 16
            "/current-consultation.xhtml", // 17
            "/queue-display.xhtml", // 18
            "/medical-board.xhtml", // 19
            "/medical-board-session.xhtml", // 20
            "/report-management.xhtml", // 21
            "/manage-report.xhtml" // 22
        };

        // Pages that all logged in users can enter
        if (path.equals(pathArr[0])
                || path.equals(pathArr[1])
                || path.equals(pathArr[2])
                || path.equals(pathArr[3])
                || path.equals(pathArr[4])
                || path.equals(pathArr[21])
                || path.equals(pathArr[22])) {
            return 1;
        }

        if (accessRight == EmployeeRoleEnum.SUPER_USER) {
            if (path.equals(pathArr[5])
                    || path.equals(pathArr[6])
                    || path.equals(pathArr[7])
                    || path.equals(pathArr[8])
                    || path.equals(pathArr[9])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.MEDICAL_OFFICER) {
            if (path.equals(pathArr[14])
                    || path.equals(pathArr[15])
                    || path.equals(pathArr[16])
                    || path.equals(pathArr[17])
                    || path.equals(pathArr[19])
                    || path.equals(pathArr[20])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.CLERK) {
            if (path.equals(pathArr[10])
                    || path.equals(pathArr[12])
                    || path.equals(pathArr[18])) {
                return 1;
            }
        } else if (accessRight == EmployeeRoleEnum.MB_ADMIN) {
            if (path.equals(pathArr[10])
                    || path.equals(pathArr[11])
                    || path.equals(pathArr[12])
                    || path.equals(pathArr[13])
                    || path.equals(pathArr[18])) {
                return 1;
            }
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
