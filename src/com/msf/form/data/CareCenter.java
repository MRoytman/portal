package com.msf.form.data;

public class CareCenter {
    
    private String careCenterCode;
    private String careCenterName;

    public CareCenter(String careCenterCode, String careCenterName) {
        this.careCenterCode = careCenterCode;
        this.careCenterName = careCenterName;
    }

    /**
     * Get the value of careCenterName
     *
     * @return the value of careCenterName
     */
    public String getCareCenterName() {
        return careCenterName;
    }

    /**
     * Set the value of careCenterName
     *
     * @param careCenterName new value of careCenterName
     */
    public void setCareCenterName(String careCenterName) {
        this.careCenterName = careCenterName;
    }


    /**
     * Get the value of careCenterCode
     *
     * @return the value of careCenterCode
     */
    public String getCareCenterCode() {
        return careCenterCode;
    }

    /**
     * Set the value of careCenterCode
     *
     * @param careCenterCode new value of careCenterCode
     */
    public void setCareCenterCode(String careCenterCode) {
        this.careCenterCode = careCenterCode;
    }

}
