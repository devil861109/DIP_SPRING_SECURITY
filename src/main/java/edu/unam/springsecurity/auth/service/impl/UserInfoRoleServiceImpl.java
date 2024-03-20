package edu.unam.springsecurity.auth.service.impl;

import edu.unam.springsecurity.auth.dto.UserInfoRoleDTO;
import edu.unam.springsecurity.auth.exception.UserInfoRoleNotFoundException;
import edu.unam.springsecurity.auth.model.UserInfoRole;
import edu.unam.springsecurity.auth.repository.UserInfoRoleRepository;
import edu.unam.springsecurity.auth.service.UserInfoRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserInfoRoleServiceImpl implements UserInfoRoleService {
    private final UserInfoRoleRepository userInfoRoleRepository;

    @Autowired
    public UserInfoRoleServiceImpl(UserInfoRoleRepository userInfoRoleRepository) {
        this.userInfoRoleRepository = userInfoRoleRepository;
    }

    @Override
    public List<UserInfoRoleDTO> findAll() {
        log.info("Service - UserInfoRoleServiceImpl.findAll");
        List<UserInfoRole> theList = userInfoRoleRepository.findAllByOrderByUsrIdAsc();
        return theList.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserInfoRoleDTO> findAllOrderByUsrRoleName() {
        log.info("Service - UserInfoRoleServiceImpl.findAll");
        List<UserInfoRole> theList = userInfoRoleRepository.findAllByOrderByUsrRoleNameAsc();
        return theList.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public UserInfoRoleDTO findById(Long id) throws UserInfoRoleNotFoundException {
        log.info("Service - UserInfoRoleServiceImpl.findById {}", id);
        UserInfoRole object = userInfoRoleRepository.findById(id).orElseThrow(() ->
                new UserInfoRoleNotFoundException(id));
        return convertEntityToDTO(object);
    }

    @Override
    public UserInfoRoleDTO save(UserInfoRoleDTO role) {
        log.info("Service - UserInfoRoleServiceImpl.save object {} ", role);
        UserInfoRole finalStatus = convertDTOtoEntity(role);
        finalStatus = userInfoRoleRepository.save(finalStatus);
        return convertEntityToDTO(finalStatus);
    }

    public UserInfoRoleDTO convertEntityToDTO(UserInfoRole userInfo) {
        UserInfoRoleDTO dto = new UserInfoRoleDTO();
        dto.setUsrId(userInfo.getUsrId());
        dto.setUsrRoleName(userInfo.getUsrRoleName());
        dto.setUsrIdStatus(userInfo.getUsrIdStatus());
        dto.setUsrCreatedBy(userInfo.getUsrCreatedBy());
        dto.setUsrCreatedDate(userInfo.getUsrCreatedDate());
        dto.setUsrModifiedBy(userInfo.getUsrModifiedBy());
        dto.setUsrModifiedDate(userInfo.getUsrModifiedDate());
        return dto;
    }

    public UserInfoRole convertDTOtoEntity(UserInfoRoleDTO userInfo) {
        UserInfoRole entity = new UserInfoRole();
        entity.setUsrId(userInfo.getUsrId());
        entity.setUsrRoleName(userInfo.getUsrRoleName());
        entity.setUsrIdStatus(userInfo.getUsrIdStatus());
        entity.setUsrCreatedBy(userInfo.getUsrCreatedBy());
        entity.setUsrCreatedDate(userInfo.getUsrCreatedDate());
        entity.setUsrModifiedBy(userInfo.getUsrModifiedBy());
        entity.setUsrModifiedDate(userInfo.getUsrModifiedDate());
        return entity;
    }
}
