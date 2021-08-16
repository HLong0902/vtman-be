package com.viettel.vtman.cms.security.jwt;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.PermissionActionService;
import com.viettel.vtman.cms.service.RoleService;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.viettel.vtman.cms.utils.Common.authenticate;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtRequestFilter.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PermissionActionService permissionActionService;

    @Autowired
    private RoleService roleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        Claims jwtPayload = authenticate(request);
        if (jwtPayload != null && !StringUtils.isEmpty(jwtPayload.get("userId"))) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            EmployeeDto employeeDto = employeeService.findByUserId(Long.valueOf(jwtPayload.get("userId").toString()));
            if (employeeDto == null) {
                employeeDto = employeeService.createEmployeeCms(jwtPayload);
            }

            if (employeeDto != null && employeeDto.getStatus().compareTo(1L) == 0) {
                boolean needUpdate = false;
                if (!String.valueOf(jwtPayload.get("phone")).equals(employeeDto.getPhone())) {
                    needUpdate = true;
                }
                if (!String.valueOf(jwtPayload.get("mabuucuc")).equals(employeeDto.getPostOfficeCode())) {
                    needUpdate = true;
                }
                if (needUpdate) {
                    employeeService.updateEmployeeCms(employeeDto.getEmployeeId(), jwtPayload);
                }

                PermissionActionDTO searchDTO = new PermissionActionDTO();
                searchDTO.setRoleId(employeeDto.getRoleId());
                List<PermissionActionDTO> permissionList = permissionActionService.getPermissionActionByDTO(searchDTO);
                for (PermissionActionDTO permission : permissionList) {
                    if (permission.getPropPage() == null || StringUtils.isEmpty(permission.getPropPage().getComponent())) {
                        continue;
                    }
                    for (String action : permission.getActionId().split(",")) {
                        authorities.add(new SimpleGrantedAuthority(permission.getPropPage().getComponent() + "_" + action.trim()));
                    }
                }

                if (Objects.nonNull(employeeDto.getRoleId())) {
                    Role role = roleService.findById(employeeDto.getRoleId());
                    employeeDto.setRoleDTO(Objects.isNull(role) ? null : new RoleDTO(role));
                }

                employeeDto.setJwtPayload(jwtPayload);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(employeeDto, null, authorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }

    /*
    private Claims authenticate(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                Claims jwtPayload = Jwts.parser()
                        .setSigningKey(readPublicKey())
                        .parseClaimsJws(token.substring(7))
                        .getBody();

//                if (jwtPayload != null && jwtPayload.get("exp") != null) {
//                    if (jwtPayload.getExpiration().after(new Date())) {
//                        return jwtPayload;
//                    }
//                }
                if (jwtPayload != null) {
                    return jwtPayload;
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public static RSAPublicKey readPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] bytes = ByteStreams.toByteArray(new ClassPathResource("sso_public_key/ssopublic.pem").getInputStream());
        String key = new String(bytes, Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
    */
}
