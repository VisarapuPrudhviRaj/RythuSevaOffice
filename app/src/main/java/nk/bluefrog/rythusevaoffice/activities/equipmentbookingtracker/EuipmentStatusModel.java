package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

/**
 * Created by rajkumar on 2/1/19.
 */

public class EuipmentStatusModel {
    private String status;
    private String remark;
    private String todaydate;
    private String hours;

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTodaydate() {
        return todaydate;
    }

    public void setTodaydate(String todaydate) {
        this.todaydate = todaydate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
