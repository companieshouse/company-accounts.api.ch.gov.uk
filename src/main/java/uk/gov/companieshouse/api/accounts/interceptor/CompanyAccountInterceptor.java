package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;

@Component
public class CompanyAccountInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CompanyAccountService companyAccountService;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        HttpSession session = request.getSession();
        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        Map<String, String> link = new HashMap<>();
        link.put("self", StringUtils.substringBeforeLast(request.getRequestURI(), "/small-full"));
        companyAccountDataEntity.setLinks(link);
        companyAccountEntity.setData(companyAccountDataEntity);
        CompanyAccountEntity result = companyAccountService.findByExample(companyAccountEntity);
        session.setAttribute(AttributeName.COMPANY_ACCOUNT.getValue(), result);
        return true;
    }
}