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
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;

@Component
public class SmallFullInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private SmallFullService smallFullService;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        Map<String, String> links = new HashMap<>();
        links.put("self", StringUtils.substringBeforeLast(request.getRequestURI(), "/"));
        smallFullDataEntity.setLinks(links);
        smallFullEntity.setData(smallFullDataEntity);
        SmallFullEntity smallFull = smallFullService.findByExample(smallFullEntity);
        HttpSession session = request.getSession();
        session.setAttribute(AttributeName.SMALLFULL.getValue(), smallFull);
        return true;
    }
}