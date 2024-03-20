package edu.unam.springsecurity.auth.service;

import edu.unam.springsecurity.auth.dto.UserInfoRoleDTO;
import edu.unam.springsecurity.auth.exception.UserInfoRoleNotFoundException;
import edu.unam.springsecurity.auth.model.UserInfoRole;

import java.util.List;

public interface UserInfoRoleService {
    List<UserInfoRoleDTO> findAll();
    List<UserInfoRoleDTO> findAllOrderByUsrRoleName();
    UserInfoRoleDTO findById(Long id) throws UserInfoRoleNotFoundException;
    UserInfoRoleDTO save(UserInfoRoleDTO role);
    UserInfoRoleDTO convertEntityToDTO(UserInfoRole userInfo);
    UserInfoRole convertDTOtoEntity(UserInfoRoleDTO userInfo);
}
