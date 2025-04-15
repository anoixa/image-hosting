package moe.imtop1.imagehosting.system.service;

public interface IUserInfoService {
    boolean setPassword(String userEmail, String newPassword);

    boolean isRegistered(String userEmail);
}
