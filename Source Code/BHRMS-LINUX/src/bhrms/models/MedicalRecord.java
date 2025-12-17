package bhrms.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class MedicalRecord {
    private int recordId;
    private int patientId;
    private Date visitDate;
    private String visitType;
    private BigDecimal temperature;
    private String bloodPressure;
    private BigDecimal weight;
    private BigDecimal height;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String prescribedMeds;
    private String notes;
    private Date followUpDate;
    private int healthWorkerId;
    private String patientName; // For display purposes
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public MedicalRecord() {}
    
    public MedicalRecord(int patientId, Date visitDate, String visitType, 
                        BigDecimal temperature, String bloodPressure, 
                        BigDecimal weight, BigDecimal height, String symptoms, 
                        String diagnosis, String treatment, String prescribedMeds, 
                        String notes, Date followUpDate, int healthWorkerId) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.visitType = visitType;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescribedMeds = prescribedMeds;
        this.notes = notes;
        this.followUpDate = followUpDate;
        this.healthWorkerId = healthWorkerId;
    }
    
    // Getters and setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public Date getVisitDate() { return visitDate; }
    public void setVisitDate(Date visitDate) { this.visitDate = visitDate; }
    
    public String getVisitType() { return visitType; }
    public void setVisitType(String visitType) { this.visitType = visitType; }
    
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    
    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getPrescribedMeds() { return prescribedMeds; }
    public void setPrescribedMeds(String prescribedMeds) { this.prescribedMeds = prescribedMeds; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Date getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(Date followUpDate) { this.followUpDate = followUpDate; }
    
    public int getHealthWorkerId() { return healthWorkerId; }
    public void setHealthWorkerId(int healthWorkerId) { this.healthWorkerId = healthWorkerId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public String getBMICategory() {
        if (weight == null || height == null) {
            return "N/A";
        }
        
        double heightInMeters = height.doubleValue() / 100;
        double bmi = weight.doubleValue() / (heightInMeters * heightInMeters);
        
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
}