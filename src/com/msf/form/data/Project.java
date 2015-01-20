package com.msf.form.data;

import java.util.ArrayList;
import java.util.List;

public class Project {
    
    private String projectCode;
    private String projectName;
    private List<CareCenter> careCenters;

    public Project(String projectCode, String projectName) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.careCenters = new ArrayList<CareCenter>();
    }
    
    /**
     * Get the value of projectCode
     *
     * @return the value of projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Set the value of projectCode
     *
     * @param projectCode new value of projectCode
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Get the value of projectName
     *
     * @return the value of projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Set the value of projectName
     *
     * @param projectName new value of projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Get the value of careCenters
     *
     * @return the value of careCenters
     */
    public List<CareCenter> getCareCenters() {
        return careCenters;
    }

    /**
     * Set the value of careCenters
     *
     * @param careCenters new value of careCenters
     */
    public void setCareCenters(List<CareCenter> careCenters) {
        this.careCenters = careCenters;
    }

}
