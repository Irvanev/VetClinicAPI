package dev.clinic.notificationservice.dtos;

public class DeviceTokenEvent {
    private Long userId;
    private String deviceToken;

    public DeviceTokenEvent() {}
    public DeviceTokenEvent(Long userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }
}
