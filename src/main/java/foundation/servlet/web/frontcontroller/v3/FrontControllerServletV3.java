package foundation.servlet.web.frontcontroller.v3;


import foundation.servlet.web.frontcontroller.ModelView;
import foundation.servlet.web.frontcontroller.MyView;
import foundation.servlet.web.frontcontroller.v2.ControllerV2;

import foundation.servlet.web.frontcontroller.v3.controller.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//v3의 핵심은  viewResolver     view name 매핑을 통해
//중복 코드를 하나로 줄임
@WebServlet(name = "frontControllerServletV3",urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {
    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() { //생성자 컨트롤러 매핑 처리
        controllerMap.put("/front-controller/v3/members/new-form",new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save",new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members",new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI); //다형성 부모가 자식을 담을 수 있다.
        if(controller == null){ // 예외처리 컨트롤러 매핑정보가 없으면 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Map<String, String> paramMap = createParamMap(request);

        ModelView mv = controller.process(paramMap);
        String viewName = mv.getViewName();//논리이름 가져오기 new-form
        System.out.println("viewName = " + mv.getViewName());
        MyView view = viewResolver(viewName);
        System.out.println("viewName = " + view.getViewPath());
        view.render(mv.getModel(),request,response);
    }

    private static MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private static Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
