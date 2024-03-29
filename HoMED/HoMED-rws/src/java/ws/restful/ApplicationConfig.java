package ws.restful;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.restful.ConsultationResource.class);
        resources.add(ws.restful.CorsFilter.class);
        resources.add(ws.restful.FormResource.class);
        resources.add(ws.restful.MedicalBoardResource.class);
        resources.add(ws.restful.MedicalCentreResource.class);
        resources.add(ws.restful.NotificationResource.class);
        resources.add(ws.restful.SchedulerResource.class);
        resources.add(ws.restful.ServicemanResource.class);
    }

}
