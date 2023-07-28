package com.shanjun.ncbi.uitls;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class RequestUtils {


    public static   String getParam(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        StringBuilder stringBuilder = new StringBuilder();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if(paramName.equals("page") || paramName.equals("size")  ){
                continue;
            }
            stringBuilder.append(paramName+"="+paramValues[0]);
//            System.out.println(paramName + " = " + paramValues[0]);
        }
        String param = stringBuilder.toString();
        if(!StringUtils.isEmpty(param)){
            param = param+"&";
        }
        return param;
    }
}
