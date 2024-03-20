package edu.unam.springsecurity.auth.service;

import edu.unam.springsecurity.auth.dto.UserInfoDTO;
import edu.unam.springsecurity.auth.exception.UserInfoNotFoundException;

import java.util.List;

public interface UserInfoService {
    List<UserInfoDTO> findAll();
    UserInfoDTO findById(Long id) throws UserInfoNotFoundException;
    UserInfoDTO save(UserInfoDTO userAdmin) throws UserInfoNotFoundException;
    UserInfoDTO findByUseEmail(String email) throws UserInfoNotFoundException;
}
