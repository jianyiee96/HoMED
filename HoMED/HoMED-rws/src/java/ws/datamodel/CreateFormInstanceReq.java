/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class CreateFormInstanceReq {
    
    private Long servicemanId;
    private Long formTemplateId;

    public CreateFormInstanceReq() {
    }

    public CreateFormInstanceReq(Long servicemanId, Long formTemplateId) {
        this.servicemanId = servicemanId;
        this.formTemplateId = formTemplateId;
    }
    
    public Long getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

    public Long getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(Long formTemplateId) {
        this.formTemplateId = formTemplateId;
    }
    
    
    
}
