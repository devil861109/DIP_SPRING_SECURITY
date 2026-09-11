package edu.unam.springsecurity.service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import javax.naming.Name;
import javax.naming.ldap.LdapName;

import edu.unam.springsecurity.dto.NewUser;
import edu.unam.springsecurity.dto.UserInfo;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLdapService {

    // Solo estos atributos viajan desde LDAP; userPassword nunca sale
    private static final String[] ATRIBUTOS = {"uid", "cn", "sn", "givenName", "mail"};

    private final LdapTemplate ldapTemplate;
    private final LdapName base;  // dc=unam,dc=org
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // genera $2a$

    private final ContextMapper<UserInfo> usuarioMapper =
            ctx -> mapear((DirContextOperations) ctx);
    private final AttributesMapper<String> cnMapper =
            attrs -> (String) attrs.get("cn").get();

    public UserLdapService(LdapTemplate ldapTemplate, LdapContextSource contextSource) {
        this.ldapTemplate = ldapTemplate;
        this.base = contextSource.getBaseLdapName();
    }

    // ================= CONSULTAS =================

    public Optional<UserInfo> buscarPorUid(String uid) {
        return ldapTemplate.search(
                        query().base("ou=usuarios").attributes(ATRIBUTOS)
                                .where("objectClass").is("inetOrgPerson").and("uid").is(uid),
                        usuarioMapper)
                .stream().findFirst();
    }

    public List<UserInfo> listar() {
        return ldapTemplate.search(
                query().base("ou=usuarios").attributes(ATRIBUTOS)
                        .where("objectClass").is("inetOrgPerson"),
                usuarioMapper);
    }

    private UserInfo mapear(DirContextOperations c) {
        String dnCompleto = LdapUtils.prepend(c.getDn(), base).toString();
        return new UserInfo(
                c.getStringAttribute("uid"),
                c.getStringAttribute("givenName"),
                c.getStringAttribute("sn"),
                c.getStringAttribute("cn"),
                c.getStringAttribute("mail"),
                dnCompleto,
                gruposDe(dnCompleto));
    }

    private List<String> gruposDe(String dnCompleto) {
        return ldapTemplate.search(
                query().base("ou=grupos").attributes("cn").where("member").is(dnCompleto),
                cnMapper);
    }

    // ================= CREACION =================

    public void crear(NewUser u) {
        Name userDn = LdapNameBuilder.newInstance("ou=usuarios").add("uid", u.uid()).build();

        DirContextAdapter entry = new DirContextAdapter(userDn);
        entry.setAttributeValues("objectClass",
                new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
        entry.setAttributeValue("uid", u.uid());
        entry.setAttributeValue("cn", u.nombre() + " " + u.apellido());
        entry.setAttributeValue("sn", u.apellido());
        entry.setAttributeValue("givenName", u.nombre());
        entry.setAttributeValue("mail", u.email());
        entry.setAttributeValue("userPassword",
                ("{CRYPT}" + encoder.encode(u.password())).getBytes(StandardCharsets.UTF_8));

        ldapTemplate.bind(entry);  // NameAlreadyBoundException si el uid ya existe

        try {
            agregarAGrupo(userDn, u.grupo());
        } catch (RuntimeException e) {
            ldapTemplate.unbind(userDn);  // si falla el grupo, no deja usuarios huerfanos
            throw e;
        }
    }

    private void agregarAGrupo(Name userDn, String grupo) {
        Name groupDn = LdapNameBuilder.newInstance("ou=grupos").add("cn", grupo).build();
        DirContextOperations group = ldapTemplate.lookupContext(groupDn); // NameNotFoundException si no existe
        group.addAttributeValue("member", LdapUtils.prepend(userDn, base).toString());
        ldapTemplate.modifyAttributes(group);
    }
}
