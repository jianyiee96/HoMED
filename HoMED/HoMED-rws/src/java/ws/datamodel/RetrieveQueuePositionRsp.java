/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class RetrieveQueuePositionRsp {
    
    int position;

    public RetrieveQueuePositionRsp() {
    }

    public RetrieveQueuePositionRsp(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
}
