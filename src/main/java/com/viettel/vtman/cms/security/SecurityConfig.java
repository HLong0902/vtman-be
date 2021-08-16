package com.viettel.vtman.cms.security;

import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.security.csrf.CsrfTokenGeneratorFilter;
import com.viettel.vtman.cms.security.jwt.JwtAuthenticationEntryPoint;
import com.viettel.vtman.cms.security.jwt.JwtRequestFilter;
import com.viettel.vtman.cms.security.jwt.JwtRequestFilterApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtRequestFilterApp jwtRequestFilterApp;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private CsrfTokenGeneratorFilter csrfTokenGeneratorFilter;

    private static final String[] SWAGGER_URLS = {
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/v2/api-docs",
        "/webjars/**"
    };

    private static final String[] LOCKED_API = {
            "/api/menu/loadSideMenu",
            "/api/page/getRoutes",
            "/api/historyFaqs/notification/questionAnswerCms"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();

        http.authorizeRequests()
            .antMatchers(LOCKED_API).authenticated()
            .antMatchers(SWAGGER_URLS).permitAll()
            .antMatchers(Const.PAGE_PERMISSION.TOPIC_READ_API).hasAuthority("Topic_1")
            .antMatchers(Const.PAGE_PERMISSION.TOPIC_WRITE_API).hasAuthority("Topic_2")
            .antMatchers(Const.PAGE_PERMISSION.TOPIC_UPDATE_API).hasAuthority("Topic_3")
            .antMatchers(Const.PAGE_PERMISSION.TOPIC_DELETE_API).hasAuthority("Topic_4")
            .antMatchers(Const.PAGE_PERMISSION.QUESTION_DEFINITION_READ_API).hasAuthority("QuestionDefinition_1")
            .antMatchers(Const.PAGE_PERMISSION.QUESTION_DEFINITION_WRITE_API).hasAuthority("QuestionDefinition_2")
            .antMatchers(Const.PAGE_PERMISSION.QUESTION_DEFINITION_UPDATE_API).hasAuthority("QuestionDefinition_3")
            .antMatchers(Const.PAGE_PERMISSION.QUESTION_DEFINITION_DELETE_API).hasAuthority("QuestionDefinition_4")
            .antMatchers(Const.PAGE_PERMISSION.BANNED_CONTENT_READ_API).hasAuthority("BannedContent_1")
            .antMatchers(Const.PAGE_PERMISSION.BANNED_CONTENT_WRITE_API).hasAuthority("BannedContent_2")
            .antMatchers(Const.PAGE_PERMISSION.BANNED_CONTENT_UPDATE_API).hasAuthority("BannedContent_3")
            .antMatchers(Const.PAGE_PERMISSION.BANNED_CONTENT_DELETE_API).hasAuthority("BannedContent_4")
            .antMatchers(Const.PAGE_PERMISSION.CONTENT_AUTOMATIC_READ_API).hasAuthority("ContentAutomatic_1")
            .antMatchers(Const.PAGE_PERMISSION.CONTENT_AUTOMATIC_WRITE_API).hasAuthority("ContentAutomatic_2")
            .antMatchers(Const.PAGE_PERMISSION.CONTENT_AUTOMATIC_UPDATE_API).hasAuthority("ContentAutomatic_3")
            .antMatchers(Const.PAGE_PERMISSION.CONTENT_AUTOMATIC_DELETE_API).hasAuthority("ContentAutomatic_4")
            .antMatchers(Const.PAGE_PERMISSION.HISTORY_FAQ_READ_API).hasAuthority("HistoryFaq_1")
            .antMatchers(Const.PAGE_PERMISSION.HISTORY_FAQ_WRITE_API).hasAuthority("HistoryFaq_2")
            .antMatchers(Const.PAGE_PERMISSION.HISTORY_FAQ_UPDATE_API).hasAuthority("HistoryFaq_3")
            .antMatchers(Const.PAGE_PERMISSION.HISTORY_FAQ_DELETE_API).hasAuthority("HistoryFaq_4")
            .antMatchers(Const.PAGE_PERMISSION.WORK_CALENDAR_READ_API).hasAuthority("WorkCalendar_1")
            .antMatchers(Const.PAGE_PERMISSION.WORK_CALENDAR_WRITE_API).hasAuthority("WorkCalendar_2")
            .antMatchers(Const.PAGE_PERMISSION.WORK_CALENDAR_UPDATE_API).hasAuthority("WorkCalendar_3")
            .antMatchers(Const.PAGE_PERMISSION.WORK_CALENDAR_DELETE_API).hasAuthority("WorkCalendar_4")
            .antMatchers(Const.PAGE_PERMISSION.FUNCTION_CONFIG_READ_API).hasAuthority("FunctionConfig_1")
            .antMatchers(Const.PAGE_PERMISSION.FUNCTION_CONFIG_WRITE_API).hasAuthority("FunctionConfig_2")
            .antMatchers(Const.PAGE_PERMISSION.FUNCTION_CONFIG_UPDATE_API).hasAuthority("FunctionConfig_3")
            .antMatchers(Const.PAGE_PERMISSION.FUNCTION_CONFIG_DELETE_API).hasAuthority("FunctionConfig_4")
            .antMatchers(Const.PAGE_PERMISSION.REPORT_READ_API).hasAuthority("Report_1")
            .antMatchers(Const.PAGE_PERMISSION.REPORT_WRITE_API).hasAuthority("Report_2")
            .antMatchers(Const.PAGE_PERMISSION.REPORT_UPDATE_API).hasAuthority("Report_3")
            .antMatchers(Const.PAGE_PERMISSION.REPORT_DELETE_API).hasAuthority("Report_4")
            .antMatchers(Const.PAGE_PERMISSION.MENU_MANAGEMENT_READ_API).hasAuthority("Menu_1")
            .antMatchers(Const.PAGE_PERMISSION.MENU_MANAGEMENT_WRITE_API).hasAuthority("Menu_2")
            .antMatchers(Const.PAGE_PERMISSION.MENU_MANAGEMENT_UPDATE_API).hasAuthority("Menu_3")
            .antMatchers(Const.PAGE_PERMISSION.MENU_MANAGEMENT_DELETE_API).hasAuthority("Menu_4")
            .antMatchers(Const.PAGE_PERMISSION.PAGE_MANAGEMENT_READ_API).hasAuthority("Page_1")
            .antMatchers(Const.PAGE_PERMISSION.PAGE_MANAGEMENT_WRITE_API).hasAuthority("Page_2")
            .antMatchers(Const.PAGE_PERMISSION.PAGE_MANAGEMENT_UPDATE_API).hasAuthority("Page_3")
            .antMatchers(Const.PAGE_PERMISSION.PAGE_MANAGEMENT_DELETE_API).hasAuthority("Page_4")
            .antMatchers(Const.PAGE_PERMISSION.PERMISSION_READ_API).hasAuthority("Permission_1")
            .antMatchers(Const.PAGE_PERMISSION.PERMISSION_WRITE_API).hasAuthority("Permission_2")
            .antMatchers(Const.PAGE_PERMISSION.PERMISSION_UPDATE_API).hasAuthority("Permission_3")
            .antMatchers(Const.PAGE_PERMISSION.PERMISSION_DELETE_API).hasAuthority("Permission_4")
            .antMatchers(Const.PAGE_PERMISSION.USER_AUTHORIZATION_READ_API).hasAuthority("UserAuthorization_1")
            .antMatchers(Const.PAGE_PERMISSION.USER_AUTHORIZATION_WRITE_API).hasAuthority("UserAuthorization_2")
            .antMatchers(Const.PAGE_PERMISSION.USER_AUTHORIZATION_UPDATE_API).hasAuthority("UserAuthorization_3")
            .antMatchers(Const.PAGE_PERMISSION.USER_AUTHORIZATION_DELETE_API).hasAuthority("UserAuthorization_4")
            .antMatchers(Const.PAGE_PERMISSION.ROLE_READ_API).hasAuthority("Role_1")
            .antMatchers(Const.PAGE_PERMISSION.ROLE_WRITE_API).hasAuthority("Role_2")
            .antMatchers(Const.PAGE_PERMISSION.ROLE_UPDATE_API).hasAuthority("Role_3")
            .antMatchers(Const.PAGE_PERMISSION.ROLE_DELETE_API).hasAuthority("Role_4")
            .antMatchers(Const.PAGE_PERMISSION.DEPARTMENT_READ_API).hasAuthority("Department_1")
            .antMatchers(Const.PAGE_PERMISSION.DEPARTMENT_WRITE_API).hasAuthority("Department_2")
            .antMatchers(Const.PAGE_PERMISSION.DEPARTMENT_UPDATE_API).hasAuthority("Department_3")
            .antMatchers(Const.PAGE_PERMISSION.DEPARTMENT_DELETE_API).hasAuthority("Department_4")
            .antMatchers("/authen/oauth2", "/sso/login", "/sso/logout").permitAll()
            .antMatchers("/api/topic/**").permitAll()
            .antMatchers("/api/questionDefinitions").permitAll()
            .antMatchers("/api/question/**").permitAll()
            .antMatchers("/api/historyFaqs/**").permitAll()
            .antMatchers("/api/autoContent/**").permitAll()
            .antMatchers("/api/response/**").permitAll()
            .antMatchers("/api/pushNotification/**").permitAll()
            .antMatchers("/api/functionConfig/**").permitAll()
            .antMatchers("/api/bannedContent/**").permitAll()
            .antMatchers("/api/workCalendar/**").permitAll()
            .antMatchers("/api/menu/**").permitAll()
            .antMatchers("/api/page/**").permitAll()
            .antMatchers("/api/role/**").permitAll()
            .antMatchers("/api/userAuthorization/**").permitAll()
            .antMatchers("/api/permissionAction/**").permitAll()
            .antMatchers("/api/report/**").permitAll()
            .antMatchers("/download/**").permitAll()
                .antMatchers("/api/evaluate/**").permitAll()
            .anyRequest()
            .authenticated()
            .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilterApp, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(csrfTokenGeneratorFilter, CsrfFilter.class);
    }
}
